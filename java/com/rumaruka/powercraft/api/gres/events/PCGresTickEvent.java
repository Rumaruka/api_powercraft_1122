package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;

public class PCGresTickEvent extends PCGresPrePostEvent {
    public PCGresTickEvent(PCGresComponent c, EventType type) {
        super(c, type);
    }
}
