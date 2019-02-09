package com.rumaruka.powercraft.api.gres.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PCSlotPhantom extends PCSlot {
    public PCSlotPhantom(IInventory inventory, int slotIndex) {
        super(inventory, slotIndex);
    }

    @Override
    public int getSlotStackLimit() {
        return 0;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        super.putStack(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public void putStack(ItemStack stack) {
        ItemStack is = stack;
        int limit = is.getMaxStackSize();
        if(!is.isEmpty()){

            is=is.copy();
            limit=0;
        }
        super.putStack(is);
    }
}
