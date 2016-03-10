package com.mygdx.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.Jumpy;

/**
 * Created by user on 1/3/2016.
 */
public class Controls implements Disposable {

    private Viewport viewport;
    public Stage stage;

    private final TextButton buttonLeft;
    private final TextButton buttonRight;
    private final TextButton buttonJump;
    private final TextButton toggleButton;

    public Controls(SpriteBatch batch) {
        viewport = new FitViewport(Jumpy.WIDTH,Jumpy.HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport, batch);

        Table table = new Table();
        Table tabletop = new Table();

        table.bottom();
        tabletop.top().right();

        table.setFillParent(true);
        tabletop.setFillParent(true);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        buttonLeft = new TextButton("LEFT",skin);
        buttonRight = new TextButton("RIGHT",skin);
        buttonJump = new TextButton("JUMP",skin);
        toggleButton = new TextButton("DPAD",skin);

        buttonLeft.getLabel().setFontScale(0.1f);
        buttonRight.getLabel().setFontScale(0.1f);
        buttonJump.getLabel().setFontScale(0.1f);
        toggleButton.getLabel().setFontScale(0.1f);

        table.add(buttonLeft).expandX().padBottom(1).padLeft(1).padRight(1).width(10).height(4);
        table.add(buttonRight).expandX().padBottom(1).padLeft(1).padRight(1).width(10).height(4);
        table.add(buttonJump).expandX().padBottom(1).padLeft(1).padRight(1).width(10).height(4);
        tabletop.add(toggleButton).padRight(1).padTop(1).width(6).height(5);

        stage.addActor(table);
        stage.addActor(tabletop);
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

//    public void update(float dt){
//
//    };

    public TextButton getButtonLeft() {
        return buttonLeft;
    }

    public TextButton getButtonRight() {
        return buttonRight;
    }

    public TextButton getButtonJump() {
        return buttonJump;
    }

    public TextButton getToggleButton() {
        return toggleButton;
    }
}
