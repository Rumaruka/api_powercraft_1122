package com.rumaruka.powercraft.api.gres;


import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.gres.events.PCGresKeyEvent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;
import com.rumaruka.powercraft.api.gres.layot.IGresLayout;
import com.rumaruka.powercraft.api.gres.slot.PCSlot;
import com.rumaruka.powercraft.api.gres.slot.PCSlotPhantom;
import com.rumaruka.powercraft.api.inventory.PCInventoryUtils;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PCGresGuiHandler extends PCGresContainer {

    private final IGresGui gui;
    private final Minecraft mc;
    private boolean initialized;
    private PCGresComponent focusedComponent = this;
    private PCGresComponent mouseOverComponent = this;

    private final List<Slot> selectedSlots = new ArrayList<Slot>();
    private Slot slotOver;
    private int stackSize;
    private int slotClickButton = -1;
    private Slot lastSlotOver;
    private int lastClickButton;
    private boolean takeAll;
    private int scale;

    private final PCVec2I lastMouse = new PCVec2I(-1, -1);

    private long last = System.currentTimeMillis();

    private PCGresHistory history = new PCGresHistory(100);

    private int workings;

    private int workingTick;

    protected PCGresGuiHandler(IGresGui gui) {
        this.fontColors[0] = -1;
        this.fontColors[1] = -1;
        this.fontColors[2] = -1;
        this.fontColors[3] = -1;
        this.gui = gui;
        this.mc = PCClientUtils.mc();
        super.setLayout(new IGresLayout() {

            @Override
            public PCVec2I getPreferredLayoutSize(PCGresContainer container) {
                return container.getPrefSize();
            }

            @Override
            public PCVec2I getMinimumLayoutSize(PCGresContainer container) {
                return container.getMaxSize();
            }

            @Override
            public void updateLayout(PCGresContainer container) {
                for (PCGresComponent component : container.children) {
                    if(component.layoutData==null)
                        component.putInRect(0, 0, container.rect.width, container.rect.height);
                }
            }

        });
    }

    @Override
    public PCGresContainer setLayout(IGresLayout layout) {
        return this;
    }

    public IGresGui getClient() {

        return this.gui;
    }


    public void close() {

        this.mc.player.closeScreen();
    }


    @Override
    public void setVisible(boolean visible) {

        if (!visible) close();
    }


    @Override
    protected void setParentVisible(boolean visible) {

        throw new IllegalArgumentException("GresGuiHandler can't have a parent");
    }


    @Override
    protected void setParent(PCGresContainer parent) {

        throw new IllegalArgumentException("GresGuiHandler can't have a parent");
    }


    @Override
    public PCGresGuiHandler getGuiHandler() {

        return this;
    }


    @Override
    public void setRect(PCRectI rect) {

        throw new IllegalArgumentException("GresGuiHandler can't set rect");
    }


    @Override
    public PCGresComponent setSize(PCVec2I size) {

        throw new IllegalArgumentException("GresGuiHandler can't set size");
    }


    @Override
    public void setMinSize(PCVec2I minSize) {

        throw new IllegalArgumentException("GresGuiHandler can't set minsize");
    }


    @Override
    protected PCVec2I calculateMinSize() {

        return new PCVec2I();
    }


    @Override
    public void setMaxSize(PCVec2I maxSize) {

        throw new IllegalArgumentException("GresGuiHandler can't set maxsize");
    }


    @Override
    protected PCVec2I calculateMaxSize() {

        return new PCVec2I();
    }


    @Override
    public void setPrefSize(PCVec2I prefSize) {

        throw new IllegalArgumentException("GresGuiHandler can't set prefsize");
    }


    @Override
    protected PCVec2I calculatePrefSize() {

        return new PCVec2I();
    }


    @SuppressWarnings("hiding")
    @Override
    protected void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom) {

        PCGresRenderer.drawGradientRect(0, 0, this.rect.width, this.rect.height, -1072689136, -804253680);
        int t = (this.workingTick/2)%8;
        if(t>0 && this.workings>0){
            String s;
            switch(t){
                case 1:
                case 7:
                    s=".";
                    break;
                case 2:
                case 6:
                    s="..";
                    break;
                case 3:
                case 5:
                    s="...";
                    break;
                case 4:
                    s="....";
                    break;
                default:
                    s="";
                    break;
            }
            String l = t>4?s:"....";
            int size = fontRenderer.getStringSize(l).x;
            drawString(s, this.rect.width-size-3, 3, false);
        }
    }


    protected void eventInitGui(int width, int height) {
        this.minSize.setTo(new PCVec2I(width, height));
        this.maxSize.setTo(this.minSize);
        this.prefSize.setTo(this.minSize);
        super.setSize(this.minSize);
        if (!this.initialized) {
            this.gui.initGui(this);
            this.initialized = true;
        }
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int newScale = scaledresolution.getScaleFactor();
        if(newScale!=this.scale){
            this.scale = newScale;
            onScaleChanged(newScale);
        }
    }


    protected void eventUpdateScreen() {
        fireEvent(new PCGresTickEvent(this, EventType.PRE));
        this.workingTick++;
        onTick();
        fireEvent(new PCGresTickEvent(this, EventType.POST));
        if (!this.mc.player.isEntityAlive() || this.mc.player.isDead){
            close();
        }
    }


    @SuppressWarnings("unused")
    protected void eventDrawScreen(PCVec2I mouse, float timeStamp) {
        long t = System.currentTimeMillis();
        float ts = (t-this.last)/1000.0f;
        this.last = t;

        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        fireEvent(new PCGresPaintEvent(this, EventType.PRE, ts));
        onDrawTick(ts);
        PCGresRenderer.setupGuiItemLighting();
        PCGresRenderer.disableGuiItemLighting();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        doPaint(new PC_Vec2(0, 0), null, scaledresolution.getScaleFactor(), this.mc.displayHeight, ts, 1.0f);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        drawMouseItemStack(mouse);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        fireEvent(new PCGresPaintEvent(this, EventType.POST, ts));
    }


    private void drawTooltip(PCVec2I mouse) {

        List<String> list = this.mouseOverComponent.onGetTooltip(mouse.sub(new PCVec2I(this.mouseOverComponent.getRealLocation())).div(this.mouseOverComponent.getRecursiveZoom()));
        if (list != null && !list.isEmpty()) {
            PCGresRenderer.drawTooltip(mouse.x, mouse.y, this.rect.width, this.rect.height, list);
        }
    }


    protected void eventKeyTyped(char key, int keyCode, boolean repeat) {

        switch(keyCode){
            case Keyboard.KEY_Z:
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)){
                    this.history.undo();
                    return;
                }
                break;
            case Keyboard.KEY_Y:
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)){
                    this.history.redo();
                    return;
                }
                break;
            default:
                break;
        }

        PCGresComponent c = this.focusedComponent;
        while(c!=null && !c.onKeyTyped(key, keyCode, repeat, this.history)){
            c = c.getParent();
        }
        if(c==null){
            PCGresKeyEvent event = new PCGresKeyEvent(this, key, keyCode, repeat, this.history);
            fireEvent(event);
            if (!event.isConsumed()) {
                if(!checkHotbarKeys(keyCode)){
                    if(keyCode==Keyboard.KEY_ESCAPE || keyCode==Keyboard.KEY_E){
                        close();
                    }else{
                        tryActionOnKeyTyped(key, keyCode, repeat, this.history);
                    }
                }
            }
        }
    }

    private boolean checkHotbarKeys(int keyCode){
        if (this.mc.player.inventory.getItemStack() == null && this.slotOver != null){
            for (int j = 0; j < 9; ++j){
                if (keyCode == 2 + j){
                    sendMouseClickToServer(this.slotOver.slotNumber, j, 2);
                    return true;
                }
            }
        }

        return false;
    }


    private void checkMouseOverComponent(PCVec2I mouse, int buttons) {

        PCGresComponent newMouseOverComponent = getComponentAtPosition(mouse);
        if (newMouseOverComponent == null) {
            newMouseOverComponent = this;
        }
        if (newMouseOverComponent != this.mouseOverComponent) {
            this.mouseOverComponent.onMouseLeave(mouse.sub(new PCVec2I(this.mouseOverComponent.getRealLocation())).div(this.mouseOverComponent.getRecursiveZoom()), buttons, this.history);
            newMouseOverComponent.onMouseEnter(mouse.sub(new PCVec2I(newMouseOverComponent.getRealLocation())).div(this.mouseOverComponent.getRecursiveZoom()), buttons, this.history);
            this.mouseOverComponent = newMouseOverComponent;
        }
    }


    protected void eventMouseButtonDown(PCVec2I mouse, int buttons, int eventButton, boolean doubleClick) {
        this.lastMouse.setTo(mouse);
        setFocus(this.mouseOverComponent);
        inventoryMouseDown(mouse, buttons, eventButton, doubleClick);
        this.focusedComponent.onMouseButtonDown(mouse.sub(new PCVec2I(this.focusedComponent.getRealLocation())).div(this.focusedComponent.getRecursiveZoom()), buttons, eventButton, doubleClick, this.history);
    }


    protected void eventMouseButtonUp(PCVec2I mouse, int buttons, int eventButton) {
        this.lastMouse.setTo(mouse);
        inventoryMouseUp(mouse, buttons, eventButton);
        this.focusedComponent.onMouseButtonUp(mouse.sub(new PCVec2I(this.focusedComponent.getRealLocation())).div(this.focusedComponent.getRecursiveZoom()), buttons, eventButton, this.history);
    }


    protected void eventMouseMove(PCVec2I mouse, int buttons) {
        this.lastMouse.setTo(mouse);
        checkMouseOverComponent(mouse, buttons);
        inventoryMouseMove(mouse, buttons);
        this.mouseOverComponent.onMouseMove(mouse.sub(new PCVec2I(this.mouseOverComponent.getRealLocation())).div(this.mouseOverComponent.getRecursiveZoom()), buttons, this.history);
        if(this.mouseOverComponent!=this.focusedComponent)
            this.focusedComponent.onMouseMove(mouse.sub(new PCVec2I(this.focusedComponent.getRealLocation())).div(this.focusedComponent.getRecursiveZoom()), buttons, this.history);
    }


    protected void eventMouseWheel(PCVec2I mouse, int buttons, int wheel) {
        this.lastMouse.setTo(mouse);
        PCGresComponent c = this.focusedComponent;
        while(c!=null && !c.onMouseWheel(mouse.sub(new PCVec2I(c.getRealLocation())).div(c.getRecursiveZoom()), buttons, wheel, this.history)){
            c = c.getParent();
        }
    }


    @Override
    protected void notifyChange() {

        updateLayout();
    }


    public void setFocus(PCGresComponent focusedComponent) {
        if(this.focusedComponent != focusedComponent){
            PCGresComponent oldFocusedComponent = this.focusedComponent;
            this.focusedComponent = focusedComponent;
            if(oldFocusedComponent!=null){
                oldFocusedComponent.onFocusLost(focusedComponent, this.history);
            }
            if(focusedComponent!=null){
                focusedComponent.onFocusGot(oldFocusedComponent, this.history);
            }
            onFocusChaned(oldFocusedComponent, focusedComponent);
        }
    }

    private ItemStack getMouseItemStack(){
        return this.mc.player.inventory.getItemStack();
    }

    @SuppressWarnings("hiding")
    private void drawMouseItemStack(PCVec2I mouse){
        ItemStack holdItemStack = getMouseItemStack();
        if (holdItemStack.isEmpty()) {
            drawTooltip(mouse);
        }else{
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            String text = null;
            holdItemStack = holdItemStack.copy();
            if(this.selectedSlots.size()>1){
                int stacksize = holdItemStack.getMaxStackSize();
                stacksize = this.stackSize==-1?0:this.stackSize;
                if(stacksize==0){
                    text = TextFormatting.YELLOW+"0";
                }
            }
            PCGresRenderer.drawItemStackAllreadyLighting(mouse.x-8, mouse.y-8, holdItemStack, text);
        }
    }

    @SuppressWarnings("unused")
    private void inventoryMouseMove(PCVec2I mouse, int buttons){
        this.slotOver = getSlotAtPosition(mouse);
        if(!this.takeAll && this.slotOver!=null && getMouseItemStack().isEmpty() && this.stackSize!=-1 && this.slotClickButton!=-1 && isItemStacksCompatibleForSlot(getMouseItemStack(), this.slotOver) && canDragIntoSlot(this.slotOver)){
            if(!this.selectedSlots.contains(this.slotOver)){
                this.selectedSlots.add(this.slotOver);
                calcMouseStackSize();
            }
        }
    }

    private void calcMouseStackSize(){
        ItemStack itemStack = getMouseItemStack();
        if(itemStack.isEmpty()){
            this.stackSize = 0;
        }else{
            this.stackSize = itemStack.getMaxStackSize();
            for(Slot slot:this.selectedSlots){
                int size = slot.getHasStack()?slot.getStack().getMaxStackSize():0;
                ItemStack is = itemStack.copy();
                int ismaxstack = is.getMaxStackSize();
                ismaxstack = size+calcCount();
                int max = PCInventoryUtils.getMaxStackSize(is, slot);
                if (ismaxstack > max) {
                    ismaxstack = max;
                }
                this.stackSize -= ismaxstack;
            }
            if(this.selectedSlots.size()>=itemStack.getMaxStackSize()){
                this.stackSize = -1;
            }
        }
    }

    private static boolean canDragIntoSlot(Slot slot){
        if(slot instanceof PCSlot){
            return ((PCSlot)slot).canDragIntoSlot();
        }
        return true;
    }

    private static boolean isItemStacksCompatibleForSlot(ItemStack itemStack, Slot slot){
        ItemStack slotItemStack = slot.getStack();
        if(itemStack==null){
            return false;
        }
        if(slotItemStack.isEmpty() || slot instanceof PCSlotPhantom)
            return true;
        return itemStack.isItemEqual(slotItemStack) && ItemStack.areItemStackTagsEqual(itemStack, slotItemStack);
    }

    private void inventoryMouseDown(PCVec2I mouse, int buttons, int eventButton, boolean doubleClick) {
        if(this.slotClickButton==-1){
            boolean flag = this.lastSlotOver == this.slotOver && this.lastSlotOver!=null && doubleClick && this.lastClickButton == eventButton;
            this.lastSlotOver = this.slotOver;
            if (this.slotOver!=null && this.slotOver.getHasStack() && eventButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100){
                sendMouseClickToServer(this.slotOver.slotNumber, eventButton, 3);
            }else if(this.slotOver!=null && this.slotOver.getHasStack()){
                this.selectedSlots.clear();
                this.slotClickButton = eventButton;
                inventoryMouseMove(mouse, buttons);
                if(this.mc.gameSettings.touchscreen){
                    onSlotClicked();
                    if(getMouseItemStack().isEmpty())
                        this.stackSize = getMouseItemStack().getAnimationsToGo();
                }
            }else if(getMouseItemStack().isEmpty()){
                if(this.slotOver!=null){
                    this.takeAll = flag;
                    this.slotClickButton = eventButton;
                    this.selectedSlots.clear();
                    inventoryMouseMove(mouse, buttons);
                }else if(this.mouseOverComponent==this){
                    sendMouseClickToServer(-999, eventButton, 0);
                }
            }
        }
    }

    public PCGresComponent getMouseOverComponent(){
        return this.mouseOverComponent;
    }

    private void onSlotClicked(){
        if(GuiScreen.isShiftKeyDown()){
            sendMouseClickToServer(this.slotOver.slotNumber, this.slotClickButton, 1);
        }else{
            sendMouseClickToServer(this.slotOver.slotNumber, this.slotClickButton, 0);
        }
    }

    @SuppressWarnings("unused")
    private void inventoryMouseUp(PCVec2I mouse, int buttons, int eventButton){
        if(this.slotClickButton==eventButton && this.slotOver!=null && this.slotOver.getHasStack() && this.selectedSlots.size()<=1){
            onSlotClicked();
        }else if(getMouseItemStack().isEmpty()){
            onSlotFill();
        }
        this.selectedSlots.clear();
        this.takeAll = false;
        this.slotClickButton = -1;
        this.stackSize=-1;
        if(getMouseItemStack().isEmpty())
            this.stackSize = getMouseItemStack().getMaxStackSize();
    }

    private void onSlotFill(){
        if(this.takeAll){
            this.takeAll = false;
            sendMouseClickToServer(this.lastSlotOver.slotNumber, this.slotClickButton, 6);
        }else{
            if(this.selectedSlots.size()==1){
                sendMouseClickToServer(this.selectedSlots.get(0).slotNumber, this.slotClickButton, 0);
            }else if(this.selectedSlots.size()>0){
                sendMouseClickToServer(-999, Container.func_94534_d(0, this.slotClickButton), 5);
                for(Slot slot:this.selectedSlots){
                    sendMouseClickToServer(slot.slotNumber, Container.func_94534_d(1, this.slotClickButton), 5);
                }
                sendMouseClickToServer(-999, Container.func_94534_d(2, this.slotClickButton), 5);
            }
        }
    }

    private int calcCount(){
        switch (this.slotClickButton){
            case 0:
                ItemStack itemStack = getMouseItemStack();
                if(itemStack.isEmpty())
                    return 0;
                return PCMathHelper.floor_float(itemStack.getMaxStackSize() / (float)this.selectedSlots.size());
            case 1:
                return 1;
            default:
                return 0;
        }
    }

    private void sendMouseClickToServer(int slotNumber, int mouseButton, ClickType transfer){
        if(this.gui instanceof PCGresBaseWithInventory){
            EntityPlayer player = this.mc.player;
            int transactionID = player.openContainer.getNextTransactionID(player.inventory);
            ItemStack itemstack = player.openContainer.slotClick(slotNumber, mouseButton, transfer, player);
            PCPacketHandler.sendToServer(new PCPacketClickWindow(((PCGresBaseWithInventory)this.gui).windowId, slotNumber, mouseButton, transfer, transactionID, itemstack));
        }
    }

    @SuppressWarnings("hiding")
    protected void renderSlot(int x, int y, Slot slot) {
        boolean renderGray = false;
        String text = null;
        ItemStack itemStack = slot.getStack();
        ItemStack mouseItemStack = getMouseItemStack();
        if(this.selectedSlots.contains(slot) && this.selectedSlots.size()>1){
            if(isItemStacksCompatibleForSlot(mouseItemStack, slot) && canDragIntoSlot(slot)){
                int size = slot.getHasStack()?itemStack.getMaxStackSize():0;
                itemStack = mouseItemStack.copy();
                int itemStackMax = itemStack.getMaxStackSize();
                itemStackMax = size+calcCount();
                renderGray = true;
                int max = PCInventoryUtils.getMaxStackSize(mouseItemStack, slot);
                if (itemStackMax > max){
                    text = ""+TextFormatting.YELLOW + max;
                    itemStackMax= max;
                }
            }else{
                this.selectedSlots.remove(slot);
            }
        }else if(slot==this.slotOver){
            renderGray = true;
        }
        renderGray &= slot.func_111238_b();
        boolean renderGrayAfter = false;
        if(slot instanceof PCSlot){
            PCSlot sSlot = (PCSlot)slot;
            if(itemStack.isEmpty()){
                itemStack = sSlot.getBackgroundStack();
                renderGrayAfter = sSlot.renderGrayWhenEmpty();
            }
        }
        if(renderGray){
            PCGresRenderer.drawGradientRect(x, y, 16, 16, -2130706433, -2130706433);
        }
        PCGresRenderer.drawItemStackAllreadyLighting(x, y, itemStack, text);
        if(renderGrayAfter){
            PCGresRenderer.drawGradientRect(x, y, 16, 16, -2130706433, -2130706433);
        }
    }

    public void eventGuiClosed() {
        if (this.mc.player != null && this.gui instanceof PCGresBaseWithInventory) {
            ((PCGresBaseWithInventory)this.gui).onContainerClosed(this.mc.player);
        }
    }

    public PCGresHistory getHistory(){
        return this.history;
    }

    public void onAdded(PCGresComponent component) {
        component.onScaleChanged(this.scale);
    }

    @Override
    protected void addToBase(PCGresComponent c){
        add(c);
    }

    public PCVec2I getMousePos() {
        return this.lastMouse;
    }

    public void addWorking(){
        if(this.workings==0)
            this.workingTick=0;
        this.workings++;
    }

    public void removeWorking(){
        if(this.workings>0){
            this.workings--;
        }
    }
}
