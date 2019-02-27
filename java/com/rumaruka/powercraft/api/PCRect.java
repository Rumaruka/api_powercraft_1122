package com.rumaruka.powercraft.api;

public class PCRect {

    public double x;
    public double y;
    public double width;
    public double height;


    public PCRect() {

    }


    public PCRect(double x, double y, double width, double height) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public PCRect(PCRect rect) {

        this(rect.x, rect.y, rect.width, rect.height);
    }


    public PCRect(PCRectI rect) {

        this(rect.x, rect.y, rect.width, rect.height);
    }


    public PCRect(String attribute) {

        String[] attributes = attribute.split(",");
        if (attributes.length != 4) throw new NumberFormatException();
        this.x = Double.parseDouble(attributes[0].trim());
        this.y = Double.parseDouble(attributes[1].trim());
        this.width = Double.parseDouble(attributes[2].trim());
        this.height = Double.parseDouble(attributes[3].trim());
    }


    public void setTo(PCRect rect) {

        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }


    public boolean setLocation(PCVec2I location) {

        if (this.x != location.x || this.y != location.y) {
            this.x = location.x;
            this.y = location.y;
            return true;
        }
        return false;
    }


    public PCVec2 getLocation() {

        return new PCVec2(this.x, this.y);
    }

    public PCVec2 getLocationF() {

        return new PCVec2(this.x, this.y);
    }


    public boolean setSize(PCVec2I size) {

        if (this.width != size.x || this.height != size.y) {
            this.width = size.x;
            this.height = size.y;
            return true;
        }
        return false;
    }


    public PCVec2 getSize() {

        return new PCVec2(this.width, this.height);
    }


    public PCRect averageQuantity(PCRect rect) {

        PCRect fin = new PCRect();
        double v1, v2;

        if (this.x > rect.x) {
            fin.x = this.x;
        } else {
            fin.x = rect.x;
        }

        if (this.y > rect.y) {
            fin.y = this.y;
        } else {
            fin.y = rect.y;
        }

        v1 = this.x + this.width;
        v2 = rect.x + rect.width;

        if (v1 > v2) {
            fin.width = v2 - fin.x;
        } else {
            fin.width = v1 - fin.x;
        }

        v1 = this.y + this.height;
        v2 = rect.y + rect.height;

        if (v1 > v2) {
            fin.height = v2 - fin.y;
        } else {
            fin.height = v1 - fin.y;
        }

        return fin;
    }


    public boolean contains(PCVec2I vec) {

        return this.x <= vec.x && this.x + this.width > vec.x && this.y <= vec.y && this.y + this.height > vec.y;
    }


    @Override
    public String toString() {

        return "PCRectI [x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.height);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.width);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PCRect other = (PCRect) obj;
        if (Double.doubleToLongBits(this.height) != Double.doubleToLongBits(other.height)) return false;
        if (Double.doubleToLongBits(this.width) != Double.doubleToLongBits(other.width)) return false;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) return false;
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) return false;
        return true;
    }
}
