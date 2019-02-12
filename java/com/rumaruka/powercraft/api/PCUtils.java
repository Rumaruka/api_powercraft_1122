package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.block.AbstractBlockBase;
import com.rumaruka.powercraft.api.building.PCBuild;
import com.rumaruka.powercraft.api.building.PCBuild.ItemStackSpawn;
import com.rumaruka.powercraft.api.reflect.PCFields;
import com.rumaruka.powercraft.api.reflect.PCReflect;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    public static AxisAlignedBB rotateAABB(IBlockAccess world, int x, int y, int z, AxisAlignedBB box) {
        Block block = getBlock(world, new BlockPos(x,y,z));
        if (block instanceof AbstractBlockBase) {
            if (((AbstractBlockBase) block).canRotate(world, x, y, z)) {
                IPC3DRotation rotation = ((AbstractBlockBase) block).getRotation(world, x, y, z);
                if (rotation != null) {
                    return rotation.rotateBox(box);
                }
            }
        }
        return box;
    }
    public static int getRedstoneValue(World world, int x, int y, int z) {
        return world.getStrongPower(new BlockPos(x, y, z));
    }

    public static GameType getGameTypeFor(EntityPlayer player) {
        return instance.iGetGameTypeFor(player);
    }

    public static boolean isCreative(EntityPlayer entityPlayer) {
        return getGameTypeFor(entityPlayer).isCreative();
    }

    public static void notifyBlockOfNeighborChange(World world, int x, int y, int z, Block neightbor) {
        Block block = getBlock(world, x, y, z);
        if (block != null) {
            block.onNeighborBlockChange(world, x, y, z, neightbor);
        }
    }

    public static void notifyBlockChange(World world, int x, int y, int z, Block block) {
        world.notifyBlockChange(x, y, z, block);
    }



    @SuppressWarnings("static-method")
    GameType iGetGameTypeFor(EntityPlayer player) {

        return ((EntityPlayerMP) player).getServer().getGameType();
    }

    public static ResourceLocation getResourceLocation(PCModule module, String file) {

        return new ResourceLocation(module.getMetadata().modId.toLowerCase(), file);
    }





    public static String getUsername(EntityPlayer player) {
        return player.getGameProfile().getName();
    }


    public static NBTTagCompound getNBTTagOf(Object obj) {
        NBTTagCompound tag;
        if (obj instanceof Entity) {
            tag = ((Entity) obj).getEntityData();
        } else if (obj instanceof ItemStack) {
            tag = ((ItemStack) obj).getTagCompound();
        } else {
            return null;
        }
        if (tag == null || !tag.hasKey("PowerCraft"))
            return null;
        return tag.getCompoundTag("PowerCraft");
    }

    public static NBTTagCompound getWritableNBTTagOf(Object obj) {
        NBTTagCompound tag;
        if (obj instanceof Entity) {
            tag = ((Entity) obj).getEntityData();
        } else if (obj instanceof ItemStack) {
            tag = ((ItemStack) obj).getTagCompound();
            if (tag == null) {
                ((ItemStack) obj).setTagCompound(tag = new NBTTagCompound());
            }
        } else {
            return null;
        }
        if (tag.hasKey("PowerCraft")) {
            return tag.getCompoundTag("PowerCraft");
        }
        NBTTagCompound pctag = new NBTTagCompound();
        tag.setTag("PowerCraft", pctag);
        return pctag;
    }

    public static EntityPlayer getClientPlayer() {
        return instance.iGetClientPlayer();
    }

    @SuppressWarnings("static-method")
    EntityPlayer iGetClientPlayer() {
        return null;
    }

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMD5(String s) {
        return new String(digest.digest(s.getBytes()));
    }
    @SuppressWarnings("unused")
    public static int getSideRotation(IBlockAccess world, int x, int y, int z, PCDirection side, int faceSide) {
        notImplementedYet("getSideRotation");
        // TODO Auto-generated method stub
        return 0;
    }

    public static int getBurnTime(ItemStack itemStack) {
        return TileEntityFurnace.getItemBurnTime(itemStack);
    }

    public static ItemStack getSmeltingResult(ItemStack itemStack) {
        if (itemStack == null)
            return null;
        ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(itemStack);
        if (smelted.isEmpty())
            return null;
        return smelted.copy();
    }

    public static Entity getEntity(World world, int entityID) {
        return world.getEntityByID(entityID);
    }

    public static <T> T getEntity(World world, int entityID, Class<T> c) {
        return as(getEntity(world, entityID), c);
    }

    public static PCVec4I averageVec4I(PCVec4I... vecs) {
        PCVec4I allInOne = PCVec4I.sum(vecs);
        int notNullNumber = 0;
        for (PCVec4I vec : vecs) {
            if (vec != null)
                if (!vec.isZero())
                    notNullNumber++;
        }
        return allInOne.divide(notNullNumber);
    }

    public static boolean isEntityFX(Entity entity) {
        return instance.iIsEntityFX(entity);
    }

    @SuppressWarnings({ "static-method", "unused" })
    boolean iIsEntityFX(Entity entity) {
        return false;
    }

    public static void deleteDirectoryOrFile(File file) {
        if (file.isDirectory()) {
            for (File c : file.listFiles()) {
                deleteDirectoryOrFile(c);
            }
        }
        file.delete();
    }

    public static int getDimensionID(World world) {
        return world.provider.getDimension();
    }

    public static int getTemperature(World world, int x, int y, int z) {
        return PCBlockTemperatures.getTemperature(world, x, y, z);
    }

    public static Biome getBiome(World world, int x, int z) {
        return world.getBiome(new BlockPos(x,0, z));
    }
    private static List<String> messages = new ArrayList<String>();
    public static void notImplementedYet(String what){
        if(!messages.contains(what)){
            messages.add(what);
            PCLogger.severe("%s not implemented yet", what);
        }
    }
    public static int getColorFor(int index) {
        return ItemDye.DYE_COLORS[index];
    }

    public static int countBits(int mask) {
        int bits = 0;
        for(int i=0; i<32; i++){
            if((mask & 1<<i)!=0){
                bits++;
            }
        }
        return bits;
    }

    public static boolean isBlockReplaceable(World world, int x, int y, int z) {
        Block block = getBlock(world, x, y, z);
        if(block.isReplaceable(world, x, y, z))
            return true;
        return block==Blocks.SNOW_LAYER||block == Blocks.VINE || block == Blocks.TALLGRASS || block == Blocks.DEADBUSH;
    }

    public static PCVec3 getLookDir(EntityPlayer player) {
        float pitch = (float) (player.rotationPitch*Math.PI/180);
        double y = -PCMathHelper.sin(pitch);
        double o = PCMathHelper.cos(pitch);
        float yaw = (float) (player.rotationYaw*Math.PI/180);
        double x = PCMathHelper.sin(-yaw)*o;
        double z = PCMathHelper.cos(-yaw)*o;
        PCVec3 lookDir = new PCVec3(x, y, z);
        return lookDir;
    }

    public static void sendMessage(EntityPlayer player, String message){
        sendMessage(player, new ChatComponentText(message));
    }

    public static void sendMessageToTranslate(EntityPlayer player, String message, Object...args){
        sendMessage(player, new ChatComponentTranslation(message, args));
    }

    public static void sendMessage(EntityPlayer player, IChatComponent chatComponent){
        player.addChatMessage(chatComponent);
    }



    @SuppressWarnings("unchecked")
    public static List<IRecipe> getRecipesForProduct(ItemStack prod) {
        List<IRecipe> recipes = new ArrayList<IRecipe>(CraftingManager.getInstance().getRecipeList());
        List<IRecipe> ret = new ArrayList<IRecipe>();

        for (IRecipe recipe : recipes) {
            ItemStack out = recipe.getRecipeOutput();
            if (PCInventoryUtils.itemStacksEqual(out, prod)){
                ret.add(recipe);
            }
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    public static List<ItemStack>[][] getExpectedInput(IRecipe recipe, int width, int hight) {
        List<ItemStack>[][] list;
        int w = width;
        int h = hight;
        if (recipe instanceof ShapedRecipes) {
            ShapedRecipes sr = (ShapedRecipes)recipe;
            int sizeX = sr.recipeWidth;
            int sizeY = sr.recipeHeight;
            ItemStack[] stacks = sr.recipeItems;
            if (w == -1)
                w = sizeX;
            if (h == -1)
                h = sizeY;
            if (sizeX > w || sizeY > h)
                return null;
            list = new List[w][h];
            int i = 0;
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    if (i < stacks.length) {
                        if (stacks[i] != null) {
                            list[x][y] = new ArrayList<ItemStack>();
                            list[x][y].add(stacks[i]);
                        }
                    }
                    i++;
                }
            }
        } else if (recipe instanceof ShapelessRecipes) {
            List<ItemStack> stacks = ((ShapelessRecipes) recipe).recipeItems;
            if (w == -1)
                w = stacks.size();
            if (h == -1)
                h = 1;
            if (h * w < stacks.size())
                return null;
            list = new List[w][h];
            int i = 0;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (i < stacks.size()) {
                        list[x][y] = new ArrayList<ItemStack>();
                        list[x][y].add(stacks.get(i));
                    }
                    i++;
                }
            }
        } else if (recipe instanceof ShapedOreRecipe){
            ShapedOreRecipe sor = (ShapedOreRecipe)recipe;
            int sizeX = PCFields.ShapedOreRecipe_width.getValue(sor).intValue();
            Object[] stacks = sor.getInput();
            int sizeY = stacks.length/sizeX;
            if (w == -1)
                w = sizeX;
            if (h == -1)
                h = sizeY;
            if (sizeX > w || sizeY > h)
                return null;
            list = new List[w][h];
            int i = 0;
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    if (i < stacks.length) {
                        list[x][y] = getItemStacksForOreItem(stacks[i]);
                    }
                    i++;
                }
            }
        } else if (recipe instanceof ShapelessOreRecipe){
            ShapelessOreRecipe sor = (ShapelessOreRecipe)recipe;
            List<Object> stacks = sor.getInput();
            if (w == -1)
                w = stacks.size();
            if (h == -1)
                h = 1;
            if (h * w < stacks.size())
                return null;
            list = new List[w][h];
            int i = 0;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (i < stacks.size()) {
                        list[x][y] = getItemStacksForOreItem(stacks.get(i));
                    }
                    i++;
                }
            }
        } else {
            return null;
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<ItemStack> getItemStacksForOreItem(Object oreItem) {
        if(oreItem instanceof ItemStack){
            List<ItemStack> list = new ArrayList<ItemStack>();
            list.add((ItemStack) oreItem);
            return list;
        }else if(oreItem instanceof List){
            return new ArrayList<ItemStack>((List<ItemStack>) oreItem);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity>List<E> getEntitiesWithinAABB(World world, AxisAlignedBB aabb, Class<E> c) {
        return world.getEntitiesWithinAABB(c, aabb);
    }

    @SuppressWarnings("unchecked")
    public static List<Entity> getEntitiesWithinAABB(World world, AxisAlignedBB aabb) {
        return world.getEntitiesWithinAABB(Entity.class, aabb);
    }

    public static AxisAlignedBB getBoundingBox(Entity entity) {
        AxisAlignedBB bb = entity.getCollisionBoundingBox();
        return bb==null?entity.getCollisionBoundingBox():bb;
    }

}
