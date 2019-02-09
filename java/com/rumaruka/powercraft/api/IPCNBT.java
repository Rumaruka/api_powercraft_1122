package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.PCField.Flag;
import net.minecraft.nbt.NBTTagCompound;

public interface IPCNBT {
     void saveToNBT(NBTTagCompound tag, Flag flag);
}
