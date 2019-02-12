package com.rumaruka.powercraft.api.energy;

final class PCEnergyNodeConduit extends PCEnergyNode<IEnergyGridConduit> {

    PCEnergyNodeConduit(PCEnergyGrid grid, IEnergyGridConduit tile) {
        super(grid, tile);
    }

    public float getMaxEnergy(){
        return getTiles().getMaxEnergy();
    }

    @Override
    public float getFlow() {
        return 0;
    }

    @Override
    public void addToInfo(PCEnergyInfo info) {
        //
    }

    @Override
    public float takeEnergy() {
        return 0;
    }
}
