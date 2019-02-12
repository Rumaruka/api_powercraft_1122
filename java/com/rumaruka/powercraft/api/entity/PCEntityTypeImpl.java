package com.rumaruka.powercraft.api.entity;

import net.minecraft.entity.Entity;

public class PCEntityTypeImpl<E extends Entity & IEntityPC> extends PCEntityType {


    private final Class<E>entity;

    public PCEntityTypeImpl(Class<E>entity){
        this.entity=entity;

    }
    @Override
    public Class<E> getEntity() {
        return null;
    }
}
