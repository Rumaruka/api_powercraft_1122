package com.rumaruka.powercraft.api;

import net.minecraft.util.text.translation.I18n;

public class PCLangHelper {

    public static String translate(String key, Object...args){
        return I18n.translateToLocalFormatted(key, args);
    }

    /**
     * translate a key to user language
     * @param key the key
     * @param args arguments to put in %s and so
     * @return the translated and formatted string
     */
    public static String tr(String key, Object...args){
        return I18n.translateToLocalFormatted(key, args);
    }

    private PCLangHelper(){
        PCUtils.staticClassConstructor();
    }
}
