package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.block.AbstractBlockBase;
import com.rumaruka.powercraft.api.building.PCBuild;
import com.rumaruka.powercraft.api.building.PCBuild.ItemStackSpawn;
import com.rumaruka.powercraft.api.reflect.PCReflect;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PCUtils {

    private static PCUtils instance;
    private static MinecraftServer server;
    public static final int BLOCK_NOTIFY = 1, BLOCK_UPDATE=2,BLOCK_ONLY_SERVERSIDE=4;
    public static final int WILDCARD_VALUE = Short.MAX_VALUE;
    private Block block;


    PCUtils() throws InstanceAlreadyExistsException{
        if(instance!=null){
            throw new InstanceAlreadyExistsException();
        }
        instance=this;
    }

    public static <T>T as(Object obj,Class<T> c){
        if(obj!=null&&c.isAssignableFrom(obj.getClass())){
            return c.cast(obj);
        }
        return null;
    }

    public static TileEntity getTileEntity(IBlockAccess w, BlockPos pos){
        return w.getTileEntity(pos);
    }
    public static <T>T getTileEntity(IBlockAccess w,BlockPos pos,Class<T> c){
        return as(w.getTileEntity(pos),c);
    }
    public static Block getBlock(IBlockAccess world, BlockPos pos) {
        return (Block) world.getBlockState(pos);
    }

    public static <T> T getBlock(IBlockAccess world, BlockPos pos, Class<T> c) {
        return as(world.getBlockState(pos), c);
    }

    public static Block getBlock(IBlockAccess world, PCVec3I pos) {
        return getBlock(world, new BlockPos(pos.x, pos.y, pos.z));
    }

    public static <T> T getBlock(IBlockAccess world, PCVec3I pos, Class<T> c) {
        return getBlock(world, new BlockPos(pos.x, pos.y, pos.z), c);
    }

    public static Block getBlock(String name) {
        return getBlock(name, Block.class);
    }

    public static ResourceLocation getBlockSID(Block block) {


        return Block.REGISTRY.getNameForObject(block);
    }

    public static <T> T getBlock(String modId, String name, Class<T> c) {
        return as(Block.REGISTRY.getObject(new ResourceLocation(modId + ":" + name)), c);
    }

    public static <T> T getBlock(String name, Class<T> c) {
        return as(Block.REGISTRY.getObject(new ResourceLocation(name)), c);
    }

    public static boolean setBlock(World world, BlockPos pos, Block block, int metadata, int flag) {
        return world.setBlockState(pos, (IBlockState) block, metadata);
    }

    public static boolean setBlock(World world, BlockPos pos, Block block, int metadata) {
        return setBlock(world, pos, block, metadata, BLOCK_NOTIFY | BLOCK_UPDATE);
    }

    public static boolean setBlock(World world, BlockPos pos, Block block) {
        return setBlock(world, pos, block, 0);
    }

    public static boolean setBlock(World world, PCVec3I pos, Block block, int metadata, int flag) {
        return setBlock(world, new BlockPos(pos.x, pos.y, pos.z), block, metadata, flag);
    }

    public static boolean setBlock(World world, PCVec3I pos, Block block, int metadata) {
        return setBlock(world, pos, block, metadata, BLOCK_NOTIFY | BLOCK_UPDATE);
    }

    public static boolean setBlock(World world, PCVec3I pos, Block block) {
        return setBlock(world, pos, block, 0);
    }

    public static boolean setAir(World world, BlockPos pos) {
        return setBlock(world,pos, Blocks.AIR);
    }

    public static boolean setAir(World world, PCVec3I pos) {
        return setBlock(world, pos, Blocks.AIR);
    }

    public static boolean isServer() {
        return getSide() == PCSide.SERVER;
    }

    public static boolean isClient() {
        return getSide() == PCSide.CLIENT;
    }
    public static PCSide getSide() {
        return instance.iGetSide();
    }
    public static int getDimension(World world) {
        return world.provider.getDimension();
    }

    @SuppressWarnings("static-method")
    PCSide iGetSide() {
        return PCSide.SERVER;
    }
    public static Item getItem(ItemStack itemStack) {
        return itemStack.getItem();
    }

    public static <T> T getItem(ItemStack itemStack, Class<T> c) {
        return as(getItem(itemStack), c);
    }

    public static Item getItem(String name) {
        return getItem(name, Item.class);
    }

    public static Item getItemForBlock(Block block) {
        return Item.getItemFromBlock(block);
    }

    public static <T> T getItem(String modId, String name, Class<T> c) {
        return as(Item.REGISTRY.getObject(new ResourceLocation(modId + ":" + name)), c);
    }

    public static <T> T getItem(String name, Class<T> c) {
        return as(Item.REGISTRY.getObject(new ResourceLocation(name)), c);
    }

    public static PCModule getActiveModule() {
        ModContainer container = Loader.instance().activeModContainer();
        Object mod = container.getMod();
        if (mod instanceof PCModule) {
            return (PCModule) mod;
        }
        return null;
    }

    public static void spawnItem(World world, double x, double y, double z, ItemStack itemStack) {
        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && itemStack != null) {
            spawnItemChecked(world, x, y, z, itemStack);
        }
    }

    public static void spawnItem(World world, PCVec3 pos, ItemStack itemStack) {
        spawnItem(world, pos.x, pos.y, pos.z, itemStack);
    }

    public static void spawnItems(World world, double x, double y, double z, List<ItemStack> itemStacks) {
        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && itemStacks != null) {
            for (ItemStack itemStack : itemStacks) {
                if (itemStack != null) {
                    spawnItemChecked(world, x, y, z, itemStack);
                }
            }
        }
    }

    public static void spawnItems(World world, PCVec3 pos, List<ItemStack> itemStack) {
        spawnItems(world, pos.x, pos.y, pos.z, itemStack);
    }

    public static void spawnItems(World world, List<ItemStackSpawn> itemStacks) {
        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && itemStacks != null) {
            for (ItemStackSpawn itemStack : itemStacks) {
                if (itemStack != null) {
                    spawnItemChecked(world, itemStack.pos.x, itemStack.pos.y, itemStack.pos.z, itemStack.itemStack);
                }
            }
        }
    }

    private static void spawnItemChecked(World world, double x, double y, double z, ItemStack itemStack) {
        float f = 0.7F;
        double d0 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5;
        double d1 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5;
        double d2 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5;
        EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
        entityitem.setPickupDelay(10);
        world.spawnEntity(entityitem);
    }
    public static ModContainer getActiveMod() {
        return Loader.instance().activeModContainer();
    }

    public static File getPowerCraftFile(String directory, String f) {
        File file = instance.iGetPowerCraftFile();
        if (!file.exists())
            file.mkdir();
        if (directory != null) {
            file = new File(file, directory);
            if (!file.exists())
                file.mkdir();
        }
        if(f==null)
            return file;
        return new File(file, f);
    }
    @SuppressWarnings("static-method")
    File iGetPowerCraftFile() {
        return mcs().getFile("PowerCraft");
    }
    public static void staticClassConstructor() {
        Class<?> caller = PCReflect.getCallerClass();
        throw new InstantiationError(caller+" is a static class, therefore there can't be an instance");
    }



    public static MinecraftServer mcs() {

      return server.getServer();
    }
    public static CreativeTabs[] getCreativeTabsFor(CreativeTabs creativeTab, PCModule module) {
        List<CreativeTabs> creativeTabList = new ArrayList<CreativeTabs>();
        creativeTabList.add(creativeTab);
        if(!creativeTabList.contains(module.getCreativeTab()))
            creativeTabList.add(module.getCreativeTab());
        if(!creativeTabList.contains(PCApi.instance.getCreativeTab()))
            creativeTabList.add(PCApi.instance.getCreativeTab());
        return creativeTabList.toArray(new CreativeTabs[creativeTabList.size()]);
    }

}
