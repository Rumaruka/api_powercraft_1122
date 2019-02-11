package com.rumaruka.powercraft.api.multiblock;

import com.rumaruka.powercraft.api.IPCNBT;
import com.rumaruka.powercraft.api.PCDirection;
import com.rumaruka.powercraft.api.PCField;
import com.rumaruka.powercraft.api.PCField.Flag;
import com.rumaruka.powercraft.api.PCNBTTagHandler;
import com.rumaruka.powercraft.api.grid.IGridTile;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.network.packet.PCPacketMultiblockObjectSync;
import com.rumaruka.powercraft.api.reflect.PCProcessor;
import com.rumaruka.powercraft.api.reflect.PCReflect;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.List;

public abstract class PCMultiblockObject implements IPCNBT {

    private boolean sync;

    protected PCMultiblockIndex index;
    protected PCTileEntityMultiblock multiblock;
    @PCField(flags={Flag.SAVE, Flag.SYNC})
    protected int thickness;

    public PCMultiblockObject(NBTTagCompound tagCompound, Flag flag) {
        readFromNBT(tagCompound, flag);
    }

    public PCMultiblockObject(int thickness) {
        this.thickness = thickness;
    }

    public PCTileEntityMultiblock getTileEntity() {
        return this.multiblock;
    }


    public int getThickness() {
        return this.thickness;
    }

    public boolean isClient() {
        return this.multiblock.isClient();
    }

    @SuppressWarnings("static-method")
    public List<AxisAlignedBB> getCollisionBoundingBoxes() {
        return null;
    }
    public AxisAlignedBB getSelectedBoundingBox() {
        AxisAlignedBB aabb = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        List<AxisAlignedBB> list = getCollisionBoundingBoxes();
        if(list==null){
            return aabb;
        }
        aabb = list.get(0);
        aabb = new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
        for(AxisAlignedBB e:list){
            if(aabb.minX>e.minX){
                aabb.minX = e.minX;
            }
            if(aabb.minY>e.minY){
                aabb.minY = e.minY;
            }
            if(aabb.minZ>e.minZ){
                aabb.minZ = e.minZ;
            }
            if(aabb.maxX<e.maxX){
                aabb.maxX = e.maxX;
            }
            if(aabb.maxY<e.maxY){
                aabb.maxY = e.maxY;
            }
            if(aabb.maxZ<e.maxZ){
                aabb.maxZ = e.maxZ;
            }
        }
        return aabb;
    }

    @SuppressWarnings("static-method")
    public List<ItemStack> getDrop() {
        return null;
    }

    public void onPreRemove() {
        //
    }

    public void onRemoved() {
        //
    }

    @SuppressWarnings("unused")
    public void onClicked(EntityPlayer player) {
        //
    }

    public ItemStack getPickBlock() {
        return new ItemStack(PCMultiblocks.getItem(this), 1, 0);
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean onBlockActivated(EntityPlayer player) {
        return false;
    }

    @SuppressWarnings("unused")
    public void onNeighborBlockChange(Block neighbor) {
        onChange();
    }

    @SuppressWarnings({ "static-method", "unused" })
    public float getPlayerRelativeHardness(EntityPlayer player) {
        return 0;
    }

    public void fillWithRain() {
        //
    }

    @SuppressWarnings("static-method")
    public int getLightValue() {
        return 0;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean isLadder(EntityLivingBase entity) {
        return false;
    }

    @SuppressWarnings("static-method")
    public boolean isBurning() {
        return false;
    }

    @SuppressWarnings("static-method")
    public float getEnchantPowerBonus() {
        return 0;
    }

    @SuppressWarnings("unused")
    public void onNeighborTEChange(int tileX, int tileY, int tileZ) {
        onChange();
    }


    @SuppressWarnings("static-method")
    public boolean isSolid() {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean canConnectRedstone(PCDirection side) {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean canMixWith(PCMultiblockObject multiblockObject) {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public PCMultiblockObject mixWith(PCMultiblockObject multiblockObject) {
        return null;
    }

    public void setIndexAndMultiblock(PCMultiblockIndex index, PCTileEntityMultiblock multiblock) {
        this.index = index;
        this.multiblock = multiblock;
    }

    @SuppressWarnings("static-method")
    public boolean onAdded() {
        return true;
    }

    public void updateObject() {
        if (!isClient() && this.sync) {
            PCPacketHandler.sendToAllAround(getSyncPacket(), getWorld(), this.multiblock.xCoord, this.multiblock.yCoord, this.multiblock.zCoord, 32);
            this.sync = false;
        }
    }

    public final PCPacket getSyncPacket(){
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        makeSync(nbtTagCompound);
        return new PCPacketMultiblockObjectSync(this, nbtTagCompound);
    }

    public final void makeSync(NBTTagCompound nbtTagCompound) {
        writeToNBT(nbtTagCompound, Flag.SYNC);
    }

    public void onChunkUnload() {
        //
    }

    public void markDirty(){
        this.multiblock.markDirty();
    }

    @SuppressWarnings({ "hiding" })
    public boolean isUsing(PCMultiblockIndex index, PCMultiblockObject multiblockObject){
        return this.index == index && !canMixWith(multiblockObject);
    }

    public World getWorld(){
        return this.multiblock.getWorld();
    }

    public void sync(){
        if (!isClient())
            this.sync = true;
    }

    @Override
    public void saveToNBT(NBTTagCompound tag, Flag flag) {
        writeToNBT(tag, flag);
    }

    protected final void readFromNBT(final NBTTagCompound nbtTagCompound, final Flag flag){
        PCReflect.processFields(this, new PCProcessor(){

            @Override
            public void process(Field field, Object value, EnumMap<Result, Object> results) {
                PCField info = field.getAnnotation(PCField.class);
                if(info!=null && flag.isIn(info)){
                    String name = info.name();
                    if(name.isEmpty()){
                        name = field.getName();
                    }
                    Class<?> type = field.getType();
                    Object nvalue = PCNBTTagHandler.loadFromNBT(nbtTagCompound, name, type, flag);
                    results.put(Result.SET, nvalue);
                }
            }

        });
        onLoadedFromNBT(flag);
    }

    @SuppressWarnings("unused")
    protected void onLoadedFromNBT(Flag flag){
        //
    }

    protected final void writeToNBT(final NBTTagCompound nbtTagCompound, final Flag flag){
        PCReflect.processFields(this, new PCProcessor(){

            @Override
            public void process(Field field, Object value, EnumMap<Result, Object> results) {
                if(value==null)
                    return;
                PCField info = field.getAnnotation(PCField.class);
                if(info!=null && flag.isIn(info)){
                    String name = info.name();
                    if(name.isEmpty()){
                        name = field.getName();
                    }
                    PCNBTTagHandler.saveToNBT(nbtTagCompound, name, value, flag);
                }
            }

        });
    }

    public PCMultiblockIndex getIndex(){
        return this.index;
    }

    public final void applySync(NBTTagCompound nbtTagCompound) {
        readFromNBT(nbtTagCompound, Flag.SYNC);
        this.multiblock.renderUpdate();
    }

    public void onInternalChange() {
        onChange();
    }

    public void onChange(){
        //
    }

    @SuppressWarnings({ "static-method", "unused" })
    public int getRedstonePowerValue(PCDirection side) {
        return 0;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean canProvideStrongPower(PCDirection side) {
        return true;
    }

    @SuppressWarnings("unused")
    public <T extends IGridTile<?, T, ?, ?>> T getGridTile(int flags, Class<T> tileClass){
        if(tileClass.isAssignableFrom(getClass())){
            return tileClass.cast(this);
        }
        return null;
    }

}
