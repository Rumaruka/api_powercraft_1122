package com.rumaruka.powercraft.api.block;

import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.PCField.Flag;
import com.rumaruka.powercraft.api.beam.EnumBeamHitResult;
import com.rumaruka.powercraft.api.beam.IBeam;
import com.rumaruka.powercraft.api.gres.PCGresBaseWithInventory;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.reflect.PCProcessor;
import com.rumaruka.powercraft.api.reflect.PCReflect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.*;

public class PCTileEntity extends TileEntity {

    private static WeakHashMap<EntityPlayer, Session> sessions = new WeakHashMap<EntityPlayer, Session>();
    private static WeakHashMap<PCTileEntity, List<PCGresBaseWithInventory>> containers = new WeakHashMap<PC_TileEntity, List<PC_GresBaseWithInventory>>();

    private static class Session {

        private static Random sessionRand = new Random();

        final int dimension;
        final int x;
        final int y;
        final int z;
        final long session;

        Session(PCTileEntity tileEntity) {
            this.session = sessionRand.nextLong();
            this.dimension = PCUtils.getDimension(tileEntity.getWorld());
            this.x = tileEntity.pos.getX();
            this.y = tileEntity.pos.getY();
            this.z = tileEntity.pos.getZ();
        }

    }

    private String owner;
    private String password;

    private long session;

    protected boolean sync = false;

    @PCField(flags = { Flag.SAVE, Flag.SYNC })
    private int redstoneValue;

    @PCField(flags = { Flag.SAVE })
    protected PCRedstoneWorkType workWhen;

    public PCTileEntity(){
        PCRedstoneWorkType[] types = getAllowedRedstoneWorkTypes();
        if(types==null || types.length==0){
            this.workWhen = null;
        }else{
            this.workWhen = types[0];
        }
    }

    public boolean isClient() {

        if (this.world == null)
            return PCUtils.isClient();
        return this.world.isRemote;
    }

    public void onBreak() {
        if (this instanceof IGridHolderPC) {
            ((IGridHolderPC) this).removeFromGrid();
        }
    }

    @SuppressWarnings("static-method")
    public float getHardness() {
        return Float.NaN;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick() {
        //
    }

    @SuppressWarnings("unused")
    public void onNeighborBlockChange(Block neighbor) {
        int newRedstoneValue = PCUtils.getRedstoneValue(this.world, this.pos);
        updateRedstone(newRedstoneValue);
    }

    protected void updateRedstone(int newRedstoneValue) {
        if (newRedstoneValue != this.redstoneValue) {
            onRedstoneValueChanging(newRedstoneValue, this.redstoneValue);
            this.redstoneValue = newRedstoneValue;
        }
    }

    protected void onRedstoneValueChanging(int newValue, int oldValue) {
        if (this.workWhen == null)
            return;
        switch (this.workWhen) {
            case ON_FLANK:
                if ((newValue == 0 && oldValue != 0) || (newValue != 0 && oldValue == 0)) {
                    startWorking();
                    doWork();
                    stopWorking();
                }
                break;
            case ON_HI_FLANK:
                if (newValue != 0 && oldValue == 0) {
                    startWorking();
                    doWork();
                    stopWorking();
                }
                break;
            case ON_LOW_FLANK:
                if (newValue == 0 && oldValue != 0) {
                    startWorking();
                    doWork();
                    stopWorking();
                }
                break;
            case ON_OFF:
                if (newValue == 0 && oldValue != 0) {
                    startWorking();
                } else if (newValue != 0 && oldValue == 0) {
                    stopWorking();
                }
                break;
            case ON_ON:
                if (newValue != 0 && oldValue == 0) {
                    startWorking();
                } else if (newValue == 0 && oldValue != 0) {
                    stopWorking();
                }
                break;
            default:
                break;
        }
    }

    protected void startWorking() {
        //
    }

    protected void doWork() {
        //
    }

    protected void stopWorking() {
        //
    }

    public boolean isWorking() {
        return this.workWhen == PCRedstoneWorkType.ALWAYS
                || (this.redstoneValue == 0 && this.workWhen == PCRedstoneWorkType.ON_OFF)
                || (this.redstoneValue != 0 && this.workWhen == PCRedstoneWorkType.ON_ON);
    }


    public final void updateTile() {
        if (isInvalid())
            return;
        if (this instanceof PC_IGridHolder) {
            ((PC_IGridHolder) this).getGridIfNull();
        }
        if (isWorking()) {
            doWork();
        }
        onTick();
        if (!isClient() && this.sync) {
            PCPacketHandler.sendToAllAround(getSyncPacket(), this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 32);
            this.sync = false;
        }
    }

    public void onTick() {
        //
    }

    public void sync() {
        if (isClient())
            return;
        this.sync = true;
        markDirty();
    }

    public void notifyNeighbors() {
        if (this.worldObj != null)
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, getBlockType());
    }

