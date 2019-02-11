package com.rumaruka.powercraft.api.dimension;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

public class PCWorldProvider extends WorldProvider {

    protected final PCDimension dimension;

    public PCWorldProvider(){
        this.dimension=PCDimensions.getDimenstionForProvider(getClass());
    }

    @Override
    public int getDimension() {
        return dimension.id;
    }


    @Override
    public DimensionType getDimensionType() {
        return PCDimension.dimensionType;
    }


}
