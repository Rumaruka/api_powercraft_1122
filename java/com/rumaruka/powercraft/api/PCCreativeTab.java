package com.rumaruka.powercraft.api;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PCCreativeTab extends CreativeTabs {

    private PCModule module;

    public PCCreativeTab(String label, PCModule module) {
        super(label);
        this.module=module;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return this.module.getCreativeTabItemStack();
    }


}
