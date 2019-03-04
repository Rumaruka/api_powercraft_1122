package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.gres.events.*;
import com.rumaruka.powercraft.api.gres.font.PCFontRenderer;
import com.rumaruka.powercraft.api.gres.font.PCFonts;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;
import com.rumaruka.powercraft.api.renderer.PCOpenGL;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public  abstract class PCGresComponent {
    protected PCGresContainer parent;

    protected String text = "";

    protected final PCRectI rect = new PCRectI();

    protected final PCRectI padding = new PCRectI(0, 0, 0, 0);

    protected PCGresAlign.H alignH = PCGresAlign.H.CENTER;

    protected PCGresAlign.V alignV = PCGresAlign.V.CENTER;

    protected PCGresAlign.Fill fill = PCGresAlign.Fill.NONE;

    protected final PCVec2I minSize = new PCVec2I(0, 0);

    protected boolean minSizeSet;

    protected final PCVec2I maxSize = new PCVec2I(-1, -1);

    protected boolean maxSizeSet;

    protected final PCVec2I prefSize = new PCVec2I(-1, -1);

    protected boolean prefSizeSet;

    protected boolean visible = true;

    protected boolean parentVisible = true;

    protected boolean enabled = true;

    protected boolean parentEnabled = true;

    protected boolean editable = true;

    protected boolean mouseOver;

    protected boolean mouseDown;

    protected static PCFontRenderer fontRenderer = new PCFontRenderer(PCFonts.getFontByName("Default"));

    protected final int fontColors[] = { 0x000000, 0x000000, 0x000000, 0x333333 };

    protected final List<IGresEventListener> eventListeners = new ArrayList<IGresEventListener>();

    protected Object layoutData;

    protected boolean focus;

    public PCGresComponent() {

    }

    public PCGresComponent(String text) {
        this.text = text;
    }

    protected void setLayoutData(Object layoutData){
        if(this.layoutData != layoutData){
            this.layoutData = layoutData;
            notifyChange();
        }
    }

    protected void setParent(PCGresContainer parent) {

        if (parent == null) {
            if (this.parent!=null && !this.parent.isChild(this)) {
                this.parent = null;
                this.parentVisible = true;
                this.parentEnabled = true;
                notifyChange();
            }
        } else if (parent.isChild(this)) {
            this.parent = parent;
            this.parentVisible = parent.isRecursiveVisible();
            this.parentEnabled = parent.isRecursiveEnabled();
            if(getGuiHandler()!=null)
                getGuiHandler().onAdded(this);
            notifyChange();
        }
    }


    public PCGresContainer getParent() {

        return this.parent;
    }


    public void setText(String text) {

        if (!this.text.equals(text)) {
            this.text = text;
            notifyChange();
        }
    }


    public String getText() {

        return this.text;
    }


    public void setRect(PCRectI rect) {

        if (!this.rect.equals(rect)) {
            this.rect.setTo(rect);
            notifyChange();
        }
    }


    public PCRectI getRect() {

        return new PCRectI(this.rect);
    }

    public PCRectI getRectScaled() {

        PCRectI r = getRect();
        float zoom = getZoom();
        r.width *= zoom;
        r.height *= zoom;
        return r;
    }


    public PCGresComponent setLocation(PCVec2I location) {

        if (this.rect.setLocation(location)) {
            notifyChange();
        }

        return this;
    }


    public PCVec2I getLocation() {

        return this.rect.getLocation();
    }


    public PCGresComponent setSize(PCVec2I size) {

        if (this.rect.setSize(size)) {
            notifyChange();
        }

        return this;
    }


    public PCVec2I getSize() {

        return this.rect.getSize();
    }


    public PCGresComponent setPadding(PCRectI rect) {

        this.padding.setTo(rect);
        return this;
    }


    public PCRectI getPadding() {

        return new PCRectI(this.padding);
    }


    public PCGresComponent setAlignH(PCGresAlign.H alignH) {

        this.alignH = alignH;
        return this;
    }


    public PCGresAlign.H getAlignH() {

        return this.alignH;
    }


    public PCGresComponent setAlignV(PCGresAlign.V alignV) {

        this.alignV = alignV;
        return this;
    }


    public PCGresAlign.V getAlignV() {

        return this.alignV;
    }


    public PCGresComponent setFill(PCGresAlign.Fill fill) {

        this.fill = fill;
        return this;
    }


    public PCGresAlign.Fill getFill() {

        return this.fill;
    }


    public void putInRect(int x, int y, int w, int h) {
        int width = w;
        int height = h;
        //if (width > maxSize.x && maxSize.x >= 0) width = maxSize.x;
        //if (height > maxSize.y && maxSize.y >= 0) height = maxSize.y;
        if (width < this.minSize.x && this.minSize.x >= 0) width = this.minSize.x;
        if (height < this.minSize.y && this.minSize.y >= 0) height = this.minSize.y;
        boolean needUpdate = false;
        if (this.fill == PCGresAlign.Fill.BOTH || this.fill == PCGresAlign.Fill.HORIZONTAL) {
            needUpdate |= this.rect.x != x;
            this.rect.x = x;
            needUpdate |= this.rect.width != width;
            this.rect.width = width;
        } else {
            switch (this.alignH) {
                case CENTER:
                    needUpdate |= this.rect.x != x + width / 2 - this.rect.width / 2;
                    this.rect.x = x + width / 2 - this.rect.width / 2;
                    break;
                case RIGHT:
                    needUpdate |= this.rect.x != x + width - this.rect.width;
                    this.rect.x = x + width - this.rect.width;
                    break;
                default:
                    needUpdate |= this.rect.x != x;
                    this.rect.x = x;
                    break;
            }
        }
        if (this.fill == PCGresAlign.Fill.BOTH || this.fill == PCGresAlign.Fill.VERTICAL) {
            needUpdate |= this.rect.y != y;
            this.rect.y = y;
            needUpdate |= this.rect.height != height;
            this.rect.height = height;
        } else {
            switch (this.alignV) {
                case CENTER:
                    needUpdate |= this.rect.y != y + (height - this.rect.height) / 2;
                    this.rect.y = y + (height - this.rect.height) / 2;
                    break;
                case BOTTOM:
                    needUpdate |= this.rect.y != y + height - this.rect.height;
                    this.rect.y = y + height - this.rect.height;
                    break;
                default:
                    needUpdate |= this.rect.y != y;
                    this.rect.y = y;
                    break;
            }
        }
        if (needUpdate) notifyChange();
    }


    public void setMinSize(PCVec2I minSize) {

        if (minSize == null) {
            this.minSize.setTo(calculateMinSize());
            this.minSizeSet = false;
        } else {
            this.minSize.setTo(minSize);
            this.minSizeSet = true;
        }
        setSize(getSize().max(this.minSize));
    }


    public PCVec2I getMinSize() {

        return this.minSize;
    }


    public void updateMinSize() {

        if (!this.minSizeSet) {
            setMinSize(null);
        }
    }


    protected abstract PCVec2I calculateMinSize();


    public void setMaxSize(PCVec2I maxSize) {

        if (maxSize == null) {
            this.maxSize.setTo(calculateMaxSize());
            this.maxSizeSet = false;
        } else {
            this.maxSize.setTo(maxSize);
            this.maxSizeSet = true;
        }
    }


    public PCVec2I getMaxSize() {

        return this.maxSize;
    }


    public void updateMaxSize() {

        if (!this.maxSizeSet) {
            setMaxSize(null);
        }
    }


    protected abstract PCVec2I calculateMaxSize();


    public void setPrefSize(PCVec2I prefSize) {

        if (prefSize == null) {
            this.prefSize.setTo(calculatePrefSize());
            this.prefSizeSet = false;
        } else {
            this.prefSize.setTo(prefSize);
            this.prefSizeSet = true;
        }
    }


    public PCVec2I getPrefSize() {

        return this.prefSize;
    }


    public void updatePrefSize() {

        if (!this.prefSizeSet) {
            setPrefSize(null);
        }
    }


    protected abstract PCVec2I calculatePrefSize();


    public void setVisible(boolean visible) {

        this.visible = visible;
        notifyParentOfChange();
    }


    protected void setParentVisible(boolean visible) {

        this.parentVisible = visible;
    }


    public boolean isVisible() {

        return this.visible;
    }


    public boolean isRecursiveVisible() {

        return this.visible && (this.parent == null || this.parent.isRecursiveVisible());
    }


    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }


    protected void setParentEnabled(boolean enabled) {

        this.parentEnabled = enabled;
    }


    public boolean isEnabled() {

        return this.enabled;
    }


    public boolean isRecursiveEnabled() {

        return this.enabled && (this.parent == null || this.parent.isRecursiveEnabled());
    }

    public void setEditable(boolean editable) {

        this.editable = editable;
    }


    public boolean isEditable() {

        return this.editable;
    }

    public boolean hasFocus(){

        return this.focus;

    }

    public boolean hasFocusOrChild(){
        return this.focus;
    }

    public void takeFocus(){

        PCGresGuiHandler guiHandler = getGuiHandler();
        if(guiHandler!=null){
            guiHandler.setFocus(this);
        }

    }

    protected void notifyChange() {

        updateMinSize();
        updatePrefSize();
        updateMaxSize();
        notifyParentOfChange();
    }


    protected void notifyParentOfChange() {

        if (this.parent != null) this.parent.notifyChildChange(this);
    }


    protected static PCRect setDrawRect(PCRect old, PCRect _new, double scale, int displayHeight, float zoom) {

        PCRect rect;
        if (old == null) {
            rect = new PCRect(_new);
            rect.width *= zoom;
            rect.height *= zoom;
        } else {
            rect = new PCRect(_new);
            rect.width *= zoom;
            rect.height *= zoom;
            rect = old.averageQuantity(rect);
        }
        if (rect.width <= 0 || rect.height <= 0) return null;
        GL11.glScissor((int) (rect.x * scale), displayHeight - (int) ((rect.y + rect.height) * scale), (int) (rect.width * scale),
                (int) (rect.height * scale));
        return rect;
    }


    @SuppressWarnings("hiding")
    protected void doPaint(PCVec2 offset, PCRect scissorOld, double scale, int displayHeight, float timeStamp, float zoom) {

        if (this.visible) {
            float tzoom = getZoom();
            float zoomm = zoom * tzoom;
            PCRect rect = new PCRect(this.rect);
            rect.x *= zoom;
            rect.y *= zoom;
            rect.x += offset.x;
            rect.y += offset.y;
            PCRect scissor = setDrawRect(scissorOld, rect, scale, displayHeight, zoomm);
            if(scissor==null)
                return;
            PCOpenGL.pushMatrix();
            GL11.glTranslatef(this.rect.x, this.rect.y, 0);
            GL11.glScalef(tzoom, tzoom, 1);
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            paint(scissor, scale, displayHeight, timeStamp, zoomm);
            doDebugRendering(0, 0, rect.width, rect.height);
            PCOpenGL.popMatrix();
        }
    }

    protected void doDebugRendering(double x, double y, double width, double height){
        if(PCDebug.DEBUG){
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            int hash = hashCode();
            int red = hash>>16&255;
            int green = hash>>8&255;
            int blue = hash&255;
            Tessellator tessellator = Tessellator.getInstance();

            BufferBuilder bufferBuilder = tessellator.getBuffer();

            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferBuilder.color(red, green, blue, 128);
            bufferBuilder.pos(x, y + height, 0);
            bufferBuilder.pos(x + width, y + height, 0);
            bufferBuilder.pos(x + width, y, 0);
            bufferBuilder.pos(x, y, 0);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    protected abstract void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom);


    protected boolean onKeyTyped(char key, int keyCode, boolean repeat, PCGresHistory history) {

        PCGresKeyEvent event = new PCGresKeyEvent(this, key, keyCode, repeat, history);
        fireEvent(event);
        boolean result = true;
        if (!event.isConsumed()) {
            result = handleKeyTyped(key, keyCode, repeat, history);
        }
        PCGresKeyEventResult postEvent = new PCGresKeyEventResult(this, key, keyCode, repeat, result, history);
        fireEvent(postEvent);
        return postEvent.getResult();
    }


    @SuppressWarnings("static-method")
    protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PCGresHistory history) {

        return false;
    }


    protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PCGresHistory history) {
        //
    }


    protected void onMouseEnter(PCVec2I mouse, int buttons, PCGresHistory history) {

        PCGresMouseEvent event = new PCGresMouseMoveEvent(this, mouse, buttons, PCGresMouseMoveEvent.Event.ENTER, history);
        fireEvent(event);
        if (!event.isConsumed()) {
            handleMouseEnter(mouse, buttons, history);
        }
    }


    protected void handleMouseEnter(PCVec2I mouse, int buttons, PCGresHistory history) {

        this.mouseOver = this.enabled && this.parentEnabled;
    }


    protected void onMouseLeave(PCVec2I mouse, int buttons, PCGresHistory history) {

        PCGresMouseEvent event = new PCGresMouseMoveEvent(this, mouse, buttons, PCGresMouseMoveEvent.Event.LEAVE, history);
        fireEvent(event);
        if (!event.isConsumed()) {
            handleMouseLeave(mouse, buttons, history);
        }
    }


    protected void handleMouseLeave(PCVec2I mouse, int buttons, PCGresHistory history) {

        this.mouseOver = false;
        this.mouseDown = false;
    }


    protected boolean onMouseMove(PCVec2I mouse, int buttons, PCGresHistory history) {

        PCGresMouseEvent event = new PCGresMouseMoveEvent(this, mouse, buttons, PCGresMouseMoveEvent.Event.MOVE, history);
        fireEvent(event);
        boolean result = true;
        if (!event.isConsumed()) {
            result = handleMouseMove(mouse, buttons, history);
        }
        PCGresMouseMoveEventResult ev = new PCGresMouseMoveEventResult(this, mouse, buttons, PCGresMouseMoveEvent.Event.MOVE, result, history);
        fireEvent(ev);
        return ev.getResult();
    }


    @SuppressWarnings("static-method")
    protected boolean handleMouseMove(PCVec2I mouse, int buttons, PCGresHistory history) {
        return false;
    }


    protected boolean onMouseButtonDown(PCVec2I mouse, int buttons, int eventButton, boolean doubleClick, PCGresHistory history) {

        PCGresMouseEvent event = new PCGresMouseButtonEvent(this, mouse, buttons, eventButton, doubleClick, PCGresMouseButtonEvent.Event.DOWN, history);
        fireEvent(event);
        boolean result = true;
        if (!event.isConsumed()) {
            result = handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
        }
        PCGresMouseEventResult ev = new PCGresMouseButtonEventResult(this, mouse, buttons, eventButton, false, PCGresMouseButtonEvent.Event.DOWN, result, history);
        fireEvent(ev);
        return ev.getResult();
    }


    protected boolean handleMouseButtonDown(PCVec2I mouse, int buttons, int eventButton, boolean doubleClick, PCGresHistory history) {

        this.mouseDown = this.enabled && this.parentEnabled;
        return false;
    }


    protected boolean onMouseButtonUp(PCVec2I mouse, int buttons, int eventButton, PCGresHistory history) {

        PCGresMouseEvent event = new PCGresMouseButtonEvent(this, mouse, buttons, eventButton, false, PCGresMouseButtonEvent.Event.UP, history);
        fireEvent(event);
        boolean result = true;
        if (!event.isConsumed()) {
            result = handleMouseButtonUp(mouse, buttons, eventButton, history);
        }
        PCGresMouseEventResult ev = new PCGresMouseButtonEventResult(this, mouse, buttons, eventButton, false, PCGresMouseButtonEvent.Event.UP, result, history);
        fireEvent(ev);
        return ev.getResult();
    }


    protected boolean handleMouseButtonUp(PCVec2I mouse, int buttons, int eventButton, PCGresHistory history) {

        boolean consumed = false;
        if (this.mouseDown) {
            PCGresMouseEvent event = new PCGresMouseButtonEvent(this, mouse, buttons, eventButton, false, PCGresMouseButtonEvent.Event.CLICK, history);
            fireEvent(event);
            if (!event.isConsumed()) {
                consumed = handleMouseButtonClick(mouse, buttons, eventButton, history);
            }else{
                consumed = true;
            }
            PCGresMouseEventResult ev = new PCGresMouseButtonEventResult(this, mouse, buttons, eventButton, false, PCGresMouseButtonEvent.Event.CLICK, consumed, history);
            fireEvent(ev);
            consumed = ev.getResult();
        }
        this.mouseDown = false;
        return consumed;
    }


    @SuppressWarnings("static-method")
    protected boolean handleMouseButtonClick(PCVec2I mouse, int buttons, int eventButton, PCGresHistory history) {
        return false;
    }


    protected final boolean onMouseWheel(PCVec2I mouse, int buttons, int wheel, PCGresHistory history) {
        PCGresMouseWheelEvent event = new PCGresMouseWheelEvent(this, mouse, buttons, wheel, history);
        onMouseWheel(event, history);
        PCGresMouseWheelEventResult ev = new PCGresMouseWheelEventResult(this, mouse, buttons, event.isConsumed(), history);
        fireEvent(ev);
        return ev.getResult();
    }

    protected void onMouseWheel(PCGresMouseWheelEvent event, PCGresHistory history) {
        fireEvent(event);
        if (!event.isConsumed()) {
            handleMouseWheel(event, history);
        }
    }

    protected void handleMouseWheel(PCGresMouseWheelEvent event, PCGresHistory history) {
        //
    }

    protected void onFocusLost(PCGresComponent newFocusedComponent, PCGresHistory history) {
        PCGresFocusLostEvent event = new PCGresFocusLostEvent(this, newFocusedComponent);
        fireEvent(event);
        if (!event.isConsumed()) {
            handleFocusLost(history);
        }
    }

    protected void handleFocusLost(PCGresHistory history) {
        this.focus = false;
        this.mouseDown = false;
    }

    protected void onFocusGot(PCGresComponent oldFocusedComponent, PCGresHistory history) {
        PCGresFocusGotEvent event = new PCGresFocusGotEvent(this, oldFocusedComponent);
        fireEvent(event);
        if (!event.isConsumed()) {
            handleFocusGot(history);
        }
    }

    protected void handleFocusGot(PCGresHistory history) {
        moveToTop();
        this.focus = true;
    }

    public PCGresComponent getComponentAtPosition(PCVec2I mouse) {

        return this.visible ? this : null;
    }

    public void getComponentsAtPosition(PCVec2I mouse, List<PCGresComponent> list) {

        if(this.visible){
            list.add(this);
        }
    }


    protected void onTick() {
        //
    }

    protected void onDrawTick(float timeStamp) {
        //
    }


    @SuppressWarnings("static-method")
    public Slot getSlotAtPosition(PCVec2I position) {

        return null;
    }

    protected List<String> onGetTooltip(PCVec2I position) {
        List<String> tooltip = getTooltip(position);
        PCGresTooltipGetEvent event = new PCGresTooltipGetEvent(this, tooltip);
        fireEvent(event);
        if (event.isConsumed()) {
            return event.getTooltip();
        }
        return tooltip;
    }

    @SuppressWarnings("static-method")
    protected List<String> getTooltip(PCVec2I position) {

        return null;
    }


    public PCVec2 getRealLocation() {

        if (this.parent == null) {
            return this.rect.getLocationF().mul(getRecursiveZoom());
        }
        return this.rect.getLocationF().mul(getRecursiveZoom()).add(this.parent.getRealLocation()).add(this.parent.getFrame().getLocationF().mul(this.parent.getRecursiveZoom()));
    }


    public PCGresGuiHandler getGuiHandler() {

        if (this.parent == null) return null;
        return this.parent.getGuiHandler();
    }


    public PCGresComponent addEventListener(IGresEventListener eventListener) {

        if (!this.eventListeners.contains(eventListener)) this.eventListeners.add(eventListener);
        return this;
    }


    public void removeEventListener(IGresEventListener eventListener) {

        this.eventListeners.remove(eventListener);
    }


    protected void fireEvent(PCGresEvent event) {

        for (IGresEventListener eventListener : this.eventListeners) {
            if (eventListener instanceof IGresEventListenerEx) {
                IGresEventListenerEx eventListenerEx = (IGresEventListenerEx) eventListener;
                Class<? extends PCGresEvent>[] handelableEvents = eventListenerEx.getHandelableEvents();
                for (int i = 0; i < handelableEvents.length; i++) {
                    if (handelableEvents[i].isInstance(event)) {
                        eventListenerEx.onEvent(event);
                        break;
                    }
                }
            } else {
                eventListener.onEvent(event);
            }
        }
    }

    public int getCState(){
        return this.enabled && this.parentEnabled ? this.mouseDown ? 2 : this.mouseOver ? 1 : 0 : 3;
    }

    protected void drawTexture(String textureName, int x, int y, int width, int height) {

        drawTexture(textureName, x, y, width, height, getCState());
    }


    @SuppressWarnings("static-method")
    protected void drawTexture(String textureName, int x, int y, int width, int height, int state) {

        PCGresTexture texture = PCGres.getGresTexture(textureName);
        if (texture != null) {
            texture.draw(x, y, width, height, state);
        }
    }


    @SuppressWarnings("static-method")
    protected PCVec2I getTextureMinSize(String textureName) {

        PCGresTexture texture = PCGres.getGresTexture(textureName);
        if (texture == null) {
            return new PCVec2I(0, 0);
        }
        return texture.getMinSize();
    }

    @SuppressWarnings("static-method")
    protected PCRectI getTextureFrame(String textureName) {

        PCGresTexture texture = PCGres.getGresTexture(textureName);
        if (texture == null) {
            return new PCRectI();
        }
        return new PCRectI(texture.getFrame());
    }


    @SuppressWarnings("static-method")
    protected PCVec2I getTextureDefaultSize(String textureName) {

        PCGresTexture texture = PCGres.getGresTexture(textureName);
        if (texture == null) {
            return new PCVec2I(0, 0);
        }
        return texture.getDefaultSize();
    }


    @SuppressWarnings("hiding")
    protected void drawString(String text, int x, int y, boolean shadow) {
        PCVec2I size = fontRenderer.getStringSize(text);
        drawString(text, x, y, size.x, size.y, PCGresAlign.H.LEFT, PCGresAlign.V.TOP, shadow);
    }


    @SuppressWarnings("hiding")
    protected void drawString(String text, int x, int y, int width, PCGresAlign.H alignH, boolean shadow) {
        PCVec2I size = fontRenderer.getStringSize(text);
        drawString(text, x, y, width, size.y, alignH, PCGresAlign.V.TOP, shadow);
    }

    @SuppressWarnings("hiding")
    protected void drawString(String text, int x, int y, int width, int height, PCGresAlign.H alignH, PCGresAlign.V alignV, boolean shadow) {
        drawString(text, x, y, width, height, alignH, alignV, shadow, getCState());
    }

    @SuppressWarnings("hiding")
    protected void drawString(String text, int x, int y, int width, int height, PCGresAlign.H alignH, PCGresAlign.V alignV, boolean shadow, int state) {
        int nx = x, ny = y;
        PCVec2I size = fontRenderer.getStringSize(text);
        switch (alignV) {
            case BOTTOM:
                ny += height - size.y;
                break;
            case CENTER:
                ny += height / 2 - size.y / 2;
                break;
            default:
                break;
        }
        String writeText = text;
        if (size.x > width) {
            PCVec2I sizeD = fontRenderer.getStringSize("...");
            writeText = fontRenderer.trimStringToWidth(text, width - sizeD.x) + "...";
        }
        size = fontRenderer.getStringSize(writeText);
        switch (alignH) {
            case CENTER:
                nx += width / 2 - size.x / 2;
                break;
            case RIGHT:
                nx += width - size.x;
                break;
            default:
                break;
        }
        fontRenderer.drawString(writeText, nx, ny, this.fontColors[state], shadow);
        GL11.glEnable(GL11.GL_BLEND);
    }

    public void moveToTop(){
        if(this.parent!=null){
            this.parent.moveToTop(this);
        }
    }

    public void moveToBottom(){
        if(this.parent!=null){
            this.parent.moveToBottom(this);
        }
    }

    protected void onScaleChanged(int newScale){
        //
    }

    protected void onFocusChaned(PCGresComponent oldFocus, PCGresComponent newFocus){
        //
    }

    @SuppressWarnings("static-method")
    public float getZoom(){
        return 1.0f;
    }

    public float getRecursiveZoom(){
        if(this.parent!=null){
            return getZoom()*this.parent.getRecursiveZoom();
        }
        return getZoom();
    }

    protected void addToBase(PCGresComponent c){
        if(this.getParent()!=null){
            this.getParent().addToBase(c);
        }
    }

    public boolean canAddTo(PCGresComponent c) {
        PCGresComponent cc = this;
        while(cc!=null){
            if(cc==c)
                return false;
            cc = cc.getParent();
        }
        return true;
    }

}
