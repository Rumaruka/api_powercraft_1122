package com.rumaruka.powercraft.api.building;

import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PCPacketBlockBreaking extends PCPacketServerToClient {

    private int x;
    private int y;
    private int z;
    private int damage;


    public PCPacketBlockBreaking(){

    }
    public PCPacketBlockBreaking(int x,int y,int z,int damage){
        this.x=x;
        this.y=y;
        this.z=z;
        this.damage=damage;
    }


    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        PCBlockDamage.setClientDamage(this.x,this.y,this.z,this.damage);
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.damage = buf.readInt();
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.damage);
    }
}
