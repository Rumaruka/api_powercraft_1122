package com.rumaruka.powercraft.api.gres.history;

public class PCGresHistory {

    private final IGresHistoryEntry[] historyEntries;
    private int min;
    private int max;
    private int pos;
    private IGresHistoryEntry newest;

    public PCGresHistory(int size){
        this.historyEntries = new IGresHistoryEntry[size];
    }

    public void addHistoryEntry(IGresHistoryEntry historyEntry){
        if(this.newest!=null){
            if(this.newest.tryToMerge(historyEntry))
                return;
        }
        this.newest = historyEntry;
        this.historyEntries[this.pos] = historyEntry;
        this.pos = (this.pos+1)%this.historyEntries.length;
        this.max = this.pos;
        if(this.min==this.max){
            this.min = (this.min+1)%this.historyEntries.length;
        }
    }

    public void redo(){
        if(this.pos!=this.max){
            this.newest = null;
            this.historyEntries[this.pos].doAction();
            this.pos = (this.pos+1)%this.historyEntries.length;
        }
    }

    public void undo(){
        if(this.pos!=this.min){
            this.newest = null;
            if(this.pos<=0)
                this.pos = this.historyEntries.length;
            this.pos = this.pos-1;
            this.historyEntries[this.pos].undoAction();
        }
    }
}
