package com.rumaruka.powercraft.api.block;

import com.rumaruka.powercraft.api.PCApi;
import com.rumaruka.powercraft.api.PCImmutableList;
import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.reflect.PCSecurity;

import java.util.ArrayList;
import java.util.List;

public final class PCBlocks {
    private static boolean doneConstruct;
    private static boolean doneRecipes;
    private static List<AbstractBlockBase> blocks = new ArrayList<AbstractBlockBase>();
    private static List<AbstractBlockBase> immutableBlocks = new PCImmutableList<AbstractBlockBase>(blocks);



    static void addBlock(AbstractBlockBase block){
        if(doneConstruct){

            PCLogger.severe("A block want to register while startup is done");
        }else{
            PCLogger.info("Block-ADD: %s", block);
            blocks.add(block);
        }
    }
    public static List<AbstractBlockBase> getBlocks(){
        return immutableBlocks;
    }

    public static void construct(){
        PCSecurity.allowedCaller("PC_Blocks.construct()", PCApi.class);
        if(!doneConstruct){
            doneConstruct = true;
            for(AbstractBlockBase block:blocks){
                PCLogger.info("CONSTRUCT: %s", block);
                block.construct();
            }
        }
    }

    public static void initRecipes() {
        PCSecurity.allowedCaller("PCBlocks.initRecipes()", PCApi.class);
        if(!doneRecipes && doneConstruct){
            doneRecipes = true;
            for(AbstractBlockBase block:blocks){
                block.initRecipes();
            }
        }
    }

    private PCBlocks(){
        PCUtils.staticClassConstructor();
    }
}
