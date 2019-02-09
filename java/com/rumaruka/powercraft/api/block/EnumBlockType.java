package com.rumaruka.powercraft.api.block;


import com.rumaruka.powercraft.api.PCMaterial;
import net.minecraft.block.material.Material;

public enum EnumBlockType {

    MACHINE(PCMaterial.MACHINES),
    OTHER(Material.GROUND);

    public final Material material;

    EnumBlockType(Material material){
        this.material=material;
    }

}
