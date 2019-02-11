package com.rumaruka.powercraft.api.redstone;

import com.rumaruka.powercraft.api.grid.PCNode;

public class PCRedstoneNode extends PCNode<PCRedstoneGrid, IRedstoneGridTile, PCRedstoneNode, PCRedstoneEdge> {
    protected PCRedstoneNode(PCRedstoneGrid grid, IRedstoneGridTile tile) {
        super(grid, tile);
    }

    @Override
    protected boolean canBecomeEdge() {
        return !this.tile.isID();
    }
    public int getPower(){
        if(this.tile.isID()){
            return this.tile.getPower();
        }

        return 0;
    }
    public void onRedstoneChange(){
        this.tile.onRedstonePowerChange();
    }
}
