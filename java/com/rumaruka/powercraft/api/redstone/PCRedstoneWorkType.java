package com.rumaruka.powercraft.api.redstone;

public enum PCRedstoneWorkType {

    ALWAYS,ON_ON,ON_OFF,ON_HI_FLANK,ON_LOW_FLANK,ON_FLACK;

    public static final PCRedstoneWorkType NEVER=null;
    public static final PCRedstoneWorkType[]ALL={NEVER,ALWAYS,ON_ON,ON_OFF,ON_HI_FLANK,ON_LOW_FLANK,ON_FLACK};
}
