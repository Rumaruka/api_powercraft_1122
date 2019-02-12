package com.rumaruka.powercraft.api.energy;

final class PCEnergyNodeBuffer extends PCEnergyNode<IEnergyGridBuffer> implements Comparable<PCEnergyNodeBuffer> {

    protected float level;
    protected float maxIn;
    protected float maxOut;
    protected float used;

    PCEnergyNodeBuffer(PCEnergyGrid grid, IEnergyGridBuffer tile) {
        super(grid, tile);
    }



    @Override
    protected boolean canBecomeEdge() {
        return false;
    }

    @Override
    public void onTickStart() {
        this.level = getTiles().getEnergyLevel();
        this.maxIn = getTiles().getEnergyMaxIn();
        this.maxOut = getTiles().getEnergyMaxOut();
        this.used = 0;
    }

    @Override
    public void onTickEnd() {
        getTiles().addEnergy(this.used);
    }

    @Override
    public float getFlow() {
        return this.used;
    }

    @Override
    public void addToInfo(PCEnergyInfo info) {
        info.energyWantBuffers += this.maxIn + this.maxOut;
    }

    @Override
    public float takeEnergy() {
        this.level -= this.maxOut;
        this.used = -this.maxOut;
        return this.maxOut;
    }

    @Override
    public int compareTo(PCEnergyNodeBuffer o) {
        return (float)this.level>o.level?1:this.level<o.level?-1:0;
    }

    public float addEnergy(float energy, float toAdd) {
        this.used += toAdd;
        float nta = toAdd;
        if(this.used>this.maxIn){
            nta -= this.used-this.maxIn;
            this.used=this.maxIn;
        }
        return energy-nta;
    }

    public boolean full() {
        return this.used>=this.maxIn;
    }

}