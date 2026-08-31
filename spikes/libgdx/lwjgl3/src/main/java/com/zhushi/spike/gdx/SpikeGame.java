package com.zhushi.spike.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.zhushi.spike.core.BlackwaterRules;
import com.zhushi.spike.core.EventChoice;
import com.zhushi.spike.core.LifeStatus;
import com.zhushi.spike.core.SaveGameCodec;
import com.zhushi.spike.core.SpikeState;

import java.nio.file.Path;
import java.util.List;

public final class SpikeGame extends ApplicationAdapter {
    private static final float WORLD_WIDTH = 640f;
    private static final float WORLD_HEIGHT = 384f;
    private static final float VIEW_WIDTH = 480f;
    private static final float VIEW_HEIGHT = 288f;
    private static final Rectangle WATER = new Rectangle(320, 128, 96, 96);
    private static final Vector2 ALTAR = new Vector2(520, 190);
    private static final Vector2 NPC = new Vector2(220, 210);

    private final BlackwaterRules rules = new BlackwaterRules();
    private final SaveGameCodec saveCodec = new SaveGameCodec();
    private final Path savePath = Path.of(System.getProperty("java.io.tmpdir"), "zhushi-libgdx-engine-spike.save");
    private final SpikeState state = new SpikeState();
    private final Vector2 player = new Vector2(96, 96);

    private OrthographicCamera worldCamera;
    private OrthographicCamera uiCamera;
    private FitViewport viewport;
    private ShapeRenderer shapes;
    private SpriteBatch batch;
    private BitmapFont font;
    private List<EventChoice> choices = List.of();
    private String message = "Walk to the wine altar and press E";

    @Override
    public void create() {
        worldCamera = new OrthographicCamera();
        uiCamera = new OrthographicCamera();
        viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT, worldCamera);
        uiCamera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(0.75f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void render() {
        updatePlayer(Gdx.graphics.getDeltaTime());
        handleActions();
        updateCamera();

        Gdx.gl.glClearColor(0.035f, 0.055f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        shapes.setProjectionMatrix(worldCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.12f, 0.20f, 0.16f, 1f);
        shapes.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        shapes.setColor(0.18f, 0.30f, 0.22f, 1f);
        shapes.rect(32, 48, 576, 48);
        shapes.rect(176, 48, 48, 280);
        shapes.setColor(0.07f, 0.18f, 0.25f, 1f);
        shapes.rect(WATER.x, WATER.y, WATER.width, WATER.height);
        shapes.setColor(0.72f, 0.60f, 0.25f, 1f);
        shapes.circle(ALTAR.x, ALTAR.y, 12);
        shapes.setColor(0.25f, 0.72f, 0.60f, 1f);
        shapes.circle(NPC.x, NPC.y, 10);
        shapes.setColor(state.lifeStatus() == LifeStatus.ALIVE ? Color.WHITE : Color.DARK_GRAY);
        shapes.rect(player.x - 8, player.y - 8, 16, 16);
        shapes.end();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "libGDX spike | WASD move | E interact | 1/2 choose | F5 save | F9 load", 10, 278);
        font.draw(batch, "Life: " + state.lifeStatus() + " | Knowledge: " + state.knowledge(), 10, 260);
        font.draw(batch, message, 10, 28);
        if (state.eventOpen()) {
            float y = 120;
            font.draw(batch, "Blackwater spirit wine", 80, y + 60);
            for (int i = 0; i < choices.size(); i++) {
                font.draw(batch, (i + 1) + ". " + choices.get(i).label(), 80, y + 38 - i * 18);
            }
        }
        batch.end();
    }

    private void updatePlayer(float delta) {
        if (state.lifeStatus() == LifeStatus.DEAD || state.eventOpen()) {
            return;
        }
        Vector2 direction = new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) direction.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) direction.x += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) direction.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) direction.y += 1;
        if (direction.isZero()) return;
        direction.nor().scl(120f * delta);
        float nextX = Math.max(12, Math.min(WORLD_WIDTH - 12, player.x + direction.x));
        float nextY = Math.max(12, Math.min(WORLD_HEIGHT - 12, player.y + direction.y));
        Rectangle candidate = new Rectangle(nextX - 8, nextY - 8, 16, 16);
        if (!candidate.overlaps(WATER)) {
            player.set(nextX, nextY);
        }
    }

    private void handleActions() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && state.lifeStatus() == LifeStatus.ALIVE
            && player.dst(ALTAR) < 48f && !state.eventOpen()) {
            choices = rules.openWineEvent(state);
            message = "Choose 1 or 2";
        }
        if (state.eventOpen()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) applyChoice(0);
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) applyChoice(1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            try {
                saveCodec.save(state, savePath);
                message = "Saved to temp directory";
            } catch (Exception e) {
                message = "Save failed: " + e.getMessage();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
            try {
                SpikeState loaded = saveCodec.load(savePath);
                state.setLifeStatus(loaded.lifeStatus());
                state.knowledge().clear();
                state.knowledge().addAll(loaded.knowledge());
                state.setEventOpen(false);
                choices = List.of();
                message = "Loaded authoritative state";
            } catch (Exception e) {
                message = "Load failed: " + e.getMessage();
            }
        }
    }

    private void applyChoice(int index) {
        if (index < 0 || index >= choices.size()) return;
        EventChoice choice = choices.get(index);
        rules.choose(state, choice.id());
        choices = List.of();
        message = state.lifeStatus() == LifeStatus.DEAD
            ? "You died and learned the wine is poisoned. Restart the spike to simulate next life."
            : "You refused the wine and survived.";
    }

    private void updateCamera() {
        float halfW = VIEW_WIDTH / 2f;
        float halfH = VIEW_HEIGHT / 2f;
        worldCamera.position.set(
            Math.max(halfW, Math.min(WORLD_WIDTH - halfW, player.x)),
            Math.max(halfH, Math.min(WORLD_HEIGHT - halfH, player.y)),
            0
        );
        worldCamera.update();
        uiCamera.update();
    }

    @Override
    public void dispose() {
        shapes.dispose();
        batch.dispose();
        font.dispose();
    }
}
