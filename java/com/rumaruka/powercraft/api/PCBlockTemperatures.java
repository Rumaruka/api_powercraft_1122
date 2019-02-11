package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.block.AbstractBlockBase;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.fluids.IFluidBlock;

import java.util.HashMap;

public final class PCBlockTemperatures {

    public static final int CELCIUS0_TEMPERATURE = 273;

    public static final int DEFAULT_TEMPERATURE = 24+CELCIUS0_TEMPERATURE;

    private static HashMap<Block,Integer> blockTemperatures = new HashMap<Block, Integer>();


    public static int getTemperature(World world, int x, int y, int z){
        int temperature = getTemperatureForBiomeAndHeight2(world, x, y, z);
        int blockTemperature = getTemperature2(world, x, y, z);
        return blockTemperature+temperature-DEFAULT_TEMPERATURE;
    }

    /**
     * Get the temperature of an block not connected to the hight-temperature
     * @param world the world
     * @param x x-coord
     * @param y y-coord
     * @param z z-coord
     * @return the temperature in Kelvin
     */
    public static int getTemperature2(World world, int x, int y, int z){
        Block block = PCUtils.getBlock(world, new BlockPos( x, y, z));
        if(block instanceof AbstractBlockBase){
            return ((AbstractBlockBase)block).getTemperature(world, x, y, z);
        }else if(block instanceof IFluidBlock){
            return ((IFluidBlock)block).getFluid().getTemperature(world, new BlockPos( x, y, z));
        }
        return getTemperatureFallback(block);
    }

    /**
     * Get the temperature of an block
     * @param block the block
     * @return the temperature in Kelvin
     */
    public static int getTemperature(Block block) {
        if(block instanceof AbstractBlockBase){
            return ((AbstractBlockBase)block).getTemperature();
        }else if(block instanceof IFluidBlock){
            return ((IFluidBlock)block).getFluid().getTemperature();
        }
        return getTemperatureFallback(block);
    }

    /**
     * Temperature of Minecraft blocks
     * @param block the Minecraft block
     * @return the temperature in Kelvin
     */
    public static int getTemperatureFallback(Block block){
        Integer temperature = blockTemperatures.get(block);
        return temperature==null?DEFAULT_TEMPERATURE:temperature;
    }

    /**
     * Get the temperature for a specific height and biome
     * @param world the world
     * @param x x-coord
     * @param y y-coord
     * @param z z-coord
     * @return the temperature in Kelvin
     */
    public static int getTemperatureForBiomeAndHeight(World world, int x, int y, int z){
        Biome biome = PCUtils.getBiome(world, x, z);
        return (int) (biome.getTemperature(new BlockPos(x, y, z))-0.1)*35+CELCIUS0_TEMPERATURE;
    }

    /**
     * Get the temperature for a specific height and biome and weather
     * @param world the world
     * @param x x-coord
     * @param y y-coord
     * @param z z-coord
     * @return the temperature in Kelvin
     */
    public static int getTemperatureForBiomeAndHeight2(World world, int x, int y, int z){
        Biome biome = PCUtils.getBiome(world, x, z);
        int temperature = (int) ((biome.getTemperature(new BlockPos(x, y, z))-0.2)*32);
        if(world.isRaining() && (biome.getEnableSnow())){
            temperature -= biome.getRainfall()*10;
        }
        int height = world.getHeight(x, z);
        if(height<y+10 && height>=y){
            temperature = (int) (temperature*(1-(height-y)/10.0f));
        }
        if(y<40){
            temperature += 40-y;
        }
        return temperature+CELCIUS0_TEMPERATURE;
    }

    /**
     * Set a temperature for a Minecraft block
     * @param block the block which should have a temperature
     * @param temperature the temperature for the block
     */
    public static void setTemperatureFor(Block block, int temperature){
        if(block instanceof AbstractBlockBase || block instanceof IFluidBlock){
            PCLogger.warning("PowerCraft Blocks and IFluidBlock's have a function for temperature");
        }else{
            blockTemperatures.put(block,temperature);
        }
    }

    /**
     * convert °C to Kelvin
     * @param temperature temperature in °C
     * @return temperature in Kelvin
     */
    public static int celciusToKelvin(int temperature){
        return temperature+CELCIUS0_TEMPERATURE;
    }

    /**
     * convert Kelvin to °C
     * @param temperature temperature in Kelvin
     * @return temperature in °C
     */
    public static int kelvinToCelcius(int temperature){
        return temperature-CELCIUS0_TEMPERATURE;
    }

    static{
        setTemperatureFor(Blocks.LAVA, 1000+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.LIT_FURNACE, 100+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.TORCH, 50+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.ICE, -10+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.PACKED_ICE, -30+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.SNOW, -10+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.SNOW_LAYER, -10+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.FIRE, 100+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.LIT_REDSTONE_LAMP, 50+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.LIT_PUMPKIN, 40+CELCIUS0_TEMPERATURE);
        setTemperatureFor(Blocks.REDSTONE_TORCH, 40+CELCIUS0_TEMPERATURE);
    }

    private PCBlockTemperatures(){
        PCUtils.staticClassConstructor();
    }
}
