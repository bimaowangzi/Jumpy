package com.mygdx.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.JumpyHelper.AssetLoader;
import com.mygdx.JumpyHelper.Avatar;
import com.mygdx.Platform;
import com.mygdx.PlatformHandler;
import com.mygdx.PowerUp;
import com.mygdx.Sprites.OtherPlayer;
import com.mygdx.Sprites.Player;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Admin on 3/21/2016.
 */
public class GameRenderer {

    private GameWorld myWorld;
    private OrthographicCamera cam;
    private ShapeRenderer shapeRenderer;

    private SpriteBatch batcher;

    // Game Objects
    private Player player;
    private CopyOnWriteArrayList<OtherPlayer> otherPlayers;
    private PlatformHandler platformHandler;
    private ArrayList<Platform> platforms;
    private Platform finishingline;
    private PowerUp powerUp;

    // Game Assets
    private TextureRegion bg;
    private ArrayList<TextureRegion> platformTextures;
    private ArrayList<TextureRegion> powerupTextures;
    private ArrayList<Avatar> avatars;
    private TextureRegion life;
    private TextureRegion groundTexture;

    private float gameWidth;
    private float gameHeight;


    private TextureRegion currentFrame;
    private float stateTime;
    private int lightingCounter = 0;

    public GameRenderer (OrthographicCamera cam, GameWorld world, float gameWidth, float gameHeight) {
        myWorld = world;

        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        this.cam = cam;
        cam.setToOrtho(true, gameWidth, gameHeight);

        batcher = new SpriteBatch();
        //attach batcher to camera
        batcher.setProjectionMatrix(cam.combined);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        // Call helper methods to initialize instance variables
        initGameObjects();
        initAssets();
    }

    private void initGameObjects() {
        player = myWorld.getPlayer();
        otherPlayers = myWorld.getOtherPlayers();
        platformHandler = myWorld.getPlatformHandler();
        platforms = platformHandler.getPlatforms();
        finishingline = platformHandler.getFinishlineLine();
        powerUp = myWorld.getPowerUp();
    }

    private void initAssets() {
        bg = AssetLoader.bg;
        avatars = AssetLoader.avatars;
        platformTextures = AssetLoader.platformTextures;
        powerupTextures = AssetLoader.powerupTextures;
        life = AssetLoader.life;
        groundTexture = AssetLoader.groundTexture;
    }

    private void drawPlatforms() {
        for (Platform p:platforms) {
            batcher.draw(platformTextures.get(p.getType()), p.getX(), p.getY(), p.getWidth(), p.getHeight());
        }
        batcher.draw(AssetLoader.finishingLineTexure, finishingline.getX(), finishingline.getY(), finishingline.getWidth(), finishingline.getHeight());
    }

