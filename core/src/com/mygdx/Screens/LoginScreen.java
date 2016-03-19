package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by user on 11/3/2016.
 */
public class LoginScreen extends AbstractScreen{

    private final TextButton buttonConnect;
    private final TextField textUser;
    private final Label labelGame;
    private final Label labelUser;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public LoginScreen() {
        buttonConnect = new TextButton("Connect",skin);
        buttonConnect.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String text = textUser.getText();
                if (text.length()>0){
                    ScreenManager.getInstance().showScreen( ScreenEnum.ROOMSELECTION , text);
                }
                return false;
            }
        });
        textUser = new TextField("",skin);
        labelGame = new Label("Jumpy",skin);
        labelUser = new Label("Username:",skin);
        buildStage();
    }

    @Override
    public void buildStage() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.setDebug(true);

        table.add(labelGame).colspan(2).pad(1);
        table.row();
        table.add(labelUser).pad(2);
        table.add(textUser).pad(2);
        table.row();
        table.add(buttonConnect).colspan(2).pad(1);
        addActor(table);
    }
}
