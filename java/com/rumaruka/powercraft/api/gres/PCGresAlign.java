package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PCGresAlign {


    public  enum H {
        RIGHT, CENTER, LEFT
    }

    public  enum V {
        TOP, CENTER, BOTTOM
    }

    public  enum Fill {
        NONE, VERTICAL, HORIZONTAL, BOTH;
    }

    public  enum Size {
        SELV, BIGGEST;
    }

    private PCGresAlign(){
        PCUtils.staticClassConstructor();
    }
}
