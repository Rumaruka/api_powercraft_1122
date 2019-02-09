package com.rumaruka.powercraft.api.inventory;

import net.minecraft.item.ItemStack;

public interface IInventorySizeOverriderPC {

    int getMaxStackSize(ItemStack itemStack,int slot);
}
