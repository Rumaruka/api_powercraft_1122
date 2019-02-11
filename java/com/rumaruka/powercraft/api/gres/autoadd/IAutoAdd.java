package com.rumaruka.powercraft.api.gres.autoadd;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IAutoAdd {

    void onCharAdded(PCStringAdd add);
}
