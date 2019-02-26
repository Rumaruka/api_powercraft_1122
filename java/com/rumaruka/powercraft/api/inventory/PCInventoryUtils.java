package com.rumaruka.powercraft.api.inventory;

import com.rumaruka.powercraft.api.PCDirection;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.PCVec3I;
import com.rumaruka.powercraft.api.item.IItemPC;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class PCInventoryUtils {

    public static void loadInventoryFromNBT(IInventory inventory, NBTTagCompound nbtTagCompound, String key) {

        if(inventory instanceof IInventorySetNoMark){
            IInventorySetNoMark isnm = (IInventorySetNoMark)inventory;
            NBTTagList list = nbtTagCompound.getTagList(key, 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbtTagCompound2 = list.getCompoundTagAt(i);
                isnm.setInventorySlotContentsNoMark(nbtTagCompound2.getInteger("slot"), new ItemStack(nbtTagCompound2));
            }
        }else{
            NBTTagList list = nbtTagCompound.getTagList(key, 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbtTagCompound2 = list.getCompoundTagAt(i);
                inventory.setInventorySlotContents(nbtTagCompound2.getInteger("slot"), new ItemStack(nbtTagCompound2));
            }
        }
    }


    public static void saveInventoryToNBT(IInventory inventory, NBTTagCompound nbtTagCompound, String key) {

        NBTTagList list = new NBTTagList();
        int size = inventory.getSizeInventory();
        for (int i = 0; i < size; i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack.isEmpty()) {
                NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
                nbtTagCompound2.setInteger("slot", i);
                itemStack.writeToNBT(nbtTagCompound2);
                list.appendTag(nbtTagCompound2);
            }
        }
        nbtTagCompound.setTag(key, list);
    }

    public static int getSlotStackLimit(IInventory inventory, int i){
        if(inventory instanceof IInventoryPC){
            return ((IInventoryPC)inventory).getSlotStackLimit(i);
        }
        return inventory.getInventoryStackLimit();
    }

    public static void onTick(IInventory inventory, World world) {
        int size = inventory.getSizeInventory();
        for(int i=0; i<size; i++){
            ItemStack itemStack = inventory.getStackInSlot(i);
            if(itemStack.isEmpty()){
                Item item = itemStack.getItem();
                if(item instanceof IItemPC){
                    ((IItemPC)item).onTick(itemStack, world, inventory, i);
                }
            }
        }
    }

    public static IInventory getInventoryFrom(Object obj) {
        if(obj instanceof IInventoryProvider){
            return ((IInventoryProvider)obj).getInventory();
        }else if(obj instanceof IInventory){
            return (IInventory)obj;
        }else if(obj instanceof EntityPlayer){
            return ((EntityPlayer)obj).inventory;
        }else if(obj instanceof EntityLiving){
            return new PCWrapperInventory(((EntityLiving)obj).getActiveItemStack());
        }
        return null;
    }

    public static ItemStack tryToStore(World world, int x, int y, int z, PCDirection to, ItemStack itemstack) {
        IInventory inventory = getInventoryAt(world, x, y, z);
        if(inventory!=null){
            if(storeItemStackToInventoryFrom(inventory, itemstack, to)==0)
                return null;
        }
        return itemstack;
    }

    public static IInventory getBlockInventoryAt(IBlockAccess world, int x, int y, int z) {
        IInventory inv = getInventoryFrom(PCUtils.getTileEntity(world, new BlockPos(x, y, z)));
        if(inv != null){
            Block block = PCUtils.getBlock(world, new BlockPos(x, y, z));
            final Block[] chests = {Blocks.CHEST, Blocks.TRAPPED_CHEST};
            for(Block chest:chests){
                if(block==chest){
                    if (PCUtils.getBlock(world, new BlockPos(x-1, y, z)) == chest) {
                        inv = new InventoryLargeChest("Large chest", (IInventory) PCUtils.getTileEntity(world,new BlockPos( x-1, y, z), inv);
                    }else if (PCUtils.getBlock(world,new BlockPos( x+1, y, z) )== chest) {
                        inv = new InventoryLargeChest("Large chest", inv, (IInventory) PCUtils.getTileEntity(world, new BlockPos(x+1, y, z));
                    }else if (PCUtils.getBlock(world, new BlockPos(x, y, z-1)) == chest) {
                        inv = new InventoryLargeChest("Large chest", (IInventory) PCUtils.getTileEntity(world,new BlockPos( x, y, z-1), inv);
                    }else if (PCUtils.getBlock(world,new BlockPos( x, y, z+1)) == chest) {
                        inv = new InventoryLargeChest("Large chest", inv, (IInventory) PCUtils.getTileEntity(world, new BlockPos(x, y, z+1));
                    }
                }
            }
        }
        return inv;
    }



    private static class EntitySelector implements IEntitySelector{

        private boolean livingEnabled;

        public EntitySelector(boolean livingEnabled) {
            this.livingEnabled = livingEnabled;
        }


        public boolean isEntityApplicable(Entity entity) {
            return (this.livingEnabled || !(entity instanceof EntityLivingBase)) && getInventoryFrom(entity)!=null;
        }

    }

    public static IInventory getEntityInventoryAt(World world, int x, int y, int z){
        return getEntityInventoryAt(world, x, y, z, true);
    }

    public static IInventory getEntityInventoryAt(World world, int x, int y, int z, boolean livingEnabled){
        List<?> list = world.getEntitiesWithinAABBExcludingEntity(new EntitySelector(livingEnabled),

               new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)
                        .expand(0.6D, 0.6D, 0.6D) );

        if (list.size() >= 1) {
            return getInventoryFrom(list.get(0));
        }

        return null;
    }

    public static IInventory getInventoryAt(World world, int x, int y, int z) {
        IInventory invAt = getBlockInventoryAt(world, x, y, z);

        if (invAt != null) {
            return invAt;
        }

        return getEntityInventoryAt(world, x, y, z);
    }

    public static IInventory getInventoryAt(World world, PCVec3I pos) {
        return getInventoryAt(world, pos.x, pos.y, pos.z);
    }

    public static IInventory getInventoryAt(World world, int x, int y, int z, boolean livingEnabled) {
        IInventory invAt = getBlockInventoryAt(world, x, y, z);

        if (invAt != null) {
            return invAt;
        }

        return getEntityInventoryAt(world, x, y, z, livingEnabled);
    }

    public static IInventory getInventoryAt(World world, PCVec3I pos, boolean livingEnabled) {
        return getInventoryAt(world, pos.x, pos.y, pos.z, livingEnabled);
    }

    public static int[] getInvIndexesForSide(IInventory inv, PCDirection side){
        if(side==null)
            return null;
        EnumFacing sideID = side.toForgeDirection();
        if(inv instanceof ISidedInventory){
            return ((ISidedInventory) inv).getSlotsForFace(sideID);
        }
        return null;
    }

    public static int[] makeIndexList(int start, int end){
        int[] indexes = new int[end-start];
        for(int i=0; i<indexes.length; i++){
            indexes[i] = i+start;
        }
        return indexes;
    }

    public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack){
        return getFirstEmptySlot(inv, itemstack, (int[])null);
    }

    public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack, PCDirection side){
        return getFirstEmptySlot(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack, int[] indexes){
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                if (inv.getStackInSlot(i) == null && inv.isItemValidForSlot(i, itemstack) && getSlotStackLimit(inv, i)>0) {
                    return i;
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                if (inv.getStackInSlot(i) == null && inv.isItemValidForSlot(i, itemstack) && getSlotStackLimit(inv, i)>0) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack){
        return getSlotWithPlaceFor(inv, itemstack, (int[])null);
    }

    public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack, PCDirection side){
        return getSlotWithPlaceFor(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack, int[] indexes){
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    if(slot.isItemEqual(itemstack) && getMaxStackSize(slot, inv, i)>slot.getMaxStackSize()){
                        if(inv.isItemValidForSlot(i, itemstack))
                            return i;
                    }
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    if(slot.isItemEqual(itemstack) && getMaxStackSize(slot, inv, i)>slot.getMaxStackSize()){
                        if(inv.isItemValidForSlot(i, itemstack))
                            return i;
                    }
                }
            }
        }
        return getFirstEmptySlot(inv, itemstack, indexes);
    }

    /**
     * @see PCInventoryUtils#storeItemStackToInventoryFrom(IInventory, ItemStack, int[])
     */
    public static int storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack){
        return storeItemStackToInventoryFrom(inv, itemstack, (int[])null);
    }

    /**
     * @see PCInventoryUtils#storeItemStackToInventoryFrom(IInventory, ItemStack, int[])
     */
    public static int storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack, PCDirection side){
        return storeItemStackToInventoryFrom(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    /**
     * @param inv
     * @param itemstack
     * @param indexes
     * @return
     * <br>
     * <br>
     * 0 -> itemStack entirely stored<br>
     * 1 -> itemStack partially stored<br>
     * 2 -> no slot for itemStack<br>
     * <br>
     * previously returned true now means 0 (-> check on 0 for same result)<br>
     */
    public static int storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack, int[] indexes){
        int output=2;
        while(itemstack.getMaxStackSize()>0){
            int slot = getSlotWithPlaceFor(inv, itemstack, indexes);
            if(slot<0)
                break;
            storeItemStackToSlot(inv, itemstack, slot);
            output=1;
        }
        if(itemstack.getMaxStackSize()==0) return 0;
        return output;
    }

    public static boolean storeItemStackToSlot(IInventory inv, ItemStack itemstack, int i){
        ItemStack slot = inv.getStackInSlot(i);
        if (slot.isEmpty()) {
            int store = getMaxStackSize(itemstack, inv, i);
            if(store>itemstack.getMaxStackSize()){
                store = itemstack.getMaxStackSize();
            }
            slot = itemstack.copy();
            slot.setCount(store);
            itemstack.setCount(--store)  ;
        }else{
            if(slot.isItemEqual(itemstack)){
                int store = getMaxStackSize(itemstack, inv, i);
                store -= slot.getMaxStackSize();
                if(store>0){
                    if(store>itemstack.getMaxStackSize()){
                        store = itemstack.getMaxStackSize();
                    }
                    itemstack.setCount(--store)  ;
                    slot.setCount(++store);
                }
            }
        }
        inv.setInventorySlotContents(i, slot);
        return itemstack.getMaxStackSize()==0;
    }

    public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack){
        return getInventorySpaceFor(inv, itemstack, (int[])null);
    }

    public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack, PCDirection side){
        return getInventorySpaceFor(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack, int[] indexes){
        int space=0;
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if(itemstack.isEmpty()){
                    if (slot.isEmpty()) {
                        space += getSlotStackLimit(inv, i);
                    }
                }else{
                    int slotStackLimit = getMaxStackSize(itemstack, inv, i);
                    if (slot.isEmpty()) {
                        if(slot.isItemEqual(itemstack) && slotStackLimit>slot.getMaxStackSize()){
                            if(inv.isItemValidForSlot(i, itemstack)){
                                space += slotStackLimit-slot.getMaxStackSize();
                            }
                        }
                    }else{
                        space += slotStackLimit;
                    }
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if(itemstack==null){
                    if (slot == null) {
                        space += getSlotStackLimit(inv, i);
                    }
                }else{
                    int slotStackLimit = getMaxStackSize(itemstack, inv, i);
                    if (slot != null) {
                        if(slot.isItemEqual(itemstack) && slotStackLimit>slot.stackSize){
                            if(inv.isItemValidForSlot(i, itemstack)){
                                space += slotStackLimit-slot.stackSize;
                            }
                        }
                    }else{
                        space += slotStackLimit;
                    }
                }
            }
        }
        return space;
    }

    public static int getInventoryCountOf(IInventory inv, ItemStack itemstack){
        return getInventoryCountOf(inv, itemstack, (int[])null);
    }

    public static int getInventoryCountOf(IInventory inv, ItemStack itemstack, PC_Direction side){
        return getInventoryCountOf(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    public static int getInventoryCountOf(IInventory inv, ItemStack itemstack, int[] indexes){
        int count=0;
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    if(itemstack==null){
                        count += slot.stackSize;
                    }else{
                        if(slot.isItemEqual(itemstack)){
                            count += slot.stackSize;
                        }
                    }
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    if(itemstack==null){
                        count += slot.stackSize;
                    }else{
                        if(slot.isItemEqual(itemstack)){
                            count += slot.stackSize;
                        }
                    }
                }
            }
        }
        return count;
    }

    public static int getInventoryFreeSlots(IInventory inv){
        return getInventoryFreeSlots(inv, (int[])null);
    }

    public static int getInventoryFreeSlots(IInventory inv, PC_Direction side){
        return getInventoryFreeSlots(inv, getInvIndexesForSide(inv, side));
    }

    public static int getInventoryFreeSlots(IInventory inv, int[] indexes){
        int freeSlots=0;
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if (slot == null) {
                    freeSlots++;
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if (slot == null) {
                    freeSlots++;
                }
            }
        }
        return freeSlots;
    }

    public static int getInventoryFullSlots(IInventory inv){
        return getInventoryFullSlots(inv, (int[])null);
    }

    public static int getInventoryFullSlots(IInventory inv, PC_Direction side){
        return getInventoryFullSlots(inv, getInvIndexesForSide(inv, side));
    }

    public static int getInventoryFullSlots(IInventory inv, int[] indexes){
        int fullSlots=0;
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    fullSlots++;
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    fullSlots++;
                }
            }
        }
        return fullSlots;
    }

    public static void moveStacks(IInventory from, IInventory to){
        moveStacks(from, (int[])null, to, (int[])null);
    }

    public static void moveStacks(IInventory from, PC_Direction fromSide, IInventory to, PC_Direction toSide) {
        moveStacks(from, getInvIndexesForSide(from, fromSide), to, toSide);
    }

    public static void moveStacks(IInventory from, int[] indexes, IInventory to, PC_Direction toSide) {
        moveStacks(from, indexes, to, getInvIndexesForSide(to, toSide));
    }

    public static void moveStacks(IInventory from, PC_Direction fromSide, IInventory to, int[] indexes) {
        moveStacks(from, getInvIndexesForSide(from, fromSide), to, indexes);
    }

    public static void moveStacks(IInventory from, int[] fromIndexes, IInventory to, int[] toIndexes) {
        if(fromIndexes==null){
            int size = from.getSizeInventory();
            for (int i = 0; i < size; i++) {
                if (from.getStackInSlot(i) != null) {

                    storeItemStackToInventoryFrom(to, from.getStackInSlot(i), toIndexes);

                    if (from.getStackInSlot(i) != null && from.getStackInSlot(i).stackSize <= 0) {
                        from.setInventorySlotContents(i, null);
                    }
                }
            }
        }else{
            for (int j = 0; j < fromIndexes.length; j++) {
                int i=fromIndexes[j];
                if (from.getStackInSlot(i) != null) {

                    storeItemStackToInventoryFrom(to, from.getStackInSlot(i), toIndexes);

                    if (from.getStackInSlot(i) != null && from.getStackInSlot(i).stackSize <= 0) {
                        from.setInventorySlotContents(i, null);
                    }
                }
            }
        }
    }

    public static ItemStack[] groupStacks(ItemStack[] input) {
        List<ItemStack> list = stacksToList(input);
        groupStacks(list);
        return stacksToArray(list);
    }

    public static void groupStacks(List<ItemStack> input) {
        if (input == null) {
            return;
        }

        for (ItemStack st1 : input) {
            if (st1 != null) {
                for (ItemStack st2 : input) {
                    if (st2 != null && st2.isItemEqual(st1)) {
                        int movedToFirst = Math.min(st2.stackSize, st1.getMaxStackSize()
                                - st1.stackSize);

                        if (movedToFirst <= 0) {
                            break;
                        }

                        st1.stackSize += movedToFirst;
                        st2.stackSize -= movedToFirst;
                    }
                }
            }
        }

        ArrayList<ItemStack> copy = new ArrayList<ItemStack>(input);

        for (int i = copy.size() - 1; i >= 0; i--) {
            if (copy.get(i) == null || copy.get(i).stackSize <= 0) {
                input.remove(i);
            }
        }
    }

    public static List<ItemStack> stacksToList(ItemStack[] stacks) {
        ArrayList<ItemStack> myList = new ArrayList<ItemStack>();
        Collections.addAll(myList, stacks);
        return myList;
    }

    public static ItemStack[] stacksToArray(List<ItemStack> stacks) {
        return stacks.toArray(new ItemStack[stacks.size()]);
    }

    public static void dropInventoryContent(IInventory inventory, World world, double x, double y, double z) {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
            int size = inventory.getSizeInventory();
            for (int i = 0; i < size; i++) {
                if(inventory instanceof PC_IInventory){
                    if(!((PC_IInventory)inventory).canDropStack(i))
                        continue;
                }
                ItemStack itemStack = inventory.getStackInSlot(i);
                if (itemStack != null) {
                    inventory.setInventorySlotContents(i, null);
                    PC_Utils.spawnItem(world, x, y, z, itemStack);
                }
            }
        }
    }

    public static int useFuel(IInventory inv, World world, PC_Vec3 pos) {
        return useFuel(inv, (int[])null, world, pos);
    }

    public static int useFuel(IInventory inv, PC_Direction side, World world, PC_Vec3 pos) {
        return useFuel(inv, getInvIndexesForSide(inv, side), world, pos);
    }

    public static int useFuel(IInventory inv, int[] indexes, World world, PC_Vec3 pos) {
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack is = inv.getStackInSlot(i);
                int fuel = PC_Utils.getBurnTime(is);
                if (fuel > 0) {
                    inv.decrStackSize(i, 1);
                    ItemStack container = is.getItem().getContainerItem(is);
                    if (container != null) {
                        storeItemStackToInventoryFrom(inv, container, indexes);
                        if (container.stackSize > 0) {
                            PC_Utils.spawnItem(world, pos, container);
                        }
                    }
                    return fuel;
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack is = inv.getStackInSlot(i);
                int fuel = PC_Utils.getBurnTime(is);
                if (fuel > 0) {
                    inv.decrStackSize(i, 1);
                    ItemStack container = is.getItem().getContainerItem(is);
                    if (container != null) {
                        storeItemStackToInventoryFrom(inv, container, indexes);
                        if (container.stackSize > 0) {
                            PC_Utils.spawnItem(world, pos, container);
                        }
                    }
                    return fuel;
                }
            }
        }
        return 0;
    }

    public static ItemStack decreaseStackSize(ItemStack[] inventoryContents, int slot, int amount){
        if (inventoryContents[slot] == null) return null;
        ItemStack itemstack;
        if (inventoryContents[slot].stackSize <= amount) {
            itemstack = inventoryContents[slot];
            inventoryContents[slot] = null;
            return itemstack;
        }
        itemstack = inventoryContents[slot].splitStack(amount);
        if (inventoryContents[slot].stackSize == 0) {
            inventoryContents[slot] = null;
        }
        return itemstack;
    }

    public static ItemStack getStackInSlot(ItemStack[] inventoryContents, int slot){
        if(inventoryContents==null || inventoryContents.length<=slot) return null;
        return inventoryContents[slot];
    }

    public static int getMaxStackSize(ItemStack itemStack, IInventory inv, int i){
        if(inv instanceof PC_IInventorySizeOverrider){
            return ((PC_IInventorySizeOverrider)inv).getMaxStackSize(itemStack, i);
        }
        int maxStack = itemStack.getMaxStackSize();
        int slotStack = getSlotStackLimit(inv, i);
        return maxStack>slotStack?slotStack:maxStack;
    }


    public static int getMaxStackSize(ItemStack itemStack, Slot slot) {
        if(slot.inventory instanceof PC_IInventorySizeOverrider){
            return ((PC_IInventorySizeOverrider)slot.inventory).getMaxStackSize(itemStack, slot.getSlotIndex());
        }
        int maxStack = itemStack.getMaxStackSize();
        int maxSlot = slot.getSlotStackLimit();
        return maxStack>maxSlot?maxSlot:maxStack;
    }

    public static boolean itemStacksEqual(ItemStack one, ItemStack two){
        if(one == null && two == null) return true;
        if(one == null || two == null) return false;
        if(!(one.getItem()==two.getItem())) return false;
        if(one.getItemDamage()==PC_Utils.WILDCARD_VALUE || two.getItemDamage()==PC_Utils.WILDCARD_VALUE)
            return true;
        if(!(one.getItemDamage()==two.getItemDamage())) return false;
        return ItemStack.areItemStackTagsEqual(one, two);
    }

    private PC_InventoryUtils(){
        PC_Utils.staticClassConstructor();
    }

}
