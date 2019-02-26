package com.rumaruka.powercraft.api.item;

import com.rumaruka.powercraft.api.PCModule;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.inventory.PCInventoryUtils;
import com.rumaruka.powercraft.api.registry.ItemRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PCItemArmor extends ItemArmor implements IItemPC, ISpecialArmor {

    public static final EntityEquipmentSlot HEAD=EntityEquipmentSlot.HEAD,
            TORSO=EntityEquipmentSlot.CHEST,
            LEGS=EntityEquipmentSlot.LEGS,
            FEET=EntityEquipmentSlot.FEET;
    protected PCArmorProperties properties;
    protected String texturesName = "armor";
    private static final CreativeTabs[] NULLCREATIVTABS = {};

    private final ModContainer module;
    private CreativeTabs[] creativeTabs = NULLCREATIVTABS;
    private boolean constructed;
    public PCItemArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, int priority, double ratio, int max) {
        super(ArmorMaterial.IRON, 0, equipmentSlotIn);
        PCItems.addItem(this);
        this.module= PCUtils.getActiveMod();
        this.properties = new PCArmorProperties(priority, ratio, max);
    }



    @Override
    public void onTick(ItemStack itemStack, World world, IInventory inventory, int slot) {

    }

    @Override

    public int getBurnTime(ItemStack fuel) {
        return 0;
    }

    @Override
    @SuppressWarnings("hiding")
    public void construct() {
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
    public PCModule getModule() {
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
    public String[] getOreNames() {
        return null;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        onTick(stack, worldIn, PCInventoryUtils.getInventoryFrom(entityIn), itemSlot);
    }

    @Override
    public float updateDigSpeed(ItemStack itemStack, float speed, int x, int y, int z, EntityPlayer entityPlayer) {
        return speed;
    }

    @Override
    public void initRecipes() {

    }

    public PCArmorProperties getProperties() {
        return properties;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, int slot) {
        return null;
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, @Nonnull ItemStack armor, int slot) {
        return this.properties.AbsorbMax;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, @Nonnull ItemStack stack, DamageSource source, int damage, int slot) {

    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return getModule().getName()+":textures/items/"+getTextureFolderName()+"/"+this.texturesName+".png";
    }


}
