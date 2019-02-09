package com.rumaruka.powercraft.api.inventory;

import net.minecraft.item.ItemStack;

public interface IInventoryBackgroundPC {

    ItemStack getBackgroundStack(int slot);

    boolean renderGrayWhenEmpty(int slot);
}
