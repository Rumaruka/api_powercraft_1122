package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.reflect.PCReflect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;

public class PCUtils {

    private static PCUtils instance;
    private static MinecraftServer server;
    public static final int BLOCK_NOTIFY = 1, BLOCK_UPDATE=2,BLOCK_ONLY_SERVERSIDE=4;
    public static final int WILDCARD_VALUE = Short.MAX_VALUE;


    PCUtils() throws InstanceAlreadyExistsException{
        if(instance!=null){
            throw new InstanceAlreadyExistsException();
        }
        instance=this;
    }

    public static <T>T as(Object obj,Class<T> c){
        if(obj!=null&&c.isAssignableFrom(obj.getClass())){
            return c.cast(obj);
        }
        return null;
    }

    public static TileEntity getTileEntity(IBlockAccess w, BlockPos pos){
        return w.getTileEntity(pos);
    }
    public static <T>T getTileEntity(IBlockAccess w,BlockPos pos,Class<T> c){
        return as(w.getTileEntity(pos),c);
    }


    public static PCModule getActiveModule() {
        ModContainer container = Loader.instance().activeModContainer();
        Object mod = container.getMod();
        if (mod instanceof PCModule) {
            return (PCModule) mod;
        }
        return null;
    }

    public static ModContainer getActiveMod() {
        return Loader.instance().activeModContainer();
    }

    public static File getPowerCraftFile(String directory, String f) {
        File file = instance.iGetPowerCraftFile();
        if (!file.exists())
            file.mkdir();
        if (directory != null) {
            file = new File(file, directory);
            if (!file.exists())
                file.mkdir();
        }
        if(f==null)
            return file;
        return new File(file, f);
    }
    @SuppressWarnings("static-method")
    File iGetPowerCraftFile() {
        return mcs().getFile("PowerCraft");
    }
    public static void staticClassConstructor() {
        Class<?> caller = PCReflect.getCallerClass();
        throw new InstantiationError(caller+" is a static class, therefore there can't be an instance");
    }



    public static MinecraftServer mcs() {

      return server.getServer();
    }

}
