package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by user on 11/3/2016.
 */

/**This screen represents a waiting room for the players prior to the game.
 * Players can chat with one another, initiate avatar change by switching to avatar screen,
 * Switching status to signal ready or selecting*/
public class LobbyScreen extends AbstractScreen {

    private WarpClient warpClient;
    private RoomData room;
    private String roomId;
    private String roomName;
    private String[] liveUsers;

    public static Integer startSeed;
    public static Integer intervalSeed;

    public static volatile boolean startGame;

    private final TextButton buttonSend;
    private final TextButton buttonExit;
    private final TextButton buttonChangeAvatar;
    private final TextButton buttonStatusToggle;
    private final TextField textInput;
    private final ScrollPane scrollChat;
    private final Label labelChat;
    private final Label labelRoom;
    private final Label labelNumOfPlayers;
    private final Label labelPlayers;
    private final List listPlayers;
    private final Label labelStatus;
    private final List listStatus;
    private final Label labelAvatar;
    private final List listAvatar;
    ConcurrentHashMap avatarMap = WarpController.getAvatarMap();

    final LobbyUpdateThread lobbyUpdateThread;
    final CheckStartThread checkStartThread;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public LobbyScreen() {

        System.out.println("Lobby Constructed");

        getWarpClient();

        startGame = false;

        room = WarpController.getRoom();
        roomId = WarpController.getRoomId();
        roomName = room.getName();

        if (WarpController.getLiveUsers()[0].equals(WarpController.getLocalUser())){
            Random ranGen = new Random();

            startSeed =  ranGen.nextInt(100) + 1;
            intervalSeed = ranGen.nextInt(100) + 1;
        }

        /**Start thread to update in-lobby players*/
        lobbyUpdateThread = new LobbyUpdateThread(warpClient,this,roomId);
        lobbyUpdateThread.start();

        /**Start thread to check for start*/
        checkStartThread = new CheckStartThread();
        checkStartThread.start();

        /**Button for sending messages*/
        buttonSend = new TextButton("Send",skin);
        buttonSend.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String text = textInput.getText();
                if (text != null) {
                    warpClient.sendChat(text);
                    textInput.setText("");
                    return true;
                } else {
                    return false;
                }
            }
        });

        /**Button for Exiting Lobby Screen to Room Sel Screen*/
        buttonExit = new TextButton("Exit",skin);
        buttonExit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                lobbyUpdateThread.interrupt();
                checkStartThread.interrupt();
                System.out.println("Leaving Room " + roomId + ".");
                warpClient.unsubscribeRoom(roomId);
                warpClient.leaveRoom(roomId);
                WarpController.clearLiveUsers();
                WarpController.clearStatusMap();
                WarpController.clearAvatarMap();
                Gdx.input.setOnscreenKeyboardVisible(false);
                ScreenManager.getInstance().showScreen(ScreenEnum.ROOMSELECTION);
                return false;
            }
        });

        /**Button for switching to Avatar Screen*/
        buttonChangeAvatar = new TextButton("Change Avatar",skin);
        buttonChangeAvatar.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                lobbyUpdateThread.interrupt();
                checkStartThread.interrupt();
                Gdx.input.setOnscreenKeyboardVisible(false);
                ScreenManager.getInstance().showScreen(ScreenEnum.AVATAR);
                return false;
            }
        });

        /**Toggle button for signaling Selecting or Ready status*/
        buttonStatusToggle = new TextButton("Ready",skin);
        buttonStatusToggle.setChecked(false);
        buttonStatusToggle.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (buttonStatusToggle.isChecked()) {

                    buttonStatusToggle.setText("Ready");

                    System.out.println("Sets Selecting");

                    warpClient.setCustomUserData(WarpController.getLocalUser(), "Selecting," + avatarMap.get(WarpController.getLocalUser()));
                } else {

                    buttonStatusToggle.setText("Selecting");

                    System.out.println("Sets Ready");
                    warpClient.setCustomUserData(WarpController.getLocalUser(), "Ready," + avatarMap.get(WarpController.getLocalUser()));
                }
                return false;
            }
        });

        textInput = new TextField("",skin);
        labelChat = new Label("",skin);

        // There is a bug that if u set fill parent, Spaces will be created at the top and bottom is unreadable.
