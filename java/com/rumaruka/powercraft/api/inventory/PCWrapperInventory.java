package com.rumaruka.powercraft.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class PCWrapperInventory implements IInventory {

    private final ItemStack inventoryContents;

    public PCWrapperInventory(ItemStack inventoryContents) {
        this.inventoryContents = inventoryContents;
    }

    @Override
    public int getSizeInventory() {
        return this.inventoryContents.getCount();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.inventoryContents;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }


    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        //
    }


    @Override
    public String getName() {
        return "wrapper";
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 0;
    }

    @Override
    public void markDirty() {
        //
    }



    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return false;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }
}
