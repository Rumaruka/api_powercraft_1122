package com.rumaruka.powercraft.api.energy;

public interface IEnergyGridConduit extends IEnergyGridTile {


    float getMaxEnergy();

    void setEnergyFlow(float energy);

    void handlerToMuchEnergy(float energy);
}
