package com.rumaruka.powercraft.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.world.World;

public interface IInventoryPC extends ISidedInventory {


    int getSlotStackLimit(int limit);

    boolean canTakeStack(int lmt, EntityPlayer player);

    boolean canDropStack(int dropLimit);

    boolean canBeDragged(int limit);

    void onTick(World world);

    int[] getAppliendGroups(int i);

    int[] getAppliedSides(int side);
}
