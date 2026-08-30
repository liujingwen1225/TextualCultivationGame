package com.zhushi.spike.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.zhushi.spike.core.ScenarioRunner;

public final class HeadlessSmokeLauncher {
    private HeadlessSmokeLauncher() {
    }

    public static void main(String[] args) {
        new HeadlessApplication(new ApplicationAdapter() {
            @Override
            public void create() {
                try {
                    ScenarioRunner.run();
                    System.out.println("LIBGDX_HEADLESS_OK");
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    System.exit(1);
                } finally {
                    Gdx.app.exit();
                }
            }
        }, new HeadlessApplicationConfiguration());
    }
}
