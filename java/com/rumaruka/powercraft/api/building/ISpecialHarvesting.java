package com.rumaruka.powercraft.api.building;

import net.minecraft.world.World;

public interface ISpecialHarvesting {

     boolean useFor(World world, int x, int y, int z, int priority);

     PCHarvest harvest(World world, int x, int y, int z, int usesLeft);
}