//        labelChat.setFillParent(true);

        WarpController.setLabelChat(labelChat);
        Table chatTable = new Table();
        chatTable.add(labelChat).expand();
        scrollChat = new ScrollPane(chatTable);
        scrollChat.setScrollingDisabled(true,false);
        labelRoom = new Label(roomName,skin,"title");
        warpClient.getLiveRoomInfo(roomId);
        liveUsers = WarpController.getLiveUsers();
        if (liveUsers != null){
            labelNumOfPlayers = new Label(liveUsers.length + "/" + room.getMaxUsers(),skin);
        } else {
            labelNumOfPlayers = new Label(0 + "/" + room.getMaxUsers(),skin);
        }
        labelPlayers = new Label("Players", skin,"title");
        listPlayers = new List(skin,"noHighlight");

        // sometimes crash due to liveusers being null
        if (liveUsers!=null) {
            listPlayers.setItems(liveUsers);
        }

        labelStatus = new Label("Status", skin,"title");
        listStatus = new List(skin,"noHighlight");
        labelAvatar = new Label("Avatar", skin,"title");
        listAvatar = new List(skin,"noHighlight");

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

        if (startGame){
            lobbyUpdateThread.interrupt();
            checkStartThread.interrupt();
            Gdx.input.setOnscreenKeyboardVisible(false);
            ScreenManager.getInstance().showScreen(ScreenEnum.PLAY);
        }
    }

    @Override
    public void buildStage() {
        Table tableBig = new Table();
        tableBig.setFillParent(true);
        if (phoneDisplay){
            tableBig.pad(15,0,350,0);
        }else {
            tableBig.pad(50,0,200,0);
        }
        tableBig.top();
        tableBig.setDebug(false);

        Table tableTop = new Table();
        tableTop.setDebug(false);
        tableTop.add(labelRoom).left().pad(5);
        tableTop.add(labelNumOfPlayers).right().pad(5);
        tableBig.add(tableTop);
        tableBig.row();

        Table tableMid = new Table();
        tableMid.setDebug(false);
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
        tableMid.add(textInput).colspan(2).fill();
        tableMid.add(buttonSend);
        tableMid.row();
        tableMid.add(buttonExit);
        tableMid.add(buttonChangeAvatar);
        tableMid.add(buttonStatusToggle);
        tableBig.add(tableMid);

        addActor(tableBig);
    }

    /**Method to update status list*/
    public void addStatusToList(){
        liveUsers = WarpController.getLiveUsers();
        String[] statuses = new String[liveUsers.length];
        ConcurrentHashMap statusMap = WarpController.getStatusMap();
        for (int i = 0;i<liveUsers.length;i++){
            String user = liveUsers[i];
            statuses[i] = (String) statusMap.get(user);
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
    }

    /**Method to update avatar list*/
    public void addImageToList(){
        liveUsers = WarpController.getLiveUsers();
        String[] avatars = new String[liveUsers.length];
        for (int i = 0;i<liveUsers.length;i++){
            String user = liveUsers[i];
            avatars[i] = (String) avatarMap.get(user);
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
    }
}

/**Thread to check if start condition has been met*/
class CheckStartThread extends Thread{

    private static volatile ConcurrentHashMap<String,String> statusMap = new ConcurrentHashMap<String,String>();
    private boolean allReady;

    public CheckStartThread() {
        allReady = false;
    }

    @Override
    public void run() {
        while (!allReady){
            statusMap = WarpController.getStatusMap();

            if (isInterrupted()){
                break;
            }

            /**Check if there is at least 2 players in the room*/
            if (statusMap.size() <= 1){
                continue;
            }

            /**Iterate through the players in the room to check if everyone has selected an avatar*/
            boolean avatarFlag = true;
            for (Map.Entry<String, String> entry: WarpController.getAvatarMap().entrySet()) {
                if (entry.getValue()=="none") {
                    avatarFlag = false;
                    break;
                }
            }

            if (!avatarFlag)
                continue;

            /**Iterate through the players in the room to check if everyone is ready*/
            Iterator it = statusMap.entrySet().iterator();
            allReady = true;
            while (it.hasNext()){
                Map.Entry pair = (Map.Entry) it.next();
                String status = (String) pair.getValue();
                if (!status.equals("Ready")){
                    allReady = false;
                    break;
                }
            }

            if (!allReady){
                continue;
            }

            System.out.println("All Ready and more than 1 player in room");

            if (isInterrupted()){
                break;
            }
        }
        if (!isInterrupted()){
            LobbyScreen.startGame = true;
            System.out.println("Starting Game");
        }
        System.out.println("start thread ended");
    }
}

/**Thread to pull update so that liveUsers and each user's status is refreshed*/
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
