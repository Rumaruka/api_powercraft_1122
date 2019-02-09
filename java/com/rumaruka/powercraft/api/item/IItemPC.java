package com.rumaruka.powercraft.api.item;

import com.rumaruka.powercraft.api.PCModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IItemPC {

    public void onTick(ItemStack itemStack, World world, IInventory inventory, int slot);

    public int getBurnTime(ItemStack fuel);

    public void construct();

    public PCModule getModule();

    public String getRegisterName();

    public String getTextureFolderName();

    public String[] getOreNames();

    public float updateDigSpeed(ItemStack itemStack, float speed, int x, int y, int z, EntityPlayer entityPlayer);
}
