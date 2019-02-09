package com.rumaruka.powercraft.api;

import net.minecraft.nbt.NBTTagCompound;

public class PCVec4I implements IPCNBT {

    public int x;
    public int y;
    public int z;
    public int w;

    public PCVec4I() {

    }

    public PCVec4I(int x, int y, int z, int w) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public PCVec4I(PCVec4I vec) {

        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;
    }

    @SuppressWarnings("unused")
    public PCVec4I(NBTTagCompound tag, PCField.Flag flag) {
        this.x = tag.getInteger("x");
        this.y = tag.getInteger("y");
        this.z = tag.getInteger("z");
        this.w = tag.getInteger("w");
    }

    @Override
    public void saveToNBT(NBTTagCompound tag, PCField.Flag flag) {
        tag.setInteger("x", this.x);
        tag.setInteger("y", this.y);
        tag.setInteger("z", this.z);
        tag.setInteger("w", this.w);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof PCVec4I) {
            PCVec4I vec = (PCVec4I) obj;
            return vec.x == this.x && vec.y == this.y && vec.z == this.z && vec.w == this.w;
        }
        return false;
    }

    @Override
    public int hashCode() {

        return (this.x) ^ 78 + (this.x) ^ 34 + (this.y) ^ 12 + (this.z);
    }

    @Override
    public String toString() {

        return "Vec4I[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }

    public void setTo(PCVec4I vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;
    }

    public void add(PCVec4I vec) throws NullPointerException {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        this.w += vec.w;
    }

    public static PCVec4I sum(PCVec4I... vec) {
        PCVec4I result = new PCVec4I(0, 0, 0, 0);
        for (PCVec4I cvec : vec) {
            try {
                result.add(cvec);
            } catch (NullPointerException e) {//
            }
        }
        return result;
    }

    public PCVec4I divide(double divident) {
        return new PCVec4I(this.x / (int)divident, this.y / (int)divident, this.z / (int)divident, this.w / (int)divident);
    }

    public boolean isZero() {
        return this.x == 0 && this.y == 0 && this.z == 0 && this.w == 0;
    }
}
