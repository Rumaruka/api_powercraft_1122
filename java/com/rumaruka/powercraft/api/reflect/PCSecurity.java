package com.rumaruka.powercraft.api.reflect;

import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCUtils;

public class PCSecurity {

    public static boolean allowedCaller(String funktion, Class<?>... allowedCallers){
        Class<?> caller = PCReflect.getCallerClass(1);
        for(int i=0; i<allowedCallers.length; i++){
            if(allowedCallers[i]==caller){
                return true;
            }
        }
        PCLogger.severe("Security Exception %s try to call a non allowed function: %s", caller, funktion);
        return false;
    }

    public static boolean allowedCallerNoException(Class<?>... allowedCallers){
        Class<?> caller = PCReflect.getCallerClass(1);
        for(int i=0; i<allowedCallers.length; i++){
            if(allowedCallers[i]==caller){
                return true;
            }
        }
        return false;
    }

    public static boolean allowedCaller(String funktion, String... allowedCallers){
        Class<?> caller = PCReflect.getCallerClass(1);
        for(int i=0; i<allowedCallers.length; i++){
            if(allowedCallers[i].equals(caller.getName())){
                return true;
            }
        }
        PCLogger.severe("Security Exception %s try to call a non allowed function: %s", caller, funktion);
        return false;
    }

    public static boolean allowedCallerNoException(String... allowedCallers){
        Class<?> caller = PCReflect.getCallerClass(1);
        for(int i=0; i<allowedCallers.length; i++){
            if(allowedCallers[i].equals(caller.getName())){
                return true;
            }
        }
        return false;
    }

    private PCSecurity() {
        PCUtils.staticClassConstructor();
    }
}
