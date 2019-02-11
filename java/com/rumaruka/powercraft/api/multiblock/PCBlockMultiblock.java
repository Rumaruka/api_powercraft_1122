package com.rumaruka.powercraft.api.multiblock;

import com.rumaruka.powercraft.api.PCClientUtils;
import com.rumaruka.powercraft.api.PCDirection;
import com.rumaruka.powercraft.api.PCTickHandler;
import com.rumaruka.powercraft.api.block.EnumBlockType;
import com.rumaruka.powercraft.api.block.PCBlockTileEntity;
import com.rumaruka.powercraft.api.block.PCTileEntity;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.network.packet.PCPacketSelectMultiblockTile;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;
import java.util.WeakHashMap;

public final class PCBlockMultiblock extends PCBlockTileEntity implements PCTickHandler.IRenderTickHandler {
    private static boolean damageDrawn;
    private static ISelector selector;
    static WeakHashMap<EntityPlayer, PCMultiblockIndex> playerSelection = new WeakHashMap<EntityPlayer, PCMultiblockIndex>();

    public PCBlockMultiblock( ) {
        super(EnumBlockType.MACHINE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onStartTick(float renderTickTime) {

    }

    @Override
    public void onEndTick(float renderTickTime) {

    }

    @Override
    public Class<? extends PCTileEntity> getTileEntityClass() {
        return null;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

    }

    @Override
    public boolean canRedstoneConnectTo(World world, int x, int y, int z, PCDirection side, int faceSide) {
        return false;
    }

    @Override
    public int getRedstonePower(World world, int x, int y, int z, PCDirection side, int faceSide) {
        return 0;
    }

    @Override
    public void setRedstonePower(World world, int x, int y, int z, PCDirection side, int faceSide, int value) {

    }


    private interface ISelector{
        void select(World world, BlockPos pos, RayTraceResult select);
    }

    @SideOnly(Side.CLIENT)
    private static class ClientSelector implements ISelector{

        public ClientSelector(){

        }

        @Override
        public void select(World world, BlockPos pos, RayTraceResult select) {
            EntityPlayer player = PCClientUtils.mc().player;
            PCMultiblockIndex selectIndex = playerSelection.get(player);
            PCMultiblockIndex bestIndex = PCMultiblockIndex.values()[select.subHit];
            if(selectIndex!=bestIndex){
                playerSelection.put(player,bestIndex);
                PCPacketHandler.sendToServer(new PCPacketSelectMultiblockTile());
            }
        }
    }
}
