package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;

public class PCGresKeyEvent extends PCGresConsumeableEvent {

    private final char key;
    private final int keyCode;
    private final boolean repeat;
    private final PCGresHistory history;

    public PCGresKeyEvent(PCGresComponent c, char key, int keyCode, boolean repeat, PCGresHistory history) {
        super(c);
        this.key = key;
        this.keyCode = keyCode;
        this.history = history;
        this.repeat = repeat;
    }

    public char getKey() {
        return this.key;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public boolean isRepeatEvent() {
        return  this.repeat;
    }

    public PCGresHistory getHistory() {
        return this.history;
    }
}
