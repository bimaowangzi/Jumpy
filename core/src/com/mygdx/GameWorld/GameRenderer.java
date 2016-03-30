package com.mygdx.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.JumpyHelper.AssetLoader;
import com.mygdx.Platform;
import com.mygdx.PlatformHandler;
import com.mygdx.PowerUp;
import com.mygdx.Sprites.Player;

import java.util.ArrayList;

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
    private PlatformHandler platformHandler;
    private ArrayList<Platform> platforms;
    private PowerUp powerUp;

    // Game Assets
    private TextureRegion bg;
    private ArrayList<TextureRegion> platformTextures;
    private ArrayList<TextureRegion> powerupTextures;
    private ArrayList<TextureRegion> playerTextures;
    private TextureRegion life;
    private TextureRegion groundTexture;

    private float gameWidth;
    private float gameHeight;

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
        platformHandler = myWorld.getPlatformHandler();
        platforms = platformHandler.getPlatforms();
        powerUp = myWorld.getPowerUp();
    }

    private void initAssets() {
        bg = AssetLoader.bg;
        playerTextures = AssetLoader.playerTextures;
        platformTextures = AssetLoader.platformTextures;
        powerupTextures = AssetLoader.powerupTextures;
        life = AssetLoader.life;
        groundTexture = AssetLoader.groundTexture;

    }

    private void drawPlatforms() {
        for (Platform p:platforms) {
            batcher.draw(platformTextures.get(p.getType()), p.getX(), p.getY(), p.getWidth(), p.getHeight());
        }

    }

    public void render(float delta) {
        // Move cam only if player is alive
        if (myWorld.isRunning())
            cam.position.y+=myWorld.getScrollSpeed()*delta;

        // Fill the entire screen with black, to prevent potential flickering.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        batcher.draw(bg, 0, 0, gameWidth, gameHeight);;

        // The platforms and player needs transparency, so we enable that again.
        batcher.enableBlending();
        // Draw platforms
        drawPlatforms();
        // Draw power up
        if (powerUp.isActive())
            batcher.draw(powerupTextures.get(powerUp.getType()), powerUp.getX(), powerUp.getY(),
                    powerUp.getRadius()*2, powerUp.getRadius()*2);
        // Draw player
        batcher.draw(playerTextures.get(player.getPowerUpState()), player.getX(), player.getY(),
                player.getWidth(), player.getHeight());
        // Draw ground
        Platform ground = platformHandler.getGround();
        if (!ground.isScrolledDown())
            batcher.draw(groundTexture, ground.getX(), ground.getY(), ground.getWidth(), ground.getHeight());
        // Show lives
        for (int i=0; i<player.getLives(); i++) {
            batcher.draw(life, 2+i*5, 2, 4, 3);
        }

        // Show height (score)
        AssetLoader.shadow.draw(batcher, "Height: " + player.getScore() + " m", 20, 2);
        AssetLoader.font.draw(batcher, "Height: " + player.getScore() + " m", 20, 2);

        // Handling Game Over
        if (myWorld.isGameOver()) {
            AssetLoader.shadow.draw(batcher, "Game Over", 18, gameHeight/3);
            AssetLoader.font.draw(batcher, "Game Over", 18, gameHeight/3);
        }

        // End SpriteBatch
        batcher.end();

/*        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(player.getX(), player.getY() + gameHeight / 2, 2.5f);
        shapeRenderer.end();*/
    }
}
