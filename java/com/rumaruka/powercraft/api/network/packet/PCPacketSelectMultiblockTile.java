package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCSide;
import com.rumaruka.powercraft.api.network.PCPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class PCPacketSelectMultiblockTile extends PCPacket {
    @Override
    protected void fromByteBuffer(ByteBuf buf) {

    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {

    }

    @Override
    protected PCPacket doAndReply(PCSide side, INetHandler iNetHandler) {
        return null;
    }
}
