package com.rumaruka.powercraft.api.building;

import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.reflect.PCSecurity;
import com.rumaruka.powercraft.api.xml.PCXMLLoader;
import com.rumaruka.powercraft.api.xml.PCXMLNode;
import com.rumaruka.powercraft.api.xml.PCXMLProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PCCropHarvesting implements ISpecialHarvesting {
    private static final PCCropHarvesting INSTANCE = new PCCropHarvesting();
    private static final File folder = PCUtils.getPowerCraftFile("crops", null);
    private static List<Crop> crops;
    static Random rand = new Random();

    private PCCropHarvesting(){
        if(INSTANCE!=null){
            PCUtils.staticClassConstructor();
        }
    }

    public static void register(){
        PCSecurity.allowedCaller("PCTreeHarvesting.register()", PCApi.class);
        loadCrops();
        PCBuild.addSpecialHarvesting(INSTANCE);
    }

    @Override
    public boolean useFor(World world, int x, int y, int z, int priority) {
        if(priority<2){
            return false;
        }
        Block block = PCUtils.getBlock(world, x, y, z);
        int meta = PCUtils.getMetadata(world, x, y, z);
        if(getCropFor(block, meta)!=null){
            return true;
        }
        return block instanceof BlockCrops;
    }

    @Override
    public PCHarvest harvest(World world, int x, int y, int z, int usesLeft) {
        PCHarvest harvest = new PCHarvest();
        if(maxGrown(world, x, y, z)){
            harvest.positions.add(new PCVec3I(x, y, z));
        }
        return harvest;
    }

    public static boolean maxGrown(World world, int x, int y, int z){
        Block block = PCUtils.getBlock(world, x, y, z);
        int meta = PCUtils.getMetadata(world, x, y, z);
        Crop crop = getCropFor(block, meta);
        if(crop==null){
            if(!(block instanceof BlockCrops))
                return true;
            return  meta==7;
        }
        ResourceLocation blockSID = PCUtils.getBlockSID(block);
        return crop.isFinished(blockSID, meta);
    }

    private static Crop getCropFor(Block block, int meta){
        for(Crop crop:crops){
            if(crop.isCrop(PCUtils.getBlockSID(block), meta)){
                return crop;
            }
        }
        return null;
    }

    private static final String defaultFileContent =
            "<?xml version='1.1' encoding='UTF-8' ?>\n"
                    + "<!-- \n"
                    + "  CROP HARVESTER CONFIG FILE\n\n"
                    + "  You can add your own crops into this file.\n"
                    + "  Any other xml files in this folder will be parsed too.\n"
                    + "  If you make a setup file for some mod, please post it on forums.\n\n"
                    + "  Format:\n"
                    + "  <crops>\n"
                    + "  \t<crop name=\"Name\">\n"
                    + "  \t\t<block id=\"Block:id\" growing=\"metas\" harvesting=\"metas\"/>\n"
                    + "  \t</crop>\n"
                    + "  </crops>\n\n"
                    + "  Special values:\n"
                    + "  metas = -1  ...  any metadata\n"
                    + "  Item meta can be ranged - use [4;7] for meta in range 4 to 7 (inclusive).\n"
                    + "  or list metas 1,2,3 or ranges 1,[4;7] (this would be equalent to 1,4,5,6,7)\n"
                    + "-->\n\n"
                    + "<crops>\n\n"
                    + "</crops>";

    /**
     * Call this method to explicitly init static fields -> list of crops from
     * XML files
     */
    private static void loadCrops() {
        if (crops!=null) {
            return;
        }

        crops = new ArrayList<Crop>();

        PCLogger.finer("Loading XML configuration for crops.");

        if (!(new File(folder + "/default.xml")).exists()) {

            try {
                PCLogger.finest("Generating default crops config in " + folder + "/default.xml");

                FileWriter out = new FileWriter(new File(folder + "/default.xml"));

                // write the default crops
                out.write(defaultFileContent);

                out.close();

            } catch (IOException e) {
                PCLogger.severe("Generating default crops config file failed due to an IOException.");
                e.printStackTrace();
            }

        }

        String[] files = folder.list(new FilenameFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {
                return arg1.matches("[^.]+[.]xml");
            }
        });

        for (String filename : files) {

            PCLogger.finest("* loading file " + filename + "...");
            File file = new File(folder + "/" + filename);
            parseFile(file);

        }

        PCLogger.finer("Crops configuration loaded.");

    }

    /**
     * Load and parse XML file with crops specs
     *
     * @param file the file to load
     */
    @SuppressWarnings("hiding")
    private static void parseFile(File file) {

        PCXMLNode node = PCXMLLoader.load(file);

        for(int i=0; i<node.getChildCount(); i++){
            PCXMLNode crops = node.getChild(i);
            if(!crops.getName().equalsIgnoreCase("Crops"))
                continue;
            for(int j=0; j<crops.getChildCount(); j++){
                PCXMLNode crop = node.getChild(i);
                if(!crop.getName().equalsIgnoreCase("Crop"))
                    continue;
                Crop c = new Crop();
                for(int k=0; k<crop.getChildCount(); k++){
                    PCXMLNode block = crop.getChild(k);
                    if(!block.getName().equalsIgnoreCase("Block"))
                        continue;
                    PCXMLProperty property = block.getProperty("id");
                    if(property==null){
                        PCLogger.warning("Crop manager - parseFile - Error while parsing " + file + " - no blockid in <block>");
                        continue;
                    }
                    String id = property.getValue();
                    if (id.equals("")) {
                        PCLogger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad block ID");
                        continue;
                    }
                    PCValueRangeI[] ranges = c.states.get(id);
                    if(ranges==null)
                        c.states.put(id, ranges=new PCValueRangeI[]{new PCValueRangeI(), new PCValueRangeI()});

                    PCXMLProperty property_growing = block.getProperty("growing");
                    PCXMLProperty property_harvesting = block.getProperty("harvesting");
                    if(property_growing==null && property_harvesting==null){
                        PCLogger.warning("Crop manager - parseFile - Error while parsing " + file + " - no meta");
                        continue;
                    }
                    if(property_growing!=null){
                        try{
                            ranges[0].addRange(PCValueRangeI.pharseRange(property_growing.getValue()));
                        }catch(NumberFormatException e){
                            PCLogger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad meta");
                            continue;
                        }
                    }
                    if(property_harvesting!=null){
                        try{
                            ranges[1].addRange(PCValueRangeI.pharseRange(property_harvesting.getValue()));
                        }catch(NumberFormatException e){
                            PCLogger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad meta");
                            continue;
                        }
                    }
                }
                PCCropHarvesting.crops.add(c);
            }
        }
    }

    private static class Crop{

        public HashMap<String, PCValueRangeI[]> states = new HashMap<String, PCValueRangeI[]>();

        public Crop() {

        }

        public boolean isCrop(ResourceLocation blockSID, int metadata){
            PCValueRangeI[] range = this.states.get(blockSID);
            return range!=null && (range[0].in(metadata) || range[1].in(metadata) || range[0].in(-1) || range[1].in(-1));
        }

        public boolean isFinished(ResourceLocation blockSID, int metadata){
            PCValueRangeI[] range = this.states.get(blockSID);
            return range!=null && (range[1].in(metadata) || (range[1].in(-1) && !range[0].in(metadata)));
        }

    }
}
