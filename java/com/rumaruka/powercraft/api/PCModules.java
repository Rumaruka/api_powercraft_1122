package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.reflect.PCSecurity;

import java.util.ArrayList;
import java.util.List;

public class PCModules {


    private static boolean done;
    private static List<PCModule>modules=new ArrayList<PCModule>();
    private static List<PCModule>immutableModules=new ArrayList<PCModule>();

    static void addModules(PCModule module){
        if (done){
            PCLogger.severe("A module want to register while startup is done!");
        }
        else {
            PCLogger.info("Module-ADD: %s",module);
            modules.add(module);
        }
    }

    public static List<PCModule> getModules() {
        return immutableModules;
    }
    public static void construct(){
        PCSecurity.allowedCaller("PCModules.construct()",PCApi.class);
        if(!done){
            done=true;
        }
    }
    public static void saveConfig(){
        for (PCModule module:modules){
            module.saveConfig();
        }
    }

}
