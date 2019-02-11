package com.rumaruka.powercraft.api.grid;

import com.rumaruka.powercraft.api.PCDirection;

public interface IGridSided {

    <T extends IGridTile<?,T,?,?>>T getTile(PCDirection side,int flags,Class<T>tileClass);
}
