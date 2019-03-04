package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;

public class PCGresMouseMoveEvent extends PCGresMouseEvent {

    private final Event event;

    protected PCGresMouseMoveEvent(PCGresComponent c, PCVec2I mouse, int buttons,Event event, PCGresHistory history) {
        super(c, mouse, buttons, history);
        this.event=event;
    }

    public Event getEvent() {
        return this.event;
    }
    public  enum Event{
        MOVE, LEAVE, ENTER
    }
}
