package com.rumaruka.powercraft.api.gres.layot;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.PCGresContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGresLayout {
     PCVec2I getPreferredLayoutSize(PCGresContainer container);


     PCVec2I getMinimumLayoutSize(PCGresContainer container);


     void updateLayout(PCGresContainer container);

}
