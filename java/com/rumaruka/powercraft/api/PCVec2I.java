package com.rumaruka.powercraft.api;

public class PCVec2I {

    public int x;
    public int y;


    public PCVec2I() {

    }


    public PCVec2I(int x, int y) {

        this.x = x;
        this.y = y;
    }


    public PCVec2I(PCVec2I vec) {

        this(vec.x, vec.y);
    }


    public PCVec2I(String attribute) {

        String[] attributes = attribute.split(",");
        if (attributes.length != 2) throw new NumberFormatException();
        this.x = Integer.parseInt(attributes[0].trim());
        this.y = Integer.parseInt(attributes[1].trim());
    }


    public PCVec2I(PCVec2 vec) {
        this.x = (int) (vec.x+0.5);
        this.y = (int) (vec.y+0.5);
    }


    public void setTo(PCVec2I vec) {

        this.x = vec.x;
        this.y = vec.y;
    }


    @Override
    public boolean equals(Object obj) {

        if (obj instanceof PCVec2I) {
            PCVec2I vec = (PCVec2I) obj;
            return vec.x == this.x && vec.y == this.y;
        }
        return false;
    }


    @Override
    public int hashCode() {

        return this.x ^ 34 + this.y;
    }


    @Override
    public String toString() {

        return "Vec2I[" + this.x + ", " + this.y + "]";
    }


    public PCVec2I add(int n) {

        return new PCVec2I(this.x + n, this.y + n);
    }


    @SuppressWarnings("hiding")
    public PCVec2I add(int x, int y) {

        return new PCVec2I(this.x + x, this.y + y);
    }


    public PCVec2I add(PCVec2I vec) {

        return new PCVec2I(this.x + vec.x, this.y + vec.y);
    }


    public PCVec2I sub(int n) {

        return new PCVec2I(this.x - n, this.y - n);
    }


    @SuppressWarnings("hiding")
    public PCVec2I sub(int x, int y) {

        return new PCVec2I(this.x - x, this.y - y);
    }


    public PCVec2I sub(PCVec2I vec) {

        return new PCVec2I(this.x - vec.x, this.y - vec.y);
    }


    public PCVec2I max(int n) {

        return new PCVec2I(this.x > n ? this.x : n, this.y > n ? this.y : n);
    }


    @SuppressWarnings("hiding")
    public PCVec2I max(int x, int y) {

        return new PCVec2I(this.x > x ? this.x : x, this.y > y ? this.y : y);
    }


    public PCVec2I max(PCVec2I vec) {

        return new PCVec2I(this.x > vec.x ? this.x : vec.x, this.y > vec.y ? this.y : vec.y);
    }

    public PCVec2I min(int n) {

        return new PCVec2I(this.x < n ? this.x : n, this.y < n ? this.y : n);
    }


    @SuppressWarnings("hiding")
    public PCVec2I min(int x, int y) {

        return new PCVec2I(this.x < x ? this.x : x, this.y < y ? this.y : y);
    }


    public PCVec2I min(PCVec2I vec) {

        return new PCVec2I(this.x < vec.x ? this.x : vec.x, this.y < vec.y ? this.y : vec.y);
    }


    public PCVec2I mul(float v) {
        return new PCVec2I((int)(this.x * v), (int)(this.y * v));
    }

    public PCVec2I div(float v) {
        return new PCVec2I((int)(this.x / v), (int)(this.y / v));
    }


}
