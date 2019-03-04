package com.rumaruka.powercraft.api.gres.font;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class PCFontTexture extends AbstractTexture {


    private ResourceLocation location;
    private int fontHeight;
    private int textureSize;
    private char[] customCharsArray = new char[0];
    private PCCharData[] charArray = new PCCharData[256];
    private Map<Character, PCCharData> customChars = new HashMap<Character, PCCharData>();
    private int fontID;
    private PCFontData fd;
    public PCFontTexture(PCFontData fd) {
        this.fontID = PCFonts.addFont(this);
        this.location = new ResourceLocation("PowerCraft", "*font" + this.fontID);
        this.fd = fd;
    }

    public int getFontID() {
        return this.fontID;
    }

    public ResourceLocation getResourceLocation() {
        return this.location;
    }

    public PCCharData getCharData(char c) {
        if (c < 256)
            return this.charArray[c];
        return this.customChars.get(c);
    }

    public int getTextureSize() {
        return this.textureSize;
    }
    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        deleteGlTexture();
        Font font = PCFonts.getFontOrLoad(this.fd);
        this.textureSize = 256;
        int lastTextureSize = 0;
        BufferedImage imgTemp = new BufferedImage(this.textureSize, this.textureSize,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = imgTemp.getGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, this.textureSize, this.textureSize);
        int rowHeight = 0;
        int positionX = 0;
        int positionY = 0;

        int customCharsLength = (this.customCharsArray != null) ? this.customCharsArray.length : 0;

        for (int i = 0; i < 256 + customCharsLength; i++) {

            // get 0-255 characters and then custom characters
            char ch = (i < 256) ? (char) i : this.customCharsArray[i - 256];

            BufferedImage fontImage = getFontImage(ch, font);

            PCCharData newIntObject = new PCCharData();

            newIntObject.width = fontImage.getWidth();
            newIntObject.height = fontImage.getHeight();

            if (positionX + newIntObject.width >= this.textureSize) {
                positionY += rowHeight;
                rowHeight = 0;
                if (positionY < lastTextureSize)
                    positionX = lastTextureSize;
                else
                    positionX = 0;
            }

            if (newIntObject.height > this.fontHeight)
                this.fontHeight = newIntObject.height;

            if (newIntObject.height > rowHeight)
                rowHeight = newIntObject.height;

            if (positionY + rowHeight >= this.textureSize) {
                lastTextureSize = this.textureSize;
                this.textureSize *= 2;
                positionY = 0;
                positionX = lastTextureSize;
                BufferedImage newImg = new BufferedImage(this.textureSize, this.textureSize,
                        BufferedImage.TYPE_INT_ARGB);
                Graphics gn = newImg.getGraphics();
                gn.setColor(new Color(0, 0, 0, 0));
                gn.fillRect(0, 0, this.textureSize, this.textureSize);
                gn.drawImage(imgTemp, 0, 0, null);
                g = gn;
                imgTemp = newImg;
            }

            newIntObject.storedX = positionX;
            newIntObject.storedY = positionY;

            // Draw it here
            g.drawImage(fontImage, positionX, positionY, null);

            positionX += newIntObject.width;

            if (i < 256)
                this.charArray[i] = newIntObject;
            else
                this.customChars.put(new Character(ch), newIntObject);

            fontImage = null;
        }

        int bpp = imgTemp.getColorModel().getPixelSize();
        ByteBuffer byteBuffer;
        DataBuffer db = imgTemp.getData().getDataBuffer();
        if (db instanceof DataBufferInt) {
            int intI[] = ((DataBufferInt) db).getData();
            byteBuffer = ByteBuffer.allocateDirect(this.textureSize * this.textureSize * (bpp / 8)).order(
                    ByteOrder.nativeOrder());
            for (int in : intI) {
                byteBuffer.put((byte) in);
                byteBuffer.put((byte) (in >> 8));
                byteBuffer.put((byte) (in >> 16));
                byteBuffer.put((byte) (in >> 24));
            }
        } else
            byteBuffer = ByteBuffer.allocateDirect(this.textureSize * this.textureSize * (bpp / 8))
                    .order(ByteOrder.nativeOrder()).put(((DataBufferByte) db).getData());
        byteBuffer.flip();

        int internalFormat = GL11.GL_RGBA8, format = GL11.GL_RGBA;
        int textureId = getGlTextureId();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);//GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);//GL11.GL_LINEAR);

        GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

        GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, internalFormat, this.textureSize, this.textureSize, format,
                GL11.GL_UNSIGNED_BYTE, byteBuffer);
    }

    private BufferedImage getFontImage(char ch, Font font) {
        BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = tempfontImage.getGraphics();
        if (this.fd.antiAlias == true && g instanceof Graphics2D)
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics();
        Rectangle2D r = fontMetrics.getStringBounds(ch=='\t'?"    ":"" + ch, g);

        int charwidth = (int) r.getWidth();
        int charheight = (int) r.getHeight();
        if(charwidth<=0)
            charwidth=1;
        // Create another image holding the character we are creating
        BufferedImage fontImage;
        fontImage = new BufferedImage(charwidth, charheight, BufferedImage.TYPE_INT_ARGB);
        Graphics gt = fontImage.getGraphics();
        if (this.fd.antiAlias == true && gt instanceof Graphics2D)
            ((Graphics2D) gt).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        gt.setColor(new Color(0, 0, 0, 1));
        gt.fillRect(0, 0, charwidth, charheight);
        gt.setFont(font);
        gt.setColor(Color.WHITE);
        int charx = 0;
        int chary = 0;
        gt.drawString(String.valueOf(ch), (charx), (chary) + fontMetrics.getAscent());

        return fontImage;
    }

    public boolean addCustomChars(char[] pCustomCharsArray) {
        if (pCustomCharsArray != null) {
            List<Character> customCharsList = new ArrayList<Character>();
            for (int i = 0; i < this.customCharsArray.length; i++) {
                Character c = this.customCharsArray[i];
                if (!customCharsList.contains(c)) customCharsList.add(c);
            }
            boolean changed = false;
            for (int i = 0; i < pCustomCharsArray.length; i++) {
                Character c = pCustomCharsArray[i];
                if (!customCharsList.contains(c)) {
                    customCharsList.add(c);
                    changed = true;
                }
            }
            this.customCharsArray = new char[customCharsList.size()];
            for (int i = 0; i < this.customCharsArray.length; i++)
                this.customCharsArray[i] = customCharsList.get(i);
            return changed;
        }
        return false;
    }
}
