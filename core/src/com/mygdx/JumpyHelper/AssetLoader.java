package com.mygdx.JumpyHelper;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

/**
 * Created by Admin on 3/21/2016.
 */
public class AssetLoader {

    public static Texture texture;
    public static TextureRegion bg;
    public static ArrayList<TextureRegion> platformTextures = new ArrayList<TextureRegion>();
    public static ArrayList<TextureRegion> powerupTextures = new ArrayList<TextureRegion>();
    public static ArrayList<TextureRegion> playerTextures = new ArrayList<TextureRegion>();
    public static TextureRegion groundTexture;
    public static TextureRegion life;
    public static BitmapFont font, shadow;


    public static void load() {

        texture = new Texture(Gdx.files.internal("BG.png"));
        bg = new TextureRegion(texture, 0, 0, 600, 1000);
        bg.flip(false, true);

        texture = new Texture(Gdx.files.internal("tileset.png"));
        platformTextures.add(new TextureRegion(texture, 91, 144, 52, 17)); //normal
        platformTextures.add(new TextureRegion(texture, 91, 324, 52, 17)); //slippery
        platformTextures.add(new TextureRegion(texture, 91, 252, 52, 17)); //bouncy
        platformTextures.add(new TextureRegion(texture, 144, 361, 53, 16)); //moving

        for (TextureRegion t:platformTextures)
            t.flip(false, true);

        texture = new Texture(Gdx.files.internal("powerups.png"));
        powerupTextures.add(new TextureRegion(texture, 417, 19, 16, 16)); // high jump
        powerupTextures.add(new TextureRegion(texture, 381, 104, 16, 16)); // low jump
        powerupTextures.add(new TextureRegion(texture, 42, 18, 16, 16)); // flyer
        powerupTextures.add(new TextureRegion(texture, 362, 51, 18, 19)); // life

        for (TextureRegion t:powerupTextures)
            t.flip(false, true);

        playerTextures.add(new TextureRegion(texture, 6, 90, 16, 16)); // normal
        playerTextures.add(new TextureRegion(texture, 6, 107, 16, 16)); // high jump
        playerTextures.add(new TextureRegion(texture, 24, 107, 16, 16)); // low jump
        playerTextures.add(new TextureRegion(texture, 42, 39, 16, 20)); // flyer
        playerTextures.add(new TextureRegion(texture, 6, 90, 16, 16)); // normal

        for (TextureRegion t:playerTextures)
            t.flip(false, true);

        life = new TextureRegion(texture, 458, 79, 16, 11);
        life.flip(false, true);

        font = new BitmapFont(Gdx.files.internal("text.fnt"));
        font.getData().setScale(.05f, -.05f);
        shadow = new BitmapFont(Gdx.files.internal("shadow.fnt"));
        shadow.getData().setScale(.05f, -.05f);

        texture = new Texture(Gdx.files.internal("ground.png"));
        groundTexture = new TextureRegion(texture, 0, 10, 300, 130);
        groundTexture.flip(false, true);

    }

    public static void dispose() {
        // We must dispose of the texture when we are finished.
        texture.dispose();
        font.dispose();
        shadow.dispose();
    }

}