package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;

public class PCGresMouseWheelEventResult extends PCGresMouseEventResult {


    protected PCGresMouseWheelEventResult(PCGresComponent component, PCVec2I mouse, int buttons, boolean result, PCGresHistory history) {
        super(component, mouse, buttons, result, history);
    }
}
