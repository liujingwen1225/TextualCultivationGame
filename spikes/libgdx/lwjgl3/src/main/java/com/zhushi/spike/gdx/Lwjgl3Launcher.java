package com.zhushi.spike.gdx;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public final class Lwjgl3Launcher {
    private Lwjgl3Launcher() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Zhushi Engine Spike - libGDX");
        config.setWindowedMode(960, 576);
        config.useVsync(true);
        new Lwjgl3Application(new SpikeGame(), config);
    }
}
