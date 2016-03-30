package com.mygdx.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
public class LobbyScreen extends AbstractScreen {

    private WarpClient warpClient;

    private Game game;


    private RoomData room;
    private String roomId;
    private String roomName;
    private String[] liveUsers;

    private final TextButton buttonSend;
    private final TextButton buttonExit;
    private final TextButton buttonChangeAvatar;
    private final TextButton buttonStatusToggle;
    private final TextField textInput;
    private final ScrollPane scrollChat;
//    private final Table chatTable;
    private final Label labelChat;
    private final Label labelRoom;
    private final Label labelNumOfPlayers;
    private final Label labelPlayers;
    private final List listPlayers;
    private final Label labelStatus;
    private final List listStatus;
    private final Label labelAvatar;
    private final List listAvatar;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public LobbyScreen() {
        getWarpClient();

        room = WarpController.getRoom();
        roomId = WarpController.getRoomId();
        roomName = room.getName();

        // start thread to update in-lobby players
        final LobbyUpdateThread lobbyUpdateThread = new LobbyUpdateThread(warpClient,this,roomId);
        lobbyUpdateThread.start();

        buttonSend = new TextButton("Send",skin);
        buttonSend.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String text = textInput.getText();
                if (text != null) {
//                    warpClient.sendChat(WarpController.getLocalUser() + ": " + text);
                    warpClient.sendChat(text);
                    textInput.setText("");
                    return true;
                } else {
                    return false;
                }
            }
        });
        buttonExit = new TextButton("Exit",skin);
        buttonExit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Exit to RoomSel Screen, done
                // unsubscribe, leave room, done
                // delete room if empty, to be done
                lobbyUpdateThread.interrupt();
                System.out.println("Leaving Room " + roomId + ".");
                warpClient.unsubscribeRoom(roomId);
                warpClient.leaveRoom(roomId);
                WarpController.clearLiveUsers();
                ScreenManager.getInstance().showScreen(ScreenEnum.ROOMSELECTION);
                return false;
            }
        });
        buttonChangeAvatar = new TextButton("Change Avatar",skin);
        buttonChangeAvatar.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ScreenManager.getInstance().showScreen(ScreenEnum.AVATAR);
                return false;
            }
        });
        buttonStatusToggle = new TextButton("Selecting",skin);
        buttonStatusToggle.setChecked(false);
        buttonStatusToggle.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Toggle between Selecting and Ready
                System.out.println("Before:" + buttonStatusToggle.isChecked());
                if (buttonStatusToggle.isChecked()) {

                    buttonStatusToggle.setText("Selecting");
//                    buttonStatusToggle.setChecked(false);

                    System.out.println("Sets Selecting");

                    warpClient.setCustomUserData(WarpController.getLocalUser(),"Selecting");
                } else {

                    buttonStatusToggle.setText("Ready");
//                    buttonStatusToggle.setChecked(true);

                    System.out.println("Sets Ready");
                    warpClient.setCustomUserData(WarpController.getLocalUser(), "Ready");
                }
                System.out.println("After:" + buttonStatusToggle.isChecked());
                return false;
            }
        });

        textInput = new TextField("",skin);
        labelChat = new Label("",skin);
//        labelChat.setWidth(300);
//        labelChat.setBounds(0,0,300,400);
//        labelChat.setWrap(true);
        // This is a bug that if u set fill parent, Spaces will be created at the top and bottom is unreadable.
//        labelChat.setFillParent(true);


        WarpController.setLabelChat(labelChat);
        Table chatTable = new Table();
        chatTable.add(labelChat).expand();
        scrollChat = new ScrollPane(chatTable);
        scrollChat.setScrollingDisabled(true,false);
