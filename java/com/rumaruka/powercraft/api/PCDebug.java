package com.rumaruka.powercraft.api;

import net.minecraft.client.renderer.entity.RenderManager;

public class PCDebug {


    public static final boolean DEBUG=false;
    private static  RenderManager manager;

    public static void println(String s){
        if(DEBUG){
            System.out.println(s);
        }
    }
    public static void setup(){
        if(DEBUG){
            manager.setDebugBoundingBox(true);
        }
    }

    public PCDebug(){
        PCUtils.staticClassConstructor();
    }
}
