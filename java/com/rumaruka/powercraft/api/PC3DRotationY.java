package com.rumaruka.powercraft.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import javax.swing.plaf.TableHeaderUI;

public class PC3DRotationY implements IPC3DRotation {

    private int rotation;

    public PC3DRotationY(int rotation){
        this.rotation=(rotation%4+4)%4;
    }
    public PC3DRotationY(Entity entity){
       // this.rotation=PCUtils.getR
    }
    @SuppressWarnings("unused")
    public PC3DRotationY(NBTTagCompound nbtTagCompound, PCField.Flag flag){
        this.rotation = nbtTagCompound.getByte("rotation");
    }

    @Override
    public PCDirection getSidePosition(PCDirection side) {
        return side.rotate(PCDirection.UP, this.rotation);
    }

    @Override
    public PCDirection getSidePositionInv(PCDirection side) {
        return side.rotate(PCDirection.DOWN, this.rotation);
    }

    @Override
    public int getSideRotation(PCDirection side) {
        if(side==PCDirection.UP){
            return this.rotation;
        }else if(side==PCDirection.DOWN){
            return (4-this.rotation)%4;
        }
        return 0;
    }

    @Override
    public IPC3DRotation rotateAround(PCDirection axis) {
        if(axis==PCDirection.UP){
            return new PC3DRotationY(this.rotation+1%4);
        }else if(axis==PCDirection.DOWN){
            return new PC3DRotationY(this.rotation+3%4);
        }
        return null;
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};

    }

    @Override
    public AxisAlignedBB rotateBox(AxisAlignedBB box) {
        for(int i=0; i<this.rotation; i++)
            PCUtils.rotateAABB(box, PCDirection.UP);
        return box;
    }

    @Override
    public void saveToNBT(NBTTagCompound tag, PCField.Flag flag) {
        tag.setByte("rotation", (byte) this.rotation);

    }

    @Override
    public String toString() {
        return "PC3DRotationY [rotation=" + this.rotation + "]";

    }
}
