package com.rumaruka.powercraft.api;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class PCBootstrap {

    private static boolean loaded;


    @SuppressWarnings("unused")
    static void prepare(){
        try {
            if(FMLCommonHandler.instance().getSide()== Side.CLIENT){
                Class.forName("com.rumaruka.powercraft.api.PCClientUtils");
                Class.forName("com.rumaruka.powercraft.api.PCClientRegistry");
            }
            else {
                new PCUtils();
                new PCRegistry();
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed load PowerCraft");
        }
        PCLogger.init(PCUtils.getPowerCraftFile(null,PCModule.POWERCRAFT+".log"));
        PCDebug.setup();
        loaded=true;
    }

    public static boolean isLoaded() {
        return loaded;
    }
    public PCBootstrap(){
        PCUtils.staticClassConstructor();
    }
}
