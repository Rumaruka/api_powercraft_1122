package com.rumaruka.powercraft.api.block;

import com.rumaruka.powercraft.api.reflect.PCReflect;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class PCBlockTileEntity extends AbstractBlockBase implements ITileEntityProvider {

    public PCBlockTileEntity(Material materialIn) {
        super(materialIn);
    }
    public PCBlockTileEntity(EnumBlockType type){
        super(type);
    }

    public abstract Class<? extends PCTileEntity> getTileEntityClass();

    @Nullable
    @Override
    public PCTileEntity createNewTileEntity(World worldIn, int meta) {
        return PCReflect.newInstance(getTileEntityClass());
    }

    @Override
    void construct() {
        super.construct();
    }
}
