package com.rumaruka.powercraft.api.gres.font;


import com.rumaruka.powercraft.api.*;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PCFonts implements PCResourceReloadListener.IResourceReloadLisener {

    private static List<PCFontTexture> fonts = new ArrayList<PCFontTexture>();
    private static HashMap<PCFontData, PCFontTexture> fontData = new HashMap<PCFontData, PCFontTexture>();
    private static HashMap<String, Font> loadedFonts = new HashMap<String, Font>();

    static int addFont(PCFontTexture font) {
        fonts.add(font);
        return fonts.size();
    }

    public static PCFontTexture get(int fontID) {
        return fonts.get(fontID - 1);
    }

    public static PCFontTexture getFontByName(String name) {
        return getFontByName(name, 8.0f, 0);
    }

    public static PCFontTexture getFontByName(String name, int style) {
        return getFontByName(name, 8.0f, style);
    }

    public static PCFontTexture getFontByName(String name, float size) {
        return getFontByName(name, size, 0);
    }

    public static PCFontTexture getFontByName(String name, float size, int style) {
        boolean isDefault = name.equals("Default") || name.equals("Minecraftia");
        String fontName;
        if (isDefault) {
            fontName = "Minecraftia";
        } else {
            fontName = name;
        }
        PCFontData fd = new PCFontData(fontName, size, style);
        PCFontTexture fontTexture = fontData.get(fd);
        if(fontTexture==null){
            fontData.put(fd, fontTexture = new PCFontTexture(fd));
            PCClientUtils.mc().renderEngine.loadTexture(fontTexture.getResourceLocation(), fontTexture);
        }
        return fontTexture;
    }

    public static Font getFontOrLoad(PCFontData fd) {
        Font font = getFontOrLoad(fd.name);
        return font.deriveFont(fd.style, fd.size);
    }

    private static Font getFontOrLoad(String name){
        Font font = loadedFonts.get(name);
        if(font==null){
            loadedFonts.put(name, font = loadFont(name));
        }
        return font;
    }

    private static Font loadFont(String name){
        Font font = loadFromResourcePack(name);
        if(font!=null)
            return font;
        font = getSystemFont(name);
        if(font!=null)
            return font;
        if(name.equalsIgnoreCase("Minecraftia")){
            PCLogger.severe("Default font not found, fallback to "+Font.SANS_SERIF);
            return loadFont(Font.SANS_SERIF);
        }else if(name.equalsIgnoreCase(Font.SANS_SERIF)){
            PCLogger.severe(Font.SANS_SERIF+" font not found");
            return null;
        }
        PCLogger.severe("%s font not found, fallback to Minecraftia", name);
        return loadFont("Minecraftia");
    }

    private static Font loadFromResourcePack(String name){
        ResourceLocation resourceLocation = PCUtils.getResourceLocation(PCApi.instance, "fonts/"+name+".ttf");
        InputStream inputstream = null;
        Font font = null;
        try {
            IResource resource = PCClientUtils.mc().getResourceManager().getResource(resourceLocation);
            inputstream = resource.getInputStream();
            font = Font.createFont(Font.TRUETYPE_FONT, inputstream);
            inputstream.close();
        } catch(FileNotFoundException e){
            PCLogger.warning("Font %s not found in resource pack", name);
            return null;
        } catch (Exception e) { // Do not use Java 1.7, use Java 1.6
            throw new RuntimeException(e); // Should we create a runtime Error and crash report?
        } finally {
            if (inputstream != null)
                try {
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return font;
    }

    private static Font[] getSystemFonts() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    }

    private static Font getSystemFont(String name) {
        for (Font font : getSystemFonts()) {
            if (font.getName().equalsIgnoreCase(name)) {
                return font;
            }
        }
        return null;
    }
    @Override
    public void onResourceReload() {
        loadedFonts.clear();
    }
}
