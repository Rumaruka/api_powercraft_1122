package com.rumaruka.powercraft.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;


import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class PCChunkManager implements LoadingCallback {


    private static  PCChunkManager INSTANCE = new PCChunkManager();

    private static WeakHashMap<World, WorldInfo>worldInfos = new WeakHashMap<World, WorldInfo>();
    private static class WorldInfo{
        /**
         * The World
         */
        private World world;
        /**
         * All the Forge chunk loader tickets
         */
        private  ArrayList<Ticket> tickets = new ArrayList<>();

        WorldInfo(World world) {
            this.world = world;
        }

        private Ticket getTicketForChunk(ChunkPos pair){
            String key = pair.toString();
            for(Ticket ticket: tickets){
                if(ticket.getModData().hasKey(key)){
                    return ticket;
                }
            }
            return null;
        }

        void forceChunk(int x, int z) {
            ChunkPos pair = new ChunkPos(x>>4, z>>4);
            Ticket ticket = getTicketForChunk(pair);
            if(ticket==null){
                ticket = getFreeTicket();
                ForgeChunkManager.forceChunk(ticket, pair);
                ticket.getModData().setTag(pair.toString(), new NBTTagCompound());
            }
            NBTTagCompound nbtTagCompound = ticket.getModData().getCompoundTag(pair.toString());
            nbtTagCompound.setInteger("num", nbtTagCompound.getInteger("num")+1);
        }

        void unforceChunk(int x, int z) {
            ChunkPos pair = new ChunkPos(x>>4, z>>4);
            Ticket ticket = getTicketForChunk(pair);
            if(ticket!=null){
                NBTTagCompound nbtTagCompound = ticket.getModData().getCompoundTag(pair.toString());
                int num;
                nbtTagCompound.setInteger("num", num = nbtTagCompound.getInteger("num")-1);
                if(num<=0){
                    ForgeChunkManager.unforceChunk(ticket, pair);
                    ticket.getModData().removeTag(pair.toString());
                }
            }
        }

        private Ticket getFreeTicket(){


            for(Ticket ticket:tickets){
                if(ticket.getChunkListDepth()<ticket.getMaxChunkListDepth())
                    ticket.setChunkListDepth(ticket.getMaxChunkListDepth());
                if(tickets.size()<ticket.getChunkListDepth()){
                    return ticket;
                }
            }
            return askForNewTicket();
        }

        private Ticket askForNewTicket(){
            List<PCModule> modules = PCModules.getModules();
            for(PCModule module:modules){
                int ticketCount = ForgeChunkManager.ticketCountAvailableFor(module, this.world);
                if(ticketCount>0){
                    Ticket ticket = ForgeChunkManager.requestTicket(module, this.world, ForgeChunkManager.Type.NORMAL);
                    tickets.add(ticket);
                    return ticket;
                }
            }
            PCLogger.severe("No more Chunkloader Tickets :/");
            return null;
        }

    }

    private PCChunkManager(){

    }

    static void register(){
        List<PCModule> modules = PCModules.getModules();
        for(PCModule module:modules){
            ForgeChunkManager.setForcedChunkLoadingCallback(module, INSTANCE);
        }
    }
    public static void forceChunk(World world, int x, int z){
        getWorldInfo(world).forceChunk(x, z);
    }
    private static WorldInfo getWorldInfo(World world){
        WorldInfo worldInfo = worldInfos.get(world);
        if(worldInfo==null){
            worldInfos.put(world, worldInfo = new WorldInfo(world));
        }
        return worldInfo;
    }



    @Override
    public void ticketsLoaded(List<Ticket> tickets1, World world) {
                    getWorldInfo(world).tickets.addAll(tickets1);
    }
}
