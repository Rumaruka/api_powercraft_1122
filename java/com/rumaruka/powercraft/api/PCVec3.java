package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.PCField.Flag;
import net.minecraft.nbt.NBTTagCompound;

public class PCVec3 implements IPCNBT {
    public double x;
    public double y;
    public double z;


    public PCVec3() {

    }


    public PCVec3(double x, double y, double z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PCVec3(PCVec3 vec) {

        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    @SuppressWarnings("unused")
    public PCVec3(NBTTagCompound nbtTagCompound, Flag flag) {
        this.x = nbtTagCompound.getDouble("x");
        this.y = nbtTagCompound.getDouble("y");
        this.z = nbtTagCompound.getDouble("z");
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof PCVec3) {
            PCVec3 vec = (PCVec3) obj;
            return vec.x == this.x && vec.y == this.y && vec.z == this.z;
        }
        return false;
    }

    @Override
    public int hashCode() {

        return ((int)this.x) ^ 34 + ((int)this.y) ^ 12 + ((int)this.z);
    }


    @Override
    public String toString() {

        return "Vec3[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    public void setTo(PCVec3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public PCVec3 neg(){
        return mul(-1);
    }

    public PCVec3 mul(double v) {
        return new PCVec3(this.x*v, this.y*v, this.z*v);
    }

    public PCVec3 add(PCVec3 vec) {
        return new PCVec3(this.x+vec.x, this.y+vec.y, this.z+vec.z);
    }

    public PCVec3 add(double value) {
        return new PCVec3(this.x+value, this.y+value, this.z+value);
    }

    public PCVec3 sub(PCVec3 vec) {
        return new PCVec3(this.x-vec.x, this.y-vec.y, this.z-vec.z);
    }

    public double distanceTo(PCVec3 pos) {
        return sub(pos).length();
    }

    public double length() {
        return PCMathHelper.sqrt_double(this.x*this.x+this.y*this.y+this.z*this.z);
    }


    public PCVec3 normalize() {
        double l = length();
        return new PCVec3(this.x/l, this.y/l, this.z/l);
    }


    public double dot(PCVec3 vec) {
        return this.x*vec.x+this.y*vec.y+this.z*vec.z;
    }


    @Override
    public void saveToNBT(NBTTagCompound tag, Flag flag) {
        tag.setDouble("x", this.x);
        tag.setDouble("y", this.y);
        tag.setDouble("z", this.z);
    }


    public PCVec3 cross(PCVec3 other) {
        return new PCVec3(this.y*other.z-this.z*other.y, this.z*other.x-this.x*other.z, this.x*other.y-this.y*other.x);
    }

}
