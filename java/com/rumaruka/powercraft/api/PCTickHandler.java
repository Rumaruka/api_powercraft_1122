package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.reflect.PCSecurity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.util.ArrayList;
import java.util.List;

public class PCTickHandler {
    private static final PCTickHandler INSTANCE = new PCTickHandler();

    private static int count = 0;
    private static final List<IBaseTickHandler> toAdd = new ArrayList<IBaseTickHandler>();
    private static final List<IBaseTickHandler> toDelete = new ArrayList<IBaseTickHandler>();
    private static final List<ITickHandler> tickHandlers = new ArrayList<ITickHandler>();
    private static final List<IWorldTickHandler> worldTickHandlers = new ArrayList<IWorldTickHandler>();
    private static final List<IPlayerTickHandler> playerTickHandlers = new ArrayList<IPlayerTickHandler>();
    private static final List<IRenderTickHandler> renderTickHandlers = new ArrayList<IRenderTickHandler>();

    static void register(){
        PCSecurity.allowedCaller("PC_TickHandler.register()", PCApi.class);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    private PCTickHandler(){}

    public static void registerTickHandler(IBaseTickHandler tickHandler){
        if(count==0){
            if(tickHandler instanceof ITickHandler){
                tickHandlers.add((ITickHandler) tickHandler);
            }
            if(tickHandler instanceof IWorldTickHandler){
                worldTickHandlers.add((IWorldTickHandler) tickHandler);
            }
            if(tickHandler instanceof IPlayerTickHandler){
                playerTickHandlers.add((IPlayerTickHandler) tickHandler);
            }
            if(tickHandler instanceof IRenderTickHandler){
                renderTickHandlers.add((IRenderTickHandler) tickHandler);
            }
        }else{
            toAdd.add(tickHandler);
        }
    }

    public static void removeTickHander(IBaseTickHandler tickHandler) {
        if(count==0){
            tickHandlers.remove(tickHandler);
            worldTickHandlers.remove(tickHandler);
            playerTickHandlers.remove(tickHandler);
            renderTickHandlers.remove(tickHandler);
        }else{
            toDelete.add(tickHandler);
        }
    }

    private static void startIteration(){
        synchronized(INSTANCE){
            count++;
        }
    }

    private static void endIteration(){
        synchronized(INSTANCE){
            count--;
            if(count==0){
                for(IBaseTickHandler tickHandler:toAdd){
                    registerTickHandler(tickHandler);
                }
                toAdd.clear();
                for(IBaseTickHandler tickHandler:toDelete){
                    removeTickHander(tickHandler);
                }
                toDelete.clear();
            }
        }
    }

    @SuppressWarnings("static-method")
    @SubscribeEvent
    public void tickEvent(TickEvent event){
        switch(event.phase){
            case END:
                onEndTickEvent(event);
                break;
            case START:
                onStartTickEvent(event);
                break;
            default:
                break;
        }
    }

    private static void onStartTickEvent(TickEvent event){
        PCSide side = PCSide.from(event.side);
        startIteration();
        switch(event.type){
            case CLIENT:
            case SERVER:
                for(ITickHandler tickHandler:tickHandlers){
                    tickHandler.onStartTick(side);
                }
                break;
            case PLAYER:
                EntityPlayer player = ((PlayerTickEvent)event).player;
                for(IPlayerTickHandler playerTickHandler:playerTickHandlers){
                    playerTickHandler.onStartTick(side, player);
                }
                break;
            case RENDER:
                float renderTickTime = ((RenderTickEvent)event).renderTickTime;
                for(IRenderTickHandler renderTickHandler:renderTickHandlers){
                    renderTickHandler.onStartTick(renderTickTime);
                }
                break;
            case WORLD:
                World world = ((WorldTickEvent)event).world;
                for(IWorldTickHandler worldTickHandler:worldTickHandlers){
                    worldTickHandler.onStartTick(side, world);
                }
                break;
            default:
                break;
        }
        endIteration();
    }

    private static void onEndTickEvent(TickEvent event){
        PCSide side = PCSide.from(event.side);
        startIteration();
        switch(event.type){
            case CLIENT:
            case SERVER:
                for(ITickHandler tickHandler:tickHandlers){
                    tickHandler.onEndTick(side);
                }
                break;
            case PLAYER:
                EntityPlayer player = ((PlayerTickEvent)event).player;
                for(IPlayerTickHandler playerTickHandler:playerTickHandlers){
                    playerTickHandler.onEndTick(side, player);
                }
                break;
            case RENDER:
                float renderTickTime = ((RenderTickEvent)event).renderTickTime;
                for(IRenderTickHandler renderTickHandler:renderTickHandlers){
                    renderTickHandler.onEndTick(renderTickTime);
                }
                break;
            case WORLD:
                World world = ((WorldTickEvent)event).world;
                for(IWorldTickHandler worldTickHandler:worldTickHandlers){
                    worldTickHandler.onEndTick(side, world);
                }
                break;
            default:
                break;
        }
        endIteration();
    }

     interface IBaseTickHandler{
        //
    }

    public static interface ITickHandler extends IBaseTickHandler{

        public void onStartTick(PCSide side);

        public void onEndTick(PCSide side);

    }

    public  interface IWorldTickHandler extends IBaseTickHandler{

        public void onStartTick(PCSide side, World world);

        public void onEndTick(PCSide side, World world);

    }

    public  interface IPlayerTickHandler extends IBaseTickHandler{

        public void onStartTick(PCSide side, EntityPlayer player);

        public void onEndTick(PCSide side, EntityPlayer player);

    }

    public static interface IRenderTickHandler extends IBaseTickHandler{

        public void onStartTick(float renderTickTime);

        public void onEndTick(float renderTickTime);

    }
}
