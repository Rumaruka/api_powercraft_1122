package com.rumaruka.powercraft.api.dimension;

import com.rumaruka.powercraft.api.PCApi;
import com.rumaruka.powercraft.api.PCImmutableList;
import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.reflect.PCSecurity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCDimensions {


    private static boolean done;
    private static List<PCDimension> dimensions = new ArrayList<PCDimension>();
    private static List<PCDimension> immutableDimensions = new PCImmutableList<PCDimension>(dimensions);
    private static HashMap<Class<? extends PCWorldProvider>, PCDimension> providers = new HashMap<Class<? extends PCWorldProvider>, PCDimension>();

    static void addDimensions(PCDimension dimension) {
        if(done){
            PCLogger.severe("A dimension want to register while startup is done");
        }else{
            PCLogger.info("Dimension-ADD: %s", dimension);
            dimensions.add(dimension);
            providers.put(dimension.getWorldProvider(), dimension);
        }
    }

    public static List<PCDimension> getDimensions(){
        return immutableDimensions;
    }

    public static void construct(){
        PCSecurity.allowedCaller("PCDimensions.construct()", PCApi.class);
        if(!done){
            done = true;
            for(PCDimension dimension:dimensions){
                PCLogger.info("CONSTRUCT: %s", dimension);
                dimension.construct();
            }
        }
    }

    public static PCDimension getDimenstionForProvider(Class<? extends PCWorldProvider> provider){
        return providers.get(provider);
    }

    private PCDimensions(){
        PCUtils.staticClassConstructor();
    }
}
