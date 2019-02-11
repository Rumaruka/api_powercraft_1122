package com.rumaruka.powercraft.api.redstone;

import com.rumaruka.powercraft.api.grid.PCEdge;

public class PCRedstoneEdge extends PCEdge<PCRedstoneGrid, IRedstoneGridTile,PCRedstoneNode,PCRedstoneEdge> {
    protected PCRedstoneEdge(PCRedstoneGrid grid, PCRedstoneNode start, PCRedstoneNode end) {
        super(grid, start, end);
    }

    public void onRedstoneChange(){
        for (IRedstoneGridTile tile:getTile()){
            tile.onRedstonePowerChange();
        }
    }
}
