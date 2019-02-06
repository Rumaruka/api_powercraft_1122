package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.reflect.PCSecurity;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class PCResourceReloadListener {

    private static List<IResourceReloadLisener> liseners = new ArrayList<IResourceReloadLisener>();



    @SideOnly(Side.CLIENT)
    static void register(){
        PCSecurity.allowedCaller("PCResourceReloadListener.register()",PCClientUtils.class);
    }


    private PCResourceReloadListener(){
        PCUtils.staticClassConstructor();
    }
    static void onResourceReload(){
        for (IResourceReloadLisener lisener:liseners){
            lisener.onResourceReload();
        }

    }
    public static void registerResourceReloadListener(IResourceReloadLisener listener){
        if(!liseners.contains(listener))
            liseners.add(listener);
    }
    @SideOnly(Side.CLIENT)
    private static final class PCResourceListener implements IResourceManagerReloadListener {

        public static final PCResourceListener INSTANCE = new PCResourceListener();

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            onResourceReload();
        }

    }
    public static interface IResourceReloadLisener{
        public void onResourceReload();
    }
}
