package com.rumaruka.powercraft.api.dimension;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class PCDimension {

    protected int id;
    protected Class<? extends PCWorldProvider> worldProvider;
    protected boolean keepLoaded;
    public static DimensionType dimensionType;
    public static  String dimensions_name;
    public PCDimension(Class<? extends PCWorldProvider> worldProvider){
        this.worldProvider = worldProvider;
        PCDimensions.addDimensions(this);
        this.id = worldProvider.getClass().getName().hashCode();
    }

    public PCDimension(Class<? extends PCWorldProvider> worldProvider, int id){
        this.worldProvider = worldProvider;
        PCDimensions.addDimensions(this);
        this.id = id;
    }

    public int getID(){
        return this.id;
    }

    public final Class<? extends PCWorldProvider> getWorldProvider(){
        return this.worldProvider;
    }

    public boolean keepLoaded(){
        return this.keepLoaded;
    }

    void construct() {
        dimensionType = DimensionType.register(dimensions_name,"_",getID(), getWorldProvider(), keepLoaded());
        DimensionManager.registerDimension(getID(),dimensionType);
    }
}
