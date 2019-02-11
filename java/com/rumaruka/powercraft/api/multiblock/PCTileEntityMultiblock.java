package com.rumaruka.powercraft.api.multiblock;

import com.rumaruka.powercraft.api.PCDirection;
import com.rumaruka.powercraft.api.PCField;
import com.rumaruka.powercraft.api.PCField.Flag;
import com.rumaruka.powercraft.api.block.PCTileEntity;
import com.rumaruka.powercraft.api.grid.IGridSided;
import com.rumaruka.powercraft.api.grid.IGridSidedSide;
import com.rumaruka.powercraft.api.grid.IGridTile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

public final class PCTileEntityMultiblock extends PCTileEntity implements IGridSided, IGridSidedSide {


    @Override
    public <T extends IGridTile<?, T, ?, ?>> T getTile(PCDirection side, int flags, Class<T> tileClass) {
        return null;
    }

    @Override
    public <T extends IGridTile<?, T, ?, ?>> T getTile(PCDirection dir, PCDirection dir2, int flags, Class<T> tileClass) {
        return null;
    }
}
