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
public class LoginScreen extends AbstractScreen{

    private WarpClient warpClient;
    private Stage stage = this;

    private final TextButton buttonConnect;
    private final TextField textUser;
    private final Label labelGame;
    private final Label labelUser;
    private Table table;
//    ShapeRenderer shapeRenderer;
//    private float progress = 0f;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public LoginScreen() {

//        shapeRenderer = new ShapeRenderer();

        final Label labelError = new Label("Unable to connect Appwarp, try a different ID and check your connections",skin);
        labelError.setWrap(true);
        final Label labelConnecting = new Label("Connecting to the Server",skin);
        labelConnecting.setWrap(true);

        buttonConnect = new TextButton("Connect",skin);
        buttonConnect.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String text = textUser.getText();
                if (text.length()>0){

                    table.setVisible(false);
                    Dialog connectDia = new Dialog("Connecting",skin){

                        {
                            this.getContentTable().add(labelConnecting).prefWidth(table.getWidth());
                        }

                        @Override
                        protected void result(Object object) {
                            super.result(object);
//                            table.setVisible(true);
                        }

                    }.show(stage);

                    // pass in text as local user
                    WarpController.getInstance().startApp(text);
                    System.out.println("Connecting to Appwarp...");
                    while(!WarpController.isWaitflag()){}
                    WarpController.setWaitflag(false);
                    getWarpClient();
                    System.out.println("Status: " + WarpController.isStatusflag());
                    connectDia.remove();
                    if (WarpController.isStatusflag()){
                        WarpController.setStatusflag(false);
                        // wait for setRoomData
                        warpClient.getRoomInRange(0, 3);
                        while (!WarpController.isWaitRoomFlag()){
                            // busy waiting
                        }
                        WarpController.setWaitRoomFlag(false);
                        ScreenManager.getInstance().showScreen(ScreenEnum.ROOMSELECTION);
                    } else {
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
                }
                return false;
            }
        });
        textUser = new TextField("",skin);
        labelGame = new Label("Jumpy",skin);
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
        table.setDebug(true);

        table.add(labelGame).colspan(2).pad(1);
        table.row();
        table.add(labelUser).pad(2);
        table.add(textUser).pad(2);
        table.row();
        table.add(buttonConnect).colspan(2).pad(1);

        table.row();
        table.add().colspan(2).pad(1);
        addActor(table);

    }

    @Override
    public void render(float delta) {
        super.render(delta);
//        if (progress < 1){
//            progress+=0.01f;
//        }
//
//        System.out.println("connect width is " + buttonConnect.getWidth() + " and connect height is " + buttonConnect.getHeight());
//        System.out.println("connect x is " + buttonConnect.getX() + " and connect y is " + buttonConnect.getY());
//        System.out.println("table width is " + table.getWidth() + " and table height is " + table.getHeight());
//        System.out.println("width is " + width + " and height is " + height);
//        System.out.println("progress is " + progress);
//
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.BLACK);
////        shapeRenderer.rect(width/4, height/2 - table.getHeight() * 0.6f,width/2,height/20);
//        shapeRenderer.rect(width/4, buttonConnect.getY(), width / 2, buttonConnect.getHeight());
//        shapeRenderer.end();
//
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.BLUE);
////        shapeRenderer.rect(width/4, height/2 - table.getHeight() * 0.6f,width/2 * progress,height/20);
////        shapeRenderer.rect(width/4, buttonConnect.getY() - (buttonConnect.getHeight() * 4f), width / 2 * progress, buttonConnect.getHeight());
//        shapeRenderer.rect(width/4, buttonConnect.getY(), width / 2 * progress, buttonConnect.getHeight());
//        shapeRenderer.end();
    }
}
