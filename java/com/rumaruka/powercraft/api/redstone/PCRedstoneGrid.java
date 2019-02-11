package com.rumaruka.powercraft.api.redstone;

import com.rumaruka.powercraft.api.grid.IGridFactory;
import com.rumaruka.powercraft.api.grid.PCGrid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.init.Blocks;

import java.util.List;

public class PCRedstoneGrid extends PCGrid<PCRedstoneGrid,IRedstoneGridTile,PCRedstoneNode,PCRedstoneEdge> {


    public static final IGridFactory<PCRedstoneGrid,IRedstoneGridTile,PCRedstoneNode,PCRedstoneEdge> factory = new Factory();

    private static class Factory implements IGridFactory<PCRedstoneGrid,IRedstoneGridTile,PCRedstoneNode,PCRedstoneEdge>{

        Factory(){

        }
        @Override
        public PCRedstoneGrid make(IRedstoneGridTile tile) {
            return new PCRedstoneGrid(tile);
        }
    }
    protected boolean firstTick = true;

    private int power = -1;
    PCRedstoneGrid(IRedstoneGridTile tile){
        super(tile);
    }
    PCRedstoneGrid(){
        super();
    }

    @Override
    public void update() {
        int newPower = 0;
        for (PCRedstoneNode node:this.nodes){
            int p = node.getPower();
            if(p>newPower){
                newPower=p;
            }
        }
        if (this.power != newPower) {
            this.power = newPower;
            for (PCRedstoneNode node : this.nodes) {
                node.onRedstoneChange();
            }
            for (PCRedstoneEdge edge : this.edges) {
                edge.onRedstoneChange();
            }
        }
    }


    @Override
    protected void removeTile(IRedstoneGridTile tile) {
        super.removeTile(tile);
        update();
    }

    @SuppressWarnings("hiding")
    @Override
    protected void addAll(List<PCRedstoneNode> nodes, List<PCRedstoneEdge> edges) {
        super.addAll(nodes, edges);
        int oldPower = this.power;
        update();
        if(this.power==oldPower){
            for (PCRedstoneNode node : nodes) {
                node.onRedstoneChange();
            }
            for (PCRedstoneEdge edge : edges) {
                edge.onRedstoneChange();
            }
        }
    }

    @Override
    protected void splitGridsIfAble() {
        super.splitGridsIfAble();
        update();
    }

    @Override
    protected PCRedstoneNode createNode(IRedstoneGridTile tile) {
        return new PCRedstoneNode(this,tile);
    }

    @Override
    protected PCRedstoneEdge createEdge(PCRedstoneNode start, PCRedstoneNode end) {
        return new PCRedstoneEdge(this,start,end);
    }

    @Override
    protected PCRedstoneGrid createGrid() {
        return new PCRedstoneGrid();
    }


    public int getRedstonePowerValue(){

        if(Blocks.REDSTONE_WIRE.getDefaultState().canProvidePower()) return this.power;

        return this.power==0?0:this.power - 1;
    }
}
