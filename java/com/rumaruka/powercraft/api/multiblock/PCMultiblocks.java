package com.rumaruka.powercraft.api.multiblock;

import com.rumaruka.powercraft.api.PCApi;
import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.network.packet.PCPacketMultiblockObjectSync;
import com.rumaruka.powercraft.api.network.packet.PCPacketSelectMultiblockTile;
import com.rumaruka.powercraft.api.reflect.PCSecurity;
import com.rumaruka.powercraft.core.PCCore_Core;

public class PCMultiblocks {

    private static boolean done;
    private static List<PCMultiblockItem> multiblockItems = new ArrayList<PCMultiblockItem>();
    private static List<PCMultiblockItem> immutableMultiblockItems = new PCImmutableList<PCMultiblockItem>(multiblockItems);
    private static HashMap<PCMultiblockItem, Class<? extends PCMultiblockObject>> itemMapper = new HashMap<PCMultiblockItem, Class<? extends PCMultiblockObject>>();
    private static HashMap<Class<? extends PCMultiblockObject>, PCMultiblockItem> itemMapperRev = new HashMap<Class<? extends PCMultiblockObject>, PCMultiblockItem>();

    public static void register(){
        PCSecurity.allowedCaller("PCMultiblocks.register()", PCApi.class);
        PCPacketHandler.registerPacket(PCPacketMultiblockObjectSync.class);
        PCPacketHandler.registerPacket(PCPacketSelectMultiblockTile.class);
        PCPacketHandler.registerPacket(PCPacketSelectMultiblockTile2.class);
    }

    public static PCBlockMultiblock getMultiblock(){
        if(PCCore_Core.MULTIBLOCK==null){
            PCSecurity.allowedCaller("PC_Multiblocks.getMultiblock()", PCco_Core.class);
            return new PCBlockMultiblock();
        }
        return PCCore_Core.MULTIBLOCK;
    }

    static void addMultiblock(PCMultiblockItem multiblockItem, Class<? extends PCMultiblockObject> multiblockObjectClass) {
        if(done){
            PCLogger.severe("A Multiblock want to register while startup is done");
        }else{
            PCLogger.info("Multiblock-ADD: %s", multiblockItem);
            multiblockItems.add(multiblockItem);
            itemMapper.put(multiblockItem, multiblockObjectClass);
            itemMapperRev.put(multiblockObjectClass, multiblockItem);
        }
    }

    public static List<PC_MultiblockItem> getBlocks(){
        return immutableMultiblockItems;
    }

    public static void construct(){
        PC_Security.allowedCaller("PC_Multiblocks.construct()", PC_Api.class);
        if(!done){
            done = true;
        }
    }

    private PC_Multiblocks(){
        PC_Utils.staticClassConstructor();
    }

    public static PC_MultiblockItem getItem(PC_MultiblockObject multiblockObject) {
        return itemMapperRev.get(multiblockObject.getClass());
    }

    @SideOnly(Side.CLIENT)
    static void loadMultiblockIcons(PC_IconRegistry iconRegistry) {
        for(PC_MultiblockItem multiblockItem:multiblockItems){
            multiblockItem.loadMultiblockIcons(PC_ClientRegistry.getIconRegistry(iconRegistry, multiblockItem));
        }
    }
}
