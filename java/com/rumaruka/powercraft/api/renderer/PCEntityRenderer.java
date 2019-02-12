package com.rumaruka.powercraft.api.renderer;

import com.rumaruka.powercraft.api.entity.IEntityPC;
import com.rumaruka.powercraft.api.entity.PCEntityType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class PCEntityRenderer<E extends Entity & IEntityPC>extends Render {


    private PCEntityType<E> type;

    protected PCEntityRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
