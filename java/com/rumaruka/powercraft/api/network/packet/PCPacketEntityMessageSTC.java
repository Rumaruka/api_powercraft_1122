package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PCPacketEntityMessageSTC extends PCPacketServerToClient {

    private int entityID;
    private NBTTagCompound nbtTagCompound;

    public PCPacketEntityMessageSTC(){

    }

    public PCPacketEntityMessageSTC(IEntity entity, NBTTagCompound nbtTagCompound){
        this.entityID = entity.getEntityId();
        this.nbtTagCompound = nbtTagCompound;
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        IEntity entity = PCUtils.getEntity(world, this.entityID, IEntity.class);
        if(entity!=null){
            entity.onClientMessage(player, this.nbtTagCompound);
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.nbtTagCompound = readNBTFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.entityID);
        writeNBTToBuf( this.nbtTagCompound,buf);
    }
}
