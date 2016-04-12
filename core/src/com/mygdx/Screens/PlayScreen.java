package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.GameWorld.GameRenderer;
import com.mygdx.GameWorld.GameWorld;
import com.mygdx.JumpyHelper.InputHandler;
import com.mygdx.JumpyHelper.PlayerResult;
import com.mygdx.appwarp.WarpController;

import org.json.JSONObject;

/**
 * Created by acer on 16/2/2016.
 */
public class PlayScreen extends AbstractScreen {

    private GameWorld world;
    private GameRenderer renderer;
    private OrthographicCamera cam;

    private Thread fetchDataThread;

    private float runTime = 0;

//    // testing on screen
//    private Label labelState;
//
//    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public PlayScreen (){
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        cam = new OrthographicCamera();

        float gameWidth = 50;
        float gameHeight = screenHeight/(screenWidth/gameWidth);

        world = new GameWorld(cam, gameWidth, gameHeight); // initialize world
        renderer = new GameRenderer(cam, world, gameWidth, gameHeight); // initialize renderer

        Gdx.input.setInputProcessor(new InputHandler(world));

        fetchDataThread = new FetchDataThread(world);
        fetchDataThread.start();

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
                    int score = data.getInt("score");
                    float worldHeight = (float) data.getDouble("worldHeight");
                    int lives = data.getInt("lives");

                    world.getOtherPlayer(userName).update(x, y, vx, vy, width, height, powerUpState, score, worldHeight, lives);
                    WarpController.dataAvailable = false;
                    boolean lightningStruck = data.getBoolean("lightning");
                    if (lightningStruck & world.isRunning())
                        world.getPlayer().lightningStrike();
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

