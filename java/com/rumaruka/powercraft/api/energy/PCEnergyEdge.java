package com.rumaruka.powercraft.api.energy;

import com.rumaruka.powercraft.api.grid.PCEdge;

public class PCEnergyEdge extends PCEdge<PCEnergyGrid, IEnergyGridTile, PCEnergyNode<?>, PCEnergyEdge> {

    private float maxEnergy;

    PCEnergyEdge(PCEnergyGrid grid, PCEnergyNode<?> start, PCEnergyNode<?> end) {
        super(grid, start, end);
    }

    @Override
    protected void onChanged() {
        this.maxEnergy = Float.POSITIVE_INFINITY;
        for(IEnergyGridTile tile:this.tiles){
            float conduitMax = ((IEnergyGridConduit)tile).getMaxEnergy();
            if(conduitMax>=0 && conduitMax<this.maxEnergy){
                this.maxEnergy = conduitMax;
            }
        }
    }
}
