package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.block.AbstractBlockBase;
import com.rumaruka.powercraft.api.block.PCBlocks;
import com.rumaruka.powercraft.api.entity.IEntityPC;
import com.rumaruka.powercraft.api.item.IItemPC;
import com.rumaruka.powercraft.api.reflect.PCSecurity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class PCForgeHandler implements IFuelHandler, IWorldGenerator {

    private static final PCForgeHandler instance = new PCForgeHandler();

    private PCForgeHandler(){

    }

    public static void register(){
        PCSecurity.allowedCaller("PCFuelHandler.register()",PCApi.class);
        GameRegistry.registerFuelHandler(instance);
        GameRegistry.registerWorldGenerator(instance,0);
        MinecraftForge.EVENT_BUS.register(instance);
    }
    @Override
    public int getBurnTime(ItemStack fuel) {
       IItemPC item = PCUtils.getItem(fuel, IItemPC.class);
        if(item!=null){
            return item.getBurnTime(fuel);
        }
        return 0;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        List<AbstractBlockBase> list = PCBlocks.getBlocks();
        for(AbstractBlockBase block:list){
            block.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }

    @SuppressWarnings("static-method")
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onGuiOpened(GuiOpenEvent event){
        GuiScreen gui = event.getGui();
        PCHacks.hackGui(gui);
    }
    @SuppressWarnings("static-method")
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void shouldDrawPlayer(RenderPlayerEvent.Pre pre){
        Entity riding = pre.getEntityPlayer().getRidingEntity();
        if(riding instanceof IEntityPC){
            if(!((IEntityPC)riding).shouldRenderRider()){
                pre.setCanceled(true);
            }
        }
    }

    @SuppressWarnings("static-method")
    @SubscribeEvent
    public void getDigSpeed(PlayerEvent.BreakSpeed speed){
        ItemStack itemStack = speed.getEntityPlayer().getHeldItemMainhand();
        if(itemStack.isEmpty()){
            Item item = itemStack.getItem();
            if(item instanceof IItemPC){
                speed.setNewSpeed(((IItemPC)item).updateDigSpeed(itemStack, speed.getNewSpeed(), speed.getPos().getX(), speed.getPos().getY(), speed.getPos().getZ(), speed.getEntityPlayer()));
            }
        }
    }
}
