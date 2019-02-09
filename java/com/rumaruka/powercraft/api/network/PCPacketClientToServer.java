package com.rumaruka.powercraft.api.network;

import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCSide;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public abstract class PCPacketClientToServer extends PCPacket {
    protected abstract PCPacket doAndReple(NetHandlerPlayServer playServer, World world, EntityPlayerMP playerMP);
    @Override
    protected PCPacket doAndReply(PCSide side, INetHandler iNetHandler) {
        if(checkSide(side) && iNetHandler instanceof NetHandlerPlayServer){
            NetHandlerPlayServer playServer = (NetHandlerPlayServer)iNetHandler;
            return doAndReple(playServer, playServer.player.world, playServer.player);
        }
        return null;
    }
    private static boolean checkSide(PCSide side){
        if(side!=PCSide.SERVER){
            PCLogger.severe("A client to server packet can't run on client");
            return false;
        }
        return true;
    }

}
