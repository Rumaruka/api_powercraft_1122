package com.rumaruka.powercraft.api.energy;

final class PCEnergyNodeProvider extends PCEnergyNode<IEnergyGridProvider> {

    protected float useable;

    protected float used;

    protected boolean dynamic;

    PCEnergyNodeProvider(PCEnergyGrid grid, IEnergyGridProvider tile) {
        super(grid, tile);
    }

    @Override
    protected boolean canBecomeEdge() {
        return false;
    }

    @Override
    public void onTickStart() {
        this.used = 0;
        this.useable = getTiles().getEnergyUseable();
        this.dynamic = getTiles().dynamic();
    }

    @Override
    public void onTickEnd() {
        getTiles().takeEnergy(this.used);
    }

    @Override
    public float getFlow() {
        return -this.used;
    }

    @Override
    public void addToInfo(PCEnergyInfo info) {
        if (this.dynamic) info.notProduceNeccecerly += this.useable;
    }

    @Override
    public float takeEnergy() {
        this.used = this.useable;
        return this.useable;
    }

    @Override
    public float notUsing(float energy, float p) {
        if (this.dynamic) {
            this.used = this.useable * (1 - p);
            return energy - this.useable + this.used;
        }
        return energy;
    }
}

