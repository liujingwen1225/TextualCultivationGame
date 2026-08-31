package com.zhushi.spike.core;

import java.util.LinkedHashSet;
import java.util.Set;

public final class SpikeState {
    public static final String POISON_KNOWLEDGE = "K_BLACKWATER_POISON";

    private LifeStatus lifeStatus = LifeStatus.ALIVE;
    private final Set<String> knowledge = new LinkedHashSet<>();
    private boolean eventOpen;

    public LifeStatus lifeStatus() {
        return lifeStatus;
    }

    public void setLifeStatus(LifeStatus lifeStatus) {
        this.lifeStatus = lifeStatus;
    }

    public Set<String> knowledge() {
        return knowledge;
    }

    public boolean hasKnowledge(String id) {
        return knowledge.contains(id);
    }

    public void addKnowledge(String id) {
        knowledge.add(id);
    }

    public boolean eventOpen() {
        return eventOpen;
    }

    public void setEventOpen(boolean eventOpen) {
        this.eventOpen = eventOpen;
    }
}
