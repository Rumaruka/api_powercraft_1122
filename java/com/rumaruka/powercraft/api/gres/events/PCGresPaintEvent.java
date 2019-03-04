package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;

public class PCGresPaintEvent extends PCGresPrePostEvent {

    private final float timeStamp;

    public PCGresPaintEvent(PCGresComponent c, EventType type,float timeStamp) {
        super(c, type);
        this.timeStamp=timeStamp;
    }

    public float getTimeStamp() {
        return this.timeStamp;
    }
}
