package com.rumaruka.powercraft.api.block;

import com.rumaruka.powercraft.api.PCDirection;
import com.rumaruka.powercraft.api.PCModule;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.item.IItemPC;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class PCItemBlock extends ItemBlock implements IItemPC {
    final static ThreadLocal<EntityPlayer> playerStetting = new ThreadLocal<EntityPlayer>();

    public PCItemBlock(Block block) {
        super(block);
    }

    @Override
    public CreativeTabs[] getCreativeTabs(){
        if(this.field_150939_a instanceof AbstractBlockBase){
            return ((AbstractBlockBase)this.field_150939_a).getCreativeTabs();
        }
        return super.getCreativeTabs();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @SuppressWarnings("unused")
    public int getMetadata(World world, ItemStack itemStack) {
        return getMetadata(itemStack);
    }

    public int getMetadata(ItemStack itemStack) {
        return getMetadata(itemStack.getItemDamage());
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        return super.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
    }

    @Override
    public void onTick(ItemStack itemStack, World world, IInventory inventory, int slot) {
        //
    }

    @Override
    public float updateDigSpeed(ItemStack itemStack, float speed, int x, int y, int z, EntityPlayer entityPlayer){
        return speed;
    }

    @Override
    public int getBurnTime(ItemStack fuel) {
        return 0;
    }




    @Override
    public void construct() {
        //
    }

    @Override
    public PCModule getModule() {
        return ((AbstractBlockBase)this.field_150939_a).getModule();
    }

    @Override
    public String getRegisterName() {
        return ((AbstractBlockBase)this.field_150939_a).getRegisterName();
    }

    @Override
    public String getTextureFolderName() {
        return ((AbstractBlockBase)this.field_150939_a).getTextureFolderName();
    }

    @Override
    public String[] getOreNames() {
        return ((AbstractBlockBase)this.field_150939_a).getOreNames();
    }




    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        ((AbstractBlockBase)this.field_150939_a).addInformation(stack, worldIn, tooltip, flagIn);
    }
}
