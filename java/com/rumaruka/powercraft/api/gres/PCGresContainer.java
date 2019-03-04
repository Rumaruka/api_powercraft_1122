package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;
import com.rumaruka.powercraft.api.gres.layot.IGresLayout;
import com.rumaruka.powercraft.api.renderer.PCOpenGL;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;


@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public abstract class PCGresContainer extends PCGresComponent {

    protected final List<PCGresComponent> children = new CopyOnWriteArrayList<PCGresComponent>();
    protected final List<PCGresComponent> layoutChildOrder = new ArrayList<PCGresComponent>();

    private IGresLayout layout;

    private boolean updatingLayout;

    private boolean updatingLayoutAgain;

    protected final PCRectI frame = new PCRectI(0, 0, 0, 0);


    public PCGresContainer() {

    }

    public PCGresContainer(String text) {
        super(text);
    }

    public List<PCGresComponent> getChildren(){
        return new PCImmutableList<PCGresComponent>(this.children);
    }

    public List<PCGresComponent> getLayoutChildOrder(){
        return this.layoutChildOrder;
    }

    public PCRectI getFrame() {

        return new PCRectI(this.frame);
    }


    public PCRectI getChildRect() {

        return new PCRectI(this.frame.x, this.frame.y, this.rect.width - this.frame.x - this.frame.width, this.rect.height - this.frame.y - this.frame.height);
    }


    public PCGresContainer setLayout(IGresLayout layout) {

        this.layout = layout;
        notifyChange();
        return this;
    }


    public IGresLayout getLayout() {

        return this.layout;
    }


    public void updateLayout() {

        if (this.layout != null) {
            if (!this.updatingLayout) {
                this.updatingLayout = true;
                do {
                    this.updatingLayoutAgain = false;
                    this.layout.updateLayout(this);
                } while (this.updatingLayoutAgain);
                this.updatingLayout = false;
            } else {
                this.updatingLayoutAgain = true;
            }
        }
    }


    public void add(PCGresComponent component) {

        if (!this.children.contains(component)) {
            addChild(component);
            component.setParent(this);
            if(component.getParent()==this){
                giveChildFocus(component);
                notifyChange();
            }else{
                this.children.remove(component);
                this.layoutChildOrder.remove(component);
            }
        }
    }

    protected void addChild(PCGresComponent component){
        this.children.add(component);
        this.layoutChildOrder.add(component);
    }

    @SuppressWarnings("static-method")
    protected void giveChildFocus(PCGresComponent component){
        component.takeFocus();
    }

    public void remove(PCGresComponent component) {

        this.children.remove(component);
        this.layoutChildOrder.remove(component);
        if(component.hasFocusOrChild()){
            takeFocus();
        }
        if(component.getParent()==this)
            component.setParent(null);
        notifyChange();
    }

    public void removeOnly(PCGresComponent component) {

        this.children.remove(component);
        this.layoutChildOrder.remove(component);
        if(component.hasFocusOrChild()){
            takeFocus();
        }
        notifyChange();
    }

    public void removeNoFocus(PCGresComponent component) {

        this.children.remove(component);
        this.layoutChildOrder.remove(component);
        if(component.getParent()==this)
            component.setParent(null);
        notifyChange();
    }

    public void removeAll() {

        while (!this.children.isEmpty()){
            PCGresComponent component = this.children.remove(0);
            this.layoutChildOrder.remove(component);
            if(component.hasFocus()){
                takeFocus();
            }
            component.setParent(null);
            notifyChange();
        }
        notifyChange();
    }


    public boolean isChild(PCGresComponent component) {

        return this.children.contains(component);
    }


    public void notifyChildChange(PCGresComponent component) {

        notifyChange();
    }


    @Override
    protected void notifyChange() {

        super.notifyChange();
        updateLayout();
    }


    @Override
    public void setMinSize(PCVec2I minSize) {

        if (minSize == null && this.layout != null) {
            this.minSize.setTo(this.layout.getMinimumLayoutSize(this));
            this.minSize.x += this.frame.x + this.frame.width;
            this.minSize.y += this.frame.y + this.frame.height;
            this.minSizeSet = false;
        } else {
            if (minSize == null) {
                this.minSize.setTo(calculateMinSize());
                this.minSize.x += this.frame.x + this.frame.width;
                this.minSize.y += this.frame.y + this.frame.height;
                this.minSizeSet = false;
            } else {
                this.minSize.setTo(minSize);
                this.minSizeSet = true;
            }
        }
        setSize(getSize().max(this.minSize));
    }


    @Override
    public void setMaxSize(PCVec2I maxSize) {

        if (maxSize == null) {
            this.maxSize.setTo(calculateMaxSize());

            this.maxSizeSet = false;
        } else {
            this.maxSize.setTo(maxSize);
            this.maxSizeSet = true;
        }
    }


    @Override
    public void setPrefSize(PCVec2I prefSize) {

        if (prefSize == null && this.layout != null) {
            this.prefSize.setTo(this.layout.getPreferredLayoutSize(this));
            if(this.prefSize.x!=-1)
                this.prefSize.x += this.frame.x + this.frame.width;
            if(this.prefSize.y!=-1)
                this.prefSize.y += this.frame.y + this.frame.height;
            this.prefSizeSet = false;
        } else {
            if (prefSize == null) {
                this.prefSize.setTo(calculatePrefSize());
                if(this.prefSize.x!=-1)
                    this.prefSize.x += this.frame.x + this.frame.width;
                if(this.prefSize.y!=-1)
                    this.prefSize.y += this.frame.y + this.frame.height;
                this.prefSizeSet = false;
            } else {
                this.prefSize.setTo(prefSize);
                this.prefSizeSet = true;
            }
        }

    }


    @Override
    public void setVisible(boolean visible) {

        super.setVisible(visible);
        for (PCGresComponent child : this.children) {
            child.setParentVisible(visible);
        }
    }


    @Override
    protected void setParentVisible(boolean visible) {

        super.setParentVisible(visible);
        for (PCGresComponent child : this.children) {
            child.setParentVisible(visible);
        }
    }


    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        for (PCGresComponent child : this.children) {
            child.setParentEnabled(enabled);
        }
    }


    @Override
    protected void setParentEnabled(boolean enabled) {

        super.setParentEnabled(enabled);
        for (PCGresComponent child : this.children) {
            child.setParentEnabled(this.visible);
        }
    }


    @SuppressWarnings("hiding")
    @Override
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
            PCRect oldRect = new PCRect(rect);
            PCOpenGL.pushMatrix();
            GL11.glTranslatef(this.rect.x, this.rect.y, 0);
            GL11.glScalef(tzoom, tzoom, 1);
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            paint(scissor, scale, displayHeight, timeStamp, zoomm);
            doDebugRendering(0, 0, rect.width, rect.height);
            rect.x += this.frame.x*zoom;
            rect.y += this.frame.y*zoom;
            rect.width -= this.frame.x + this.frame.width;
            rect.height -= this.frame.y + this.frame.height;
            GL11.glTranslatef(this.frame.x, this.frame.y, 0);
            PCVec2 noffset = rect.getLocation();
            ListIterator<PCGresComponent> iterator = this.children.listIterator(this.children.size());
            scissor = setDrawRect(scissor, rect, scale, displayHeight, zoomm);
            if(scissor!=null){
                while(iterator.hasPrevious()){
                    iterator.previous().doPaint(noffset, scissor, scale, displayHeight, timeStamp, zoomm);
                }
            }
            PCOpenGL.popMatrix();
            PCOpenGL.pushMatrix();
            GL11.glTranslatef(this.rect.x, this.rect.y, 0);
            GL11.glScalef(tzoom, tzoom, 1);
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            setDrawRect(scissorOld, oldRect, scale, displayHeight, zoomm);
            postPaint(scissor, scale, displayHeight, timeStamp, zoomm);
            PCOpenGL.popMatrix();
        }
    }

    protected void postPaint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom){
        //
    }

    @SuppressWarnings("hiding")
    @Override
    public PCGresComponent getComponentAtPosition(PCVec2I position) {

        if (this.visible) {
            position.x /= getZoom();
            position.y /= getZoom();
            if(getChildRect().contains(position)){
                PCVec2I nposition = position.sub(this.frame.getLocation());
                for (PCGresComponent child : this.children) {
                    PCRectI rect = child.getRect();
                    if (rect.contains(nposition)){
                        PCGresComponent component = child.getComponentAtPosition(nposition.sub(rect.getLocation()));
                        if (component != null) return component;
                    }
                }
            }
            return this;
        }
        return null;
    }

    @SuppressWarnings("hiding")
    @Override
    public void getComponentsAtPosition(PCVec2I position, List<PCGresComponent> list) {

        if (this.visible) {
            position.x /= getZoom();
            position.y /= getZoom();
            if(getChildRect().contains(position)){
                PCVec2I nposition = position.sub(this.frame.getLocation());
                for (PCGresComponent child : this.children) {
                    PCRectI rect = child.getRect();
                    if (rect.contains(nposition)){
                        child.getComponentsAtPosition(nposition.sub(rect.getLocation()), list);
                    }
                }
            }
            list.add(this);
        }
    }

    @Override
    protected void onTick() {

        for (PCGresComponent child : this.children) {
            child.onTick();
        }
    }

    @Override
    protected void onDrawTick(float timeStamp) {

        for (PCGresComponent child : this.children) {
            child.onDrawTick(timeStamp);
        }
    }


    @SuppressWarnings("hiding")
    @Override
    public Slot getSlotAtPosition(PCVec2I position) {

        if (this.visible && getChildRect().contains(position)) {
            PCVec2I nposition = position.sub(this.frame.getLocation());
            for (PCGresComponent child : this.children) {
                PCRectI rect = child.getRect();
                if (rect.contains(nposition)){
                    Slot slot = child.getSlotAtPosition(nposition.sub(rect.getLocation()));
                    return slot;
                }
            }
        }
        return null;
    }


    @Override
    protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PCGresHistory history) {

        if (this.visible) {
            for (PCGresComponent child : this.children) {
                child.tryActionOnKeyTyped(key, keyCode, repeat, history);
            }
        }
    }

    protected void moveToTop(PCGresComponent component){
        if(this.children.remove(component)){
            this.children.add(0, component);
        }
        moveToTop();
    }

    protected void moveToBottom(PCGresComponent component){
        if(this.children.remove(component)){
            this.children.add(component);
        }
    }

    @Override
    protected void onScaleChanged(int newScale){
        for (PCGresComponent child : this.children) {
            child.onScaleChanged(newScale);
        }
    }

    @Override
    protected void onFocusChaned(PCGresComponent oldFocus, PCGresComponent newFocus){
        for (PCGresComponent child : this.children) {
            child.onFocusChaned(oldFocus, newFocus);
        }
    }

    @Override
    public boolean hasFocusOrChild(){
        if(this.focus)
            return true;
        for(PCGresComponent child:this.children){
            if(child.hasFocusOrChild()){
                return true;
            }
        }
        return false;
    }
}
