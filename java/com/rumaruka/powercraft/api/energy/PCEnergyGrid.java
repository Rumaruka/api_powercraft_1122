package com.rumaruka.powercraft.api.energy;


import com.rumaruka.powercraft.api.PCApi;
import com.rumaruka.powercraft.api.PCSide;
import com.rumaruka.powercraft.api.PCTickHandler;
import com.rumaruka.powercraft.api.grid.IGridFactory;
import com.rumaruka.powercraft.api.grid.PCGrid;
import com.rumaruka.powercraft.api.reflect.PCSecurity;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PCEnergyGrid extends PCGrid<PCEnergyGrid, IEnergyGridTile, PCEnergyNode<?>, PCEnergyEdge> {

    private static boolean isEnergyModulePresent;
    static List<WeakReference<PCEnergyGrid>> grids = new ArrayList<WeakReference<PCEnergyGrid>>();
    private static Ticker ticker;

    public static final IGridFactory<PCEnergyGrid, IEnergyGridTile, PCEnergyNode<?>, PCEnergyEdge> factory = new Factory();

    public static void register() {
        PCSecurity.allowedCaller("PC_EnergyGrid.register", PCApi.class);
        if (ticker == null) {
            ticker = new Ticker();
            PCTickHandler.registerTickHandler(ticker);
        }
    }

    private static class Ticker implements PCTickHandler.IWorldTickHandler {

        Ticker() {
            //
        }

        @Override
        public void onStartTick(PCSide side, World world) {
            Iterator<WeakReference<PCEnergyGrid>> iterator = grids.iterator();
            while (iterator.hasNext()) {
                PCEnergyGrid grid = iterator.next().get();
                if (grid == null) {
                    iterator.remove();
                } else {
                    grid.doEnergyTick();
                }
            }
        }

        @Override
        public void onEndTick(PCSide side, World world) {
            //
        }

    }

    private static class Factory implements IGridFactory<PCEnergyGrid, IEnergyGridTile, PCEnergyNode<?>, PCEnergyEdge> {

        Factory() {
            //
        }

        @Override
        public PCEnergyGrid make(IEnergyGridTile tile) {
            return new PCEnergyGrid(tile);
        }

    }

    public static void setEnergyModulePresent() {
        PCSecurity.allowedCaller("PCEnergyGrid.setEnergyModulePresent", "powercraft.energy.PCegEnergy");
        isEnergyModulePresent = true;
    }

    PCEnergyGrid() {
        grids.add(new WeakReference<PCEnergyGrid>(this));
    }

    PCEnergyGrid(IEnergyGridTile tile) {
        super(tile);
        grids.add(new WeakReference<PCEnergyGrid>(this));
    }

    @Override
    protected void destroy() {
        grids.remove(this);
    }

    public void doEnergyTick() {
        if (isEnergyModulePresent) {
            PCEnergyInfo info = new PCEnergyInfo();
            List<PCEnergyNodeBuffer> buffer = new ArrayList<PCEnergyNodeBuffer>();
            for (PCEnergyNode<?> node : this.nodes) {
                node.onTickStart();
                node.addToInfo(info);
                if (node instanceof PCEnergyNodeBuffer) {
                    buffer.add((PCEnergyNodeBuffer) node);
                }
            }
            float energy = 0;
            for (PCEnergyNode<?> node : this.nodes) {
                energy += node.takeEnergy();
            }
            float p = energy / info.energyRequested;
            if (p > 1) p = 1;
            for (PCEnergyNode<?> node : this.nodes) {
                energy = node.useEnergy(energy, p);
            }
            if (energy > 0 && !buffer.isEmpty()) {
                PCEnergyNodeBuffer[] array = buffer.toArray(new PCEnergyNodeBuffer[buffer.size()]);
                Arrays.sort(array);
                List<PCEnergyNodeBuffer> bufferNodes = new ArrayList<PCEnergyNodeBuffer>();
                float lastLevel = 0;
                for (int i = 0; i < array.length; i++) {
                    float nextLevel = array[i].level;
                    if (!bufferNodes.isEmpty()) {
                        float fill = nextLevel - lastLevel;
                        if (fill > energy / bufferNodes.size()) fill = energy / bufferNodes.size();
                        Iterator<PCEnergyNodeBuffer> it = bufferNodes.iterator();
                        while (it.hasNext()) {
                            PCEnergyNodeBuffer b = it.next();
                            energy = b.addEnergy(energy, fill);
                            if (b.used == b.maxIn) {
                                it.remove();
                            }
                        }
                    }
                    lastLevel = nextLevel;
                    bufferNodes.add(array[i]);
                }
                while (!bufferNodes.isEmpty() && energy > 0.00001) {
                    float fill = energy / bufferNodes.size();
                    Iterator<PCEnergyNodeBuffer> it = bufferNodes.iterator();
                    while (it.hasNext()) {
                        PCEnergyNodeBuffer b = it.next();
                        energy = b.addEnergy(energy, fill);
                        if (b.used >= b.maxIn) {
                            it.remove();
                        }
                    }
                }
            }
            if (energy > 0) {
                p = energy / info.notProduceNeccecerly;
                if (p > 1) p = 1;
                for (PCEnergyNode<?> node : this.nodes) {
                    energy = node.notUsing(energy, p);
                }
            }
        } else {
            for (PCEnergyNode<?> node : this.nodes) {
                node.onTickStart();
            }
            for (PCEnergyNode<?> node : this.nodes) {
                if (node instanceof PCEnergyNodeConsumer) {
                    PCEnergyNodeConsumer consumer = (PCEnergyNodeConsumer) node;
                    consumer.useable = consumer.requested;
                }
            }
        }
        for (PCEnergyNode<?> node : this.nodes) {
            node.onTickEnd();
        }
    }

    @Override
    protected PCEnergyNode<?> createNode(IEnergyGridTile tile) {
        if (tile instanceof IEnergyGridConduit) {
            return new PCEnergyNodeConduit(this, (IEnergyGridConduit) tile);
        } else if (tile instanceof IEnergyGridBuffer) {
            return new PCEnergyNodeBuffer(this, (IEnergyGridBuffer) tile);
        } else if (tile instanceof IEnergyGridProvider) {
            return new PCEnergyNodeProvider(this, (IEnergyGridProvider) tile);
        } else if (tile instanceof IEnergyGridConsumer) {
            return new PCEnergyNodeConsumer(this, (IEnergyGridConsumer) tile);
        }
        throw new RuntimeException();
    }

    @Override
    protected PCEnergyEdge createEdge(PCEnergyNode<?> start, PCEnergyNode<?> end) {
        return new PCEnergyEdge(this, start, end);
    }

    @Override
    protected PCEnergyGrid createGrid() {
        return new PCEnergyGrid();
    }
}