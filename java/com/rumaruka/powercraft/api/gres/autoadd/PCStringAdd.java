package com.rumaruka.powercraft.api.gres.autoadd;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PCStringAdd {

    public PCGresComponent component;
    public PCGresDocument document;
    public PCGresDocumentLine documentLine;
    public int pos;
    public String toAdd;
    public int cursorPos;
}
