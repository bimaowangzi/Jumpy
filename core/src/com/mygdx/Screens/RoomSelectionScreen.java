package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.mygdx.appwarp.WarpController;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;

import java.util.HashMap;

/**
 * Created by user on 11/3/2016.
 */
public class RoomSelectionScreen extends AbstractScreen{

    private WarpClient warpClient;

    private final TextButton buttonCreateRoom;
    private final TextButton buttonConnectRoom;
    private final TextButton buttonLogout;
    private final TextField textNewRoom;
    private final Label labelWelcome;
    private final Label labelNewRoom;
    private final Label labelRoomList;
    private final List listRooms;

    HashMap<String,String> roomMap;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public RoomSelectionScreen() {
        System.out.println("roomSelConstructed");
        getWarpClient();

        // start thread to call getRoomInRange()
        final RoomSelUpdateThread roomSelUpdateThread = new RoomSelUpdateThread(warpClient,this);
        roomSelUpdateThread.start();

        final RoomData[] roomDataList = WarpController.getRoomDatas();
        buttonCreateRoom = new TextButton("Create Room",skin);
        buttonCreateRoom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String text = textNewRoom.getText();
                if (text.length() > 0) {
                    roomSelUpdateThread.interrupt();

                    warpClient.setCustomUserData(WarpController.getLocalUser(), "Selecting");
                    warpClient.createRoom(text, WarpController.getLocalUser(), 4, null);
                    System.out.println("Creating Room...");
                    while (!WarpController.isWaitflag()) {
                    }
                    System.out.println("New Room " + text + " is created.");
                    WarpController.setWaitflag(false);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);
                }
                return false;
            }
        });
        buttonConnectRoom = new TextButton("Connect Room",skin);
        buttonConnectRoom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (listRooms.getSelected() != null){
                    roomSelUpdateThread.interrupt();

                    warpClient.setCustomUserData(WarpController.getLocalUser(), "Selecting");
                    String selected = (String) listRooms.getSelected();
                    String roomName = selected.substring(4);
                    String roomId = roomMap.get(roomName);
                    System.out.println("Joining Room " + roomId + ".");
                    warpClient.subscribeRoom(roomId);
                    System.out.println("Connecting...");
                    while(!WarpController.isWaitflag()){
                    }
                    WarpController.setWaitflag(false);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);

                }
                return false;
            }
        });
        buttonLogout = new TextButton("Logout",skin);
        buttonLogout.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                roomSelUpdateThread.interrupt();

                System.out.println("Logging Out.");
                WarpController.getInstance().disconnect();
                while (!WarpController.isWaitflag()){}
                WarpController.setWaitflag(false);
                Gdx.input.setOnscreenKeyboardVisible(false);
                ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN);
                return false;
            }
        });
        textNewRoom = new TextField("",skin);
        labelNewRoom = new Label("New Room:",skin);
        labelWelcome = new Label("Welcome, " + WarpController.getLocalUser(),skin);
        labelRoomList = new Label("Room List", skin);
        listRooms = new List(skin);
        addRoomToList(roomDataList);
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
        table.row();
        table.add(buttonConnectRoom).colspan(2).space(10);
        table.row();
        table.add(buttonLogout).colspan(2).space(10);
        addActor(table);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        RoomData[] roomDataList = WarpController.getRoomDatas();
        addRoomToList(roomDataList);
    }

    public void addRoomToList(RoomData[] roomDatas){
        roomMap = new HashMap<String, String>();
        if (roomDatas != null){
            String[] roomList = new String[roomDatas.length];
            for (int i = 0;i<roomDatas.length;i++){
                RoomData roomData = roomDatas[i];
                roomMap.put(roomData.getName(),roomData.getId());
                roomList[i] = "Rm: " + roomData.getName();
            }
            listRooms.setItems(roomList);
        } else {
            listRooms.clearItems();
        }
    }
}

class RoomSelUpdateThread extends Thread{

    WarpClient warpClient;
    RoomSelectionScreen roomSelectionScreen;

    public RoomSelUpdateThread(WarpClient warpClient, RoomSelectionScreen roomSelectionScreen) {
        this.warpClient = warpClient;
        this.roomSelectionScreen = roomSelectionScreen;
    }

    @Override
    public void run() {
        while (!isInterrupted()){
            // look for rooms with 1 to 3 players already
            warpClient.getRoomInRange(0, 3);
            while (!WarpController.isWaitRoomFlag()){
                // busy wait
            }
            WarpController.setWaitRoomFlag(false);
        }
        System.out.println("thread interrupted");
    }
}
