package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by user on 11/3/2016.
 */

/**This screen displays the list of rooms available for joining and allows the user to create room*/
public class RoomSelectionScreen extends AbstractScreen{

    private WarpClient warpClient;
    private Stage stage = this;

    private final TextButton buttonCreateRoom;
    private final TextButton buttonConnectRoom;
    private final TextButton buttonLogout;
    private final TextField textNewRoom;
    private final Label labelWelcome;
    private final Label labelNewRoom;
    private final Label labelRoomList;
    private final List listRooms;
    private Table table;

    ConcurrentHashMap<String,String> roomMap;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public RoomSelectionScreen() {

        System.out.println("roomSelConstructed");

        final Label labelNoRoomError = new Label("Please key in a room name.",skin);
        labelNoRoomError.setWrap(true);

        final Label labelRoomTakenError = new Label("Room name is taken, please key in another room name.",skin);
        labelRoomTakenError.setWrap(true);

        getWarpClient();

        /**start thread to call getRoomInRange()
         * which polls for available rooms*/
        final RoomSelUpdateThread roomSelUpdateThread = new RoomSelUpdateThread(warpClient,this);
        roomSelUpdateThread.start();

        final RoomData[] roomDataList = WarpController.getRoomDatas();
        /**Button to create room*/
        buttonCreateRoom = new TextButton("Create Room",skin);
        buttonCreateRoom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String text = textNewRoom.getText();

                RoomData[] roomDataList = WarpController.getRoomDatas();
                if (roomDataList!=null){
                    for (RoomData roomData:roomDataList){
                        System.out.println(roomData.getName() + " is it equal to " + text);
                        if ((roomData.getName()).equals(text)){
                            /**Room name taken error*/
                            table.setVisible(false);
                            new Dialog("Error",skin){

                                {
                                    this.getContentTable().add(labelRoomTakenError).prefWidth(table.getWidth());
                                    button("OK");
                                }

                                @Override
                                protected void result(Object object) {
                                    super.result(object);
                                    table.setVisible(true);
                                }

                            }.show(stage);
                            return false;
                        }
                    }
                }

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
                } else if (text.length() == 0){
                    /**Empty New Room field error*/
                    table.setVisible(false);
                    new Dialog("Error",skin){

                        {
                            this.getContentTable().add(labelNoRoomError).prefWidth(table.getWidth());
                            button("OK");
                        }

                        @Override
                        protected void result(Object object) {
                            super.result(object);
                            table.setVisible(true);
                        }

                    }.show(stage);
                    return false;
                }
                return false;
            }
        });
        /**Button to connect to selected room*/
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
        /**Button to logout to Login Screen*/
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
        labelRoomList = new Label("Room List", skin,"title");
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
        table = new Table();
        table.setFillParent(true);
        table.center();
        table.setDebug(false);

        table.add(labelWelcome).colspan(2).pad(1);
        table.row();
        table.add(labelNewRoom).pad(5);
        table.add(textNewRoom).pad(5).fill();
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

    /**Method to update room list*/
    public void addRoomToList(RoomData[] roomDatas){
        roomMap = new ConcurrentHashMap<String, String>();
        if (roomDatas != null){
            if (!listRooms.isVisible()){
                listRooms.setVisible(true);
            }
            String[] roomList = new String[roomDatas.length];
            for (int i = 0;i<roomDatas.length;i++){
                RoomData roomData = roomDatas[i];
                roomMap.put(roomData.getName(),roomData.getId());
                roomList[i] = "Rm: " + roomData.getName();
            }
            listRooms.setItems(roomList);
        } else {
            if (listRooms.isVisible()){
                listRooms.setVisible(false);
            }
            listRooms.clearItems();
        }
    }
}

/**Thread to pull update to Room Sel Screen
 * It constantly polls available rooms*/
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
            warpClient.getRoomInRange(1, 3);
            while (!WarpController.isWaitRoomFlag()){
                // busy wait
            }
            WarpController.setWaitRoomFlag(false);
        }
        System.out.println("thread interrupted");
    }
}
