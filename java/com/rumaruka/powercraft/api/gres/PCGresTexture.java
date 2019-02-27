package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCClientUtils;
import com.rumaruka.powercraft.api.PCRectI;
import com.rumaruka.powercraft.api.PCVec2I;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class PCGresTexture {


    private final ResourceLocation texture;

    private final PCVec2I size;

    private final PCRectI frame;

    private final PCVec2I locations[];

    public PCGresTexture(ResourceLocation texture,PCVec2I size,PCRectI frames, PCVec2I loc[]){
        this.texture=texture;
        this.size=size;
        this.frame=frames;
        this.locations=loc;

    }

    public PCRectI getFrame() {
        return new PCRectI(this.frame);
    }

    public PCVec2I getMinSize() {
        return this.frame.getSize().add(this.frame.getLocation());

    }

    public PCVec2I getDefaultSize() {
        return new PCVec2I(this.size);
    }

    public void drawBasic(int x,int y,int widht,int height,float u,float v,int state){
        float f = 0.00390625F;
        float f1 = 0.00390625F;

        float uc = (u*this.size.x)+this.locations[state].x;
        float vc = (v * this.size.y)+this.locations[state].y;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x,y+height,0).tex(uc*f,(vc+height)*f1).endVertex();
        bufferBuilder.pos(x + widht, y + height, 0).tex((uc + widht) * f, (vc + height) * f1).endVertex();
        bufferBuilder.pos(x + widht, y, 0).tex((uc + widht) * f, vc * f1).endVertex();
        bufferBuilder.pos(x, y, 0).tex( uc * f, vc * f1).endVertex();
        tessellator.draw();
    }
    public void draw(int x,int y,int wight,int height,int state){
        PCClientUtils.mc().getTextureManager().bindTexture(this.texture);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        PCVec2I location = this.locations[state];
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        if(this.frame.y>0){
            drawTexturedModalRect(x,y,location.x,location.y,this.frame.x,this.frame.y);
        }
        renderTextureSliced_static(x + this.frame.x, y, wight - this.frame.x - this.frame.width, this.frame.y, location.x + this.frame.x, location.y, this.size.x - this.frame.x
                - this.frame.width, this.size.y);

        if(this.frame.width>0){
            drawTexturedModalRect(x+wight-this.frame.width,y,location.x+this.size.x-this.frame.width,location.y,this.frame.width,this.frame.y);
        }
        if (this.frame.x > 0) {
            renderTextureSliced_static(x, y + this.frame.y, this.frame.x, height - this.frame.y - this.frame.height, location.x, location.y + this.frame.y, this.size.x, this.size.y
                    - this.frame.y - this.frame.height);
        }

        renderTextureSliced_static(x + this.frame.x, y + this.frame.y, wight - this.frame.x - this.frame.width, height - this.frame.y - this.frame.height, location.x + this.frame.x,
                location.y + this.frame.y, this.size.x - this.frame.x - this.frame.width, this.size.y - this.frame.y - this.frame.height);

        if (this.frame.width > 0) {
            renderTextureSliced_static(x + wight - this.frame.width, y + this.frame.y, this.frame.width, height - this.frame.y - this.frame.height, location.x + this.size.x
                    - this.frame.width, location.y + this.frame.y, this.frame.width, this.size.y - this.frame.y - this.frame.height);
        }

        if (this.frame.height > 0) {
            if (this.frame.x > 0) {
                drawTexturedModalRect(x, y + height - this.frame.height, location.x, location.y + this.size.y - this.frame.height, this.frame.x, this.frame.height);
            }
            renderTextureSliced_static(x + this.frame.x, y + height - this.frame.height, wight - this.frame.x - this.frame.width, this.frame.height, location.x + this.frame.x,
                    location.y + this.size.y - this.frame.height, this.size.x - this.frame.x - this.frame.width, this.frame.height);
            if (this.frame.width > 0) {
                drawTexturedModalRect(x + wight - this.frame.width, y + height - this.frame.height, location.x + this.size.x - this.frame.width, location.y + this.size.y
                        - this.frame.height, this.frame.width, this.frame.height);
            }
        }

        tessellator.draw();

    }
    private static void renderTextureSliced_static(int x, int y, int width, int height, int u, int v, int imgWidth, int imgHeight) {

        for (int xx = 0; xx < width; xx += imgWidth) {
            for (int yy = 0; yy < height; yy += imgHeight) {
                int sx = imgWidth;
                int sy = imgHeight;
                if (xx + sx > width) {
                    sx = width - xx;
                }
                if (yy + sy > height) {
                    sy = height - yy;
                }
                drawTexturedModalRect(x + xx, y + yy, u, v, sx, sy);
            }
        }
    }
    public void drawProzentual(int x, int y, int width, int height, float u, float v, float w, float h, int state) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        float uc = (u * this.size.x) + this.locations[state].x;
        float vc = (v * this.size.y) + this.locations[state].y;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        bufferBuilder.pos(x, y + height, 0).tex( uc * f, (vc + h*this.size.y) * f1).endVertex();
        bufferBuilder.pos(x + width, y + height, 0).tex ((uc + w*this.size.x) * f, (vc + h*this.size.y) * f1).endVertex();
        bufferBuilder.pos(x + width, y, 0).tex ((uc + w*this.size.x) * f, vc * f1).endVertex();
        bufferBuilder.pos(x, y, 0).tex( uc * f, vc * f1).endVertex();
        tessellator.draw();
    }

    private static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {

        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x, y + height, 0).tex( u * f, (v + height) * f1).endVertex();
        bufferBuilder.pos(x + width, y + height, 0).tex((u + width) * f, (v + height) * f1).endVertex();
        bufferBuilder.pos(x + width, y, 0).tex ((u + width) * f, v * f1).endVertex();
        bufferBuilder.pos(x, y, 0).tex( u * f, v * f1).endVertex();
        tessellator.draw();

    }



}
