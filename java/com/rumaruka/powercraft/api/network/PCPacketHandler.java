package com.rumaruka.powercraft.api.network;

import com.rumaruka.powercraft.api.PCApi;
import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCSide;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.reflect.PCReflect;
import com.rumaruka.powercraft.api.reflect.PCSecurity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class PCPacketHandler extends SimpleChannelInboundHandler<PCPacket> {
    private static boolean done;
    private static EnumMap<Side, FMLEmbeddedChannel> channels;
    private static List<Class<? extends PCPacket>> packetList = new ArrayList<Class<? extends PCPacket>>();
    @SuppressWarnings("unchecked")
    private static Class<? extends PCPacket>[] packets = new Class[]{PCPacketPacketResolve.class};
    private static HashMap<Class<? extends PCPacket>, Integer> packetID = new HashMap<Class<? extends PCPacket>, Integer>();

    static{
        packetID.put(PCPacketPacketResolve.class, 0);
    }


    public static class Listener{

        Listener(){

        }

        @SuppressWarnings("static-method")
        @SubscribeEvent
        public void clientConnected(PlayerEvent.PlayerLoggedInEvent event){
            sendPacketResolveTo((EntityPlayerMP) event.player);
        }

    }

    @Sharable
    public static class Indexer extends MessageToMessageCodec<FMLProxyPacket, PCPacket> {

        Indexer(){

        }

        @Override
        protected void encode(ChannelHandlerContext ctx, PCPacket msg, List<Object> out) throws Exception {
            ByteBuf buffer = Unpooled.buffer();
            writePacketToByteBuf(msg, buffer);
            FMLProxyPacket packet = new FMLProxyPacket(new PacketBuffer(buffer.copy()), "PowerCraft");
            out.add(packet);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
            out.add(getPacketFromByteBuf(msg.payload()));
        }

    }

    public static Packet getPacketFrom(PCPacket packet){
        Packet pkt = channels.get(Side.SERVER).generatePacketFrom(packet);
        if(pkt==null){
            PCLogger.severe("Anything went wrong");
        }
        return pkt;
    }

    public static void registerPacket(Class<? extends PCPacket> packet){
        if(done){
            PCLogger.severe("A packet want to register while startup is done");
        }else{
            packetList.add(packet);
        }
    }

    @SuppressWarnings("unchecked")
    public static void setupPackets(){
        PCSecurity.allowedCaller("PC_PacketHandler.setupPackets()", PCApi.class);
        done = true;
        packets = new Class[packetList.size()+1];
        packetID.clear();
        packets[0] = PCPacketPacketResolve.class;
        packetID.put(PCPacketPacketResolve.class, 0);
        for(int i=1; i<packets.length; i++){
            packets[i] = packetList.get(i-1);
            packetID.put(packets[i], i);
        }
    }

    static void sendPacketResolveTo(EntityPlayerMP player){
        String[] packetClasses = new String[packets.length-1];
        for(int i=0; i<packetClasses.length; i++){
            packetClasses[i] = packets[i+1].getName();
        }
        sendTo(new PCPacketPacketResolve(packetClasses), player);
    }

    static PCPacket getPacketFromByteBuf(ByteBuf buf) {
        int id = buf.readInt();
        PCPacket packet = PCReflect.newInstance(packets[id]);
        if(packet==null){
            PCLogger.severe("Can't create packet %s", packets[id]);
            return null;
        }
        packet.fromByteBuffer(buf);
        return packet;
    }

    static void writePacketToByteBuf(PCPacket packet, ByteBuf buf) {
        Integer id = packetID.get(packet.getClass());
        if(id==null){
            PCLogger.severe("YOU HAVE TO REGISTER CLASS %s", packet.getClass());
            return;
        }
        buf.writeInt(id.intValue());
        packet.toByteBuffer(buf);
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    static void setPackets(String[] packetClasses) {
        if(PCUtils.mcs()!=null)
            return;
        packets = new Class[packetClasses.length+1];
        packetID.clear();
        packets[0] = PCPacketPacketResolve.class;
        packetID.put(PCPacketPacketResolve.class, Integer.valueOf(0));
        for(int i=1; i<packets.length; i++){
            try {
                packets[i] = (Class<? extends PCPacket>) Class.forName(packetClasses[i-1]);
                packetID.put(packets[i], Integer.valueOf(i));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendToAll(PCPacket packet){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendTo(PCPacket packet, EntityPlayerMP player){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendToAllAround(PCPacket packet, int dimension, double x, double y, double z, double range){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendToAllAround(PCPacket packet, World world, double x, double y, double z, double range){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new NetworkRegistry.TargetPoint(PCUtils.getDimension(world), x, y, z, range));
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendToDimension(PCPacket packet, int dimension){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(Integer.valueOf(dimension));
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendToServer(PCPacket packet){
        checkClient(packet);
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private static void checkServer(PCPacket packet){
        if(!PCUtils.isServer()){
            PCLogger.severe("A server send not invoked form server");
        }else if(packet instanceof PCPacketClientToServer){
            PCLogger.severe("A server trys to send a client to server packet");
        }
    }

    private static void checkClient(PCPacket packet){
        if(!PCUtils.isClient()){
            PCLogger.severe("A client send not invoked form client");
        }else if(packet instanceof PCPacketServerToClient){
            PCLogger.severe("A client trys to send a server to client packet");
        }
    }

    public PCSide side;

    private PCPacketHandler(PCSide side){
        this.side = side;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PCPacket msg) throws Exception {
        INetHandler iNetHandler = channels.get(this.side.side).attr(NetworkRegistry.NET_HANDLER).get();
        PCPacket result = msg.doAndReply(this.side, iNetHandler);
        if (result != null){
            ctx.writeAndFlush(result).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }
}
