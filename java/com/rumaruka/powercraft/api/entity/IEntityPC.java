package com.rumaruka.powercraft.api.entity;

import com.rumaruka.powercraft.api.gres.PCGresBaseWithInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IEntityPC {

    @SideOnly(Side.CLIENT)
    public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound);

    public void onClientMessageCheck(EntityPlayer player, NBTTagCompound nbtTagCompound, long session);

    public boolean guiOpenPasswordReply(EntityPlayer player, String password);

    public void setSession(long session);

    public long getNewSession(EntityPlayer player);

    @SideOnly(Side.CLIENT)
    public void openPasswordGui();

    @SideOnly(Side.CLIENT)
    public void wrongPasswordInput();

    public void applySync(NBTTagCompound nbtTagCompound);

    public void openContainer(PCGresBaseWithInventory container);

    public void closeContainer(PCGresBaseWithInventory container);

    public void sendProgressBarUpdates();

    public int getEntityId();

    @SideOnly(Side.CLIENT)
    public String getEntityTextureName(PCEntityRenderer<?> renderer);

    @SideOnly(Side.CLIENT)
    public void doRender(PCEntityRenderer<?> renderer, double x, double y, double z, float rotYaw, float timeStamp);

    public boolean shouldRenderRider();
}
