package com.rumaruka.powercraft.api.block;

import com.rumaruka.powercraft.api.PCDirection;
import com.rumaruka.powercraft.api.PCModule;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.redstone.IRedstoneConnectable;
import com.rumaruka.powercraft.api.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class AbstractBlockBase extends Block implements IRedstoneConnectable {
    private static final CreativeTabs[] NULLCREATIVTABS = {};

    private final ModContainer module;
    private CreativeTabs[] creativeTabs = NULLCREATIVTABS;
    private boolean constructed;

    public AbstractBlockBase(Material materialIn) {
        super(materialIn);
        this.module = PCUtils.getActiveMod();
    }
    AbstractBlockBase(EnumBlockType type) {
        this(type.material);
    }

    @Override
    public Block setCreativeTab(CreativeTabs tab) {
        if(creativeTabs==null){
            this.creativeTabs=NULLCREATIVTABS;
            super.setCreativeTab(null);
        }
        else {
            if(this.constructed){
                this.creativeTabs=PCUtils.getCreativeTabsFor(tab,getModule());
                super.setCreativeTab(getModule().getCreativeTab());
            }
            else {
                this.creativeTabs=new CreativeTabs[]{tab};
            }
        }
        return this;
    }

    public PCModule getModule() {
        return (PCModule) this.module.getMod();
    }
    @SuppressWarnings("static-method")
    public Object[] getItemBlockConstructorData(){
        return null;
    }
    @SuppressWarnings("static-method")
    public String[] getOreNames(){
        return null;
    }
    public String getRegisterName() {
        return getClass().getSimpleName();
    }
    void construct() {
        Object[] itemBlockConstructorData = getItemBlockConstructorData();
        if(itemBlockConstructorData==null)
            itemBlockConstructorData = new Object[0];
        setUnlocalizedName(getRegisterName());
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
    @SuppressWarnings("static-method")
    public Class<? extends PCItemBlock> getItemBlock(){
        return PCItemBlock.class;
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
}
