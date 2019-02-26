package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.PCField.Flag;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class PCNBTTagHandler {





    public static void saveToNBT(NBTTagCompound nbtTagCompound, String name, Object value, Flag flag){
        NBTBase base = getObjectNBT(value, flag);
        if(base!=null)
            nbtTagCompound.setTag(name, base);
    }

    /**
     * create a nbt for a object<b>
     * can save base types, arrays, PC_INBT instances and Enum values
     *
     * @param value the object
     * @return the generated nbt
     */
    public static NBTBase getObjectNBT(Object value, Flag flag){
        if(value==null)
            return null;
        Class<?> c = value.getClass();
        if(c==Boolean.class){
            return new NBTTagByte((byte)(((Boolean)value).booleanValue()?1:0));
        }else if(c==Byte.class){
            return new NBTTagByte(((Byte)value).byteValue());
        }else if(c==Short.class){
            return new NBTTagShort(((Short)value).shortValue());
        }else if(c==Integer.class){
            return new NBTTagInt(((Integer)value).intValue());
        }else if(c==Long.class){
            return new NBTTagLong(((Long)value).longValue());
        }else if(c==Float.class){
            return new NBTTagFloat(((Float)value).floatValue());
        }else if(c==Double.class){
            return new NBTTagDouble(((Double)value).doubleValue());
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
        }else if(c==ItemStack[].class){
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

    /**
     * loads a object from a nbttag
     * @param nbtTagCompound the nbttag
     * @param name the key
     * @param c the expected object class
     * @return the object
     */
    public static <T> T loadFromNBT(NBTTagCompound nbtTagCompound, String name, Class<T> c, Flag flag){
        NBTBase base = nbtTagCompound.getTag(name);

        return getObjectFromNBT(base, c, flag);
    }

    /**
     * loads a object from a nbt
     * @param base the nbt
     * @param c the expected object class
     * @return the object
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T getObjectFromNBT(NBTBase base, Class<T> c, Flag flag) {
        if(base==null)
            return null;
        if(base instanceof NBTTagCompound){
            if(((NBTTagCompound)base).hasNoTags()){
                return null;
            }
        }
        if(c==Boolean.class || c==boolean.class){
            return (T)Boolean.valueOf(((NBTPrimitive)base).getId()!=0);
        }else if(c==Byte.class || c==byte.class){
            return (T)Byte.valueOf(((NBTPrimitive)base).getByte());
        }else if(c==Short.class || c==short.class){
            return (T)Short.valueOf(((NBTPrimitive)base).getShort());
        }else if(c==Integer.class || c==int.class){
            return (T)Integer.valueOf(((NBTPrimitive)base).getInt());
        }else if(c==Long.class || c==long.class){
            return (T)Long.valueOf(((NBTPrimitive)base).getLong());
        }else if(c==Float.class || c==float.class){
            return (T)Float.valueOf(((NBTPrimitive)base).getFloat());
        }else if(c==Double.class || c==double.class){
            return (T)Double.valueOf(((NBTPrimitive)base).getDouble());
        }else if(c==String.class){
            return c.cast(((NBTTagString)base).getString());
        }else if(c==int[].class){
            return c.cast(((NBTTagIntArray)base).getIntArray());
        }else if(c==byte[].class){
            return c.cast(((NBTTagByteArray)base).getByteArray());
        }else if(c==String[].class){
            NBTTagList list = (NBTTagList) base;
            int size = list.tagCount();
            String[] array = new String[size];
            for(int i=0; i<size; i++){
                array[i] = list.getStringTagAt(i);
            }
            return c.cast(array);
        }else if(c==int[][].class){
            NBTTagList list = new NBTTagList();
            int size = list.tagCount();
            int[][] array = new int[size][];
            for(int i=0; i<size; i++){
                array[i] = list.getIntArrayAt(i);
            }
            return c.cast(array);
        }else if(c==double[].class){
            NBTTagList list = new NBTTagList();
            int size = list.tagCount();
            double[] array = new double[size];
            for(int i=0; i<size; i++){
                    array[i] = list.getDoubleAt(i);
            }
            return c.cast(array);
        }else if(c==float[].class){
            NBTTagList list = new NBTTagList();
            int size = list.tagCount();
            float[] array = new float[size];
            for(int i=0; i<size; i++){
                array[i] = list.getFloatAt(i);
            }
            return c.cast(array);
        }else if(c==ItemStack[].class){
            NBTTagList list = (NBTTagList) base;
            int size = list.tagCount();
            ItemStack[] array = new ItemStack[size];
            for (int i = 0; i < array.length; i++) {
                NBTTagCompound nbtTagCompound = list.getCompoundTagAt(i);
                if(nbtTagCompound.hasKey("id")){
                    array[i] = new ItemStack(nbtTagCompound);
                }
            }
            return c.cast(array);
        }else if(c.isArray()){
            NBTTagList list = (NBTTagList) base;
            int size = list.tagCount();
            Class<?> ac = c.getComponentType();
            Object array = Array.newInstance(ac, size);
            for(int i=0; i<size; i++){
                NBTBase obj = list.removeTag(0);
                list.appendTag(obj);
                Array.set(array, i, getObjectFromNBT(obj, ac, flag));
            }
            return c.cast(array);
        }else if(IPCNBT.class.isAssignableFrom(c)){
            NBTTagCompound tag = (NBTTagCompound) base;
            String cName = tag.getString("Class");
            try {
                Class<?> cc = Class.forName(cName);
                try{
                    Constructor<?> constr = cc.getConstructor(NBTTagCompound.class, Flag.class);
                    return c.cast(constr.newInstance(tag, flag));
                }catch(NoSuchMethodException e){/**/}
                Constructor<?> constr = cc.getConstructor(NBTTagCompound.class);
                return c.cast(constr.newInstance(tag));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                PCLogger.severe("Can't find class %s form NBT save", cName);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                PCLogger.severe("Class %s need constructor %s(NBTTagCompound)", cName, cName);
            } catch (SecurityException e) {
                e.printStackTrace();
                PCLogger.severe("No Permissions :(");
            } catch (InstantiationException e) {
                e.printStackTrace();
                PCLogger.severe("Class %s can't be instantionated", cName);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                PCLogger.severe("No access to constructor %s(NBTTagCompound)", cName);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                PCLogger.severe("Class %s can't get NBTTagCompound as argument", cName);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                PCLogger.severe("Error while initialize class %s", cName);
            }
            return null;
        }else if(Enum.class.isAssignableFrom(c)){
            NBTTagCompound tag = (NBTTagCompound) base;
            String eName = tag.getString("Enum");
            try {
                Class<?> ec = Class.forName(eName);
                if(Enum.class.isAssignableFrom(ec)){
                    return c.cast(Enum.valueOf((Class<? extends Enum>)ec, tag.getString("value")));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            PCLogger.severe("Can't find enum %s form NBT save", eName);
            return null;
        }else if(Serializable.class.isAssignableFrom(c)){
            try {
                ByteArrayInputStream input = new ByteArrayInputStream(((NBTTagByteArray)base).getByteArray());
                ObjectInputStream objInp = new ObjectInputStream(input);
                Object value = objInp.readObject();
                objInp.close();
                return c.cast(value);
            } catch (IOException e) {
                e.printStackTrace();
                PCLogger.severe("Error while try to load object");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                PCLogger.severe("Error while try to load object, class not found");
            }

        }
        PCLogger.severe("Can't load an unknown object");
        return null;
    }


    public static <T extends Map<E, F>, E, F> T loadMapFromNBT(NBTTagCompound nbtTagCompound, String name, T map, Class<E> ce, Class<F> cf, Flag flag){
        map.clear();
        NBTBase base = nbtTagCompound.getTag(name);

        NBTTagList list = (NBTTagList)base;
        for(int i=0; i<list.tagCount(); i++){
            NBTTagCompound com = list.getCompoundTagAt(i);
            map.put(loadFromNBT(com, "key", ce, flag), loadFromNBT(com, "value", cf, flag));
        }
        return map;
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

    private PCNBTTagHandler() {
        PCUtils.staticClassConstructor();
    }
}
