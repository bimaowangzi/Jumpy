package com.mygdx.GameWorld;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.JumpyHelper.AssetLoader;
import com.mygdx.JumpyHelper.PlayerResult;
import com.mygdx.JumpyHelper.SoundLoader;
import com.mygdx.PlatformHandler;
import com.mygdx.PowerUp;
import com.mygdx.Sprites.OtherPlayer;
import com.mygdx.Sprites.Player;
import com.mygdx.appwarp.WarpController;

import org.json.JSONObject;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Admin on 3/21/2016.
 */
public class GameWorld {
    private Player player;
    private volatile CopyOnWriteArrayList<OtherPlayer> otherPlayers;
    private PlatformHandler platformHandler;
    private World world;
    private PowerUp powerUp;
    private float scrollSpeed;
    private float timer;
    private float speedUpPowerUpTimer, reversePowerupTimer;

    //private static GameWorld instance = null;

    private OrthographicCamera cam;
    private float gameWidth;
    private float gameHeight;

    private GameState currentState;

    public enum GameState {
        READY, RUNNING, GAMEOVER, ENDED
    }


    public GameWorld(OrthographicCamera cam, float gameWidth, float gameHeight) {
        this.cam = cam;
        this.gameHeight = gameHeight;
        this.gameWidth = gameWidth;
        speedUpPowerUpTimer = 0;
        reversePowerupTimer = 0;

        currentState = GameState.READY;
        world = new World(new Vector2(0, 70), true);

        powerUp = new PowerUp(gameWidth, gameHeight);
        int avatarID = Integer.parseInt(WarpController.getAvatarMap().get(WarpController.getLocalUser()).substring(6)) - 1;
        player = new Player(WarpController.getLocalUser(), avatarID, cam, world, powerUp, gameWidth, gameHeight);
        otherPlayers = new CopyOnWriteArrayList<OtherPlayer>();
        otherPlayers.clear();

        String[] players = WarpController.getLiveUsers();
        for (String name:players) {
            avatarID = Integer.parseInt(WarpController.getAvatarMap().get(name).substring(6)) - 1;
            if (!name.equals(player.getName()))
                otherPlayers.add(new OtherPlayer(name, avatarID, cam, world, gameWidth, gameHeight));
        }


        world.setContactFilter(player);
        world.setContactListener(player);
        scrollSpeed = -10f;
        timer = 0;
        platformHandler = new PlatformHandler(cam, world, gameWidth, gameHeight);

        player.setPlatformHandler(platformHandler);
    }

    public void update(float delta) {
        switch (currentState) {
            case READY:
                updateReady(delta);
                break;

            case RUNNING:
                updateRunning(delta);
                break;

            case GAMEOVER:
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
        timer+=delta;
        try {
            JSONObject data = new JSONObject();
            data.put("type", "GameEnd");
            data.put("dead", false);
            data.put("time", -1);
            data.put("height", -1);
            WarpController.getInstance().sendGameUpdate(data.toString());
            //                    System.out.println("sent");
        } catch (Exception e) {
            //                    System.out.println("exception caught");
            // exception in sendLocation
        }
        if (timer>=3f) {
            SoundLoader.startSound.play();
            currentState = GameState.RUNNING;
            player.setCanJump(true);
            timer=0;
        }
    }


    public void updateRunning(float delta) {
//        if (player.isAlive()) { // if player not alive, stop the world

//        } else currentState = GameState.READY;
        for (OtherPlayer other:otherPlayers) {
            if (other.getResult().getTime()<0)
                System.out.println("Other player result not NULL");
        }
        world.step(delta, 1, 1);
        timer += delta;

        scrollSpeed += -0.001;
        if (player.getPowerUpState()==-7) {
            if (speedUpPowerUpTimer==0) {
                scrollSpeed += -5;
            }
            speedUpPowerUpTimer += delta;
        } else if (speedUpPowerUpTimer>0) {
            speedUpPowerUpTimer = 0;
            scrollSpeed -= -5;
        }

        if (player.getPowerUpState()==-9) {
            if (reversePowerupTimer==0) {
                world.setGravity(new Vector2(0, -70));
                AssetLoader.reverseWorld(AssetLoader.avatars.get(player.getAvatarID()));
            }
            reversePowerupTimer += delta;
        } else if (reversePowerupTimer>0) {
            reversePowerupTimer = 0;
            world.setGravity(new Vector2(0, 70));
            AssetLoader.reverseWorld(AssetLoader.avatars.get(player.getAvatarID()));
        }

        player.update(delta);
        for (OtherPlayer other:otherPlayers)
            other.update();

        platformHandler.update(delta);
        powerUp.update(delta);

        setRespawnTimeFactor();

        if (player.getLives()<=0 || player.getWorldHeight() < platformHandler.getFinishlineLine().getWorldHeight()) {
            currentState = GameState.GAMEOVER;
            player.setResult(new PlayerResult(player.getLives() == 0, timer, player.getScore(), WarpController.getLocalUser()));
        }

    }

    public void updateEnd() {
        for (OtherPlayer other:otherPlayers)
            other.update();
        for (OtherPlayer other:otherPlayers) {
            if (other.getResult().getTime()<0)
                return;
        }

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

    private void setRespawnTimeFactor() {
        int rank = 1;
        for (OtherPlayer other:otherPlayers) {
            if (other.getWorldHeight()<player.getWorldHeight())
                rank++;
        }
        switch (rank) {
            case 1: powerUp.setRespawnTimeFactor(1.5f);
            case 2: powerUp.setRespawnTimeFactor(1);
            case 3: powerUp.setRespawnTimeFactor(0.85f);
            case 4: powerUp.setRespawnTimeFactor(0.7f);
            default: powerUp.setRespawnTimeFactor(1);
        }
    }

    public void setResult(OtherPlayer other, PlayerResult result) {
        other.setResult(result);
    }

    public float getScrollSpeed() {
        return scrollSpeed;
    }

    public Player getPlayer() {
        return player;
    }

    public OtherPlayer getOtherPlayer(String name) {
        for (OtherPlayer other:otherPlayers)
            if (other.getName().equals(name))
                return other;
        return null;
    }

    public CopyOnWriteArrayList<OtherPlayer> getOtherPlayers() {
        return otherPlayers;
    }

    public PlatformHandler getPlatformHandler() {
        return platformHandler;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public World getWorld() {
        return world;
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
