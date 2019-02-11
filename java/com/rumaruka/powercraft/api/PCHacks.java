package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.PCTickHandler.IRenderTickHandler;
import com.rumaruka.powercraft.api.reflect.PCFields;
import com.rumaruka.powercraft.api.version.PCUpdateChecker;
import com.rumaruka.powercraft.api.version.PCUpdateInfo;
import com.rumaruka.powercraft.api.version.PCVersion;
import com.rumaruka.powercraft.api.version.PCVersionInfo;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PCHacks {

    private PCHacks(){
        PCUtils.staticClassConstructor();
    }

    @SideOnly(Side.CLIENT)
    public static void hackSplash(GuiScreen gui, String splash){
        if(gui!=null && gui.getClass()== GuiMainMenu.class){
            PCFields.Client.GuiMainMenu_splashText.setValue(gui, splash);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void hackInfo(GuiScreen gui, String line1, String line2, String link){
        if(gui!=null && gui.getClass()==GuiMainMenu.class){
            PCFields.Client.GuiMainMenu_notificationLine1.setValue(gui, line1);
            PCFields.Client.GuiMainMenu_notificationLine2.setValue(gui, line2);
            PCFields.Client.GuiMainMenu_notificationLink.setValue(gui, link);
            if(gui.mc!=null){
                gui.initGui();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void hackGui(GuiScreen gui) {
        if(gui!=null && gui.getClass()==GuiMainMenu.class){
            PCUpdateInfo ui = PCUpdateChecker.getUpdateInfo();
            if(ui==null){
                ticker = new Ticker();
                PCTickHandler.registerTickHandler(ticker);
            }else{
                PCVersion v = PCApi.instance.getVersion();
                PCVersionInfo nv = ui.getNewestVersion("Api", PCApi.showPreversions);
                if(nv!=null && nv.getVersion().compareTo(v)>0){
                    hackInfo(gui, PCLangHelper.translate("PC.out.of.date"), PCLangHelper.translate("PC.version.show", v, nv.getVersion()), nv.getDownloadLink());
                }
            }
        }else if(ticker!=null){
            PCTickHandler.removeTickHander(ticker);
            ticker = null;
        }
    }

    @SideOnly(Side.CLIENT)
    static Ticker ticker;

    @SideOnly(Side.CLIENT)
    private static final class Ticker implements IRenderTickHandler {

        Ticker() {
        }

        @Override
        public void onStartTick(float renderTickTime) {
            PCUpdateInfo ui = PCUpdateChecker.getUpdateInfo();
            if(ui!=null){
                hackGui(PCClientUtils.mc().currentScreen);
                PCTickHandler.removeTickHander(this);
                ticker = null;
            }
        }

        @Override
        public void onEndTick(float renderTickTime) {
            PCUpdateInfo ui = PCUpdateChecker.getUpdateInfo();
            if(ui!=null){
                hackGui(PCClientUtils.mc().currentScreen);
                PCTickHandler.removeTickHander(this);
                ticker = null;
            }
        }

    }

}
