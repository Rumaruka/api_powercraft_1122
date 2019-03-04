package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;

public class PCGresKeyEventResult extends PCGresEventResult {


    private final char key;
    private final int keyCode;
    private final boolean repeat;
    private final PCGresHistory history;

    public PCGresKeyEventResult(PCGresComponent c, char key, int keyCode, boolean repeat, boolean result, PCGresHistory history) {
        super(c,result);
        this.key = key;
        this.keyCode = keyCode;
        this.repeat = repeat;
        this.history = history;
    }

    public char getKey(){
        return this.key;
    }

    public int getKeyCode(){
        return this.keyCode;
    }

    public boolean isRepeatEvents(){
        return this.repeat;
    }

    public PCGresHistory getHistory(){
        return this.history;
    }

}
