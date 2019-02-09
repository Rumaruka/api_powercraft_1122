package com.rumaruka.powercraft.api.network;

import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCSide;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

    protected static void writeNBTToBuf(NBTTagCompound nbtTagCompound,File f) {
        try {
            CompressedStreamTools.writeCompressed(nbtTagCompound,new FileOutputStream(f));

        } catch (IOException e) {
            e.printStackTrace();
            PCLogger.severe("Error while compressing NBTTag");
        }
    }

    protected static NBTTagCompound readNBTFromBuf(File buf) {


        try {
            return CompressedStreamTools.readCompressed(new FileInputStream(buf));
        } catch (IOException e) {
            e.printStackTrace();
            PCLogger.severe("Error while decompressing NBTTag");
        }
        return new NBTTagCompound();
    }





}
