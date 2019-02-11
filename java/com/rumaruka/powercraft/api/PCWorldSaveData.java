package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.reflect.PCReflect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.List;

public abstract class PCWorldSaveData extends WorldSavedData {


    private static List<PCWorldSaveData>datas = new ArrayList<PCWorldSaveData>();


    public PCWorldSaveData(String name) {
        super(name);
    }
    public void cleanup(){
        //
    }

    static void onServerStopping(){
        for(PCWorldSaveData data:datas){
            data.cleanup();
        }
        datas.clear();
    }

    @SuppressWarnings("unchecked")
    public static <T extends PCWorldSaveData> T loadOrCreate(String name, Class<T> c){
        T t = (T) PCUtils.mcs().getWorld(0).getMapStorage().getOrLoadData(c, name);
        if(t==null){
            t = PCReflect.newInstance(c, new Class<?>[]{String.class}, name);
            if(t!=null)
                PCUtils.mcs().getWorld(0).getMapStorage().setData(name, t);
        }
        return t;
    }


}
