package com.rumaruka.powercraft.api.grid;

import com.rumaruka.powercraft.api.PCDirection;

public interface IGridSidedSide {

     <T extends IGridTile<?, T, ?, ?>> T getTile(PCDirection dir, PCDirection dir2, int flags, Class<T> tileClass);
}
