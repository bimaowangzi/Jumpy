package com.mygdx.GameWorld;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.JumpyHelper.PlayerResult;
import com.mygdx.Platform;
import com.mygdx.PlatformHandler;
import com.mygdx.PowerUp;
import com.mygdx.Sprites.OtherPlayer;
import com.mygdx.Sprites.Player;
import com.mygdx.appwarp.WarpController;

import org.json.JSONObject;

/**
 * Created by Admin on 3/21/2016.
 */
public class GameWorld {
    private Player player;
    private OtherPlayer otherPlayer;
    private PlatformHandler platformHandler;
    private World world;
    private PowerUp powerUp;
    private float scrollSpeed;
    private float timer;
    private float powerUpTimer;

    //private static GameWorld instance = null;

    private OrthographicCamera cam;
    private float gameWidth;
    private float gameHeight;

    private GameState currentState;

    public enum GameState {
        READY, RUNNING, GAMEOVER, ENDED
    }


    public GameWorld(OrthographicCamera cam, float gameWidth, float gameHeight) {
        System.out.println("World initiated");
        this.cam = cam;
        this.gameHeight = gameHeight;
        this.gameWidth = gameWidth;
        powerUpTimer = 0;

        currentState = GameState.READY;
        world = new World(new Vector2(0, 70), true);

        powerUp = new PowerUp(gameWidth, gameHeight);

        player = new Player(cam, world, powerUp, gameWidth, gameHeight);
        otherPlayer = new OtherPlayer(cam, world, gameWidth, gameHeight);
        world.setContactFilter(player);
        world.setContactListener(player);
        scrollSpeed = -10f;
        timer = 0;
        platformHandler = new PlatformHandler(cam, world, gameWidth, gameHeight);

        player.setPlatformHandler(platformHandler);
    }

//    public static GameWorld getInstance(OrthographicCamera cam, float gameWidth, float gameHeight) {
//        if(instance == null) {
//            instance = new GameWorld(cam, gameWidth, gameHeight);
//        }
//        return instance;
//    }
//
//    public static GameWorld getInstance() {
//        return instance;
//    }

    public void update(float delta) {
        switch (currentState) {
            case READY:
                updateReady(delta);
                break;

            case RUNNING:
                updateRunning(delta);
                break;

            case GAMEOVER:
                player.setResult(new PlayerResult(player.getLives() == 0, timer, player.getScore(), WarpController.getLocalUser()));

                try {
                    JSONObject data = new JSONObject();
                    data.put("type", "GameEnd");
                    data.put("dead", player.getLives()==0);
                    data.put("time", timer);
                    data.put("height", player.getScore());
                    WarpController.getInstance().sendGameUpdate(data.toString());
//                    System.out.println("sent");
                } catch (Exception e) {
//                    System.out.println("exception caught");
                    // exception in sendLocation
                }

                updateEnd();


            default:

        }
    }

    public void updateReady(float delta) {
        if (player.isAlive()) {
            currentState = GameState.RUNNING;
        }
    }


    public void updateRunning(float delta) {
        if (player.isAlive()) { // if player not alive, stop the world
            world.step(delta, 1, 1);
            timer += delta;
        } else currentState = GameState.READY;

        scrollSpeed += -0.001;
        if (player.getPowerUpState()==6) {
            if (powerUpTimer==0) {
                scrollSpeed += -5;
            }
            powerUpTimer += delta;
        } else if (powerUpTimer>0) {
            powerUpTimer = 0;
            scrollSpeed -= -5;
        }

        player.update(delta);
        if (otherPlayer.getResult()==null)
            otherPlayer.update();
        platformHandler.update(delta);
        powerUp.update(delta);

        if (player.getLives()<=0) {
            currentState = GameState.GAMEOVER;
        }
        Platform finishingLine = platformHandler.getFinishlineLine();

        if (player.getWorldHeight() < finishingLine.getWorldHeight())
            currentState = GameState.GAMEOVER;
    }

    public void updateEnd() {
        if (otherPlayer.getResult()!=null && player.getResult()!=null) {
            for (int i = 0; i < 10; i++) {
                try {
                    JSONObject data = new JSONObject();
                    data.put("type", "GameEnd");
                    data.put("dead", player.getLives() == 0);
                    data.put("time", timer);
                    data.put("height", player.getScore());
                    WarpController.getInstance().sendGameUpdate(data.toString());
                    //                    System.out.println("sent");
                } catch (Exception e) {
                    //                    System.out.println("exception caught");
                    // exception in sendLocation
                }
                currentState = GameState.ENDED;
            }
        }
    }



//    public void restart() {
//        currentState = GameState.READY;
//        cam.position.y = gameHeight/2;
//        player.reset();
//        platformHandler.reset();
//        scrollSpeed = -10f;
//        currentState = GameState.RUNNING;
//
//    }

    public void setResult(PlayerResult result) {
        otherPlayer.setResult(result);
    }

    public float getScrollSpeed() {
        return scrollSpeed;
    }

    public Player getPlayer() {
        return player;
    }

    public OtherPlayer getOtherPlayer() {
        return otherPlayer;
    }

    public PlatformHandler getPlatformHandler() {
        return platformHandler;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public boolean isGameOver() {
        return currentState == GameState.GAMEOVER;
    }

    public boolean isEnded() {
        return currentState == GameState.ENDED;
    }

    public boolean isRunning() {
        return currentState == GameState.RUNNING;
    }

}
