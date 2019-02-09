package com.rumaruka.powercraft.api.building;

import com.rumaruka.powercraft.api.PCVec3I;
import net.minecraft.item.ItemStack;

public class PCBuild {



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
}
