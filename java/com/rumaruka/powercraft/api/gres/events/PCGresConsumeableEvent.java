package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;

public abstract class PCGresConsumeableEvent extends PCGresEvent{

    private boolean consumed;

    protected PCGresConsumeableEvent(PCGresComponent c) {
        super(c);
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    public void consume( ) {
        this.consumed=true;
    }
}
