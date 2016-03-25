package com.mygdx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Admin on 3/21/2016.
 */
public class PlatformHandler {
    // PlatformHandler will create all platform objects we need
    private ArrayList<Platform> platforms = new ArrayList<Platform>();
    private Platform ground;
    private Random random;
    private float gap;
    private World world;
    private OrthographicCamera cam;
    private float gameWidth, gameHeight;

    // Constructor receives a float that tells us where we need to create our platforms
    public PlatformHandler(OrthographicCamera cam, World world, float gameWidth, float gameHeight) {
        this.cam = cam;
        this.world = world;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        gap = gameHeight/10;
        random = new Random();

        ground = new Platform(cam, world, 0, 0.8f*gameHeight, gameWidth, 0.2f*gameHeight, 0, gameWidth, gameHeight);

        float y = 0.8f*gameHeight;
        for (int i=0; i<5; i++) {
            y -= gap;
            platforms.add(new Platform(cam, world, random.nextInt(10), y-random.nextFloat()*gap,
                    random.nextInt(5)+10, 2, generateType(), gameWidth, gameHeight));
            y-=gap;
            platforms.add(new Platform(cam, world, 25+random.nextInt(10), y-random.nextFloat()*gap,
                    random.nextInt(5)+10, 2, generateType(), gameWidth, gameHeight));
        }
    }

    public void update(float delta) {
        ground.update(delta);
        for (int i=0; i<10; i++) {
            platforms.get(i).update(delta);
            if (platforms.get(i).isScrolledDown()) {
                if (i%2==0) //left platform
                    platforms.get(i).reset(random.nextInt(10), 0,
                            random.nextInt(5)+10, 2, generateType(), cam.position.y);
                else
                    platforms.get(i).reset(25+random.nextInt(10), 0,
                            random.nextInt(5)+10, 2, generateType(), cam.position.y);
            }


        }
    }

    private int generateType() {
        int num = random.nextInt(8);
        if (num<5) return 0;
        else if (num==5) return 1;
        else if (num==6) return 2;
        else return 3;
    }

    public void reset() {
        float y = 0;
        platforms.clear();
        for (int i=0; i<5; i++) {
            y += gap;
            platforms.add(new Platform(cam, world, random.nextInt(10), y + random.nextFloat()*gap,
                    random.nextInt(5) + 10, 2, generateType(), gameWidth, gameHeight));
            y+=gap;
            platforms.add(new Platform(cam, world, 25 + random.nextInt(10), y + random.nextFloat()*gap,
                    random.nextInt(5) + 10, 2, generateType(), gameWidth,  gameHeight));
        }
    }


    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public Platform getGround() {
        return ground;
    }
}
