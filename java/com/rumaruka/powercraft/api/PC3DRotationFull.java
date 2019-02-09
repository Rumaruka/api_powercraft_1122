package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.PCField.Flag;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class PC3DRotationFull implements IPC3DRotation {

    @SuppressWarnings("unused")
    public PC3DRotationFull(PCDirection side, Entity entity){

    }
    @SuppressWarnings("unused")
    public PC3DRotationFull(NBTTagCompound comp, Flag flag){

    }
    @Override
    public PCDirection getSidePosition(PCDirection side) {
        return null;
    }

    @Override
    public PCDirection getSidePositionInv(PCDirection side) {
        return null;
    }

    @Override
    public int getSideRotation(PCDirection side) {
        return 0;
    }

    @Override
    public IPC3DRotation rotateAround(PCDirection axis) {
        return null;
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return new EnumFacing[0];
    }

    @Override
    public AxisAlignedBB rotateBox(AxisAlignedBB box) {
        return box;
    }

    @Override
    public void saveToNBT(NBTTagCompound tag, Flag flag) {

    }
}
