package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCVec2I;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class PCGresGuiScreen extends GuiScreen {

    private static final long DOUBLE_CLICK_DIFF=250000000L;

    private PCGresGuiHandler guiHandler;

    private int buttons;
    private long lastLeftClick;


    protected PCGresGuiScreen(IGresGui client){
        this.guiHandler=new PCGresGuiHandler(client);
        Keyboard.enableRepeatEvents(true);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.guiHandler.eventDrawScreen(new PCVec2I(mouseX,mouseY),partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        PCVec2I mouse = new PCVec2I(x, y);
        int eventButton = Mouse.getEventButton();
        int eventWheel = Mouse.getEventDWheel();
        if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
            eventMouseMove(mouse, this.buttons);
        }
        if (eventButton != -1) {
            if (Mouse.getEventButtonState()) {
                boolean doubleClick = false;
                if(eventButton==0){
                    long clickTime = Mouse.getEventNanoseconds();
                    doubleClick = clickTime - this.lastLeftClick<DOUBLE_CLICK_DIFF;
                    this.lastLeftClick = clickTime;
                }
                this.buttons |= 1 << eventButton;
                eventMouseButtonDown(mouse, this.buttons, eventButton, doubleClick);
            } else {
                this.buttons &= ~(1 << eventButton);
                eventMouseButtonUp(mouse, this.buttons, eventButton);
            }
        }
        if (eventWheel != 0) {
            eventMouseWheel(mouse, this.buttons, eventWheel>0?1:-1);
        }
    }
    @SuppressWarnings("hiding")
    private void eventMouseButtonDown(PCVec2I mouse, int buttons, int eventButton, boolean doubleClick) {
        this.guiHandler.eventMouseButtonDown(mouse, buttons, eventButton, doubleClick);
    }


    @SuppressWarnings("hiding")
    private void eventMouseButtonUp(PCVec2I mouse, int buttons, int eventButton) {
        this.guiHandler.eventMouseButtonUp(mouse, buttons, eventButton);
    }


    @SuppressWarnings("hiding")
    private void eventMouseMove(PCVec2I mouse, int buttons) {
        this.guiHandler.eventMouseMove(mouse, buttons);
    }


    @SuppressWarnings("hiding")
    private void eventMouseWheel(PCVec2I mouse, int buttons, int wheel) {
        this.guiHandler.eventMouseWheel(mouse, buttons, wheel);
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.guiHandler.eventKeyTyped(key, keyCode, Keyboard.isRepeatEvent());
    }

    @Override
    public void updateScreen() {
        this.guiHandler.eventUpdateScreen();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.guiHandler.eventGuiClosed();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        this.guiHandler.eventInitGui(width, height);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    public IGresGui getCurrentClientGui(){
        return this.guiHandler.getClient();
    }
}
