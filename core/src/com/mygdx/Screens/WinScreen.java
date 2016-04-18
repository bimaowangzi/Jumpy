package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.JumpyHelper.PlayerResult;
import com.mygdx.Sprites.OtherPlayer;
import com.mygdx.appwarp.WarpController;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Thread.sleep;

/**
 * Created by user on 11/3/2016.
 */
public class WinScreen extends AbstractScreen {

    private WarpClient warpClient;

    private ArrayList<PlayerResult> playersResultArray = new ArrayList<PlayerResult>();

    private int aliveCount = 0;
    private int deadCount = 0;

    private ArrayList<String> alivePlayers = new ArrayList<String>();
    private ArrayList<String> aliveRankings = new ArrayList<String>();
    private ArrayList<String> aliveTimings = new ArrayList<String>();
    private ArrayList<String> aliveHeight = new ArrayList<String>();

    private ArrayList<String> deadPlayers = new ArrayList<String>();
    private ArrayList<String> deadRankings = new ArrayList<String>();
    private ArrayList<String> deadTimings = new ArrayList<String>();
    private ArrayList<String> deadHeight = new ArrayList<String>();

    private final Label labelWinner;
    private final Label labelPlayers;
    private final Label labelRanking;
    private final Label labelTime;
    private final Label labelHeight;
    private final List listAlivePlayers;
    private final List listAliveRanking;
    private final List listAliveTime;
    private final List listAliveHeight;
    private final List listDeadPlayers;
    private final List listDeadRanking;
    private final List listDeadTime;
    private final List listDeadHeight;
    private final TextButton buttonReturnToLobby;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public WinScreen(PlayerResult playerResult, CopyOnWriteArrayList<OtherPlayer> otherPlayers) {
        getWarpClient();

        WinUpdateThread winUpdateThread = new WinUpdateThread(warpClient,this,WarpController.getRoomId());
        winUpdateThread.start();

//        warpClient.setCustomUserData(WarpController.getLocalUser(), "Selecting," + WarpController.getAvatarMap().get(WarpController.getLocalUser()));

        playersResultArray.add(playerResult);
        for (OtherPlayer other:otherPlayers)
            playersResultArray.add(other.getResult());

        Collections.sort(playersResultArray);

        for (int i = 1; i <= playersResultArray.size(); i++){
            int index = i -1;
            if (playersResultArray.get(index).isDead()){
                deadCount++;
                deadPlayers.add(playersResultArray.get(index).getUserName());
                deadTimings.add(Double.toString(Math.round(playersResultArray.get(index).getTime()*100)/100));
                deadHeight.add(Integer.toString(playersResultArray.get(index).getHeight()));
                // need to tweak formatting if more than 10 players.
                if (i == 1){
                    deadRankings.add("1st");
                } else if (i == 2){
                    deadRankings.add("2nd");
                } else if (i == 3){
                    deadRankings.add("3rd");
                } else {
                    deadRankings.add(i + "th");
                }
            } else {
                aliveCount++;
                alivePlayers.add(playersResultArray.get(index).getUserName());
                aliveTimings.add(Double.toString(Math.round(playersResultArray.get(index).getTime()*100)/100));
                aliveHeight.add(Integer.toString(playersResultArray.get(index).getHeight()));
                // need to tweak formatting if more than 10 players.
                if (i == 1){
                    aliveRankings.add("1st");
                } else if (i == 2){
                    aliveRankings.add("2nd");
                } else if (i == 3){
                    aliveRankings.add("3rd");
                } else {
                    aliveRankings.add(i + "th");
                }
            }
        }

        if (aliveCount>0){
            // at least one player complete and the first to complete is the winner
            labelWinner = new Label(alivePlayers.get(0) + " Wins!",skin,"title");
        } else {
            // no one complete, the highest player wins
            // this is according to timing now WRONG
            labelWinner = new Label(deadPlayers.get(0) + " Wins!",skin,"title");
        }
        labelPlayers = new Label("Players",skin,"title");
        labelRanking = new Label("Ranking",skin,"title");
        labelTime = new Label("Timing",skin,"title");
        labelHeight = new Label("Height",skin,"title");
        listAlivePlayers = new List(skin,"Alive");
        listAliveRanking = new List(skin,"Alive");
        listAliveTime = new List(skin,"Alive");
        listAliveHeight = new List(skin,"Alive");
        listDeadPlayers = new List(skin,"Dead");
        listDeadRanking = new List(skin,"Dead");
        listDeadTime = new List(skin,"Dead");
        listDeadHeight = new List(skin,"Dead");
        buttonReturnToLobby = new TextButton("Back to Lobby",skin);
        buttonReturnToLobby.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Returning to lobby.");

//                warpClient.getLiveRoomInfo(WarpController.getRoomId());
//                while (!WarpController.isWaitflag()){
//                    // busy wait
//                }
//                WarpController.setWaitflag(false);
//
//                String[] liveUsers = WarpController.getLiveUsers();
//
//                for (String user : liveUsers){
//                    warpClient.getLiveUserInfo(user);
//                    while (!WarpController.isWaitflag()){
//                        // busy wait
//                    }
//                    WarpController.setWaitflag(false);
//                }
                ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);
                return false;
            }
        });

        if (aliveCount>0){
            listAlivePlayers.setItems(alivePlayers.toArray());
            listAliveRanking.setItems(aliveRankings.toArray());
            listAliveTime.setItems(aliveTimings.toArray());
            listAliveHeight.setItems(aliveHeight.toArray());
        }

        if (deadCount>0){
            listDeadPlayers.setItems(deadPlayers.toArray());
            listDeadRanking.setItems(deadRankings.toArray());
            listDeadTime.setItems(deadTimings.toArray());
            listDeadHeight.setItems(deadHeight.toArray());
        }
    }

