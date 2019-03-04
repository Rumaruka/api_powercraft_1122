package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;

public abstract class PCGresMouseEventResult extends PCGresEventResult {

    private final PCVec2I mouse;
    private final int buttons;
    private final PCGresHistory history;

    protected PCGresMouseEventResult(PCGresComponent component, PCVec2I mouse, int buttons, boolean result, PCGresHistory history) {
        super(component, result);
        this.mouse = new PCVec2I(mouse);
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
