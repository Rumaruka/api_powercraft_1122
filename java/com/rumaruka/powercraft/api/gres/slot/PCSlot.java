package com.rumaruka.powercraft.api.gres.slot;

import com.rumaruka.powercraft.api.inventory.IInventoryBackgroundPC;
import com.rumaruka.powercraft.api.inventory.IInventoryPC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PCSlot extends Slot {

    private ItemStack itemStack;
    private boolean isRenderGrayWhenEmpty;


    public PCSlot(IInventory inventory, int slotIndex){
        super(inventory,slotIndex,0,0);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {

         return this.inventory.isItemValidForSlot(getSlotIndex(),stack);

    }

    @Override
    public int getSlotStackLimit() {
        if(this.inventory instanceof IInventoryPC){
            return ((IInventoryPC) this.inventory).getSlotStackLimit(getSlotIndex());
        }

        return super.getSlotStackLimit();
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        if(this.inventory instanceof IInventoryPC){
            return ((IInventoryPC) this.inventory).canTakeStack(getSlotIndex(),playerIn);
        }

        return super.canTakeStack(playerIn);
    }

    public boolean canDragIntoSlot(){
        if(this.inventory instanceof IInventoryPC){
            return ((IInventoryPC) this.inventory).canBeDragged(getSlotIndex());
        }
        return true;
    }
    public ItemStack getBackgroundStack() {
        if(this.inventory instanceof IInventoryBackgroundPC){
            return ((IInventoryBackgroundPC)this.inventory).getBackgroundStack(getSlotIndex());
        }
        return this.itemStack;
    }

    public boolean isRenderGrayWhenEmpty() {
        if(this.inventory instanceof IInventoryBackgroundPC){
            return ((IInventoryBackgroundPC)this.inventory).renderGrayWhenEmpty(getSlotIndex());
        }
        return this.isRenderGrayWhenEmpty;

    }

    public int[] getAppliedSides() {
        if(this.inventory instanceof IInventoryPC){
            return ((IInventoryPC)this.inventory).getAppliedSides(getSlotIndex());
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        ItemStack stack = super.decrStackSize(amount);

        return stack.getMaxStackSize()==0?null:stack;
    }
}
