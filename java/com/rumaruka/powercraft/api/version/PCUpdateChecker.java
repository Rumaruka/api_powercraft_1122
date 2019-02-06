package com.rumaruka.powercraft.api.version;

import com.rumaruka.powercraft.api.PCLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class PCUpdateChecker extends Thread{

    private static final int TIMEOUT = 30000;
    private static final String URL = "";

    private static PCUpdateChecker running = null;

    private static PCUpdateInfo updateInfo;

    public static synchronized void check(){
        if(running==null){
            running = new PCUpdateChecker();
        }
    }

    public static PCUpdateInfo getUpdateInfo(){
        return updateInfo;
    }

    private PCUpdateChecker(){
        setDaemon(true);
        setName("PowerCraft Update Checker");
        start();
    }

    private static void onUpdateInfoDownloaded(String page) {
        PCLogger.fine("Update information received from server.");
        updateInfo = PCUpdateInfo.pharse(PCXMLLoader.load(page));
    }

    @Override
    public void run(){
        try{
            PCLogger.info("Request version");
            URL url = new URL(PCUpdateChecker.URL);
            URLConnection urlC = url.openConnection();
            urlC.setReadTimeout(TIMEOUT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlC.getInputStream()));
            String page = "";
            String line;

            while ((line = reader.readLine()) != null){
                page += line + "\n";
            }

            reader.close();
            onUpdateInfoDownloaded(page);
        }catch (Exception e){
            e.printStackTrace();
            PCLogger.warning("Error while downloading update info");
        }
        running = null;
    }
}
