package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;

/**
 * Created by user on 11/3/2016.
 */
public class RoomSelectionScreen extends AbstractScreen{

    private final TextButton buttonCreateRoom;
    private final TextField textNewRoom;
    private final Label labelWelcome;
    private final Label labelNewRoom;
    private final Label labelRoomList;
    private final List listRooms;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public RoomSelectionScreen(String username) {
        buttonCreateRoom = new TextButton("Connect",skin);
        buttonCreateRoom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String text = textNewRoom.getText();
                System.out.println("The user is: " + text);
                if (text.length() > 0) {
                    ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);
                }
                return false;
            }
        });
        textNewRoom = new TextField("",skin);
        labelNewRoom = new Label("New Room:",skin);
        labelWelcome = new Label("Welcome, " + username,skin);
        labelRoomList = new Label("Room List", skin);
        listRooms = new List(skin);
        listRooms.setItems("ISTD 3/4","EPD 2/4","ESD 3/4","ASD 4/4");
        buildStage();
    }

    @Override
    public void buildStage() {
        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.setDebug(true);

        table.add(labelWelcome).colspan(2).pad(1);
        table.row();
        table.add(labelNewRoom).pad(5);
        table.add(textNewRoom).pad(5);
        table.row();
        table.add(buttonCreateRoom).colspan(2).pad(5);

        table.row();
        table.add(labelRoomList).colspan(2).space(30);
        table.row();
        table.add(listRooms).colspan(2);
        addActor(table);
    }
}
