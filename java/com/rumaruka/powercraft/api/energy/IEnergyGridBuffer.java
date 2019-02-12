package com.rumaruka.powercraft.api.energy;



public interface IEnergyGridBuffer extends IEnergyGridTile {

    float getEnergyLevel();
    float getEnergyMaxIn();
    float getEnergyMaxOut();
    float addEnergy(float energy);

}

