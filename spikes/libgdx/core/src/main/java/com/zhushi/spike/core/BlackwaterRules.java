package com.zhushi.spike.core;

import java.util.ArrayList;
import java.util.List;

public final class BlackwaterRules {
    public static final String DRINK = "DRINK";
    public static final String LEAVE = "LEAVE";
    public static final String REMEMBER_REFUSE = "REMEMBER_REFUSE";

    public List<EventChoice> openWineEvent(SpikeState state) {
        if (state.lifeStatus() == LifeStatus.DEAD) {
            throw new IllegalStateException("Dead life cannot open a new event");
        }
        state.setEventOpen(true);
        List<EventChoice> choices = new ArrayList<>();
        choices.add(new EventChoice(DRINK, "Drink the offered spirit wine"));
        if (state.hasKnowledge(SpikeState.POISON_KNOWLEDGE)) {
            choices.add(new EventChoice(REMEMBER_REFUSE, "Prior life: the wine is poisoned — refuse"));
        } else {
            choices.add(new EventChoice(LEAVE, "Leave without drinking"));
        }
        return List.copyOf(choices);
    }

    public void choose(SpikeState state, String choiceId) {
        if (!state.eventOpen()) {
            throw new IllegalStateException("No event is open");
        }
        switch (choiceId) {
            case DRINK -> {
                state.setLifeStatus(LifeStatus.DEAD);
                state.addKnowledge(SpikeState.POISON_KNOWLEDGE);
            }
            case REMEMBER_REFUSE -> {
                if (!state.hasKnowledge(SpikeState.POISON_KNOWLEDGE)) {
                    throw new IllegalStateException("Prior-life choice requires Knowledge");
                }
            }
            case LEAVE -> {
                // Intentionally no authoritative state change for the spike.
            }
            default -> throw new IllegalArgumentException("Unknown choice: " + choiceId);
        }
        state.setEventOpen(false);
    }
}
