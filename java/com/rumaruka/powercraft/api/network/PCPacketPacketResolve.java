package com.rumaruka.powercraft.api.network;

import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCSide;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class PCPacketPacketResolve extends PCPacketServerToClientBase{

    private String[] packetClasses;

    public PCPacketPacketResolve(){

    }

    PCPacketPacketResolve(String[] packetClasses){
        this.packetClasses = packetClasses;
    }
    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.packetClasses= new String[buf.readInt()];
        for(int i=0; i<this.packetClasses.length; i++) {
            this.packetClasses[i] = readStringFromBuf(buf);
        }
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.packetClasses.length);
        for(int i=0; i<this.packetClasses.length; i++){
            writeStringToBuf(buf, this.packetClasses[i]);
        }
    }

    @Override
    protected PCPacket doAndReply(PCSide side, INetHandler iNetHandler) {
        if(checkSide(side)){
            PCPacketHandler.setPackets(this.packetClasses);

        }
        return null;

    }

    private static boolean checkSide(PCSide side){
        if(side!=PCSide.CLIENT){
            PCLogger.severe("A server to client packet can't run on server");
            return false;
        }
        return true;
    }
}

