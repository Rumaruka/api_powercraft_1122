package com.rumaruka.powercraft.api.redstone;

import com.rumaruka.powercraft.api.grid.IGridTile;

public interface IRedstoneGridTile extends IGridTile<PCRedstoneGrid, IRedstoneGridTile, PCRedstoneNode, PCRedstoneEdge> {

    boolean isID();

    void onRedstonePowerChange();

    int getPower();
}
