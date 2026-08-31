package com.zhushi.spike.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ScenarioRunner {
    private ScenarioRunner() {
    }

    public static void main(String[] args) throws Exception {
        run();
        System.out.println("LIBGDX_SCENARIO_OK");
    }

    public static void run() throws Exception {
        BlackwaterRules rules = new BlackwaterRules();
        SpikeState firstLife = new SpikeState();

        rules.openWineEvent(firstLife);
        rules.choose(firstLife, BlackwaterRules.DRINK);
        require(firstLife.lifeStatus() == LifeStatus.DEAD, "first life must die after drinking");
        require(firstLife.hasKnowledge(SpikeState.POISON_KNOWLEDGE), "death must grant poison Knowledge");

        Path save = Files.createTempFile("zhushi-libgdx-spike-", ".save");
        try {
            SaveGameCodec codec = new SaveGameCodec();
            codec.save(firstLife, save);
            SpikeState loaded = codec.load(save);
            require(loaded.lifeStatus() == LifeStatus.DEAD, "save/load must preserve death state");
            require(loaded.hasKnowledge(SpikeState.POISON_KNOWLEDGE), "save/load must preserve Knowledge");

            SpikeState secondLife = new SpikeState();
            loaded.knowledge().forEach(secondLife::addKnowledge);
            List<EventChoice> choices = rules.openWineEvent(secondLife);
            require(choices.stream().anyMatch(c -> BlackwaterRules.REMEMBER_REFUSE.equals(c.id())),
                "second life must expose prior-life choice");
            rules.choose(secondLife, BlackwaterRules.REMEMBER_REFUSE);
            require(secondLife.lifeStatus() == LifeStatus.ALIVE, "prior-life refusal must survive");
        } finally {
            Files.deleteIfExists(save);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
