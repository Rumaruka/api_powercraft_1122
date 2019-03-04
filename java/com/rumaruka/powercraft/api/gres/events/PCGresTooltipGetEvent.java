package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.PCImmutableList;
import com.rumaruka.powercraft.api.gres.PCGresComponent;

import java.util.List;

public class PCGresTooltipGetEvent extends PCGresConsumeableEvent {


    private List<String>tooltip;

    protected PCGresTooltipGetEvent(PCGresComponent c,List<String> tooltip) {
        super(c);
        this.tooltip=tooltip;
    }

    public List<String> getTooltip() {
        return this.tooltip==null?null:new PCImmutableList<String>(this.tooltip);
    }

    public void setTooltip(List<String> tooltip) {
        this.tooltip = tooltip;
    }
}
