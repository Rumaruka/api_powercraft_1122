package com.rumaruka.powercraft.api.entity;

import com.rumaruka.powercraft.api.PCModule;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.renderer.PCEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PCEntityType<E extends Entity & IEntityPC> {

    private final ModContainer module;

    @SuppressWarnings("unused")
    private boolean constructed;

    private int entityTypeID;

    public int trackingRange;

    public int updateFrequency;

    public boolean sendsVelocityUpdates;

    protected PCModel model;

    public PCEntityType(){
        PCEntities.addEntityType(this);
        this.module = PCUtils.getActiveMod();
    }

    public final PCModule getModule() {
        return (PCModule)this.module.getMod();
    }

    public abstract Class<E> getEntity();

    public String getRegisterName() {
        return getEntity().getSimpleName();
    }

    @SuppressWarnings("hiding")
    void construct() {
        PCModule module = getModule();
        this.entityTypeID = EntityRegistry.findGlobalUniqueEntityId();
        PCRegistry.registerEntity(getEntity(), module.getName()+":"+getRegisterName(), this.entityTypeID, module, this.trackingRange, this.updateFrequency, this.sendsVelocityUpdates, this);
        if(PCUtils.isClient())
            this.model = PCModel.loadModel(module, getTextureFolderName(), "ms3d");
        this.constructed = true;
    }

    @SuppressWarnings("static-method")
    public boolean isStaticEntity() {
        return false;
    }

    public String getTextureFolderName() {
        return getEntity().getSimpleName().replaceAll("PC.*_(Entity)?", "");
    }



    @SideOnly(Side.CLIENT)
    public String getEntityTextureName(PCEntityRenderer<E> renderer, E entity) {
        return entity.getEntityTextureName(renderer);
    }

    @SideOnly(Side.CLIENT)
    public void doRender(PCEntityRenderer<E> renderer, E entity, double x, double y, double z, float rotYaw, float timeStamp) {
        entity.doRender(renderer, x, y, z, rotYaw, timeStamp);
    }

    public PCModel getModel(){
        return this.model;
    }

}
