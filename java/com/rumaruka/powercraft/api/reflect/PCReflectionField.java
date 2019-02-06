package com.rumaruka.powercraft.api.reflect;

import java.lang.reflect.Field;

public class PCReflectionField<T, T1> {
    public final Class<C> clasz;
    public final int index;
    public final Class<T> type;

    private Field field;

    public PCReflectionField(Class<C> clasz, int index, Class<T> type){
        this.clasz = clasz;
        this.index = index;
        this.type = type;
    }

    public Field getField(){
        if(this.field==null){
            this.field = PCReflect.findNearestBestField(this.clasz, this.index, this.type);
        }
        return this.field;
    }

    public T getValue(Object object) {
        return PCReflect.getValue(this, object);
    }

    public void setValue(Object object, Object value) {
        PCReflect.setValue(this, object, value);
    }

    public void setValueAndFinals(Object object, Object value) {
        PCReflect.setValueAndFinals(this, object, value);
    }
}
