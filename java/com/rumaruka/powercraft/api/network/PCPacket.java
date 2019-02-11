package com.rumaruka.powercraft.api.network;

import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCSide;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import java.io.*;

public abstract class PCPacket {

    protected abstract void fromByteBuffer(ByteBuf buf);

    protected abstract void toByteBuffer(ByteBuf buf);

    protected abstract PCPacket doAndReply(PCSide side, INetHandler iNetHandler);

    protected static void writeStringToBuf(ByteBuf buf, String string) {
        buf.writeShort(string.length());
        for(int j=0; j<string.length(); j++){
            buf.writeChar(string.charAt(j));
        }
    }

    protected static String readStringFromBuf(ByteBuf buf) {
        char[] chars = new char[buf.readUnsignedShort()];
        for(int i=0; i<chars.length; i++){
            chars[i] = buf.readChar();
        }
        return new String(chars);
    }

    protected static void writeNBTToBuf(NBTTagCompound nbtTagCompound,ByteBuf f) {
        try {
            CompressedStreamTools.write(nbtTagCompound,new ByteBufOutputStream(f));

        } catch (IOException e) {
            e.printStackTrace();
            PCLogger.severe("Error while compressing NBTTag");
        }
    }

    protected static NBTTagCompound readNBTFromBuf(ByteBuf buf) {


        try {
            return CompressedStreamTools.readCompressed(new ByteBufInputStream(buf));
        } catch (IOException e) {
            e.printStackTrace();
            PCLogger.severe("Error while decompressing NBTTag");
        }
        return new NBTTagCompound();
    }
    protected static void writeItemStackToBuf(ByteBuf buf, ItemStack itemStack) {
        if(itemStack.isEmpty()){
            buf.writeInt(-1);
        }else{
            buf.writeInt(Item.getIdFromItem(itemStack.getItem()));
            buf.writeInt(itemStack.getMaxStackSize());
            buf.writeInt(itemStack.getItemDamage());
            NBTTagCompound compound = itemStack.getTagCompound();
            if(compound==null){
                buf.writeBoolean(false);
            }else{
                buf.writeBoolean(true);
                writeNBTToBuf(compound,buf);
            }
        }
    }


    protected static ItemStack readItemStackFromBuf(ByteBuf buf) {
        int id = buf.readInt();
        if(id<0)
            return null;
        int count = buf.readInt();
        int damage = buf.readInt();
        boolean nbt = buf.readBoolean();
        ItemStack itemStack = new ItemStack(Item.getItemById(id), count, damage);
        if(nbt){
            itemStack.setTagCompound(readNBTFromBuf(buf));
        }
        return itemStack;
    }

}