//        chatTable = new Table();
//        chatTable.add(labelChat).expandX().expandY();
//        chatTable.setFillParent(true);
//        chatTable.left();
        labelRoom = new Label(roomName,skin);
        warpClient.getLiveRoomInfo(roomId);
        liveUsers = WarpController.getLiveUsers();
        if (liveUsers != null){
            labelNumOfPlayers = new Label(liveUsers.length + "/" + room.getMaxUsers(),skin);
        } else {
            labelNumOfPlayers = new Label(0 + "/" + room.getMaxUsers(),skin);
        }
        labelPlayers = new Label("Players", skin);
        listPlayers = new List(skin);

        // sometimes crash due to liveusers being null
        if (liveUsers!=null) {
            listPlayers.setItems(liveUsers);
        }

        labelStatus = new Label("Status", skin);
        listStatus = new List(skin);
        labelAvatar = new Label("Avatar", skin);
        listAvatar = new List(skin);

    }

    private void getWarpClient(){
        try {
            warpClient = WarpClient.getInstance();
        } catch (Exception ex) {
            System.out.println("Fail to get warpClient");
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        liveUsers = WarpController.getLiveUsers();
        if (liveUsers!=null && room!=null){
            labelNumOfPlayers.setText(liveUsers.length + "/" + room.getMaxUsers());
            listPlayers.setItems(liveUsers);
            addStatusToList();
            addImageToList();
        }
    }

    @Override
    public void buildStage() {
        Table tableBig = new Table();
        tableBig.setFillParent(true);
        tableBig.top();
        tableBig.setDebug(true);

        Table tableTop = new Table();
        tableTop.setDebug(true);
        tableTop.add(labelRoom).left().pad(5);
        tableTop.add(labelNumOfPlayers).right().pad(5);
        tableBig.add(tableTop);
        tableBig.row();

        Table tableMid = new Table();
        tableMid.setDebug(true);
        tableMid.add(labelPlayers);
        tableMid.add(labelStatus);
        tableMid.add(labelAvatar);
        tableMid.row();

        tableMid.add(listPlayers);
        tableMid.add(listStatus);
        tableMid.add(listAvatar);
        tableMid.row();

        tableMid.add(scrollChat).colspan(3).maxHeight(400);
        scrollChat.setSize(tableMid.getWidth(),50);
        tableMid.row();
        tableMid.add(textInput).colspan(2);
        tableMid.add(buttonSend);
        tableMid.row();
        tableMid.add(buttonExit);
        tableMid.add(buttonChangeAvatar);
        tableMid.add(buttonStatusToggle);
        tableBig.add(tableMid);

        addActor(tableBig);
    }

    public void addStatusToList(){
        liveUsers = WarpController.getLiveUsers();
        String[] statuses = new String[liveUsers.length];
        HashMap statusMap = WarpController.getStatusMap();
        for (int i = 0;i<liveUsers.length;i++){
            String user = liveUsers[i];
            statuses[i] = (String) statusMap.get(user);
//            System.out.println(user + " is " + statuses[i]);
        }
        boolean nullCheck = false;
        for (String status : statuses){
            if (status==null){
                nullCheck = true;
                break;
            }
        }
        if (!nullCheck){
            listStatus.setItems(statuses);
        }
//        if (statuses[0]!=null){
//            listStatus.setItems(statuses);
//        }
    }


    public void addImageToList(){
        liveUsers = WarpController.getLiveUsers();
        String[] avatars = new String[liveUsers.length];
        HashMap avatarMap = WarpController.getAvatarMap();
        for (int i = 0;i<liveUsers.length;i++){
            String user = liveUsers[i];
            avatars[i] = (String) avatarMap.get(user);
//            System.out.println(user + " is " + statuses[i]);
        }
        boolean nullCheck = false;
        for (String avatar : avatars){
            if (avatar==null){
                nullCheck = true;
                break;
            }
        }
        if (!nullCheck){
            listAvatar.setItems(avatars);
        }
//        if (statuses[0]!=null){
//            listStatus.setItems(statuses);
//        }
    }
}

class LobbyUpdateThread extends Thread{

    WarpClient warpClient;
    LobbyScreen lobbyScreen;
    String roomId;

    public LobbyUpdateThread(WarpClient warpClient, LobbyScreen lobbyScreen, String roomId) {
        this.warpClient = warpClient;
        this.lobbyScreen = lobbyScreen;
        this.roomId = roomId;
    }

    @Override
    public void run() {
        while (!isInterrupted()){
            warpClient.getLiveRoomInfo(roomId);
            while (!WarpController.isWaitflag()){
                // busy wait
            }
            WarpController.setWaitflag(false);

            if (isInterrupted()){
                break;
            }

            String[] liveUsers = WarpController.getLiveUsers();
            if (liveUsers != null){
                for (String user : liveUsers){
                    warpClient.getLiveUserInfo(user);
                    while (!WarpController.isWaitflag()){
                        // busy wait
                        if (isInterrupted()){
                            break;
                        }
                    }
                    WarpController.setWaitflag(false);
                    if (isInterrupted()){
                        break;
                    }
                }
            }
        }
        System.out.println("thread interrupted");
    }
}
