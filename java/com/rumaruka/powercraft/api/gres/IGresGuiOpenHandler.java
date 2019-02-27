package com.rumaruka.powercraft.api.gres;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGresGuiOpenHandler {

    @SideOnly(Side.CLIENT)
    IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData);

    PCGresBaseWithInventory openServerGui(EntityPlayer player, Object[] params);


    NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player, Object[] params);
}
