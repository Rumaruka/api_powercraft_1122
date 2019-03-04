package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;

public abstract class PCGresMouseEvent extends PCGresConsumeableEvent {

    private final PCVec2I mouse;
    private final int buttons;
    private final PCGresHistory history;

    protected PCGresMouseEvent(PCGresComponent c, PCVec2I mouse, int buttons, PCGresHistory history) {
        super(c);
        this.mouse = mouse;
        this.buttons = buttons;
        this.history = history;
    }
    public PCVec2I getMouse(){
        return this.mouse;
    }

    public int getButtonState(){
        return this.buttons;
    }

    public PCGresHistory getHistory(){
        return this.history;
    }
}
