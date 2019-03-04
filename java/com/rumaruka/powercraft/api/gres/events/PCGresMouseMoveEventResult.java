package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.events.PCGresMouseMoveEvent.Event;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;

public class PCGresMouseMoveEventResult extends PCGresMouseEventResult {

    private final Event event;

    public PCGresMouseMoveEventResult(PCGresComponent component, PCVec2I mouse, int buttons, Event event, boolean result, PCGresHistory history) {
        super(component, mouse, buttons, result, history);
        this.event = event;
    }

    public Event getEvent(){
        return this.event;
    }
}
