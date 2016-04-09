package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by user on 11/3/2016.
 */
public abstract class AbstractScreen extends Stage implements Screen{

    protected float width;
    protected float height;

    public AbstractScreen() {
        super( new FitViewport(380.0f, 640.0f, new OrthographicCamera()) );
    }

    // Subclasses must load actors in this method
    public abstract void buildStage();

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0.2f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Calling to Stage methods
        super.act(delta);
        super.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        System.out.println("resize with width " + width + " and height" + height);
        getViewport().update(width, height, true);
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
}
