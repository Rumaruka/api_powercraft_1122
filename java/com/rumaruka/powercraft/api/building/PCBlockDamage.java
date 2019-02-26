package com.rumaruka.powercraft.api.building;

import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.reflect.PCFields;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public final class PCBlockDamage extends PCWorldSaveData implements PCTickHandler.ITickHandler {

    private static final String NAME = "powercraft-blockdamage";

    private List<PCVec4I>updated = new ArrayList<PCVec4I>();

    private HashMap<PCVec4I,float[]>damages = new HashMap<PCVec4I, float[]>();

    private static PCBlockDamage instance;


    public static PCBlockDamage getInstance() {
        if(instance==null){
            instance=loadOrCreate(NAME,PCBlockDamage.class);
        }
        return instance;
    }

    public PCBlockDamage(String name) {
        super(name);
        PCTickHandler.registerTickHandler(this);
    }

    @Override
    public void onStartTick(PCSide side) {
        if(side==PCSide.SERVER){
            this.updated.clear();
        }
    }

    @Override
    public void onEndTick(PCSide side) {
        if(side==PCSide.SERVER){
            Iterator<PCVec4I>i = this.damages.keySet().iterator();
            while (i.hasNext()){
                PCVec4I vec4I = i.next();
                if(!this.updated.contains(vec4I)){
                    i.remove();
                    PCPacketHandler.sendToAllAround(new PCPacketBlockBreaking(vec4I.x, vec4I.y, vec4I.z, -1), vec4I.w, vec4I.x, vec4I.y, vec4I.z, 32);
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
            PCNBTTagHandler.loadMapFromNBT(nbt,"damages",this.damages,PCVec4I.class,float[].class, PCField.Flag.SAVE);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        PCNBTTagHandler.saveMapToNBT(compound,"damages",this.damages, PCField.Flag.SAVE);
        return compound ;
    }

    @Override
    public void cleanup() {
        instance=null;
        PCTickHandler.removeTickHander(this);
    }
    public static boolean damageBlock(World world, int x, int y, int z, float amount){
        if(world.isRemote)
            return false;
        PCVec4I v4 = new PCVec4I(x, y, z, world.provider.getDimension());
        getInstance();
        float[] damage = instance.damages.get(v4);
        int pd = -1;
        if(damage==null || damage.length!=2){
            damage = new float[2];
            PCHarvest harvest = PCBuild.getHarvest(world, x, y, z, -1);
            if(harvest==null){
                damage[1] = 1;
            }else{
                damage[1] = harvest.digTimeMultiply;
            }
            instance.damages.put(v4, damage);
        }else{
            pd = (int)damage[0];
        }
        damage[0] += amount/damage[1];
        instance.markDirty();
        int npd = (int)damage[0];
        if(damage[0]>=10){
            instance.updated.remove(v4);
            instance.damages.remove(v4);
            instance.markDirty();
            PCPacketHandler.sendToAllAround(new PCPacketBlockBreaking(x, y, z, -1), v4.w, x, y, z, 32);
            PCHarvest harvest = PCBuild.getHarvest(world, x, y, z, -1);
            List<PCBuild.ItemStackSpawn> list = PCBuild.harvestWithDropPos(world, harvest, 0);
            PCUtils.spawnItems(world, list);
            return true;
        }
        if(pd!=npd)
            PCPacketHandler.sendToAllAround(new PCPacketBlockBreaking(x, y, z, npd), v4.w, x, y, z, 32);
        if(!instance.updated.contains(v4))
            instance.updated.add(v4);
        return false;
    }@SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    static void setClientDamage(int x, int y, int z, int damage){
        Map<Number, DestroyBlockProgress> damagedBlocks = PCFields.Client.RenderGlobal_damagedBlocks.getValue(PCClientUtils.mc().renderGlobal);
        int cloudTickCounter = PCFields.Client.RenderGlobal_cloudTickCounter.getValue(PCClientUtils.mc().renderGlobal);
        PosDamage pd = new PosDamage(x, y, z);
        if(damage==-1){
            damagedBlocks.remove(pd);
        }else{
            DestroyBlockProgress destroyBlockProgress = damagedBlocks.get(pd);
            if(destroyBlockProgress==null){
                destroyBlockProgress = new DestroyBlockProgress(0, new BlockPos( x, y, z));
                damagedBlocks.put(pd, destroyBlockProgress);
            }
            destroyBlockProgress.setPartialBlockDamage(damage);
            destroyBlockProgress.setCloudUpdateTick(cloudTickCounter);
        }
    }

    @SideOnly(Side.CLIENT)
    private static class PosDamage extends Number{

        private static final long serialVersionUID = -3611831358033254957L;

        private int x;
        private int y;
        private int z;

        public PosDamage(int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public double doubleValue() {
            return -1;
        }

        @Override
        public float floatValue() {
            return -1;
        }

        @Override
        public int intValue() {
            return -1;
        }

        @Override
        public long longValue() {
            return -1;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.x;
            result = prime * result + this.y;
            result = prime * result + this.z;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PosDamage other = (PosDamage) obj;
            if (this.x != other.x) return false;
            if (this.y != other.y) return false;
            if (this.z != other.z) return false;
            return true;
        }

    }
}
