package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketClientToServer;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.reflect.PCFields;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.world.World;


import java.util.ArrayList;

public class PCPacketClickWindow extends PCPacketClientToServer {
    private int windowId;
    private int slotNumber;
    private int mouseButton;
    private ClickType transfer;
    private int transactionID;
    private ItemStack clientDone;


    public PCPacketClickWindow(){

    }
    public PCPacketClickWindow(int windowId, int slotNumber, int mouseButton, ClickType transfer, int transactionID, ItemStack clientDone){
        this.windowId = windowId;
        this.slotNumber = slotNumber;
        this.mouseButton = mouseButton;
        this.transfer = transfer;
        this.transactionID = transactionID;
        this.clientDone = clientDone;
    }
    @Override
    protected PCPacket doAndReple(NetHandlerPlayServer playServer, World world, EntityPlayerMP player) {
        player.onEntityUpdate();

        if (player.openContainer.windowId == this.windowId && player.openContainer.canInteractWith(player)){
            ItemStack itemstack = player.openContainer.slotClick(this.slotNumber, this.mouseButton, this.transfer, player);

            if (ItemStack.areItemStacksEqual(this.clientDone, itemstack)){
                player.connection.sendPacket(new SPacketConfirmTransaction(this.windowId, (short) this.transactionID, true));
                player.isChangingQuantityOnly = true;
                player.openContainer.detectAndSendChanges();
                player.updateHeldItem();
                player.isChangingQuantityOnly = false;
            }else{
                PCFields.NetHandlerPlayServer_field_147372_n.getValue(playServer).addKey(player.openContainer.windowId, Short.valueOf((short)this.transactionID));
                player.connection.sendPacket(new SPacketConfirmTransaction(this.windowId, (short) this.transactionID, false));
                player.openContainer.setCanCraft(player, false);
                ArrayList<ItemStack> itemStacks = new ArrayList<ItemStack>();

                for (int i = 0; i < player.openContainer.inventorySlots.size(); ++i)
                {
                    itemStacks.add(((Slot)player.openContainer.inventorySlots.get(i)).getStack());
                }

                PCPacketHandler.sendTo(new PCPacketWindowItems(this.windowId, itemStacks), player);
                PCPacketHandler.sendTo(new PCPacketSetSlot(-1, -1, player.inventory.getItemStack()), player);
            }
        }
        return null;
    }
    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.slotNumber = buf.readInt();
        this.mouseButton = buf.readInt();
        this.transactionID = buf.readInt();
        this.clientDone = readItemStackFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.windowId);
        buf.writeInt(this.slotNumber);
        buf.writeInt(this.mouseButton);
        buf.writeInt(this.transactionID);
        writeItemStackToBuf(buf, this.clientDone);
    }
}
