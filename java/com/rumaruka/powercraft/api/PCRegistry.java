package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.PCResourceReloadListener.IResourceReloadLisener;
import com.rumaruka.powercraft.api.PCTickHandler.IBaseTickHandler;
import com.rumaruka.powercraft.api.block.PCTileEntity;
import com.rumaruka.powercraft.api.entity.IEntityPC;
import com.rumaruka.powercraft.api.entity.PCEntityType;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.management.InstanceAlreadyExistsException;

public class PCRegistry {

    private static PCRegistry INSTANCE;

    PCRegistry() throws InstanceAlreadyExistsException {
        if(INSTANCE!=null){
            throw new InstanceAlreadyExistsException();
        }
        INSTANCE = this;
    }

    public static void registerTileEntity(Class<? extends PCTileEntity> tileEntityClass){
        INSTANCE.iRegisterTileEntity(tileEntityClass);
    }

    @SuppressWarnings("static-method")
    void iRegisterTileEntity(Class<? extends PCTileEntity> tileEntityClass){
        GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName());
    }

    public static void registerTickHandler(IBaseTickHandler tickHandler){
        PCTickHandler.registerTickHandler(tickHandler);
    }

    public static void registerResourceReloadListener(IResourceReloadLisener listener){
        PCResourceReloadListener.registerResourceReloadListener(listener);
    }

    public static void registerPacket(Class<? extends PCPacket> packet){
        PCPacketHandler.registerPacket(packet);
    }

    public static <E extends Entity & IEntityPC>void registerEntity(ResourceLocation registryNamePC,Class<? extends Entity> entity, String name, int entityTypeID, PCModule module, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, PCEntityType<E> type) {
        INSTANCE.iRegisterEntity(registryNamePC,entity, name, entityTypeID, module, trackingRange, updateFrequency, sendsVelocityUpdates, type);
    }

    @SuppressWarnings({ "static-method", "unused" })
    <E extends Entity & IEntityPC>void iRegisterEntity(ResourceLocation registryNamePC, Class<? extends Entity> entity, String name, int entityTypeID, PCModule module, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, PCEntityType<E> type){
        EntityRegistry.registerModEntity(registryNamePC,entity, name, entityTypeID, module, trackingRange, updateFrequency, sendsVelocityUpdates);

    }

    static void playSound(double x, double y, double z, String sound, float soundVolume, float pitch) {
        INSTANCE.iPlaySound(x, y, z, sound, soundVolume, pitch);
    }

    @SuppressWarnings("unused")
    void iPlaySound(double x, double y, double z, String sound, float soundVolume, float pitch) {
        //
    }
}
