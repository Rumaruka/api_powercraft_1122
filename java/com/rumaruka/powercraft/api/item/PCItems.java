package com.rumaruka.powercraft.api.item;


import com.rumaruka.powercraft.api.PCApi;
import com.rumaruka.powercraft.api.PCImmutableList;
import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.reflect.PCSecurity;

import java.util.ArrayList;
import java.util.List;

public class PCItems  {

    private static  boolean doneConstruct;
    private static  boolean doneRecipes;
    private static List<IItemPC> items = new ArrayList<IItemPC>();

    private static List<IItemPC>immutableItems = new PCImmutableList<IItemPC>(items);



    static void addItem(IItemPC itemPC){
        if(doneConstruct){
            PCLogger.severe("A item want register while startup is done");
        }
        else {
            PCLogger.info("Add Item: %s",itemPC);
            items.add(itemPC);
        }
    }

    public static List<IItemPC> getItems() {
        return immutableItems;
    }
    public static void construct(){
        PCSecurity.allowedCaller("PCItems.construct()", PCApi.class);
        if(!doneConstruct){
            doneConstruct = true;
            for(IItemPC item:items){
                PCLogger.info("CONSTRUCT: %s", item);
                item.construct();
            }
        }
    }
    public static void initRecipes() {
        PCSecurity.allowedCaller("PCItems.initRecipes()", PCApi.class);
        if(!doneRecipes && doneConstruct){
            doneRecipes = true;
            for(IItemPC item:items){
                item.initRecipes();
            }
        }
    }

    private PCItems(){
        PCUtils.staticClassConstructor();
    }
}
