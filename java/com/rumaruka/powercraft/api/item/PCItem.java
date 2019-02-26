package com.rumaruka.powercraft.api.item;

import com.rumaruka.powercraft.api.PCModule;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.gres.IGresGuiOpenHandler;
import com.rumaruka.powercraft.api.gres.PCGres;
import com.rumaruka.powercraft.api.inventory.PCInventoryUtils;
import com.rumaruka.powercraft.api.registry.ItemRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class PCItem extends Item implements IItemPC{
    private static final CreativeTabs[] NULLCREATIVTABS = {};

    private final ModContainer module;
    private CreativeTabs[] creativeTabs = NULLCREATIVTABS;
    private boolean constructed;

    public PCItem(){
        PCItems.addItem(this);
        this.module = PCUtils.getActiveMod();
    }

    @Override
    public final PCModule getModule() {
        return (PCModule)this.module.getMod();
    }

    @Override
    public String getRegisterName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getTextureFolderName() {
        return getClass().getSimpleName().replaceAll("PC.*(Item)?", "");
    }

    @Override
    public String[] getOreNames(){
        return null;
    }

    @Override
    public Item setCreativeTab(CreativeTabs creativeTab) {
        if(creativeTab==null){
            this.creativeTabs = NULLCREATIVTABS;
            super.setCreativeTab(null);
        }else{
            if(this.constructed){
                this.creativeTabs = PCUtils.getCreativeTabsFor(creativeTab, getModule());
                super.setCreativeTab(getModule().getCreativeTab());
            }else{
                this.creativeTabs = new CreativeTabs[]{creativeTab};
            }
        }
        return this;
    }

    @Override
    @SuppressWarnings("hiding")
    public final void construct() {
        PCModule module = getModule();
        setUnlocalizedName(getRegisterName());
        ItemRegister.registerItem(this, getRegisterName());
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

    @Override
    public void initRecipes(){
        //
    }

    @Override
    public CreativeTabs[] getCreativeTabs(){
        return this.creativeTabs;
    }



    @Override
    public int getBurnTime(ItemStack fuel) {
        return 0;
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int i, boolean currentItem) {
        onTick(itemStack, world, PCInventoryUtils.getInventoryFrom(entity), i);
    }

    @Override
    public void onTick(ItemStack itemStack, World world, IInventory inventory, int slot) {
        //
    }

    @Override
    public float updateDigSpeed(ItemStack itemStack, float speed, int x, int y, int z, EntityPlayer entityPlayer) {
        return speed;

    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entityPlayer, EnumHand handIn) {
        ItemStack itemStack = entityPlayer.getHeldItem(handIn);

        if(this instanceof IGresGuiOpenHandler){
            if(!world.isRemote){
                PCGres.openGui(entityPlayer, this);
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, entityPlayer.getHeldItem(handIn));
        }
        return super.onItemRightClick(world, entityPlayer,handIn);
    }
}
