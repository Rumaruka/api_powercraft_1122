package com.rumaruka.powercraft.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public interface IPC3DRotation extends IPCNBT {

     PCDirection getSidePosition(PCDirection side);

     PCDirection getSidePositionInv(PCDirection side);

     int getSideRotation(PCDirection side);

    IPC3DRotation rotateAround(PCDirection axis);

     EnumFacing[] getValidRotations();

     AxisAlignedBB rotateBox(AxisAlignedBB box);

}
