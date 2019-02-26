package com.rumaruka.powercraft.api.building;

import com.rumaruka.powercraft.api.PCDirection;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.PCVec3I;
import com.rumaruka.powercraft.api.block.AbstractBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.ArrayList;
import java.util.List;

public class PCBuild {

    private static List<ISpecialHarvesting> specialHarvestings = new ArrayList<ISpecialHarvesting>();

    public static void addSpecialHarvesting(ISpecialHarvesting specialHarvesting){
        if(!specialHarvestings.contains(specialHarvesting)){
            specialHarvestings.add(specialHarvesting);
        }
    }

    public static class ItemStackSpawn{

        public PCVec3I pos;
        public ItemStack itemStack;

        public ItemStackSpawn(int x, int y, int z, ItemStack itemStack) {
            this.pos = new PCVec3I(x, y, z);
            this.itemStack = itemStack;
        }

        public ItemStackSpawn(PCVec3I pos, ItemStack itemStack) {
            this.pos = pos;
            this.itemStack = itemStack;
        }

    }
    public static ISpecialHarvesting getSpecialHarvestingFor(World world, int x, int y, int z){
        for(int i=0; i<3; i++){
            for(ISpecialHarvesting specialHarvesting:specialHarvestings){
                if(specialHarvesting.useFor(world, x, y, z, i)){
                    return specialHarvesting;
                }
            }
        }
        return null;
    }

    public static PCHarvest getHarvest(World world, PCVec3I pos, int usesLeft){
        return getHarvest(world, pos.x, pos.y, pos.z, usesLeft);
    }

    public static PCHarvest getHarvest(World world, int x, int y, int z, int usesLeft){
        ISpecialHarvesting specialHarvesting = getSpecialHarvestingFor(world, x, y, z);
        if(specialHarvesting!=null){
            return specialHarvesting.harvest(world, x, y, z, usesLeft);
        }
        Block block = PCUtils.getBlock(world, new BlockPos(x, y, z));
        if(block==null)
            return null;
        if(!canHarvest(world, x, y, z, block)){
            return null;
        }
        PCHarvest harvest = new PCHarvest();
        harvest.positions.add(new PCVec3I(x, y, z));
        return harvest;
    }

    public static List<ItemStackSpawn> harvestWithDropPos(World world, PCHarvest harvest, int fortune){
        if(harvest==null){
            return null;
        }
        return harvestWithDropPos(world, harvest.positions, fortune);
    }

    public static List<ItemStackSpawn> harvestWithDropPos(World world, List<PCVec3I> positions, int fortune){
        List<ItemStackSpawn> drops = new ArrayList<ItemStackSpawn>();
        if(positions==null)
            return drops;
        for(PCVec3I position:positions){
            List<ItemStack> dropss = getDrops(world, position, fortune);
            for(ItemStack drop:dropss){
                drops.add(new ItemStackSpawn(position, drop));
            }

            PCUtils.setAir(world, position);
        }
        return drops;
    }

    public static List<ItemStack> harvest(World world, PCHarvest harvest, int fortune){
        return harvest(world, harvest.positions, fortune);
    }

    public static List<ItemStack> harvest(World world, List<PCVec3I> positions, int fortune){
        List<ItemStack> drops = new ArrayList<ItemStack>();
        if(positions==null)
            return drops;
        for(PCVec3I position:positions){
            drops.addAll(getDrops(world, position, fortune));

            PCUtils.setAir(world, position);
        }
        return drops;
    }

    public static List<ItemStack> getDrops(World world, PCVec3I position, int fortune){
        Block block = PCUtils.getBlock(world, position);
        if(block==null){
            return new ArrayList<ItemStack>();
        }
        List<ItemStack> list = block.getDrops(world, new BlockPos(position.x, position.y, position.z), PCUtils.getMetadata(world, position), fortune);
        if(list==null)
            return new ArrayList<ItemStack>();
        return list;
    }

    public static List<ItemStack> harvestEasy(World world, int x, int y, int z, int fortune){
        Block block = PCUtils.getBlock(world, new BlockPos(x, y, z));
        if(block==null)
            return null;
        if(!canHarvest(world, x, y, z, block)){
            return null;
        }
        List<ItemStack> drops = block.getDrops(world, new BlockPos(x, y, z), PCUtils.getMetadata(world, new BlockPos(x, y, z)), fortune);
        if(!world.isRemote)
            PCUtils.setAir(world, new BlockPos(x, y, z));
        return drops;
    }

    public static boolean canHarvest(World world, int x, int y, int z, Block block){
        if(block instanceof AbstractBlockBase)
            return ((AbstractBlockBase)block).canBeHarvested(world, x, y, z);
        return true;
    }

    public static EnumActionResult tryUseItem(World world, int x, int y, int z, PCDirection dir, ItemStack itemStack){
        if(world instanceof WorldServer)
            return itemStack.getItem().onItemUse(FakePlayerFactory.getMinecraft((WorldServer) world), world,new BlockPos( x, y, z), EnumHand.MAIN_HAND, EnumFacing.DOWN, 0,0, 0);
        return EnumActionResult.PASS;
    }

    public static EnumActionResult tryUseItem(World world, int x, int y, int z, ItemStack itemStack) {

        PCVec3I below = new PCVec3I(x, y-1, z);

        Block blockFront = PCUtils.getBlock(world, new BlockPos(x, y, z));

        Block block = blockFront;

        // try to put minecart
        if (itemStack.getItem() instanceof ItemMinecart) {


                if (!world.isRemote) {
                    world.spawnEntity(EntityMinecart.create(world, x + 0.5F, y + 0.5F, z + 0.5F,EntityMinecart.Type.RIDEABLE));
                    itemStack.splitStack(1);

                return EnumActionResult.SUCCESS;
            }
        }

        // try to place front
        if (itemStack.getItem() instanceof ItemBlock) {

            ItemBlock item = ((ItemBlock) itemStack.getItem());


                return tryUseItem(world, x, y, z, PCDirection.DOWN, itemStack);



        }

        // use on front block (usually bonemeal on crops)
        if (!PCUtils.isBlockReplaceable(world, x, y, z)) {

            return tryUseItem(world, x, y, z, PCDirection.DOWN, itemStack);

        }

        // use below
        if (PCUtils.isBlockReplaceable(world, x, y, z) && !PCUtils.isBlockReplaceable(world, below.x, below.y, below.z)) {

            return tryUseItem(world, below.x, below.y, below.z, PCDirection.UP, itemStack);

        }

        return EnumActionResult.FAIL;
    }

    private PCBuild(){
        PCUtils.staticClassConstructor();
    }

}
