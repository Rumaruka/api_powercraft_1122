package com.rumaruka.powercraft.api.gres.font;

import com.rumaruka.powercraft.api.PCClientUtils;
import com.rumaruka.powercraft.api.PCVec2I;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PCFontRenderer {

    private PCFontTexture texture;
    private float scale = 1.0f;

    public PCFontRenderer(PCFontTexture texture){
        this.texture = texture;
    }

    public PCFontRenderer(ResourceLocation location){
        ITextureObject textureObject = PCClientUtils.mc().renderEngine.getTexture(location);
        if(textureObject instanceof PCFontTexture){
            this.texture = (PCFontTexture)textureObject;
        }
    }

    public void drawString(String text, int x, int y){
        drawString(text, x, y, this.texture, this.scale);
    }

    public void drawString(String text, int x, int y, int color){
        drawString(text, x, y, this.texture, color, this.scale);
    }

    public void drawString(String text, int x, int y, int color, boolean shadow){
        drawString(text, x, y, this.texture, color, shadow, this.scale);
    }

    public PCVec2I getStringSize(String text){
        return getStringSize(text, this.texture, this.scale);
    }

    public PCVec2I getCharSize(char c){
        return getCharSize(c, this.texture, this.scale);
    }

    public String trimStringToWidth(String text, int width){
        return trimStringToWidth(text, this.texture, width, this.scale);
    }

    public String warpStringToWidthBl(String text, int width, boolean wordSplit){
        return warpStringToWidthBl(text, this.texture, width, this.scale, wordSplit);
    }

    public List<String> warpStringToWidth(String text, int width, boolean wordSplit){
        return warpStringToWidth(text, this.texture, width, this.scale, wordSplit);
    }

    public static void drawString(String text, float x, float y, ResourceLocation location, float scale){
        drawString(text, x, y, location, -1, false, scale);
    }

    public static void drawString(String text, float x, float y, ResourceLocation location, int color, float scale){
        drawString(text, x, y, location, color, false, scale);
    }

    public static void drawString(String text, float x, float y, PCFontTexture texture, float scale){
        drawString(text, x, y, texture, -1, false, scale);
    }

    public static void drawString(String text, float x, float y, PCFontTexture texture, int color, float scale){
        drawString(text, x, y, texture, color, false, scale);
    }

    public static void drawString(String text, float x, float y, ResourceLocation location, int color, boolean shadow, float scale){
        ITextureObject textureObject = PCClientUtils.mc().renderEngine.getTexture(location);
        if(textureObject instanceof PCFontTexture){
            drawString(text, x, y, (PCFontTexture)textureObject, color, shadow, scale);
        }
    }

    public static void drawString(String text, float x, float y, PCFontTexture texture, int color, boolean shadow, float scale){
        if(shadow){
            drawStringInt(text, x+1, y+1, texture, color, true, scale);
        }
        drawStringInt(text, x, y, texture, color, false, scale);
    }

    private static void drawStringInt(String text, float x, float y, PCFontTexture texture, int color, boolean shadow, float scale){
        float s = scale;
        PCFontTexture activeTexture = texture;
        PCClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
         bufferBuilder.begin(7,DefaultVertexFormats.POSITION_TEX);
        float size = texture.getTextureSize();
        int ccolor = color;
        if ((ccolor & -67108864) == 0){
            ccolor |= -16777216;
        }

        if (shadow){
            ccolor = (ccolor & 16579836) >> 2 | ccolor & -16777216;
        }

        int red = ccolor >> 16 & 255;
        int blue = ccolor >> 8 & 255;
        int green = ccolor & 255;
        int alpha = ccolor >> 24 & 255;
        int errorRed = 0;
        int errorBlue = 0;
        int errorGreen = 0;
        boolean error = false;
        float nx = x;
        float ny = y;
        int maxY = -1;
        for(int i=0; i<text.length(); i++){
            char c = text.charAt(i);
            if(c==PCFormatter.START_SEQ && i + 1 < text.length()){
                c = text.charAt(++i);
                if(c==PCFormatter.COLOR_SEQ){
                    red = text.charAt(++i);
                    green = text.charAt(++i);
                    blue = text.charAt(++i);
                }else if(c==PCFormatter.FONT_SEQ){
                    int font = text.charAt(++i);
                    activeTexture = PCFonts.get(font);
                    tessellator.draw();
                    size = activeTexture.getTextureSize();
                    PCClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
                    bufferBuilder.begin(7,DefaultVertexFormats.POSITION_TEX);
                }else if(c==PCFormatter.SCALE_SEQ){
                    int sc = text.charAt(++i);
                    s = 1.0f/sc;
                }else if(c==PCFormatter.SCALE_SEQ){
                    int font = text.charAt(++i);
                    activeTexture = PCFonts.get(font);
                    tessellator.draw();
                    size = activeTexture.getTextureSize();
                    PCClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
                    bufferBuilder.begin(7,DefaultVertexFormats.POSITION_TEX);
                }else if(c==PCFormatter.ERROR_SEQ){
                    error = true;
                    errorRed = text.charAt(++i);
                    errorGreen = text.charAt(++i);
                    errorBlue = text.charAt(++i);
                }else if(c==PCFormatter.ERRORSTOP_SEQ){
                    error = false;
                }else{
                    red = ccolor >> 16 & 255;
                    blue = ccolor >> 8 & 255;
                    green = ccolor & 255;
                    activeTexture = texture;
                    size = activeTexture.getTextureSize();
                    s = scale;
                    tessellator.draw();
                    PCClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
                    bufferBuilder.begin(7,DefaultVertexFormats.POSITION_TEX);

                }
            }else{
                if(c=='\n'){
                    if(maxY==-1){
                        ny += activeTexture.getCharData(' ').height;
                    }else{
                        ny += maxY;
                    }
                    maxY = -1;
                }else{
                    PCCharData data = activeTexture.getCharData(c);
                    if(data!=null){
                        if(data.height>maxY){
                            maxY = data.height;
                        }
                        float tx = data.storedX/size;
                        float ty = data.storedY/size;
                        float tw = data.width/size;
                        float th = data.height/size;
                        bufferBuilder.color(red, green, blue, alpha);
                        bufferBuilder.pos(nx, ny+data.height*s, 0).tex(tx, ty+th);
                        bufferBuilder.pos(nx+data.width*s, ny+data.height*s, 0).tex(tx+tw, ty+th);
                        bufferBuilder.pos(nx+data.width*s, ny, 0).tex(tx+tw, ty);
                        bufferBuilder.pos(nx, ny, 0).tex(tx, ty);
                        if(error){
                            tessellator.draw();
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            bufferBuilder.begin(7,DefaultVertexFormats.POSITION_TEX);

                            bufferBuilder.color(errorRed, errorGreen, errorBlue, alpha);
                            bufferBuilder.pos(nx, ny+data.height*s, 0);
                            bufferBuilder.pos(nx+data.width*s, ny+data.height*s, 0);
                            bufferBuilder.pos(nx+data.width*s, ny+data.height*s-1, 0);
                            bufferBuilder.pos(nx, ny+data.height*s-1, 0);
                            tessellator.draw();
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            bufferBuilder.begin(7,DefaultVertexFormats.POSITION_TEX);
                        }
                        nx += data.width*s;
                    }
                }
            }
        }
        tessellator.draw();
    }

    public static PCVec2I getStringSize(String text, ResourceLocation location, float scale){
        ITextureObject textureObject = PCClientUtils.mc().renderEngine.getTexture(location);
        if(textureObject instanceof PCFontTexture){
            return getStringSize(text, (PCFontTexture)textureObject, scale);
        }
        return null;
    }

    public static PCVec2I getStringSize(String text, PCFontTexture texture, float scale){
        PCFontTexture activeTexture = texture;
        PCVec2I size = new PCVec2I();
        int maxY = -1;
        for(int i=0; i<text.length(); i++){
            char c = text.charAt(i);
            if(c==PCFormatter.START_SEQ && i + 1 < text.length()){
                c = text.charAt(++i);
                if(c==PCFormatter.COLOR_SEQ){
                    i+=3;
                }else if(c==PCFormatter.FONT_SEQ){
                    int font = text.charAt(++i);
                    activeTexture = PCFonts.get(font);
                }else if(c==PCFormatter.ERROR_SEQ){
                    i+=3;
                }else if(c==PCFormatter.ERRORSTOP_SEQ){
                    //
                }else{
                    activeTexture = texture;
                }
            }else{
                if(c=='\n'){
                    if(maxY==-1){
                        size.y += activeTexture.getCharData(' ').height;
                    }else{
                        size.y += maxY;
                    }
                    maxY = -1;
                }else{
                    PCCharData data = activeTexture.getCharData(c);
                    if(data!=null){
                        if(data.height>maxY)
                            maxY=data.height;
                        size.x += data.width;
                    }
                }
            }
        }
        if(maxY==-1){
            size.y += activeTexture.getCharData(' ').height;
        }else{
            size.y += maxY;
        }
        size.y *= scale;
        size.x *= scale;
        return size;
    }

    public static PCVec2I getCharSize(char c, ResourceLocation location, float scale){
        ITextureObject textureObject = PCClientUtils.mc().renderEngine.getTexture(location);
        if(textureObject instanceof PCFontTexture){
            return getCharSize(c, (PCFontTexture)textureObject, scale);
        }
        return null;
    }

    public static PCVec2I getCharSize(char c, PCFontTexture texture, float scale){
        PCCharData data = texture.getCharData(c);
        if(data==null){
            return new PCVec2I();
        }
        return new PCVec2I((int)(data.width*scale), (int)(data.height*scale));
    }

    public static String trimStringToWidth(String text, PCFontTexture texture, int width, float scale){
        int length = PCFormatter.removeFormatting(text).length();
        for(int i=1; i<=length; i++){
            int l = PCFontRenderer.getStringSize(PCFormatter.substring(text, 0, i), texture, scale).x;
            if(l>width){
                return PCFormatter.substring(text, 0, i-1);
            }
        }
        return text;
    }

    public static String warpStringToWidthBl(String text, PCFontTexture texture, int width, float scale, boolean wordSplit){
        List<String> l = warpStringToWidth(text, texture, width, scale, wordSplit);
        int size = -1;
        for(String s:l){
            size += s.length()+1;
        }
        StringBuilder sb = new StringBuilder(size);
        sb.append(l.get(0));
        for(int i=1; i<l.size(); i++){
            sb.append('\n');
            sb.append(l.get(i));
        }
        return sb.toString();
    }

    public static List<String> warpStringToWidth(String text, PCFontTexture texture, int width, float scale, boolean wordSplit){
        List<String> list = new ArrayList<String>();
        PCFontTexture activeTexture = texture;
        int sizeX = 0;
        int sizeAtBest = 0;
        int bestSplit = -1;
        int start = 0;
        boolean isWhite = false;
        for(int i=0; i<text.length(); i++){
            char c = text.charAt(i);
            if(c==PCFormatter.START_SEQ && i + 1 < text.length()){
                if(c==PCFormatter.COLOR_SEQ){
                    i+=3;
                }else if(c==PCFormatter.FONT_SEQ){
                    int font = text.charAt(++i);
                    activeTexture = PCFonts.get(font);
                }else if(c==PCFormatter.ERROR_SEQ){
                    i+=3;
                }else if(c==PCFormatter.ERRORSTOP_SEQ){
                    //
                }else{
                    activeTexture = texture;
                }
            }else{
                PCCharData data = activeTexture.getCharData(c);
                int prevSize = sizeX;
                if(data!=null){
                    sizeX += data.width;
                }
                if(!((c>='A' && c<='Z') || (c>='a' && c<='z') || (c>='0' && c<='9') || c=='_')){
                    bestSplit = i;
                    isWhite = c=='\n' || c=='\r' || c=='\t' || c==' ';
                    sizeAtBest = isWhite?sizeX:prevSize;
                }
                if(sizeX*scale>width || c=='\n'){
                    if(bestSplit==-1 && wordSplit){
                        if(start+1<i){
                            list.add(text.substring(start, i));
                            start = i;
                            bestSplit = -1;
                            sizeX -= sizeAtBest;
                        }
                    }else if(bestSplit!=-1){
                        list.add(text.substring(start, bestSplit));
                        start = isWhite?bestSplit+1:bestSplit;
                        bestSplit = -1;
                        sizeX -= sizeAtBest;
                    }
                }
            }
        }
        list.add(text.substring(start));
        return list;
    }
}
