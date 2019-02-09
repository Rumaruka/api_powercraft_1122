package com.rumaruka.powercraft.api;

public class PCVec3I {

    public int x;
    public int y;
    public int z;


    public PCVec3I() {

    }


    public PCVec3I(int x, int y, int z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }


    @Override
    public boolean equals(Object obj) {

        if (obj instanceof PCVec3I) {
            PCVec3I vec = (PCVec3I) obj;
            return vec.x == this.x && vec.y == this.y && vec.z == this.z;
        }
        return false;
    }


    @Override
    public int hashCode() {

        return this.x ^ 34 + this.y ^ 12 + this.z;
    }


    @Override
    public String toString() {

        return "Vec3I[" + this.x + ", " + this.y + ", " + this.z + "]";
    }


    @SuppressWarnings("hiding")
    public PCVec3I offset(int x, int y, int z) {
        return new PCVec3I(this.x+x, this.y+y, this.z+z);
    }

    public PCVec3I offset(PCVec3I other) {
        return new PCVec3I(this.x+other.x, this.y+other.y, this.z+other.z);
    }

    public PCVec3I offset(PCDirection other) {
        return new PCVec3I(this.x+other.offsetX, this.y+other.offsetY, this.z+other.offsetZ);
    }


    public PCVec3I rotate(PCDirection pcDir, int times){
        int tmpX=0, tmpY=0, tmpZ=0;
        PCDirection tmp;

        PCDirection[] sides = {PCDirection.DOWN, PCDirection.NORTH, PCDirection.EAST};
        for(PCDirection dir:sides){
            tmp=dir.rotate(pcDir, times);
            if(dir==tmp||dir==tmp.getOpposite()){
                tmpX+=tmp.offsetX*(dir.offsetX*this.x);
                tmpY+=tmp.offsetY*(dir.offsetY*this.y);
                tmpZ+=tmp.offsetZ*(dir.offsetZ*this.z);
            }else{
                tmpX+=tmp.offsetX*(dir.offsetX*this.x+dir.offsetY*this.y+dir.offsetZ*this.z);
                tmpY+=tmp.offsetY*(dir.offsetX*this.x+dir.offsetY*this.y+dir.offsetZ*this.z);
                tmpZ+=tmp.offsetZ*(dir.offsetX*this.x+dir.offsetY*this.y+dir.offsetZ*this.z);
            }
        }
        return new PCVec3I(tmpX, tmpY, tmpZ);
    }

    public PCVec3I mirror(PCDirection pcDir){
        return new PCVec3I(pcDir.offsetX==0?this.x:-this.x, pcDir.offsetY==0?this.y:-this.y, pcDir.offsetZ==0?this.z:-this.z);
    }


    public PCVec3I mul(int v) {
        return new PCVec3I(this.x*v,this.y*v,this.z*v);
    }
}
