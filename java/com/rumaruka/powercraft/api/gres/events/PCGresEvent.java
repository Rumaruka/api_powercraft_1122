package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;

public abstract class PCGresEvent {


    private final PCGresComponent component;

    protected PCGresEvent(PCGresComponent c){
        this.component=c;
    }

    public PCGresComponent getComponent() {
        return this.component;
    }
}
