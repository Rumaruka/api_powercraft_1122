package com.rumaruka.powercraft.api;


import com.rumaruka.powercraft.api.block.PCBlocks;
import com.rumaruka.powercraft.api.building.PCCropHarvesting;
import com.rumaruka.powercraft.api.building.PCTreeHarvesting;
import com.rumaruka.powercraft.api.dimension.PCDimensions;
import com.rumaruka.powercraft.api.energy.PCEnergyGrid;
import com.rumaruka.powercraft.api.entity.PCEntities;
import com.rumaruka.powercraft.api.gres.PCGres;
import com.rumaruka.powercraft.api.item.PCItems;
import com.rumaruka.powercraft.api.multiblock.PCMultiblocks;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.script.miniscript.PCMiniscript;
import com.rumaruka.powercraft.core.PCCore_Core;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = PCApi.NAME, name = PCApi.NAME, version = PCApi.VERSION, dependencies=PCApi.DEPENDENCIES)
public class PCApi extends PCModule {

    public static final String NAME = POWERCRAFT+"-api";
    public static final String VERSION = PCBuild.BUILD_VERSION;
    public static final String DEPENDENCIES  = "required-before:"+ PCCore_Core.NAME+"@"+PCCore_Core.VERSION;


    static {
        PCBootstrap.prepare();
    }
    public static PCApi instance;

    static {
        try {
            instance = new PCApi();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @Mod.InstanceFactory
    public static PCApi factory(){
        return instance;
    }





    public PCApi() throws IllegalAccessException {
        if(getConfig().get("options","checkupdate",true).getBoolean(true)){

        }
        showPreversions=getConfig().get("options", "showPreversions", false).getBoolean(false);
    }



    @SuppressWarnings({ "static-method", "unused" })
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        PCModules.construct();

        PCPacketHandler.register();
        PCTickHandler.register();
        PCForgeHandler.register();
        PCMiniscript.register();
        PCGres.register();
        PCMultiblocks.register();
        PCEnergyGrid.register();

        PCChunkManager.register();

        PCCropHarvesting.register();
        PCTreeHarvesting.register();

        PCBlocks.construct();
        PCItems.construct();
        PCMultiblocks.construct();
        PCDimensions.construct();
        PCEntities.construct();

        PCBlocks.initRecipes();
        PCItems.initRecipes();
    }


    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //NOPE
    }

    @SuppressWarnings({ "unused", "static-method" })
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PCMiniscript.loadDefaultReplacements();
        PCModules.saveConfig();
    }
    @SuppressWarnings({ "static-method", "unused" })
    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppedEvent serverStoppedEvent){
        PCWorldSaveData.onServerStopping();
    }
    public static ItemStack creativTabItemStack;

    @Override
    public ItemStack getCreativeTabItemStack() {
        return creativTabItemStack;
    }

    public static boolean showPreversions;
}
