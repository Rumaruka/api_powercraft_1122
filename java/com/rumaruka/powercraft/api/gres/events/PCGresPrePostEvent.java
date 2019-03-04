package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;

public class PCGresPrePostEvent extends PCGresEvent {

    private EventType type;

    public PCGresPrePostEvent(PCGresComponent c,EventType type) {

        super(c);
        this.type=type;
    }

    public EventType getEventType(){
        return this.type;
    }

    public enum EventType{
        PRE,POST
    }
}
