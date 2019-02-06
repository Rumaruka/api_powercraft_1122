package com.rumaruka.powercraft.api;

import net.minecraftforge.fml.relauncher.Side;

public enum  PCSide {

    SERVER(Side.SERVER), CLIENT(Side.CLIENT);

    public final Side side;

    PCSide(Side side){
        this.side = side;
    }

    public static PCSide from(Side side) {
        switch (side) {
            case CLIENT:
                return CLIENT;
            case SERVER:
                return SERVER;
            default:
                return null;
        }
    }
}
