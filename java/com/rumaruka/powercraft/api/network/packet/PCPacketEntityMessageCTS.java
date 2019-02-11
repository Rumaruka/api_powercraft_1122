package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketClientToServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class PCPacketEntityMessageCTS extends PCPacketClientToServer {

    private int entityID;
    private NBTTagCompound nbtTagCompound;
    private long session;



    public PCPacketEntityMessageCTS(){

    }

    public PCPacketEntityMessageCTS(IEntity entity, NBTTagCompound nbtTagCompound, long session){
        this.entityID = entity.getEntityId();
        this.nbtTagCompound = nbtTagCompound;
        this.session = session;
    }
    @Override
    protected PCPacket doAndReple(NetHandlerPlayServer playServer, World world, EntityPlayerMP playerMP) {
       IEntity entity = PCUtils.getEntity(world, this.entityID, IEntity.class);
        if(entity!=null){
            entity.onClientMessageCheck(player, this.nbtTagCompound, this.session);
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.nbtTagCompound = readNBTFromBuf(buf);
        this.session = buf.readLong();
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.entityID);
        writeNBTToBuf( this.nbtTagCompound,buf);
        buf.writeLong(this.session);
    }
}
