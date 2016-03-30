package com.mygdx.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.JumpyHelper.AssetLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.appwarp.WarpController;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

import java.util.HashMap;

/**
 * Created by user on 11/3/2016.
 */
public class AvatarScreen extends AbstractScreen{
    Table table = new Table();

    private Game game;

    private final TextButton buttonSelect;
    private WarpClient warpClient;

    private Label labelAvatar;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
    private static TextureRegionDrawable avatar1 = new TextureRegionDrawable(AssetLoader.powerupTextures.get(0));
    private static TextureRegionDrawable avatar2 = new TextureRegionDrawable(AssetLoader.powerupTextures.get(1));
    private static TextureRegionDrawable avatar3 = new TextureRegionDrawable(AssetLoader.powerupTextures.get(2));
    private static TextureRegionDrawable avatar4 = new TextureRegionDrawable(AssetLoader.powerupTextures.get(3));
    private ImageButton imageAvatar1 = new ImageButton(avatar1);
    private ImageButton imageAvatar2 = new ImageButton(avatar2);
    private ImageButton imageAvatar3 = new ImageButton(avatar3);
    private Image displayAvatar = new Image(avatar1);

    private static String avatarName = "none";
    private static HashMap<String,String> avatarMap = WarpController.getAvatarMap();


    public static String getAvatarName() {
        return avatarName;
    }

    public static HashMap<String, String> getAvatarMap() {
        return avatarMap;
    }

    public AvatarScreen() {
        getWarpClient();


        if(avatarMap.containsValue("avatar1")){
            System.out.println("hello");
            imageAvatar1.setDisabled(true);
        }

        if(avatarMap.containsValue("avatar2")){
            imageAvatar2.setDisabled(true);
        }

        if(avatarMap.containsValue("avatar3")){
            imageAvatar3.setDisabled(true);
        }

        if(imageAvatar1.isDisabled()) {
            imageAvatar1 = new ImageButton(avatar4);
        }
        else{
            imageAvatar1.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                displayAvatar = new Image(avatar1);
                avatarName = "avatar1";
                labelAvatar = new Label(avatarName,skin);
                table.remove();
                table = new Table();
                buildStage();


                return false;
            }
        });
        }
        if(imageAvatar2.isDisabled()) {
            imageAvatar2 = new ImageButton(avatar4);
        }
        else{
            imageAvatar2.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    displayAvatar = new Image(avatar2);
                    avatarName = "avatar2";
                    labelAvatar = new Label(avatarName,skin);
                    table.remove();
                    table = new Table();
                    buildStage();

                    return false;
                }
            });

        }
        if(imageAvatar3.isDisabled()) {
            imageAvatar3 = new ImageButton(avatar4);
        }
        else{
            imageAvatar3.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    displayAvatar = new Image(avatar3);
                    avatarName = "avatar3";
                    labelAvatar = new Label(avatarName,skin);
                    table.remove();
                    table = new Table();
                    buildStage();

                    return false;
                }
            });
        }
        buttonSelect = new TextButton("Select", skin);


        buttonSelect.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String user = WarpController.getLocalUser();

                String avatar = avatarName;
                warpClient.setCustomUserData(WarpController.getLocalUser(),WarpController.getStatusMap()+","+avatarName);

                ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);
                return false;
            }
        });




        labelAvatar = new Label(avatarName,skin);

    }

    @Override
    public void buildStage() {
        table.setFillParent(true);
        table.center();
        table.setDebug(true);

        table.add(displayAvatar).expand().colspan(3);
        table.row();
        table.add(imageAvatar1).expand();
        table.add(imageAvatar2).expand();
        table.add(imageAvatar3).expand();
        table.row();
        table.add(labelAvatar).colspan(3);
        table.row();
        table.add(buttonSelect).colspan(3);

        addActor(table);
    }

    public Image getDisplayAvatar() {
        return displayAvatar;
    }

    private void getWarpClient(){
        try {
            warpClient = WarpClient.getInstance();
        } catch (Exception ex) {
            System.out.println("Fail to get warpClient");
        }
    }

}
