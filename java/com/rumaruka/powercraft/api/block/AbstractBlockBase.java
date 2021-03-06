package com.rumaruka.powercraft.api.block;

import com.rumaruka.powercraft.api.IPC3DRotation;
import com.rumaruka.powercraft.api.PCBlockTemperatures;
import com.rumaruka.powercraft.api.PCModule;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.beam.EnumBeamHitResult;
import com.rumaruka.powercraft.api.beam.IBeam;
import com.rumaruka.powercraft.api.redstone.IRedstoneConnectable;
import com.rumaruka.powercraft.api.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


public abstract class AbstractBlockBase extends Block implements IRedstoneConnectable {

    private static final CreativeTabs[] NULLCREATIVTABS = {};

    private final ModContainer module;
    private CreativeTabs[] creativeTabs = NULLCREATIVTABS;
    private boolean constructed;
    public AbstractBlockBase(Material materialIn) {
        super(materialIn);
        PCBlocks.addBlock(this);
        this.module= PCUtils.getActiveMod();
    }

    public abstract boolean canRotate();

    public abstract boolean canRotate(IBlockAccess world, int x, int y, int z);
    @SuppressWarnings("static-method")
    public Class<? extends PCItemBlock> getItemBlock(){
        return PCItemBlock.class;
    }

    @SuppressWarnings("static-method")
    public Object[] getItemBlockConstructorData(){
        return null;
    }


    public String getRegisterName() {
        return getClass().getSimpleName();
    }

    public String getTextureFolderName() {
        return getClass().getSimpleName().replaceAll("PC.*(Block)?", "");
    }

    public PCModule getModule() {
        return (PCModule) module.getMod();
    }
    @SuppressWarnings("static-method")
    public String[] getOreNames(){
        return null;
    }

    @Override
    public Block setCreativeTab(CreativeTabs tab) {
        if(tab==null){
            this.creativeTabs = NULLCREATIVTABS;
            super.setCreativeTab(null);

            if(this.constructed){
                this.creativeTabs = PCUtils.getCreativeTabsFor(tab, getModule());
                super.setCreativeTab(getModule().getCreativeTab());
            }else{
                this.creativeTabs = new CreativeTabs[]{tab};
            }
        }
        return this;

    }


    void construct(){
        Object[] itemBlockConstructorData = getItemBlockConstructorData();
        if(itemBlockConstructorData==null)
            itemBlockConstructorData = new Object[0];
        setRegistryName(getRegisterName());
        BlockRegistry.registerBlock(this, getItemBlock(), getRegisterName(), itemBlockConstructorData);
        String[] oreNames = getOreNames();
        if(oreNames!=null){
            for(String oreName:oreNames){
                OreDictionary.registerOre(oreName, this);
            }
        }
        this.constructed = true;
        if(this.creativeTabs.length>0)
            setCreativeTab(this.creativeTabs[0]);
    }

    public void initRecipes(){
        //
    }
    @SuppressWarnings({ "static-method", "unused" })
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB box, List<AxisAlignedBB> list, @Nullable Entity entityIn, boolean isActualState) {
        List<AxisAlignedBB> boxes = getCollisionBoundingBoxes(world, pos, entityIn);
        if(boxes==null){
            AxisAlignedBB b = getBoundingBox(state,world, pos);
            if(b.intersects(box)){
                list.add(b);
            }
        }else{
            for(AxisAlignedBB b:boxes){
                b = PCUtils.rotateAABB(world, pos.getX(),pos.getY(),pos.getZ(), b).offset(pos);
                if(b.intersects(box)){
                    list.add(b);
                }
            }
        }
    }
    public List<AxisAlignedBB> getCollisionBoundingBoxes(World world, BlockPos pos, Entity entity){
        return null;
    }


    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return super.getBoundingBox(state, source, pos);
    }

    public abstract IPC3DRotation getRotation(IBlockAccess world, int x, int y, int z);
    @SuppressWarnings("static-method")
    public int getTemperature() {
        return PCBlockTemperatures.DEFAULT_TEMPERATURE;
    }
    @SuppressWarnings({ "static-method", "unused" })
    public int getTemperature(World world, int x, int y, int z) {
        return PCBlockTemperatures.DEFAULT_TEMPERATURE;
    }

    public abstract void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider);

    @SuppressWarnings({ "static-method", "unused" })
    public EnumBeamHitResult onHitByBeam(World world, int x, int y, int z, IBeam beam){
        return EnumBeamHitResult.STANDARD;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean canBeHarvested(World world, int x, int y, int z) {
        return false;
    }

}