    private void drawOtherPlayer(OtherPlayer player) {
        Avatar avatar = avatars.get(player.getAvatarID());
        if (player.inAir()) {
            if (player.getPowerUpState() == 1) {
                batcher.draw(avatar.playerTextures.get(1), player.getX(), player.getY(),
                        player.getWidth(), player.getHeight());
            } else if (player.getPowerUpState() ==-2) {
                batcher.draw(avatar.playerTextures.get(2), player.getX(), player.getY(),
                        player.getWidth(), player.getHeight());
            } else if (player.getPowerUpState() == 3) {
                batcher.draw(avatar.playerTextures.get(3), player.getX(), player.getY(),
                        player.getWidth(), player.getHeight());
            } else
                batcher.draw(avatar.playerTextures.get(0), player.getX(), player.getY(),
                        player.getWidth(), player.getHeight());
        } else {        //ANIMATION
            if (player.MovingRight()) {
                if (player.getPowerUpState() == 1) {
                    currentFrame = avatar.playerMoveR.get(1).getKeyFrame(stateTime, true);
                    batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
                } else if (player.getPowerUpState() ==-2) {
                    currentFrame = avatar.playerMoveR.get(2).getKeyFrame(stateTime, true);
                    batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
                } else
                    currentFrame = avatar.playerMoveR.get(0).getKeyFrame(stateTime, true);
                batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());


            } else {
                if (player.getPowerUpState() == 1) {
                    currentFrame = avatar.playerMoveL.get(1).getKeyFrame(stateTime, true);
                    batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
                } else if (player.getPowerUpState() ==-2) {
                    currentFrame = avatar.playerMoveL.get(2).getKeyFrame(stateTime, true);
                    batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
                } else
                    currentFrame = avatar.playerMoveL.get(0).getKeyFrame(stateTime, true);
                batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
            }
        }
        float height = player.getWorldHeight() / platformHandler.getDistance() * 25 + 2f;
        batcher.draw(avatar.playerTextures.get(0), 5, 33 - height, 3, 3);
    }

    private void drawPlayer() {
        Avatar avatar = avatars.get(player.getAvatarID());
        if (player.inAir()) {
            if (player.getPowerUpState() == 1) {
                batcher.draw(avatar.playerTextures.get(1), player.getX(), player.getY(),
                        player.getWidth(), player.getHeight());
            } else if (player.getPowerUpState() == -2) {
                batcher.draw(avatar.playerTextures.get(2), player.getX(), player.getY(),
                        player.getWidth(), player.getHeight());
            } else if (player.getPowerUpState() == 3) {
                batcher.draw(avatar.playerTextures.get(3), player.getX(), player.getY(),
                        player.getWidth(), player.getHeight());
            } else
                batcher.draw(avatar.playerTextures.get(0), player.getX(), player.getY(),
                        player.getWidth(), player.getHeight());
        } else {        //ANIMATION
            if (player.MovingRight()) {
                if (player.getPowerUpState() == 1) {
                    currentFrame = avatar.playerMoveR.get(1).getKeyFrame(stateTime, true);
                    batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
                } else if (player.getPowerUpState() ==-2) {
                    currentFrame = avatar.playerMoveR.get(2).getKeyFrame(stateTime, true);
                    batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
                } else
                    currentFrame = avatar.playerMoveR.get(0).getKeyFrame(stateTime, true);
                batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
            } else {
                if (player.getPowerUpState() == 1) {
                    currentFrame = avatar.playerMoveL.get(1).getKeyFrame(stateTime, true);
                    batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
                } else if (player.getPowerUpState() ==-2) {
                    currentFrame = avatar.playerMoveL.get(2).getKeyFrame(stateTime, true);
                    batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
                } else
                    currentFrame = avatar.playerMoveL.get(0).getKeyFrame(stateTime, true);
                batcher.draw(currentFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
            }
        }

        float height = player.getWorldHeight()/platformHandler.getDistance() * 25 + 2f;
        batcher.draw(avatar.playerTextures.get(0), 5, 33-height, 3, 3);
    }

    public void render(float delta) {
        // Move cam only if player is alive
        if (myWorld.isRunning()) {
            cam.position.y+=myWorld.getScrollSpeed()*delta;
        }


        // Fill the entire screen with black, to prevent potential flickering.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (player.getPowerUpState() == -8) {
            if (lightingCounter%20 < 10)
                Gdx.gl.glClearColor(1, 1, 1, 1);
            else
                Gdx.gl.glClearColor(1, 1, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            lightingCounter++;
            return;
        } else
            lightingCounter = 0;

        // Begin ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw Background color
        shapeRenderer.setColor(55 / 255.0f, 80 / 255.0f, 100 / 255.0f, 1);
        shapeRenderer.rect(0, 0, gameWidth, gameHeight);

        // End ShapeRenderer
        shapeRenderer.end();

        // Begin SpriteBatch
        batcher.begin();

        // Disable transparency
        batcher.disableBlending();

        // Draw background
        batcher.draw(bg, 0, 0, gameWidth, gameHeight);

        // The platforms and player needs transparency, so we enable that again.
        batcher.enableBlending();

        // Draw platforms
        drawPlatforms();

        // Draw power up
        if (powerUp.isActive())
            batcher.draw(powerupTextures.get(powerUp.getType()-1), powerUp.getX(), powerUp.getY(),
                    powerUp.getRadius()*2, powerUp.getRadius()*2);

        stateTime += delta;

        // Draw players
        for (OtherPlayer other:otherPlayers)
            drawOtherPlayer(other);
        drawPlayer();

        batcher.draw(AssetLoader.indicator, player.getX() + 1, player.getY() - 3.5f, 3, 3);

        // Draw ground
        Platform ground = platformHandler.getGround();
        if (!ground.isScrolledDown())
            batcher.draw(groundTexture, ground.getX(), ground.getY(), ground.getWidth(), ground.getHeight());

        // Show lives
        for (int i=0; i<player.getLives(); i++) {
            batcher.draw(life, 2+i*5, 2, 4, 3);
        }

        // Show height bar
        batcher.draw(AssetLoader.heightBarTexture, 2, 8, 2, 25);

        // Show height (score)
        AssetLoader.shadow.draw(batcher, "Height: " + player.getScore() + " m", 20, 2);
        AssetLoader.font.draw(batcher, "Height: " + player.getScore() + " m", 20, 2);

        // Show power up state
        if (player.getPowerUpState()!=0 && player.getPowerUpState()!=2 && player.getPowerUpState()!=7
                && player.getPowerUpState()!=8 && player.getPowerUpState()!=9) {
            batcher.draw(powerupTextures.get(Math.abs(player.getPowerUpState())-1), 40, gameHeight*0.1f, 7, 7);

        }

        // Handling Game Over
        if (myWorld.isGameOver()) {
            AssetLoader.shadow.draw(batcher, "Game Over", 18, gameHeight/3);
            AssetLoader.font.draw(batcher, "Game Over", 18, gameHeight/3);
        }


        // Count downt
        if (stateTime<4) {
            AssetLoader.font.getData().setScale(.10f, -.10f);
            if (stateTime < 1)
                print3();
            else if (stateTime < 2)
                print2();
            else if (stateTime < 3)
                print1();
            else printStart();
            AssetLoader.font.getData().setScale(.05f, -.05f);
        }

        // End SpriteBatch
        batcher.end();
    }

    public void print3() {
        AssetLoader.shadow.draw(batcher, "3", 5, 10);
        AssetLoader.font.draw(batcher, "3", 5, 10);
    }

    public void print2() {
        AssetLoader.shadow.draw(batcher, "2", 10, 10);
        AssetLoader.font.draw(batcher, "2", 10, 10);
    }

    public void print1() {
        AssetLoader.shadow.draw(batcher, "1", 20, 10);
        AssetLoader.font.draw(batcher, "1", 20, 10);
    }

    public void printStart() {
        AssetLoader.shadow.draw(batcher, "Start!", 30, 10);
        AssetLoader.font.draw(batcher, "Start!", 30, 10);
    }
}
