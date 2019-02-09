package com.rumaruka.powercraft.api;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class PCMaterial extends Material {

    public static final Material MACHINES = new PCMaterial(MapColor.IRON).setImmovableMobility();

    private boolean liquid;
    private boolean solid = true;
    private boolean canBlockGrass = true;
    private boolean blocksMovement = true;
    private boolean translucent;

    public PCMaterial(MapColor color){
        super(color);
    }

    @Override
    public boolean isLiquid() {
        return this.liquid;
    }

    protected PCMaterial setLiquid(){
        this.liquid = true;
        return this;
    }

    @Override
    public boolean isSolid() {
        return this.solid;
    }

    protected PCMaterial setNotSolid(){
        this.solid = false;
        return this;
    }

    public boolean getCanBlockGrass() {
        return this.canBlockGrass;
    }

    protected PCMaterial setNotBlockGrass(){
        this.canBlockGrass = false;
        return this;
    }

    @Override
    public boolean blocksMovement() {
        return this.blocksMovement;
    }

    protected PCMaterial setNotBlocksMovement(){
        this.blocksMovement = false;
        return this;
    }

    @Override
    public boolean isOpaque(){
        return this.translucent ? false : this.blocksMovement();
    }

    protected PCMaterial setTranslucent() {
        this.translucent = true;
        return this;
    }

    @Override
    protected Material setRequiresTool(){
        super.setRequiresTool();
        return this;
    }

    @Override
    protected PCMaterial setBurning(){
        super.setBurning();
        return this;
    }

    @Override
    public PCMaterial setReplaceable(){
        super.setReplaceable();
        return this;
    }

    @Override
    protected PCMaterial setNoPushMobility(){
        super.setNoPushMobility();
        return this;
    }

    @Override
    protected PCMaterial setImmovableMobility(){
        super.setImmovableMobility();
        return this;
    }

    @Override
    protected PCMaterial setAdventureModeExempt(){
        super.setAdventureModeExempt();
        return this;
    }
}
