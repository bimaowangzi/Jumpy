package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

/**
 * Created by user on 11/3/2016.
 */
public class WinScreen extends AbstractScreen {

    WarpClient warpClient;

    private String[] liveUsers = new String[]{"Alvin","Lam","Daniel","Junsheng"};
    private String[] rankings = new String[]{"1st","2nd","3rd","4th"};
    private String[] timeings = new String[]{"1:30","1:40","1:53","2:02"};

    private final Label labelWinner;
    private final Label labelPlayers;
    private final Label labelRanking;
    private final Label labelTime;
    private final List listPlayers;
    private final List listRanking;
    private final List listTime;
    private final TextButton buttonReturnToLobby;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public WinScreen() {

//        getWarpClient();

        labelWinner = new Label(liveUsers[0] + " Wins!",skin,"title");
        labelPlayers = new Label("Players",skin,"title");
        labelRanking = new Label("Ranking",skin,"title");
        labelTime = new Label("Timing",skin,"title");
        listPlayers = new List(skin,"noHighlight");
        listRanking = new List(skin,"noHighlight");
        listTime = new List(skin,"noHighlight");
        buttonReturnToLobby = new TextButton("Back to Lobby",skin);
        buttonReturnToLobby.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Returning to lobby.");
                ScreenManager.getInstance().showScreen(ScreenEnum.ROOMSELECTION);
                return false;
            }
        });

        if (liveUsers!=null) {
            listPlayers.setItems(liveUsers);
        }
        if (rankings!=null) {
            listRanking.setItems(rankings);
        }
        if (timeings!=null) {
            listTime.setItems(timeings);
        }
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
        table.add(listRanking);
        table.add(listPlayers);
        table.add(listTime);
        table.row();
        table.add(buttonReturnToLobby).colspan(3);
        addActor(table);
    }
}
