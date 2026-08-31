package com.zhushi.spike.core;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class SaveGameCodec {
    public void save(SpikeState state, Path path) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("lifeStatus", state.lifeStatus().name());
        properties.setProperty("knowledge", String.join(",", state.knowledge()));
        properties.setProperty("eventOpen", Boolean.toString(state.eventOpen()));
        try (Writer writer = Files.newBufferedWriter(path)) {
            properties.store(writer, "zhushi engine spike");
        }
    }

    public SpikeState load(Path path) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path)) {
            properties.load(reader);
        }
        SpikeState state = new SpikeState();
        state.setLifeStatus(LifeStatus.valueOf(properties.getProperty("lifeStatus", "ALIVE")));
        String rawKnowledge = properties.getProperty("knowledge", "");
        if (!rawKnowledge.isBlank()) {
            for (String id : rawKnowledge.split(",")) {
                if (!id.isBlank()) {
                    state.addKnowledge(id.trim());
                }
            }
        }
        state.setEventOpen(Boolean.parseBoolean(properties.getProperty("eventOpen", "false")));
        return state;
    }
}
