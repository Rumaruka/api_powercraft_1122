package com.rumaruka.powercraft.api.reflect;

import com.rumaruka.powercraft.api.PCUtils;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Map;

public class PCFields {

    private PCFields(){
        PCUtils.staticClassConstructor();
    }
    public static final int INDEX_NetHandlerPlayServer_field_147372_n = 13;
    public static final int INDEX_EntityLivingBase_recentlyHit = 31;
    public static final int INDEX_EntityXPOrb_closestPlayer = 5;
    public static final int INDEX_EntityXPOrb_xpTargetColor = 6;
    public static final int INDEX_RenderGlobal_cloudTickCounter = 19;
    public static final int INDEX_RenderGlobal_damagedBlocks = 29;
    public static final int INDEX_RenderGlobal_destroyBlockIcons = 31;
    public static final int INDEX_PlayerControllerMP_isHittingBlock = 9;
    public static final int INDEX_PlayerControllerMP_currentGameType = 10;
    public static final int INDEX_GuiMainMenu_splashText = 3;
    public static final int INDEX_GuiMainMenu_notificationLine1 = 8;
    public static final int INDEX_GuiMainMenu_notificationLine2 = 9;
    public static final int INDEX_GuiMainMenu_notificationLink = 10;
    public static final int INDEX_ShapedOreRecipe_width = 4;

    public static final PCReflectionField<NetHandlerPlayServer, IntHashMap> NetHandlerPlayServer_field_147372_n = new PC_ReflectionField<NetHandlerPlayServer, IntHashMap>(NetHandlerPlayServer.class, INDEX_NetHandlerPlayServer_field_147372_n, IntHashMap.class);
    public static final PCReflectionField<EntityLivingBase, Integer> EntityLivingBase_recentlyHit = new PCReflectionField<EntityLivingBase, Integer>(EntityLivingBase.class, INDEX_EntityLivingBase_recentlyHit, int.class);
    public static final PCReflectionField<EntityXPOrb, EntityPlayer> EntityXPOrb_closestPlayer = new PCReflectionField<EntityXPOrb, EntityPlayer>(EntityXPOrb.class, INDEX_EntityXPOrb_closestPlayer, EntityPlayer.class);
    public static final PCReflectionField<EntityXPOrb, Integer> EntityXPOrb_xpTargetColor = new PCReflectionField<EntityXPOrb, Integer>(EntityXPOrb.class, INDEX_EntityXPOrb_xpTargetColor, int.class);
    public static final PCReflectionField<ShapedOreRecipe, Integer> ShapedOreRecipe_width = new PCReflectionField<ShapedOreRecipe, Integer>(ShapedOreRecipe.class, INDEX_ShapedOreRecipe_width, int.class);


    @SideOnly(Side.CLIENT)
    public static final class Client{
        public static final PCReflectionField<RenderGlobal, Integer> RenderGlobal_cloudTickCounter = new PCReflectionField<RenderGlobal, Integer>(RenderGlobal.class, INDEX_RenderGlobal_cloudTickCounter, int.class);
        @SuppressWarnings("rawtypes")
        public static final PCReflectionField<RenderGlobal, Map> RenderGlobal_damagedBlocks = new PCReflectionField<RenderGlobal, Map>(RenderGlobal.class, INDEX_RenderGlobal_damagedBlocks, Map.class);
        public static final PCReflectionField<PlayerControllerMP, Boolean> PlayerControllerMP_isHittingBlock = new PCReflectionField<PlayerControllerMP, Boolean>(PlayerControllerMP.class, INDEX_PlayerControllerMP_isHittingBlock, boolean.class);
        public static final PCReflectionField<PlayerControllerMP, GameType> PlayerControllerMP_currentGameType = new PCReflectionField<PlayerControllerMP, GameType>(PlayerControllerMP.class, INDEX_PlayerControllerMP_currentGameType, GameType.class);
        public static final PCReflectionField<GuiMainMenu, String> GuiMainMenu_splashText = new PCReflectionField<GuiMainMenu, String>(GuiMainMenu.class, INDEX_GuiMainMenu_splashText, String.class);
        public static final PCReflectionField<GuiMainMenu, String> GuiMainMenu_notificationLine1 = new PCReflectionField<GuiMainMenu, String>(GuiMainMenu.class, INDEX_GuiMainMenu_notificationLine1, String.class);
        public static final PCReflectionField<GuiMainMenu, String> GuiMainMenu_notificationLine2 = new PCReflectionField<GuiMainMenu, String>(GuiMainMenu.class, INDEX_GuiMainMenu_notificationLine2, String.class);
        public static final PCReflectionField<GuiMainMenu, String> GuiMainMenu_notificationLink = new PCReflectionField<GuiMainMenu, String>(GuiMainMenu.class, INDEX_GuiMainMenu_notificationLink, String.class);
    }
}
