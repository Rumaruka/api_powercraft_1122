package com.rumaruka.powercraft.api.version;

import com.rumaruka.powercraft.api.xml.PCXMLNode;
import com.rumaruka.powercraft.api.xml.PCXMLProperty;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCUpdateInfo {
    public static PCUpdateInfo pharse(PCXMLNode load) {
        if(!load.getName().equals("Versions")){
            throw new IllegalArgumentException();
        }
        HashMap<String, List<PCVersionInfo>> modules = new HashMap<String, List<PCVersionInfo>>();
        PCVersion mcv;
        try{
            mcv = PCVersion.pharse(Loader.instance().getMCVersionString().substring(10));
        }catch(Exception e){
            throw new IllegalArgumentException();
        }
        for(int i=0; i<load.getChildCount(); i++){
            PCXMLNode child = load.getChild(i);
            if(!child.getName().equals("Minecraft")){
                continue;
            }
            PCXMLProperty p = child.getProperty("mcv");
            if(p==null)
                continue;
            PCVersion mcvf;
            try{
                mcvf = PCVersion.pharse(p.getValue());
            }catch(Exception e){
                continue;
            }
            if(mcv.compareTo(mcvf)!=0){
                continue;
            }
            for(int k=0; k<child.getChildCount(); k++){
                PCXMLNode c = child.getChild(i);
                if(!c.getName().equals("Module")){
                    continue;
                }
                PCXMLProperty property = c.getProperty("name");
                if(property==null){
                    continue;
                }
                String name = property.getValue();
                if(modules.containsKey(name)){
                    throw new IllegalArgumentException();
                }
                List<PCVersionInfo> versions = new ArrayList<PCVersionInfo>();
                modules.put(name, versions);
                for(int j=0; j<c.getChildCount(); j++){
                    PCXMLNode v = c.getChild(j);
                    if(!v.getName().equals("Version")){
                        continue;
                    }
                    PCXMLProperty propertyVersion = v.getProperty("version");
                    PCXMLProperty propertyDownload = v.getProperty("download");
                    if(propertyVersion==null){
                        continue;
                    }
                    PCVersion version;
                    try{
                        version = PCVersion.pharse(propertyVersion.getValue());
                    }catch(Exception e){
                        e.printStackTrace();
                        continue;
                    }
                    if(propertyDownload==null){
                        versions.add(new PCVersionInfo(version, ""));
                    }else{
                        versions.add(new PCVersionInfo(version, propertyDownload.getValue()));
                    }
                }
            }
        }
        return new PCUpdateInfo(modules);
    }

    private HashMap<String, List<PCVersionInfo>> modules;

    private PCUpdateInfo(HashMap<String, List<PCVersionInfo>> modules) {
        this.modules = modules;
    }

    public PCVersionInfo getNewestVersion(String module, boolean preReleases){
        List<PCVersionInfo> infos = this.modules.get(module);
        if(infos==null){
            return null;
        }
        PCVersionInfo newest = null;
        for(PCVersionInfo info:infos){
            if(preReleases || !info.isPreRelease()){
                if(newest==null){
                    newest = info;
                }else if(newest.compareTo(info)<0){
                    newest = info;
                }
            }
        }
        return newest;
    }
}
