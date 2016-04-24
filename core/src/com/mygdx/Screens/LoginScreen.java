package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.mygdx.appwarp.WarpController;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

/**
 * Created by user on 11/3/2016.
 */

/**This screen allows the user to key in a username for login*/
public class LoginScreen extends AbstractScreen{

    private WarpClient warpClient;
    private Stage stage = this;

    private final TextButton buttonConnect;
    private final TextField textUser;
    private final Label labelGame;
    private final Label labelUser;
    private Table table;
    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public LoginScreen() {

        final Label labelError = new Label("Unable to connect Appwarp, try a different ID and check your connections",skin);
        labelError.setWrap(true);

        final Label labelNoUserError = new Label("Please key in a username.",skin);
        labelNoUserError.setWrap(true);

        /**Button to connect to Appwarp*/
        buttonConnect = new TextButton("Connect",skin);
        buttonConnect.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String text = textUser.getText();
                if (text.length()>0){
                    // pass in text as local user
                    WarpController.getInstance().startApp(text);
                    System.out.println("Connecting to Appwarp...");
                    while(!WarpController.isWaitflag()){}
                    WarpController.setWaitflag(false);
                    getWarpClient();
                    System.out.println("Status: " + WarpController.isStatusflag());
                    if (WarpController.isStatusflag()){
                        WarpController.setStatusflag(false);
                        // wait for setRoomData
                        WarpController.setDeleteFlag(true);
                        warpClient.getRoomInRange(0,0);
                        while (!WarpController.isWaitRoomFlag()){
                            // busy waiting
                        }
                        WarpController.setWaitRoomFlag(false);
                        Gdx.input.setOnscreenKeyboardVisible(false);
                        ScreenManager.getInstance().showScreen(ScreenEnum.ROOMSELECTION);
                    } else {
                        /**Connection or username taken error*/
                        table.setVisible(false);
                        new Dialog("Error",skin){

                            {
                                this.getContentTable().add(labelError).prefWidth(table.getWidth());
                                button("OK");
                            }

                            @Override
                            protected void result(Object object) {
                                super.result(object);
                                table.setVisible(true);
                            }

                        }.show(stage);
                    }
                } else if (text.length() == 0){
                    /**Username field empty error*/
                    table.setVisible(false);
                    new Dialog("Error",skin){

                        {
                            this.getContentTable().add(labelNoUserError).prefWidth(table.getWidth());
                            button("OK");
                        }

                        @Override
                        protected void result(Object object) {
                            super.result(object);
                            table.setVisible(true);
                        }

                    }.show(stage);
                }
                return false;
            }
        });
        textUser = new TextField("",skin);
        labelGame = new Label("Jumpy",skin,"title");
        labelUser = new Label("Username:",skin);
        buildStage();
    }

    private void getWarpClient(){
        try {
            warpClient = WarpClient.getInstance();
        } catch (Exception ex) {
            System.out.println("Fail to get warpClient");
        }
    }

    @Override
    public void buildStage() {
        table = new Table();
        table.setFillParent(true);
        table.center();
        table.setDebug(false);

        table.add(labelGame).colspan(2).pad(1);
        table.row();
        table.add(labelUser).pad(2);
        table.add(textUser).pad(2).fill();
        table.row();
        table.add(buttonConnect).colspan(2).pad(1);

        table.row();
        table.add().colspan(2).pad(1);
        addActor(table);

    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }
}
