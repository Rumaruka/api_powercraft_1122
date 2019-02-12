package com.rumaruka.powercraft.api.energy;

import com.rumaruka.powercraft.api.grid.PCNode;

import java.util.List;

abstract class PCEnergyNode<T extends IEnergyGridTile> extends PCNode<PCEnergyGrid, IEnergyGridTile, PCEnergyNode<?>, PCEnergyEdge> {

    PCEnergyNode(PCEnergyGrid grid, T tile) {
        super(grid, tile);
    }

    @SuppressWarnings("unchecked")
    public T getTiles(){
        return (T)this.tile;
    }

    public void onTickStart(){
        //
    }

    public void onTickEnd(){
        //
    }

    public abstract float getFlow();

    public abstract void addToInfo(PCEnergyInfo info);

    public abstract float takeEnergy();

    @SuppressWarnings({ "static-method", "unused" })
    public float useEnergy(float energy, float p) {
        return energy;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public float notUsing(float energy, float p) {
        return energy;
    }
}