    public void renderUpdate() {
        if (this.worldObj != null)
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public void lightUpdate() {
        if (this.worldObj != null)
            this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
    }

    @SuppressWarnings({ "static-method", "unused" })
    public float getPlayerRelativeHardness(EntityPlayer player) {
        return Float.NaN;
    }

    @SuppressWarnings("unused")
    public void onEntityWalking(Entity entity) {
        //
    }

    @SuppressWarnings("unused")
    public void onBlockClicked(EntityPlayer player) {
        //
    }

    @SuppressWarnings("unused")
    public void velocityToAddToEntity(Entity entity, Vec3 velocity) {
        //
    }

    @SuppressWarnings("static-method")
    public int getColorMultiplier() {
        return 16777215;
    }

    @SuppressWarnings("unused")
    public void onEntityCollidedWithBlock(Entity entity) {
        //
    }

    @SuppressWarnings("unused")
    public void onFallenUpon(Entity entity, float fallDistance) {
        //
    }

    public int getDamageValue() {
        return getBlockType().damageDropped(this.blockMetadata);
    }

    public void fillWithRain() {
        //
    }

    @SuppressWarnings("static-method")
    public int getLightValue() {
        return -1;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean isLadder(EntityLivingBase entity) {
        return false;
    }

    @SuppressWarnings("static-method")
    public boolean isBurning() {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public ArrayList<ItemStack> getDrops(int fortune) {
        return null;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean canCreatureSpawn(EnumCreatureType type) {
        return false;
    }

    @SuppressWarnings("static-method")
    public boolean canSustainLeaves() {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public float getExplosionResistance(Entity entity, double explosionX, double explosionY, double explosionZ) {
        return Float.NaN;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public ItemStack getPickBlock(MovingObjectPosition target) {
        return null;
    }

    @SuppressWarnings({ "static-method", "unused" })
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(EffectRenderer effectRenderer) {
        return false;
    }

    @SuppressWarnings("unused")
    public void onPlantGrow(int sourceX, int sourceY, int sourceZ) {
        //
    }

    @SuppressWarnings("static-method")
    public boolean isFertile() {
        return false;
    }

    @SuppressWarnings("static-method")
    public int getLightOpacity() {
        return -1;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean canEntityDestroy(Entity entity) {
        return true;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean isBeaconBase(int beaconX, int beaconY, int beaconZ) {
        return false;
    }

    @SuppressWarnings("static-method")
    public float getEnchantPowerBonus() {
        return 0;
    }

    @SuppressWarnings("unused")
    public void onNeighborTEChange(int tileX, int tileY, int tileZ) {
        //
    }

    @SuppressWarnings("unused")
    public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY,
                               float hitZ) {
        //
    }

    @SuppressWarnings("static-method")
    public PC_3DRotation get3DRotation() {
        return null;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public IIcon getIcon(PC_Direction side) {
        return null;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public List<AxisAlignedBB> getCollisionBoundingBoxes(Entity entity) {
        return null;
    }

    @SuppressWarnings("static-method")
    public AxisAlignedBB getMainCollisionBoundingBox() {
        return null;
    }

    @SuppressWarnings("static-method")
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox() {
        return null;
    }

    @SuppressWarnings("unused")
    public boolean onBlockActivated(EntityPlayer player, PC_Direction side) {

        if (this instanceof PC_IGresGuiOpenHandler) {

            if (!isClient()) {

                if (canDoWithoutPassword(player)) {

                    PC_Gres.openGui(player, this);

                } else if (canDoWithPassword(player)) {

                    PC_PacketHandler.sendTo(new PC_PacketPasswordRequest(this), (EntityPlayerMP) player);

                }

            }

            return true;

        }

        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public int getComparatorInput(PC_Direction side) {
        return 0;
    }

    @SuppressWarnings("unused")
    public boolean isSideSolid(PC_Direction side) {
        return getBlockType().isNormalCube(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }

    @SuppressWarnings({ "static-method", "unused" })
    public int getFlammability(PC_Direction side) {
        return 0;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean isFlammable(PC_Direction side) {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public int getFireSpreadSpeed(PC_Direction side) {
        return 0;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean isFireSource(PC_Direction side) {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean canSilkHarvest(EntityPlayer player) {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean canSustainPlant(PC_Direction side, IPlantable plantable) {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean recolourBlock(PC_Direction side, int colour) {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean shouldCheckWeakPower(PC_Direction side) {
        return getBlockType().isNormalCube();
    }

    @SuppressWarnings("static-method")
    public boolean getWeakChanges() {
        return false;
    }

    @SuppressWarnings("static-method")
    public boolean canRotate() {
        return false;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean set3DRotation(PC_3DRotation rotation) {
        return false;
    }

    @SuppressWarnings("unused")
    public void openContainer(Container container) {
        //
    }

    @SuppressWarnings("unused")
    public void closeContainer(Container container) {
        //
    }

    public void sendProgressBarUpdates() {
        //
    }

    public void sendProgressBarUpdate(int key, int value) {
        List<PC_GresBaseWithInventory> list = containers.get(this);
        if (list == null)
            return;
        for (PC_GresBaseWithInventory container : list) {
            container.sendProgressBarUpdate(key, value);
        }
    }

    public void openContainer(PCGresBaseWithInventory container) {
        List<PCGresBaseWithInventory> list = containers.get(this);
        if (list == null) {
            containers.put(this, list = new ArrayList<PCGresBaseWithInventory>());
        }
        if (!list.contains(container)) {
            list.add(container);
        }
    }

    public void closeContainer(PCGresBaseWithInventory container) {
        List<PCGresBaseWithInventory> list = containers.get(this);
        if (list == null)
            return;
        list.remove(container);
        if (list.isEmpty())
            containers.remove(this);
    }

    public void detectAndSendChanges() {
        List<PCGresBaseWithInventory> list = containers.get(this);
        if (list == null)
            return;
        for (PCGresBaseWithInventory container : list) {
            container.detectAndSendChanges();
        }
    }

    public final String getOwner() {
        return this.owner;
    }

    private final void readFromNBT(final NBTTagCompound nbtTagCompound, final Flag flag) {
        PCReflect.processFields(this, new PCProcessor() {

            @Override
            public void process(Field field, Object value, EnumMap<Result, Object> results) {
                PCField info = field.getAnnotation(PCField.class);
                if (info != null && flag.isIn(info)) {
                    String name = info.name();
                    if (name.isEmpty()) {
                        name = field.getName();
                    }
                    Class<?> type = field.getType();
                    Object nvalue = PCNBTTagHandler.loadFromNBT(nbtTagCompound, name, type, flag);
                    if(info.notNull() && nvalue==null){
                        throw new RuntimeException("Null");
                    }
                    results.put(Result.SET, nvalue);
                }
            }

        });
        onLoadedFromNBT(flag);
    }

    private final void writeToNBT(final NBTTagCompound nbtTagCompound, final Flag flag) {
        PCReflect.processFields(this, new PCProcessor() {

            @Override
            public void process(Field field, Object value, EnumMap<PCProcessor.Result, Object> results) {
                if (value == null)
                    return;
                PCField info = field.getAnnotation(PCField.class);
                if (info != null && flag.isIn(info)) {
                    String name = info.name();
                    if (name.isEmpty()) {
                        name = field.getName();
                    }
                    PCNBTTagHandler.saveToNBT(nbtTagCompound, name, value, flag);
                }
            }

        });
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        readFromNBT(nbtTagCompound, Flag.SAVE);
        if (nbtTagCompound.hasKey("owner")) {
            this.owner = nbtTagCompound.getString("owner");
            if (nbtTagCompound.hasKey("password")) {
                this.password = nbtTagCompound.getString("password");
            }
        }
        super.readFromNBT(nbtTagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        writeToNBT(nbtTagCompound, Flag.SAVE);
        if (this.owner != null) {
            nbtTagCompound.setString("owner", this.owner);
            if (this.password != null) {
                nbtTagCompound.setString("password", this.password);
            }
        }
        return nbtTagCompound;
    }

    @SuppressWarnings("unused")
    public void onLoadedFromNBT(Flag flag) {
        //
    }

    public final boolean canDoWithoutPassword(EntityPlayer player) {
        return this.owner == null || this.owner.equals(player.getGameProfile().getName()) || PCUtils.isOP(player);
    }

    public final boolean canDoWithPassword(EntityPlayer player) {
        return this.owner == null || this.owner.equals(player.getGameProfile().getName()) || this.password != null;
    }

    @SuppressWarnings("hiding")
    public final boolean checkPassword(EntityPlayer player, String password) {
        return canDoWithoutPassword(player) || (this.password != null && this.password.equals(password));
    }

    public final boolean setPassword(EntityPlayer player, String newPassword) {
        if (canDoWithoutPassword(player)) {
            if (newPassword == null) {
                this.password = null;
            } else {
                this.password = PCUtils.getMD5(newPassword);
            }
            this.owner = PCUtils.getUsername(player);
            return true;
        }
        return false;
    }

    @SuppressWarnings("hiding")
    public final boolean guiOpenPasswordReply(EntityPlayer player, String password) {
        String md5password = PCUtils.getMD5(password);
        if (checkPassword(player, md5password)) {
            PCGres.openGui(player, this);
            return true;
        }
        return false;
    }

    public long getSession() {
        if (isClient())
            return this.session;
        return 0;
    }

    public void setSession(long session) {
        if (isClient())
            this.session = session;
    }

    @SuppressWarnings("hiding")
    public long getNewSession(EntityPlayer player) {
        if (isClient())
            return 0;
        Session session = new Session(this);
        sessions.put(player, session);
        return session.session;
    }

    @SideOnly(Side.CLIENT)
    public void openPasswordGui() {
        PCGres.openClientGui(PCClientUtils.mc().player, new PCGuiPasswordInput(this), -1);
    }

    @SideOnly(Side.CLIENT)
    public void wrongPasswordInput() {
        IGresGuiPC gui = PCGres.getCurrentClientGui();
        if (gui instanceof PCGuiPasswordInput) {
            ((PCGuiPasswordInput) gui).wrongPassword(this);
        }
    }


    public final Packet getDescriptionPacket() {
        return PCPacketHandler.getPacketFrom(getSyncPacket());
    }


    public final PCPacket getSyncPacket() {
        this.sync = false;
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        makeSync(nbtTagCompound);
        return new PCPacketTileEntitySync(this, nbtTagCompound);
    }

    public final void makeSync(NBTTagCompound nbtTagCompound) {
        writeToNBT(nbtTagCompound, Flag.SYNC);
    }

    public final void applySync(NBTTagCompound nbtTagCompound) {
        if (this.world.isRemote) {
            readFromNBT(nbtTagCompound, Flag.SYNC);
        }
    }


    @SuppressWarnings("unused")
    public boolean canRedstoneConnect(PCDirection side, int faceSide) {
        return true;
    }

    @SuppressWarnings({ "unused", "static-method" })
    public int getRedstonePowerValue(PCDirection side, int faceSide) {
        return 0;
    }

    @SuppressWarnings("unused")
    public void setRedstonePowerValue(PCDirection side, int faceSide, int value) {
        updateRedstone(value);
    }

    @SuppressWarnings("hiding")
    public final void onClientMessageCheck(EntityPlayer player, NBTTagCompound nbtTagCompound, long session,
                                           boolean intern) {
        Session pSession = sessions.get(player);
        if (pSession != null && pSession.dimension == PCUtils.getDimension(this.world)
                && pSession.x == this.pos.getX() && pSession.y == this.pos.getY() && pSession.z == this.pos.getZ()
                && pSession.session == session) {
            if (intern) {
                onInternMessage(player, nbtTagCompound);
            } else {
                onMessage(player, nbtTagCompound);
            }
        } else {
            PCLogger.warning("Player %s tries to send not signated messaged", session);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
        onMessage(player, nbtTagCompound);
    }

    @SuppressWarnings("unused")
    public void onInternMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound.getInteger("type") == 0) {
            if (nbtTagCompound.hasKey("workWhen")) {
                setRedstoneWorkType(PCRedstoneWorkType.values()[nbtTagCompound.getInteger("workWhen")]);
            } else {
                setRedstoneWorkType(null);
            }
        }
    }

    @SuppressWarnings("unused")
    public void onMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
        //
    }

    public void sendMessage(NBTTagCompound nbtTagCompound) {
        if (isClient()) {
            PCPacketHandler.sendToServer(new PCPacketTileEntityMessageCTS(this, nbtTagCompound, this.session));
        } else {
            PCPacketHandler.sendToAllAround(new PCPacketTileEntityMessageSTC(this, nbtTagCompound), this.world,
                    this.pos.getX(), this.pos.getY(), this.pos.getZ(), 32);
        }
    }

    public void sendInternMessage(NBTTagCompound nbtTagCompound) {
        if (isClient()) {
            PCPacketHandler.sendToServer(new PCPacketTileEntityMessageIntCTS(this, nbtTagCompound, this.session));
        }
    }

    public void setRedstoneWorkType(PCRedstoneWorkType rwt) {
        PCRedstoneWorkType allowed[] = getAllowedRedstoneWorkTypes();
        if(allowed==null)
            return;
        for (int i = 0; i < allowed.length; i++) {
            if (allowed[i] == rwt) {
                this.workWhen = rwt;
                sync();
                markDirty();
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setInteger("type", 0);
                if (rwt != null)
                    tagCompound.setInteger("workWhen", rwt.ordinal());
                sendInternMessage(tagCompound);
            }
        }
    }

    @SuppressWarnings("static-method")
    public PCRedstoneWorkType[] getAllowedRedstoneWorkTypes() {
        return new PCRedstoneWorkType[]{null};
    }

    public PCRedstoneWorkType getRedstoneWorkType() {
        return this.workWhen;
    }

    @SuppressWarnings("unused")
    public void onAdded(EntityPlayer player) {
        if (this instanceof IGridHolderPC) {
            ((IGridHolderPC) this).getGridIfNull();
        }
    }

    @Override
    public void onChunkUnload() {
        if (this instanceof IGridHolderPC) {
            ((IGridHolderPC) this).removeFromGrid();
        }
    }

    @SuppressWarnings("static-method")
    public int getTemperature() {
        return PCBlockTemperatures.DEFAULT_TEMPERATURE;
    }

    @SuppressWarnings("unused")
    public void onPreAdded(EntityPlayer player) {
        //
    }

    @SuppressWarnings({ "static-method", "unused" })
    public boolean canProvideStrongPower(PCDirection side) {
        return true;
    }

    @SuppressWarnings({ "static-method", "unused" })
    public EnumBeamHitResult onHitByBeam(IBeam beam) {
        return EnumBeamHitResult.STANDARD;
    }
}
