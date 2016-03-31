package com.mygdx.GameWorld;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.PlatformHandler;
import com.mygdx.PowerUp;
import com.mygdx.Sprites.OtherPlayer;
import com.mygdx.Sprites.Player;

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

    //private static GameWorld instance = null;

    private OrthographicCamera cam;
    private float gameWidth;
    private float gameHeight;

    private GameState currentState;

    public enum GameState {
        READY, RUNNING, GAMEOVER
    }


    public GameWorld(OrthographicCamera cam, float gameWidth, float gameHeight) {
        System.out.println("World initiated");
        this.cam = cam;
        this.gameHeight = gameHeight;
        this.gameWidth = gameWidth;

        currentState = GameState.READY;
        world = new World(new Vector2(0, 65), true);

        powerUp = new PowerUp(gameWidth, gameHeight);

        player = new Player(cam, world, powerUp, gameWidth, gameHeight);
        otherPlayer = new OtherPlayer(cam, world, gameWidth, gameHeight);
        world.setContactFilter(player);
        world.setContactListener(player);
        scrollSpeed = -10f;
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
        } else currentState = GameState.READY;

        scrollSpeed += -0.0005;

        player.update(delta);
        platformHandler.update(delta);
        powerUp.update(delta);

        if (player.getLives()<=0) {
            currentState = GameState.GAMEOVER;
        }
    }

    public void restart() {
        currentState = GameState.READY;
        cam.position.y = gameHeight/2;
        player.reset();
        platformHandler.reset();
        scrollSpeed = -10f;
        currentState = GameState.RUNNING;

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

    public boolean isRunning() {
        return currentState == GameState.RUNNING;
    }

}