/*    private void clearInfo(){
        world.getPlayer().setResult(new PlayerResult(false, -1, -1, world.getPlayer().getName()));
        for (OtherPlayer otherPlayer : world.getOtherPlayers()){
            otherPlayer.setResult(new PlayerResult(false, -1, -1, otherPlayer.getName()));
            System.out.println("Player " + otherPlayer.getName() + " result cleared");
        }
        world.getOtherPlayers().clear();
        System.out.println("Info cleared");
    }*/

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
        if (phoneDisplay){
            table.pad(15,0,350,0);
        } else {
            table.pad(50,0,0,0);
        }
        table.setFillParent(true);
        table.top();
        table.setDebug(false);
        table.add(labelWinner).colspan(4).pad(5);
        table.row();
        table.add(labelRanking);
        table.add(labelPlayers);
        table.add(labelTime);
        table.add(labelHeight);
        table.row();
        table.add(listAliveRanking).fill();
        table.add(listAlivePlayers).fill();
        table.add(listAliveTime).fill();
        table.add(listAliveHeight).fill();
        table.row();
        table.add(listDeadRanking).fill();
        table.add(listDeadPlayers).fill();
        table.add(listDeadTime).fill();
        table.add(listDeadHeight).fill();
        table.row();
        table.add(buttonReturnToLobby).colspan(4).pad(5);
        addActor(table);
    }
}

class WinUpdateThread extends Thread{

    WarpClient warpClient;
    WinScreen winScreen;
    String roomId;

    public WinUpdateThread(WarpClient warpClient, WinScreen winScreen, String roomId) {
        this.warpClient = warpClient;
        this.winScreen = winScreen;
        this.roomId = roomId;
    }

    @Override
    public void run() {
        warpClient.getLiveRoomInfo(roomId);
        while (!WarpController.isWaitflag()){
            // busy wait
        }
        WarpController.setWaitflag(false);

        warpClient.setCustomUserData(WarpController.getLocalUser(), "Selecting," + WarpController.getAvatarMap().get(WarpController.getLocalUser()));

        String[] liveUsers = WarpController.getLiveUsers();
        if (liveUsers != null){
            for (String user : liveUsers){
                warpClient.getLiveUserInfo(user);
                while (!WarpController.isWaitflag()){
                    // busy wait
                }
                WarpController.setWaitflag(false);
            }
        }
    }
}
