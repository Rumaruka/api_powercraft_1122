package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;

public class PCGresFocusGotEvent extends PCGresConsumeableEvent {

    private final PCGresComponent oldFocusedComponent;

    public PCGresFocusGotEvent(PCGresComponent c, PCGresComponent oldFocus) {
        super(c);
        this.oldFocusedComponent=oldFocus;
    }

    public PCGresComponent getOldFocusedComponent() {
        return this.oldFocusedComponent;
    }
}
