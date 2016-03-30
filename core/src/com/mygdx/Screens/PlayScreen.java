package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.GameWorld.GameRenderer;
import com.mygdx.GameWorld.GameWorld;
import com.mygdx.JumpyHelper.InputHandler;

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

