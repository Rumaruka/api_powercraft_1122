package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;

public class PCGresMouseWheelEvent extends PCGresMouseEvent{
    private final int wheel;


    protected PCGresMouseWheelEvent(PCGresComponent c, PCVec2I mouse, int buttons, int wheel, PCGresHistory history) {
        super(c, mouse, buttons, history);
        this.wheel=wheel;
    }

    public int getWheel() {
        return this.wheel;
    }
}
