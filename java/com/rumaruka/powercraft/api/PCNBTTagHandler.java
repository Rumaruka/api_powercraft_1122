package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.PCField.Flag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Map;

public final class PCNBTTagHandler {





    public static void saveToNBT(NBTTagCompound compound , String name , Object obj, Flag flag){
        NBTBase base= getObjectNBT(obj,flag);

        if(base!=null){
            compound.setTag(name,base);
        }
    }

    public static NBTBase getObjectNBT(Object value, Flag flag){
        if(value==null)
            return null;
        Class<?> c = value.getClass();
        if(c==Boolean.class){
            return new NBTTagByte((byte)(((Boolean)value)?1:0));
        }else if(c==Byte.class){
            return new NBTTagByte(((Byte)value));
        }else if(c==Short.class){
            return new NBTTagShort(((Short)value));
        }else if(c==Integer.class){
            return new NBTTagInt(((Integer)value));
        }else if(c==Long.class){
            return new NBTTagLong(((Long)value));
        }else if(c==Float.class){
            return new NBTTagFloat(((Float)value));
        }else if(c==Double.class){
            return new NBTTagDouble(((Double)value));
        }else if(c==String.class){
            return new NBTTagString((String)value);
        }else if(c==int[].class){
            return new NBTTagIntArray((int[])value);
        }else if(c==byte[].class){
            return new NBTTagByteArray((byte[])value);
        }else if(c==String[].class){
            String[] array = (String[])value;
            NBTTagList list = new NBTTagList();
            for(int i=0; i<array.length; i++){
                list.appendTag(new NBTTagString(array[i]));
            }
            return list;
        }else if(c==int[][].class){
            int[][] array = (int[][])value;
            NBTTagList list = new NBTTagList();
            for(int i=0; i<array.length; i++){
                list.appendTag(new NBTTagIntArray(array[i]));
            }
            return list;
        }else if(c==double[].class){
            double[] array = (double[])value;
            NBTTagList list = new NBTTagList();
            for(int i=0; i<array.length; i++){
                list.appendTag(new NBTTagDouble(array[i]));
            }
            return list;
        }else if(c==float[].class){
            float[] array = (float[])value;
            NBTTagList list = new NBTTagList();
            for(int i=0; i<array.length; i++){
                list.appendTag(new NBTTagFloat(array[i]));
            }
            return list;
        }else if(c== ItemStack[].class){
            ItemStack[] array = (ItemStack[])value;
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < array.length; i++) {
                ItemStack itemStack = array[i];
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                if (itemStack != null) {
                    itemStack.writeToNBT(nbtTagCompound);
                }
                list.appendTag(nbtTagCompound);
            }
            return list;
        }else if(c.isArray()){
            NBTTagList list = new NBTTagList();
            int size = Array.getLength(value);
            for(int i=0; i<size; i++){
                Object obj = Array.get(value, i);
                NBTBase base = getObjectNBT(obj, flag);
                if(base==null){
                    base = new NBTTagCompound();
                }
                list.appendTag(base);
            }
            return list;
        }else if(IPCNBT.class.isAssignableFrom(c)){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("Class", c.getName());
            ((IPCNBT)value).saveToNBT(tag, flag);
            return tag;
        }else if(Enum.class.isAssignableFrom(c)){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("Enum", c.getName());
            tag.setString("value", ((Enum<?>)value).name());
            return tag;
        }else if(Serializable.class.isAssignableFrom(c)){
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(output);
                objOut.writeObject(value);
                objOut.close();
                return new NBTTagByteArray(output.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                PCLogger.severe("Error while try to save object %s", value);
            }

        }
        PCLogger.severe("Can't save object %s form type %s", value, c);
        return null;
    }

    public static <T extends Map<?, ?>> void saveMapToNBT(NBTTagCompound nbtTagCompound, String name, T map, Flag flag){
        NBTTagList list = new NBTTagList();
        for(Map.Entry<?, ?> e:map.entrySet()){
            NBTTagCompound com = new NBTTagCompound();
            saveToNBT(com, "key", e.getKey(), flag);
            saveToNBT(com, "value", e.getValue(), flag);
            list.appendTag(com);
        }
        nbtTagCompound.setTag(name, list);
    }

    private PCNBTTagHandler(){
        PCUtils.staticClassConstructor();
    }
}
