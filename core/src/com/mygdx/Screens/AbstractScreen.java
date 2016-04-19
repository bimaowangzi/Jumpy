package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.JumpyHelper.AssetLoader;

/**
 * Created by user on 11/3/2016.
 */

/**Abstract screens is the parent of all our screens, it inherits Stage and implements Screen*/
public abstract class AbstractScreen extends Stage implements Screen{

    protected float width;
    protected float height;
    protected boolean phoneDisplay;

    private static Texture texture;
    // Game Assets
    private TextureRegion bg;

    SpriteBatch batcher;

    public AbstractScreen() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()));

        if (Gdx.graphics.getHeight()==480) {
            /**For Desktop, note that this is not optimal*/
            phoneDisplay = false;
        } else if (Gdx.graphics.getWidth() == 1080){
            /**For 1080 * 1920 phones*/
            phoneDisplay = true;
            this.getViewport().setWorldSize((int) (Gdx.graphics.getWidth()*0.6f),(int) (Gdx.graphics.getHeight()*0.6f));
        }else {
            /**For 1440 * 2560 phones*/
            System.out.println("PhoneDisplay");
            phoneDisplay = true;
            this.getViewport().setWorldSize((int) (Gdx.graphics.getWidth()*0.5f),(int) (Gdx.graphics.getHeight()*0.5f));
        }

        texture = new Texture(Gdx.files.internal("BG.png"));
        bg = new TextureRegion(texture, 0,0, 1000, 600);
        batcher = new SpriteBatch();
        //attach batcher to camera
        batcher.setProjectionMatrix(this.getCamera().combined);
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Begin SpriteBatch
        batcher.begin();

        // Draw background
        batcher.draw(bg, 0, 0, width, height);

        // End SpriteBatch
        batcher.end();

        // Calling to Stage methods
        super.act(delta);
        super.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        System.out.println("resize with width " + this.width + " and height" + this.height);
        getViewport().update((int) this.width,(int) this.height, true);
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
