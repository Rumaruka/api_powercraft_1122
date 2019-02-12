package com.rumaruka.powercraft.api.energy;

public interface IEnergyGridConsumer extends IEnergyGridTile {

    float getEnergyRequested();

    void  useEnergy(float energy);

    float getMaxPercentToWork();
}
