package com.rumaruka.powercraft.api.network;

import com.rumaruka.powercraft.api.PCClientUtils;
import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCSide;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PCPacketServerToClient extends PCPacketServerToClientBase{

    @SideOnly(Side.CLIENT)
    protected abstract PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player);

    @Override
    protected PCPacket doAndReply(PCSide side, INetHandler iNetHandler) {
        if(checkSide(side) && iNetHandler instanceof NetHandlerPlayClient && PCClientUtils.mc().world!=null && PCClientUtils.mc().player!=null){
            return doAndReply((NetHandlerPlayClient)iNetHandler, PCClientUtils.mc().world, PCClientUtils.mc().player);
        }
        return null;
    }

    private static boolean checkSide(PCSide side){
        if(side !=PCSide.CLIENT){
            PCLogger.severe("A server to client packet can't run on server");
            return false;
        }
        return true;
    }
}
