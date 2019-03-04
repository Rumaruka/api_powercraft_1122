package com.rumaruka.powercraft.api;

public class PCVec2 {

    public double x;
    public double y;

    public PCVec2(){

    }
    public PCVec2(double x,double y){
        this.x=x;
        this.y=y;
    }

    public PCVec2(PCVec2 vec){
        this.x=vec.x;
        this.y=vec.y;

    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PCVec2){
            PCVec2 vec = (PCVec2) obj;
            return vec.x==this.x&&vec.y==this.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((int)this.x) ^ 12 + (int)this.y;
    }

    @Override
    public String toString() {
        return "Vec2 ["+this.x+","+this.y+"]";
    }

    public void  setTo(PCVec2 vec){
        this.x=vec.x;
        this.y=vec.y;
    }
    public PCVec2 mul(double v) {
        return new PCVec2(this.x * v, this.y * v);
    }

    public PCVec2 add(PCVec2 vec){
        return new PCVec2(this.x+vec.x,this.y+vec.y);
    }
    public PCVec2 sub(PCVec2 vec) {
        return new PCVec2(this.x - vec.x, this.y - vec.y);
    }


    public PCVec2 div(double v) {
        return new PCVec2(this.x / v, this.y / v);
    }


    public PCVec2 add(double v) {
        return new PCVec2(this.x + v, this.y + v);
    }


}
