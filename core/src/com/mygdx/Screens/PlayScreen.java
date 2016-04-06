package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.GameWorld.GameRenderer;
import com.mygdx.GameWorld.GameWorld;
import com.mygdx.JumpyHelper.InputHandler;
import com.mygdx.appwarp.WarpController;

import org.json.JSONObject;

/**
 * Created by acer on 16/2/2016.
 */
public class PlayScreen extends AbstractScreen {

    private GameWorld world;
    private GameRenderer renderer;
    private OrthographicCamera cam;

    private float runTime = 0;

    public PlayScreen (){
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        cam = new OrthographicCamera();

        float gameWidth = 50;
        float gameHeight = screenHeight/(screenWidth/gameWidth);

        world = new GameWorld(cam, gameWidth, gameHeight); // initialize world
        renderer = new GameRenderer(cam, world, gameWidth, gameHeight); // initialize renderer

        Gdx.input.setInputProcessor(new InputHandler(world));

        Thread fetchDataThread = new FetchDataThread(world);
        fetchDataThread.start();
    }

    @Override
    public void buildStage() {

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        runTime += delta;
        world.update(delta); // GameWorld updates
        renderer.render(delta); // GameRenderer renders
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

class FetchDataThread extends Thread {
    private GameWorld world;
    public FetchDataThread(GameWorld world) {
        this.world = world;
    }
    public void run() {
        while (true) {
            try {
                //System.out.println(WarpController.getData());
                JSONObject data = new JSONObject(WarpController.getData());
                float x = (float) data.getDouble("worldX");
                float y = (float) data.getDouble("worldY");
                float vx = (float) data.getDouble("velocityX");
                float vy = (float) data.getDouble("velocityY");
                float width = (float) data.getDouble("width");
                float height = (float) data.getDouble("height");
                int powerUpState = data.getInt("powerUpState");
                int score = data.getInt("score");
                float worldHeight = (float) data.getDouble("worldHeight");
                int lives = data.getInt("lives");

                world.getOtherPlayer().update(x, y, vx, vy, width, height, powerUpState, score, worldHeight, lives);

                boolean lightningStruck = data.getBoolean("lightning");
                if (lightningStruck)
                    world.getPlayer().lightningStrike();
            } catch (Exception e) {
                // exception
                // e.printStackTrace();
            }
        }
    }
}

