package com.rumaruka.powercraft.api.gres.history;

public interface IGresHistoryEntry {


     void doAction();

     void undoAction();

     boolean tryToMerge(IGresHistoryEntry historyEntry);
}
