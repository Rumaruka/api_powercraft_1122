package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.block.PCTileEntity;
import com.rumaruka.powercraft.api.entity.IEntityPC;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.reflect.PCSecurity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PCGres {

    private static TreeMap<String, IGresGuiOpenHandler> guiOpenHandlers = new TreeMap<String, IGresGuiOpenHandler>();
    @SideOnly(Side.CLIENT)
    private static TreeMap<String, PCGresTexture> textures;
    @SideOnly(Side.CLIENT)
    private static boolean loaded;


    public static void register() {
        PCSecurity.allowedCaller("PCGres.register()", PCApi.class);
        PCPacketHandler.registerPacket(PCPacketOpenGresHandler.class);
        PCPacketHandler.registerPacket(PCPacketOpenGresItem.class);
        PCPacketHandler.registerPacket(PCPacketOpenGresTileEntity.class);
        PCPacketHandler.registerPacket(PCPacketOpenGresEntity.class);
    }

    @SideOnly(Side.CLIENT)
    public static void openClientGui(EntityPlayer player, PCTileEntity pc_tileentiy, int windowID, NBTTagCompound tag) {
        if (pc_tileentiy instanceof IGresGuiOpenHandler) {
            IGresGui gui = ((IGresGuiOpenHandler) pc_tileentiy).openClientGui(player, tag);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void openClientGui(EntityPlayer player, IEntityPC entity, int windowId, NBTTagCompound nbtTagCompound) {

        if (entity instanceof IGresGuiOpenHandler) {
            IGresGui gui = ((IGresGuiOpenHandler) entity).openClientGui(player, nbtTagCompound);
            openClientGui(player, gui, windowId);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void openClientGui(EntityPlayer player, String guiOpenHandlerName, int windowId, NBTTagCompound nbtTagCompound) {

        IGresGuiOpenHandler guiOpenHandler = guiOpenHandlers.get(guiOpenHandlerName);
        if (guiOpenHandler != null) {
            IGresGui gui = guiOpenHandler.openClientGui(player, nbtTagCompound);
            openClientGui(player, gui, windowId);
        }
    }


    @SideOnly(Side.CLIENT)
    public static void openClientGui(EntityPlayer player, Item item, int windowId, NBTTagCompound nbtTagCompound) {

        if (item instanceof IGresGuiOpenHandler) {
            IGresGui gui = ((IGresGuiOpenHandler) item).openClientGui(player, nbtTagCompound);
            openClientGui(player, gui, windowId);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void openClientGui(EntityPlayer player, IGresGui gui, int windowId) {

        if (gui != null) {
            loadTextures();
            Minecraft mc = PCClientUtils.mc();
            mc.displayGuiScreen(new PCGresGuiScreen(gui));
            if (windowId != -1 && gui instanceof PCGresBaseWithInventory) {
                player.openContainer = (PCGresBaseWithInventory) gui;
                player.openContainer.windowId = windowId;
            }
        }

    }

    public static void openGui(EntityPlayer player, PCTileEntity tileEntity, Object... params) {

        if (player instanceof EntityPlayerMP && tileEntity instanceof IGresGuiOpenHandler) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            PCGresBaseWithInventory container = ((IGresGuiOpenHandler) tileEntity).openServerGui(player, params);
            NBTTagCompound sendToClient = ((IGresGuiOpenHandler) tileEntity).sendOnGuiOpenToClient(player, params);
            if (sendToClient == null) {
                sendToClient = new NBTTagCompound();
            }
            long session = tileEntity.getNewSession(player);
            if (container != null) {
                playerMP.getNextWindowId();
                playerMP.closeContainer();
                int windowId = playerMP.currentWindowId;
                PCPacketHandler.sendTo(new PCPacketOpenGresTileEntity(tileEntity, windowId, session, sendToClient), (EntityPlayerMP) player);
                player.openContainer = container;
                player.openContainer.windowId = windowId;
                player.openContainer.addListener(playerMP);
            } else {
                PCPacketHandler.sendTo(new PCPacketOpenGresTileEntity(tileEntity, -1, session, sendToClient), (EntityPlayerMP) player);
            }
        }
    }

    public static void openGui(EntityPlayer player, IEntityPC entity, Object... params) {

        if (player instanceof EntityPlayerMP && entity instanceof IGresGuiOpenHandler) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            PCGresBaseWithInventory container = ((IGresGuiOpenHandler) entity).openServerGui(player, params);
            NBTTagCompound sendToClient = ((IGresGuiOpenHandler) entity).sendOnGuiOpenToClient(player, params);
            if (sendToClient == null) {
                sendToClient = new NBTTagCompound();
            }
            long session = entity.getNewSession(player);
            if (container != null) {
                playerMP.getNextWindowId();
                playerMP.closeContainer();
                int windowId = playerMP.currentWindowId;
                PCPacketHandler.sendTo(new PCPacketOpenGresEntity(entity, windowId, session, sendToClient), (EntityPlayerMP) player);
                player.openContainer = container;
                player.openContainer.windowId = windowId;
                player.openContainer.addListener(playerMP);
            } else {
                PCPacketHandler.sendTo(new PCPacketOpenGresEntity(entity, -1, session, sendToClient), (EntityPlayerMP) player);
            }
        }
    }

    public static void openGui(EntityPlayer player, String guiOpenHandlerName, Object... params) {

        if (player instanceof EntityPlayerMP) {
            IGresGuiOpenHandler guiOpenHandler = guiOpenHandlers.get(guiOpenHandlerName);
            if (guiOpenHandler != null) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                PCGresBaseWithInventory container = guiOpenHandler.openServerGui(player, params);
                NBTTagCompound sendToClient = guiOpenHandler.sendOnGuiOpenToClient(player, params);
                if (sendToClient == null) {
                    sendToClient = new NBTTagCompound();
                }
                if (container != null) {
                    playerMP.getNextWindowId();
                    playerMP.closeContainer();
                    int windowId = playerMP.currentWindowId;
                    PCPacketHandler.sendTo(new PCPacketOpenGresHandler(guiOpenHandlerName, windowId, sendToClient), (EntityPlayerMP) player);
                    player.openContainer = container;
                    player.openContainer.windowId = windowId;
                    player.openContainer.addListener(playerMP);
                } else {
                    PCPacketHandler.sendTo(new PCPacketOpenGresHandler(guiOpenHandlerName, -1, sendToClient), (EntityPlayerMP) player);
                }
            }
        }
    }

    public static void openGui(EntityPlayer player, Item item, Object... params) {

        if (player instanceof EntityPlayerMP && item instanceof IGresGuiOpenHandler) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            PCGresBaseWithInventory container = ((IGresGuiOpenHandler) item).openServerGui(player, params);
            NBTTagCompound sendToClient = ((IGresGuiOpenHandler) item).sendOnGuiOpenToClient(player, params);
            if (sendToClient == null) {
                sendToClient = new NBTTagCompound();
            }
            if (container != null) {
                playerMP.getNextWindowId();
                playerMP.closeContainer();
                int windowId = playerMP.currentWindowId;
                PCPacketHandler.sendTo(new PCPacketOpenGresItem(item, windowId, sendToClient), (EntityPlayerMP) player);
                player.openContainer = container;
                player.openContainer.windowId = windowId;
                player.openContainer.addListener(playerMP);
            } else {
                PCPacketHandler.sendTo(new PCPacketOpenGresItem(item, -1, sendToClient), (EntityPlayerMP) player);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void loadTextures() {
        if (loaded) return;
        textures = new TreeMap<String, PCGresTexture>();
        loaded = true;
        final String states[] = {"loc_active", "loc_mouseOver", "loc_mouseDown", "loc_disabled"};
        IResourceManager resourceManager = PCClientUtils.mc().getResourceManager();
        try {
            IResource resource = resourceManager.getResource(PCUtils.getResourceLocation(PCApi.instance, "textures/gui/GuiDesk.xml"));
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String page = "";
            String line;
            while ((line = reader.readLine()) != null) {
                page += line + "\n";
            }
            reader.close();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(page.getBytes("UTF-8")));
            doc.getDocumentElement().normalize();
            NodeList textureNodes = ((Element) doc.getChildNodes().item(0)).getChildNodes();
            for (int i = 0; i < textureNodes.getLength(); i++) {
                Node texureNode = textureNodes.item(i);
                if (texureNode.getNodeName().equals("Texture")) {
                    Element texureElement = (Element) texureNode;
                    String textureName = texureElement.getAttribute("textureName");
                    ResourceLocation resourceLocation = PCUtils.getResourceLocation(PCApi.instance, "textures/gui/" + textureName);
                    NodeList subTextureNodes = texureNode.getChildNodes();
                    for (int j = 0; j < subTextureNodes.getLength(); j++) {
                        Node subTextureNode = subTextureNodes.item(j);
                        if (subTextureNode.getNodeName().equals("Subtexture")) {
                            Element subTextureElement = (Element) subTextureNode;
                            String name = subTextureElement.getAttribute("name");
                            PCVec2I[] others = new PCVec2I[states.length];
                            for (int k = 0; k < others.length; k++) {
                                try {
                                    others[k] = new PCVec2I(subTextureElement.getAttribute(states[k]));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    if (k == 0) {
                                        PCLogger.severe("Can't find activeLocation in xml");
                                    } else {
                                        PCLogger.warning("Can't find a specific atribute in xml");
                                        others[k] = others[0];
                                    }
                                }
                            }
                            PCVec2I size = null;
                            try {
                                size = new PCVec2I(subTextureElement.getAttribute("size"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                PCLogger.severe("Can't find size in xml");
                            }
                            PCRectI frame = null;
                            try {
                                frame = new PCRectI(subTextureElement.getAttribute("frame"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                PCLogger.severe("Can't find frame in xml");
                            }
                            if (name != null && size != null && frame != null && others[0] != null) {
                                textures.put(name, new PCGresTexture(resourceLocation, size, frame, others));
                            }
                        }
                    }
                }
            }
        } catch (IOException | org.xml.sax.SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


    }
    @SideOnly(Side.CLIENT)
    private static List<String> warnings;


    @SideOnly(Side.CLIENT)
    public static PCGresTexture getGresTexture(String name){
        PCGresTexture tex = textures.get(name);

        if(tex==null){
            warnings = new ArrayList<String>();
        }
        if(!warnings.contains(name)){
            warnings.add(name);
            PCLogger.warning("Can`t find textures %s",name);
        }
        return tex;
    }

    @SideOnly(Side.CLIENT)
    public static IGresGui getCurrentClientGui(){
        Minecraft mc = PCClientUtils.mc();
        if(mc.currentScreen instanceof PCGresGuiScreen){
            return  ((PCGresGuiScreen)mc.currentScreen).getCurrentClientGui();
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static <T>T getCurrentClientGui(Class<T> c){
        Minecraft mc = PCClientUtils.mc();
        if(mc.currentScreen instanceof PCGresGuiScreen){
            IGresGui gresGui = ((PCGresGuiScreen)mc.currentScreen).getCurrentClientGui();
            if(c.isAssignableFrom(gresGui.getClass())){
                return c.cast(gresGui);
            }
        }
        return null;
    }


    private PCGres(){
        PCUtils.staticClassConstructor();
    }


}
