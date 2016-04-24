package com.mygdx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.JumpyHelper.AssetLoader;
import com.mygdx.appwarp.WarpController;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Admin on 3/21/2016.
 */
public class PlatformHandler {
    // PlatformHandler will create all platform objects we need
    private ArrayList<Platform> platforms = new ArrayList<Platform>();
    private Platform ground;
    private Platform finishlineLine;
    private Random random;
    private float gap;
    private World world;
    private OrthographicCamera cam;
    private float gameWidth, gameHeight;
    private int index;
    private int interval;
    private float currentHeight;
    private final float distance = -940f;

    public PlatformHandler(OrthographicCamera cam, World world, float gameWidth, float gameHeight) {
        this.cam = cam;
        this.world = world;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        gap = gameHeight/10;
        random = new Random();
        index = WarpController.getStart();
        interval = WarpController.getInterval();
        ground = new Platform(cam, world, 0, 0.8f*gameHeight, gameWidth, 0.2f*gameHeight, 0, gameWidth, gameHeight);

        currentHeight = 0.8f*gameHeight;

        for (int i=0; i<5; i++) {
            currentHeight -= gap;
            platforms.add(new Platform(cam, world, AssetLoader.platformLeft[index], currentHeight-AssetLoader.platformGap[index]*gap,
                    AssetLoader.platformWidths[index], 2, AssetLoader.platformTypes[index], gameWidth, gameHeight));
            index=(index+interval)%2000;
            currentHeight-=gap;
            platforms.add(new Platform(cam, world, 25+AssetLoader.platformLeft[index], currentHeight-AssetLoader.platformGap[index]*gap,
                    AssetLoader.platformWidths[index], 2, AssetLoader.platformTypes[index], gameWidth, gameHeight));
            index=(index+interval)%2000;
        }

        finishlineLine = new Platform(cam, world, 0, distance, gameWidth, 5, 4, gameWidth, gameHeight);
    }

    public void update(float delta) {
        // update grounds and finishing line first
        ground.update(delta);
        finishlineLine.update(delta);

        // now update all platforms
        for (int i=0; i<10; i++) {
            platforms.get(i).update(delta);

            // if a platform is out of view, recycle it and put it on top
            if (platforms.get(i).isScrolledDown()) {
                // if the finishing line is reached, then do not create more platforms; just skip ahead
                if (finishlineLine.getY() > 0) {
                    continue;
                }
                currentHeight -= gap;
                if (i % 2 == 0) //left platform
                    platforms.get(i).reset(AssetLoader.platformLeft[index], 0,
                            AssetLoader.platformWidths[index], 2, AssetLoader.platformTypes[index], currentHeight - AssetLoader.platformGap[index] * gap);
                else
                    platforms.get(i).reset(25 + AssetLoader.platformLeft[index], 0,
                            AssetLoader.platformWidths[index], 2, AssetLoader.platformTypes[index], currentHeight - AssetLoader.platformGap[index] * gap);
                index=(index+interval)%2000;
            }
        }
    }

    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public Platform getGround() {
        return ground;
    }

    public Platform getFinishlineLine() {
        return finishlineLine;
    }

    public float getDistance() {
        return distance;
    }
}
