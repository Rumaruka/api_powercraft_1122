package com.rumaruka.powercraft.api.network;

import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCSide;
import net.minecraft.network.INetHandler;

public abstract class PCPacketServerToClientBase extends PCPacket {

    @Override
    protected PCPacket doAndReply(PCSide side, INetHandler iNetHandler) {
        PCLogger.severe("A server to client packet can`t run on server");
        return null;
    }
}
