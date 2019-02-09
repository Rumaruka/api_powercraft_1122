package com.rumaruka.powercraft.api.xml;

public class PCXMLProperty {

    private String key;

    private String value;

    public PCXMLProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(boolean b){
        setValue(PCPhaser.toString(b));
    }

    public boolean getValueBoolean(){
        return PCPhaser.pharseBoolean(getValue());
    }

    public void setValue(boolean[] b){
        setValue(PCPhaser.toString(b));
    }

    public boolean[] getValueBooleanArray(){
        return PCPhaser.pharseBooleanArray(getValue());
    }

    public void setValue(byte b){
        setValue(PCPhaser.toString(b));
    }

    public byte getValueByte(){
        return PCPhaser.pharseByte(getValue());
    }

    public void setValue(byte[] b){
        setValue(PCPhaser.toString(b));
    }

    public byte[] getValueByteArray(){
        return PCPhaser.pharseByteArray(getValue());
    }

    public void setValue(short s){
        setValue(PCPhaser.toString(s));
    }

    public short getValueShort(){
        return PCPhaser.pharseShort(getValue());
    }

    public void setValue(short[] s){
        setValue(PCPhaser.toString(s));
    }

    public short[] getValueShortArray(){
        return PCPhaser.pharseShortArray(getValue());
    }

    public void setValue(int i){
        setValue(PCPhaser.toString(i));
    }

    public int getValueInt(){
        return PCPhaser.pharseInt(getValue());
    }

    public void setValue(int[] i){
        setValue(PCPhaser.toString(i));
    }

    public int[] getValueIntArray(){
        return PCPhaser.pharseIntArray(getValue());
    }

    public void setValue(long l){
        setValue(PCPhaser.toString(l));
    }

    public long getValueLong(){
        return PCPhaser.pharseLong(getValue());
    }

    public void setValue(long[] l){
        setValue(PCPhaser.toString(l));
    }

    public long[] getValueLongArray(){
        return PCPhaser.pharseLongArray(getValue());
    }

    public void setValue(float f){
        setValue(PCPhaser.toString(f));
    }

    public float getValueFloat(){
        return PCPhaser.pharseFloat(getValue());
    }

    public void setValue(float[] f){
        setValue(PCPhaser.toString(f));
    }

    public float[] getValueFloatArray(){
        return PCPhaser.pharseFloatArray(getValue());
    }

    public void setValue(double d){
        setValue(PCPhaser.toString(d));
    }

    public double getValueDouble(){
        return PCPhaser.pharseDouble(getValue());
    }

    public void setValue(double[] d){
        setValue(PCPhaser.toString(d));
    }

    public double[] getValueDoubleArray(){
        return PCPhaser.pharseDoubleArray(getValue());
    }
}
