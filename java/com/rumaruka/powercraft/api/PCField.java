package com.rumaruka.powercraft.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PCField {
    public String name() default "";

    public Flag[] flags() default {Flag.SAVE};

    public boolean notNull() default false;

    public static enum Flag{
        SAVE, SYNC;

        public boolean isIn(PCField info) {
            Flag[] flags = info.flags();
            for(Flag flag:flags){
                if(flag==this)
                    return true;
            }
            return false;
        }
    }
}
