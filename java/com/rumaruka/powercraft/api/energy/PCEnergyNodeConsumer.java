package com.rumaruka.powercraft.api.energy;

final class PCEnergyNodeConsumer extends PCEnergyNode<IEnergyGridConsumer> {

    protected float requested;

    protected float useable;

    protected float maxWorkPercent;

    PCEnergyNodeConsumer(PCEnergyGrid grid, IEnergyGridConsumer tile) {
        super(grid, tile);
    }

    @Override
    protected boolean canBecomeEdge() {
        return false;
    }

    @Override
    public void onTickStart(){
        this.useable = 0;
        this.requested = getEnergyTile().getEnergyRequested();
        this.maxWorkPercent = getEnergyTile().getMaxPercentToWork();
    }

    @Override
    public void onTickEnd() {
        getEnergyTile().useEnergy(this.useable);
    }

    @Override
    public float getFlow() {
        return this.useable;
    }

    @Override
    public void addToInfo(PCEnergyInfo info) {
        info.energyRequested += this.requested;
    }

    @Override
    public float takeEnergy() {
        return 0;
    }

    @Override
    public float useEnergy(float energy, float p) {
        if(this.maxWorkPercent<=p){
            this.useable = this.requested*p;
            return energy - this.useable;
        }
        return energy;
    }
}
