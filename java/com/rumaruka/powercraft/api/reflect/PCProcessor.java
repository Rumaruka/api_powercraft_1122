package com.rumaruka.powercraft.api.reflect;

import java.lang.reflect.Field;
import java.util.EnumMap;

public interface PCProcessor {

    public void process(Field field, Object value, EnumMap<Result, Object> results);

    public static enum Result{
        SET, STOP;
    }
}
