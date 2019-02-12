package com.rumaruka.powercraft.api.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public abstract class PCBlock extends AbstractBlockBase{
    public PCBlock(Material materialIn) {
        super(materialIn);
    }
    public PCBlock(EnumBlockType type) {
        super(type.material);
    }

    @Override
    public boolean canRotate() {
        return false;
    }

    @Override
    public boolean canRotate(IBlockAccess world, int x, int y, int z) {
        return canRotate();
    }

}
