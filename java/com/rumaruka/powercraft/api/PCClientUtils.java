package com.rumaruka.powercraft.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class PCClientUtils extends PCUtils {
    private static InheritableThreadLocal<Boolean> isClient=new InheritableThreadLocal<Boolean>();

    static {
        try {
            new PCClientUtils();
        }
        catch (InstanceAlreadyExistsException e){
            throw new RuntimeException(e);
        }
    }

    PCClientUtils() throws InstanceAlreadyExistsException {

        isClient.set(Boolean.TRUE);
        PCResourceReloadListener.register();

    }

    public static Minecraft mc() {

        return Minecraft.getMinecraft();
    }

    /**
     * get the PowerCraft file in the same folder as the mod folder is
     * @return the file
     */

    File iGetPowerCraftFile() {

        return new File(mc().mcDataDir, "PowerCraft");
    }

    /**
     * get the game type for a specific player
     * @param player the player
     * @return the game type
     */
    GameType iGetGameTypeFor(EntityPlayer player) {
        return PCFields.Client.PlayerControllerMP_currentGameType.getValue(mc().playerController);
    }

    /**
     * is this game running on client
     * @return always yes
     */
    @SuppressWarnings("hiding")
    PCSide iGetSide(){
        Boolean isClient = PCClientUtils.isClient.get();
        if(isClient==null){
            return PCSide.CLIENT;
        }else if(isClient.booleanValue()){
            return PCSide.CLIENT;
        }
        return PCSide.SERVER;
    }


    void iMarkThreadAsServer(){
        isClient.set(Boolean.FALSE);
    }


    EntityPlayer iGetClientPlayer() {
        return mc().player;
    }




    public static void spawnParicle(Particle entityFX) {
        mc().effectRenderer.addEffect(entityFX);
    }


}
