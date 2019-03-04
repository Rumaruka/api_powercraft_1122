package com.rumaruka.powercraft.api.gres.events;

public interface IGresEventListenerEx {

    Class<? extends PCGresEvent>[] getHandelableEvents();
}
