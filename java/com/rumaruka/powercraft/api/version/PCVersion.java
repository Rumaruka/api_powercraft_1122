package com.rumaruka.powercraft.api.version;

import com.rumaruka.powercraft.api.xml.PCPhaser;

public class PCVersion implements Comparable<PCVersion> {
    private int[] version;

    private String type;

    public PCVersion(String type, int... version) throws IllegalAccessException {
        this.version=version;
        if(this.version.length==0){
            throw new IllegalAccessException("Nope version");

        }
        for (int i=0;i<this.version.length;i++){
            if(this.version[i]<0){
                throw new IllegalAccessException("Negative version");
            }
            this.type=type;
        }
    }
    @Override
    public int compareTo(PCVersion o) {
        int c = this.version.length>o.version.length?this.version.length:o.version.length;
        for(int i=0; i<c; i++){
            int v1 = this.version.length>i?this.version[i]:0;
            int v2 = o.version.length>i?o.version[i]:0;
            if(v1>v2){
                return i+1;
            }else if(v1<v2){
                return -i-1;
            }
        }
        return 0;
    }
    public PCVersion(int... version) throws IllegalAccessException {
        this(null,version);
    }

    @Override
    public String toString() {
        String sVersion = PCPhaser.toString(this.version,".");
        if(this.type==null){
            return sVersion;
        }
        return sVersion+this.type;
    }

    public String getType() {
        return type;
    }
    public int getNumCount(){
      return   this.version.length;
    }

    public int getVersion(int i) {
        return this.version[i];
    }
    private static final String[] prereleases = {"testbuild", "snapshot", "alpha", "a", "beta", "b"};

    public boolean isPreRelease(){
        if(this.type!=null){
            String t = this.type.toLowerCase();
            for(String prerelease:prereleases){
                if(prerelease.equals(t)){
                    return true;
                }
            }
        }
        return false;
    }
    public static PCVersion pharse(String s) throws IllegalAccessException {
        String[] nums = s.split("\\.");
        int[] version = new int[nums.length];
        for(int i=0; i<version.length-1; i++){
            version[i] = Integer.parseInt(nums[i].trim());
        }
        int i = version.length-1;
        String n = nums[i].trim();
        int k=-1;
        for(int j=0; j<n.length(); j++){
            if(!Character.isDigit(n.charAt(j))){
                k = j;
                break;
            }
        }
        if(k==-1){
            version[i] = Integer.parseInt(n);
            return new PCVersion(version);
        }
        version[i] = Integer.parseInt(n.substring(0, k));
        return new PCVersion(n.substring(k).trim(), version);
    }
}
