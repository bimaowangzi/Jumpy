package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.GameWorld.GameRenderer;
import com.mygdx.GameWorld.GameWorld;
import com.mygdx.JumpyHelper.InputHandler;
import com.mygdx.JumpyHelper.PlayerResult;
import com.mygdx.Sprites.OtherPlayer;
import com.mygdx.appwarp.WarpController;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

import org.json.JSONObject;

/**
 * Created by acer on 16/2/2016.
 */
public class PlayScreen extends AbstractScreen {

    private GameWorld world;
    private GameRenderer renderer;
    private OrthographicCamera cam;
    private WarpClient warpClient;

    private Thread fetchDataThread, checkActiveUsers;

    private float runTime = 0;

    public PlayScreen (){
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        getWarpClient();

        cam = new OrthographicCamera();

        float gameWidth = 50;
        float gameHeight = screenHeight/(screenWidth/gameWidth);

        world = new GameWorld(cam, gameWidth, gameHeight); // initialize world
        renderer = new GameRenderer(cam, world, gameWidth, gameHeight); // initialize renderer

        Gdx.input.setInputProcessor(new InputHandler(world));

        fetchDataThread = new FetchDataThread(world);
        fetchDataThread.start();

        checkActiveUsers = new CheckActiveUsers(warpClient,WarpController.getRoomId(),world);
        checkActiveUsers.start();

//        if (world.sent){
//            labelState = new Label("Ended",skin);
//        } else if (world.isGameOver()){
//            labelState = new G
//        }
    }

    @Override
    public void buildStage() {

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        runTime += delta;
        world.update(delta); // GameWorld updates
        renderer.render(delta); // GameRenderer renders
        if (world.isEnded()) {
            fetchDataThread.interrupt();
            checkActiveUsers.interrupt();
            //Player player = world.getPlayer();
            // back up array of other players for sending
//            ArrayList<OtherPlayer> otherPlayers = new ArrayList<OtherPlayer>();
//            for (int i = 0;i<world.getOtherPlayers().size();i++){
//                OtherPlayer otherPlayer = world.getOtherPlayers().get(i);
//                otherPlayers.add(new OtherPlayer(new String(otherPlayer.getName()),otherPlayer.getAvatarID(),cam, world.getWorld(),width,height));
//                otherPlayers.get(i).setResult(new PlayerResult(otherPlayer.getResult().isDead(), otherPlayer.getResult().getTime(), otherPlayer.getResult().getHeight(), otherPlayer.getName()));
//                otherPlayer.setResult(new PlayerResult(false,-1,-1,otherPlayer.getName()));
//            }
//            PlayerResult playerResult = new PlayerResult(player.getLives()==0,-1,-1,player.getName());
            ScreenManager.getInstance().showScreen(ScreenEnum.WIN, world.getPlayer().getResult(), world.getOtherPlayers());

        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void getWarpClient(){
        try {
            warpClient = WarpClient.getInstance();
        } catch (Exception ex) {
            System.out.println("Fail to get warpClient");
        }
    }
}

class FetchDataThread extends Thread {
    private GameWorld world;
    public FetchDataThread(GameWorld world) {
        this.world = world;
    }
    public void run() {
        while (true) {
            if (isInterrupted()){
                System.out.println("interrupted1");
                break;
            }
            try {
                while (!WarpController.dataAvailable) {
                    if (isInterrupted()){
                        System.out.println("interrupted2");
                        break;
                    }
                };

                String message = WarpController.getData();
                String userName = message.substring(0, message.indexOf("#@"));

                JSONObject data = new JSONObject(message.substring(message.indexOf("#@") + 2));
                String type = data.getString("type");
                if (type.equals("update")) {
                    float x = (float) data.getDouble("worldX");
                    float y = (float) data.getDouble("worldY");
                    float vx = (float) data.getDouble("velocityX");
                    float vy = (float) data.getDouble("velocityY");
                    float width = (float) data.getDouble("width");
                    float height = (float) data.getDouble("height");
                    int powerUpState = data.getInt("powerUpState");
                    boolean inAir = data.getBoolean("inAir");
                    float worldHeight = (float) data.getDouble("worldHeight");
                    int lives = data.getInt("lives");

                    world.getOtherPlayer(userName).update(x, y, vx, vy, width, height, powerUpState, inAir, worldHeight, lives);
                    WarpController.dataAvailable = false;

                    if ((powerUpState==2 || powerUpState==7 || powerUpState==8 || powerUpState==9) & world.isRunning())
                        world.getPlayer().getHit(powerUpState);
                } else if (type.equals("GameEnd")) {
                    boolean dead = data.getBoolean("dead");
                    float time = (float) data.getDouble("time");
                    int height = data.getInt("height");
                    world.setResult(world.getOtherPlayer(userName), new PlayerResult(dead, time, height, userName));
//                    System.out.println("Received");
                }
            } catch (Exception e) {
                // exception
                // e.printStackTrace();
            }
        }
    }
}

class CheckActiveUsers extends Thread {
    WarpClient warpClient;
    GameWorld world;
    String roomId;

    public CheckActiveUsers(WarpClient warpClient, String roomId, GameWorld world) {
        this.warpClient = warpClient;
        this.world = world;
        this.roomId = roomId;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            warpClient.getLiveRoomInfo(roomId);
            while (!WarpController.isWaitflag()) {
                // busy wait
            }
            WarpController.setWaitflag(false);

            if (isInterrupted()) {
                break;
            }

            String[] liveUsers = WarpController.getLiveUsers();
            for (OtherPlayer otherPlayer : world.getOtherPlayers()) {
                try{
                    boolean contains = false;
                    for (String user:liveUsers) {
                        if (otherPlayer.getName().equals(user)) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) world.getOtherPlayers().remove(otherPlayer);
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("checkactive user ended");
    }
}

