package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;

public class PCGresFocusLostEvent extends PCGresConsumeableEvent{

    private final PCGresComponent newFocusComponent;

    public PCGresFocusLostEvent(PCGresComponent c, PCGresComponent newFocus) {
        super(c);
        this.newFocusComponent = newFocus;
    }


    public PCGresComponent getNewFocusComponent() {
        return this.newFocusComponent;
    }
}
