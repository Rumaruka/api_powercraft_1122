package com.rumaruka.powercraft.api.energy;

public interface IEnergyGridProvider extends IEnergyGridTile {

    float getEnergyUseable();

    void takeEnergy(float energy);

    boolean dynamic();
}
