package com.rumaruka.powercraft.api.version;

public class PCVersionInfo implements Comparable<PCVersionInfo>{

    private PCVersion version;

    private String download;

    public PCVersionInfo(PCVersion version, String download) {
        this.version = version;
        this.download = download;
    }

    @Override
    public int compareTo(PCVersionInfo o) {
        return this.version.compareTo(o.version);
    }

    public PCVersion getVersion(){
        return this.version;
    }

    public String getDownloadLink(){
        return this.download;
    }

    public boolean isPreRelease(){
        return this.version.isPreRelease();
    }

    @Override
    public String toString() {
        return "PC_VersionInfo [version=" + this.version + ", download=" + this.download + "]";
    }
}
