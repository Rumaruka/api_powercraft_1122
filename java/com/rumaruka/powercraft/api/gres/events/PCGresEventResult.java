package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;

public abstract class PCGresEventResult extends PCGresEvent {

    private boolean result;

    protected PCGresEventResult(PCGresComponent c,boolean result) {
        super(c);
        this.result=result;
    }


    public boolean getResult() {
        return this.result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
