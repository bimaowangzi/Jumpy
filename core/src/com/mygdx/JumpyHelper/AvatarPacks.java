package com.mygdx.JumpyHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by Admin on 4/13/2016.
 */
public class AvatarPacks {
    public static Texture texture;
    public static ArrayList<ArrayList<TextureRegion>> playerTexturesList = new ArrayList<ArrayList<TextureRegion>>();
    public static TextureRegion run;
    public static ArrayList<ArrayList<Animation>> playerMoveRs = new ArrayList<ArrayList<Animation>>();
    public static ArrayList<ArrayList<Animation>> playerMoveLs = new ArrayList<ArrayList<Animation>>();

    public static void load() {

        // AVATAR 1
        texture = new Texture(Gdx.files.internal("1aa.png"));
        ArrayList<TextureRegion> playerTextures = new ArrayList<TextureRegion>();
        playerTexturesList.add(playerTextures);
        playerTextures.add(new TextureRegion(texture, 56, 0, 31, 49)); // normal
        playerTextures.add(new TextureRegion(texture, 196, 0, 31, 49)); // high jump
        playerTextures.add(new TextureRegion(texture, 340, 0, 31, 49)); // low jump
        playerTextures.add(new TextureRegion(texture, 52, 171, 45, 47)); // wings

        for (TextureRegion t:playerTextures)
            t.flip(false, true);

        ArrayList<Animation> playerMoveR = new ArrayList<Animation>();
        playerMoveRs.add(playerMoveR);
        ArrayList<Animation> playerMoveL = new ArrayList<Animation>();
        playerMoveLs.add(playerMoveL);

        // normal
        Array<TextureRegion> frames = new Array<TextureRegion>();
        run = new TextureRegion(texture,9,112,31,49);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,105,111,31,49);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f, frames));
        frames.clear();

        run = new TextureRegion(texture,9,54,31,48);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,105,54,29,49);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f, frames));
        frames.clear();

        // high jump
        run = new TextureRegion(texture,149,114,29,46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,197,114,29,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f, frames));
        frames.clear();

        run = new TextureRegion(texture,149,56,29,46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,197,56,29,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f, frames));
        frames.clear();

        // low jump
        run = new TextureRegion(texture,294,114,29,46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,342,114,29,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f, frames));
        frames.clear();

        run = new TextureRegion(texture,294,56,29,46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,342,56,29,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();


        // AVATAR 2
        texture = new Texture(Gdx.files.internal("2aa.png"));
        playerTextures = new ArrayList<TextureRegion>();
        playerTexturesList.add(playerTextures);
        playerTextures.add(new TextureRegion(texture, 53, 2, 47, 48)); // normal
        playerTextures.add(new TextureRegion(texture, 209, 2, 47, 48)); // high jump
        playerTextures.add(new TextureRegion(texture, 363, 3, 47, 48)); // low jump
        playerTextures.add(new TextureRegion(texture, 59, 166, 47, 48)); // wings


        for (TextureRegion t:playerTextures)
            t.flip(false, true);

        playerMoveR = new ArrayList<Animation>();
        playerMoveRs.add(playerMoveR);
        playerMoveL = new ArrayList<Animation>();
        playerMoveLs.add(playerMoveL);

        // normal
        run = new TextureRegion(texture, 7, 105, 47, 48);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,55,105,47,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 4, 54, 47, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,52,53,47,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

        // high jump
        run = new TextureRegion(texture, 163, 107, 47, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,121,106,47,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 160, 55, 47, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,208,54,47,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

        // low jump
        run = new TextureRegion(texture, 315, 107, 47, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,363,106,47,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 314, 55, 46, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,361,54,47,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

        // AVATAR 3
        texture = new Texture(Gdx.files.internal("3aa.png"));
        playerTextures = new ArrayList<TextureRegion>();
        playerTexturesList.add(playerTextures);
        playerTextures.add(new TextureRegion(texture, 49, 5, 37, 48)); // normal
        playerTextures.add(new TextureRegion(texture, 199, 5, 37, 48)); // high jump
        playerTextures.add(new TextureRegion(texture, 344, 3, 37, 48)); // low jump
        playerTextures.add(new TextureRegion(texture, 48, 166, 45, 48)); // wings


        for (TextureRegion t:playerTextures)
            t.flip(false, true);

        playerMoveR = new ArrayList<Animation>();
        playerMoveRs.add(playerMoveR);
        playerMoveL = new ArrayList<Animation>();
        playerMoveLs.add(playerMoveL);

        // normal
        run = new TextureRegion(texture, 2, 111, 34, 46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,50,109,35,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 5, 58, 34, 46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,53,56,34,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

        // high jump
        run = new TextureRegion(texture, 150, 111, 34, 46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,198,109,35,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 155, 57, 34, 46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,203,55,34,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

        // low jump
        run = new TextureRegion(texture, 299, 111, 34, 46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,347,109,35,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 301, 56, 34, 46);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,349,54,34,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();


        // AVATAR 4
        texture = new Texture(Gdx.files.internal("4aa.png"));
        playerTextures = new ArrayList<TextureRegion>();
        playerTexturesList.add(playerTextures);
        playerTextures.add(new TextureRegion(texture, 54, 2, 35, 46)); // normal
        playerTextures.add(new TextureRegion(texture, 194, 2, 35, 46)); // high jump
        playerTextures.add(new TextureRegion(texture, 333, 2, 35, 46)); // low jump
        playerTextures.add(new TextureRegion(texture, 48, 152, 45, 46)); // wings


        for (TextureRegion t:playerTextures)
            t.flip(false, true);

        playerMoveR = new ArrayList<Animation>();
        playerMoveRs.add(playerMoveR);
        playerMoveL = new ArrayList<Animation>();
        playerMoveLs.add(playerMoveL);

        // normal
        run = new TextureRegion(texture, 6, 99, 33, 45);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,55,98,32,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 8, 51, 33, 45);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,56,50,32,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

        // high jump
        run = new TextureRegion(texture, 146, 99, 34, 45);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,195,98,32,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 148, 51, 33, 45);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,196,50,32,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

        // low jump
        run = new TextureRegion(texture, 285, 99, 33, 45);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,334,98,32,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 287, 51, 33, 45);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,335,50,32,46);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();


        // AVATAR 5
        texture = new Texture(Gdx.files.internal("5aa.png"));
        playerTextures = new ArrayList<TextureRegion>();
        playerTexturesList.add(playerTextures);
        playerTextures.add(new TextureRegion(texture, 58, 11, 39, 48)); // normal
        playerTextures.add(new TextureRegion(texture, 200, 12, 39, 48)); // high jump
        playerTextures.add(new TextureRegion(texture, 339, 13, 39, 48)); // low jump
        playerTextures.add(new TextureRegion(texture, 53, 175, 45, 47)); // wings


        for (TextureRegion t:playerTextures)
            t.flip(false, true);

        playerMoveR = new ArrayList<Animation>();
        playerMoveRs.add(playerMoveR);
        playerMoveL = new ArrayList<Animation>();
        playerMoveLs.add(playerMoveL);

        // normal
        run = new TextureRegion(texture, 8, 121, 36, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,56,118,36,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 14, 66, 35, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,62,65,35,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

        // high jump
        run = new TextureRegion(texture, 152, 121, 36, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,200,120,36,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 157, 67, 35, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,205,66,35,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

        // low jump
        run = new TextureRegion(texture, 291, 121, 36, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,339,120,36,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveR.add(new Animation(0.1f,frames));
        frames.clear();

        run = new TextureRegion(texture, 296, 67, 35, 47);
        run.flip(false, true);
        frames.add(run);
        run = new TextureRegion(texture,344,66,35,48);
        run.flip(false, true);
        frames.add(run);
        playerMoveL.add(new Animation(0.1f,frames));
        frames.clear();

    }
}
