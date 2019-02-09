package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.block.PCTileEntity;
import com.rumaruka.powercraft.api.gres.slot.PCSlot;
import com.rumaruka.powercraft.api.inventory.IInventoryPC;
import com.rumaruka.powercraft.api.inventory.IInventorySizeOverriderPC;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class PCGresBaseWithInventory extends Container implements IInventoryPC, IInventorySizeOverriderPC {

    public static boolean SETTING_OK;
    protected final EntityPlayer player;

    //For main inventory player
    protected final Slot[][] inventoryPlayer = new Slot[9][3];
    //For toolbar player
    protected final Slot[] inventoryPlayerForToolbar = new Slot[9];
    protected final IInventory inventory;

    protected Slot[]invSlots;

    private int dragType=-1;
    private int dragState;

    private final Set<Slot>dragSlots = new HashSet<Slot>();

    public PCGresBaseWithInventory(EntityPlayer player,IInventory inventory){
        SETTING_OK= PCUtils.isServer();
        this.player=player;
        this.inventory=inventory;

        inventory.openInventory(player);
        if (inventory instanceof PCTileEntity) {
            ((PCTileEntity) inventory).openContainer(this);
        }

        if (inventory instanceof PCEntity) {
            ((PCEntity) inventory).openContainer(this);
        }


            for (int i = 0; i < 9; i++) {
                this.inventoryPlayerForToolbar[i] = new PCSlot(player.inventory, i);
                addSlotToContainer(this.inventoryPlayerForToolbar[i]);
            }

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 3; j++) {
                    this.inventoryPlayer[i][j] = new PCSlot(player.inventory, i + j * 9 + 9);
                    addSlotToContainer(this.inventoryPlayer[i][j]);
                }

        }

            Slot[]sl = getAllSlot();
            if(sl!=null){
                for (Slot s:sl){
                    addSlotToContainer(s);
                }
            }

    }
    protected Slot[] getAllSlot(){
        this.invSlots = new Slot[this.inventory.getSizeInventory()];
        for (int i = 0; i<this.invSlots.length; i++){
            this.invSlots[i]=createSlot(i);
        }
        return this.invSlots;
    }

    protected PCSlot createSlot(int i){
        return new PCSlot(this.inventory,i);
    }



    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        closeInventory(playerIn);
        if(this.inventory instanceof  PCTileEntity){
            ((PCTileEntity) this.inventory).closeContainer(this);
        }
        if (this.inventory instanceof PCEntity) {
            ((PCEntity) this.inventory).closeContainer(this);
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        if(listener instanceof EntityPlayerMP){
            if (this.listeners.contains(listener)){
                throw new IllegalArgumentException("Listener already listening");
            }
            this.listeners.add(listener);
            PCPacketHandler.sendTo(new PCPacketWindowItems(this.windowId, getInventory()), (EntityPlayerMP)crafting);
            PCPacketHandler.sendTo(new PCPacketSetSlot(-1, -1, ((EntityPlayerMP)listener).inventory.getItemStack()), (EntityPlayerMP)crafting);
            listener.sendAllContents(this, this.getInventory());
            this.detectAndSendChanges();
        }else{
            super.addListener(listener);
        }
        if (this.inventory instanceof PCTileEntity) {
            ((PCTileEntity) this.inventory).sendProgressBarUpdates();
        }
        if (this.inventory instanceof PCEntity) {
            ((PCEntity) this.inventory).sendProgressBarUpdates();
        }
    }

    @Override
    @SuppressWarnings("unchencked")
    public void detectAndSendChanges() {
        for (int i = 0; i < this.inventorySlots.size(); ++i){
            ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)){
                itemstack1 = itemstack.isEmpty() ? null : itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack1);

                for (int j = 0; j < this.listeners.size(); ++j){
                    sendSlotToContentsTo((IContainerListener) this.listeners.get(j), i, itemstack1);
                }
            }
        }
    }
    public void sendSlotToContentsTo(IContainerListener listener, int i, ItemStack stack){
        if(listener instanceof EntityPlayerMP){

            if (!((EntityPlayerMP)listener).isChangingQuantityOnly){
                PCPacketHandler.sendTo(new PCPacketSetSlot(this.windowId, i, stack), (EntityPlayerMP)listener);
            }
        }else{
            listener.sendSlotContents(this, i, stack);
        }
    }

    @Override
    public int getSlotStackLimit(int limit) {
        return 0;
    }

    @Override
    public boolean canTakeStack(int lmt, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean canDropStack(int dropLimit) {
        return false;
    }

    @Override
    public boolean canBeDragged(int limit) {
        return false;
    }

    @Override
    public void onTick(World world) {

    }

    @Override
    public int[] getAppliendGroups(int i) {
        return new int[0];
    }

    @Override
    public int[] getAppliedSides(int side) {
        return new int[0];
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack, int slot) {
        return 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public int getInventoryStackLimit() {
        return 0;
    }

    @Override
    public void markDirty() {

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
    public boolean isItemValidForSlot(int index, ItemStack stack) {
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

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
}
