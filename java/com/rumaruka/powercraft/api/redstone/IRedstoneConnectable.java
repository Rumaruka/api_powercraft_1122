package com.rumaruka.powercraft.api.redstone;

import com.rumaruka.powercraft.api.PCDirection;
import net.minecraft.world.World;

public interface IRedstoneConnectable {


     boolean canRedstoneConnectTo(World world, int x, int y, int z, PCDirection side, int faceSide);

     int getRedstonePower(World world, int x, int y, int z, PCDirection side, int faceSide);

     void setRedstonePower(World world, int x, int y, int z, PCDirection side, int faceSide, int value);
}
