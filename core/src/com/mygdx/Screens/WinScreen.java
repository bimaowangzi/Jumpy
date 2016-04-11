package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.Sprites.OtherPlayer;
import com.mygdx.Sprites.Player;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by user on 11/3/2016.
 */
public class WinScreen extends AbstractScreen {

    private ArrayList<Object> alivePlayersArray = new ArrayList<Object>();
    private ArrayList<Object> deadPlayersArray = new ArrayList<Object>();

    private String[] alivePlayers = new String[]{"Alvin","Lam"};
    private String[] aliveRankings = new String[]{"1st","2nd"};
    private String[] aliveTimeings = new String[]{"1:30","1:40"};
    private String[] aliveHeight = new String[]{"500m","500m"};

    private String[] deadPlayers = new String[]{"Daniel","Junsheng"};
    private String[] deadRankings = new String[]{"3rd","4th"};
    private String[] deadTimeings = new String[]{"1:53","2:02"};
    private String[] deadHeight = new String[]{"400m","300m"};

//    private String[] liveUsers = new String[]{"Alvin","Lam","Daniel","Junsheng"};
//    private String[] rankings = new String[]{"1st","2nd","3rd","4th"};
//    private String[] timeings = new String[]{"1:30","1:40","1:53","2:02"};

    private final Label labelWinner;
    private final Label labelPlayers;
    private final Label labelRanking;
    private final Label labelTime;
    private final List listAlivePlayers;
    private final List listAliveRanking;
    private final List listAliveTime;
    private final List listDeadPlayers;
    private final List listDeadRanking;
    private final List listDeadTime;
    private final TextButton buttonReturnToLobby;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public WinScreen(Player player,OtherPlayer otherPlayer) {

        if (player.getResult().isDead()){

        }

        if (alivePlayers!=null){
            // at least one player complete and the first to complete is the winner
            labelWinner = new Label(alivePlayers[0] + " Wins!",skin,"title");
        } else {
            // no one complete, the highest player wins
            // this is according to timing now WRONG
            labelWinner = new Label(deadPlayers[0] + " Wins!",skin,"title");
        }
        labelPlayers = new Label("Players",skin,"title");
        labelRanking = new Label("Ranking",skin,"title");
        labelTime = new Label("Timing",skin,"title");
        listAlivePlayers = new List(skin,"Alive");
        listAliveRanking = new List(skin,"Alive");
        listAliveTime = new List(skin,"Alive");
        listDeadPlayers = new List(skin,"Dead");
        listDeadRanking = new List(skin,"Dead");
        listDeadTime = new List(skin,"Dead");
        buttonReturnToLobby = new TextButton("Back to Lobby",skin);
        buttonReturnToLobby.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Returning to lobby.");
                ScreenManager.getInstance().showScreen(ScreenEnum.ROOMSELECTION);
                return false;
            }
        });

        if (alivePlayers!=null) {
            listAlivePlayers.setItems(alivePlayers);
        }
        if (aliveRankings!=null) {
            listAliveRanking.setItems(aliveRankings);
        }
        if (aliveTimeings!=null) {
            listAliveTime.setItems(aliveTimeings);
        }
        if (deadPlayers!=null) {
            listDeadPlayers.setItems(deadPlayers);
        }
        if (deadRankings!=null) {
            listDeadRanking.setItems(deadRankings);
        }
        if (deadTimeings!=null) {
            listDeadTime.setItems(deadTimeings);
        }
    }

    @Override
    public void buildStage() {
        Table table = new Table();
        if (phoneDisplay){
            table.pad(100,0,400,0);
        } else {
            table.pad(50,0,0,0);
        }
        table.setFillParent(true);
        table.top();
        table.setDebug(true);
        table.add(labelWinner).colspan(3).pad(5);
        table.row();
        table.add(labelRanking);
        table.add(labelPlayers);
        table.add(labelTime);
        table.row();
        table.add(listAliveRanking).fill();
        table.add(listAlivePlayers).fill();
        table.add(listAliveTime).fill();
        table.row();
        table.add(listDeadRanking).fill();
        table.add(listDeadPlayers).fill();
        table.add(listDeadTime).fill();
        table.row();
        table.add(buttonReturnToLobby).colspan(3);
        addActor(table);
    }
}
