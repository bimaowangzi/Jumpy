package com.mygdx;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;

/**
 * Created by Admin on 2/23/2016.
 */
public class PowerUp {
    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private final float radius = 3f;

    private int type;
    // 0 - normal, 1 - high jump, 2 - low jump, 3 - wings, 4 - extra life, 5 - giant, 6 - random,
    // 7 - speed up world, 8 - lightning, 9 - reverse world
    private boolean isActive;
    private float timer;
    private float nextRespawningTime;

    private float gameWidth, gameHeight;
    private Circle boundingCircle;
    private Random randomNumberGen;

    // the power up will modify its direction every 0.3 seconds
    private final float directionChangePeriod=0.3f;
    // these determine how much of a direction change can occur per directionChangeFrequency
    private final float minDirectionChangeAmount=0;
    private final float maxDirectionChangeAmount=5;

    // timer to keep track of when this moving circle should change direction
    float directionChangetimer=0;

    public PowerUp(float gameWidth, float gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        randomNumberGen=new Random();
        type = typeGenerator();
        isActive = false;
        boundingCircle = new Circle();

        timer = 0;
        nextRespawningTime = 7 + randomNumberGen.nextFloat()*7;
    }

    private void respawn() {
        // Generate starting position
        position.x = randomNumberGen.nextFloat()*gameWidth/2 + gameWidth/4;
        position.y = gameHeight + radius/2;

        velocity.x = 0;
        velocity.y = -25;

        type = typeGenerator();
    }

    public void update(float delta){

        if (!isActive) {
            timer+=delta;
            if (timer > nextRespawningTime) {
                respawn();
                isActive = true;
            }
            else return;
        }

        // Update position
        position.x+=velocity.x*delta;
        position.y+=velocity.y*delta;

        // If the power up goes off an edge, it comes back on the other edge
        if (position.x<0)
            position.x = gameWidth;
        if (position.x>gameWidth)
            position.x = 0;

        boundingCircle.set(position, radius);

        directionChangetimer+=delta;

        if(directionChangetimer>=directionChangePeriod){
            directionChangetimer-=directionChangePeriod;
            float directionChangeRange=maxDirectionChangeAmount-minDirectionChangeAmount;
            // calculate a random change amount between the minimum and max
            float directionChangeAmount=randomNumberGen.nextFloat()*directionChangeRange+minDirectionChangeAmount;
            // flip the sign half the time so that the velocity increases and decreases
            if(randomNumberGen.nextBoolean()){
                directionChangeAmount=-directionChangeAmount;
            }
            // apply the change amount to the velocity;
            velocity.x+=directionChangeAmount;
        }

        if (isGone()) {
            isActive = false;
            timer = 0;
            nextRespawningTime = 5 + randomNumberGen.nextFloat()*5;
            boundingCircle.set(0,0,0);
        }

    }

    public int typeGenerator() {
        int num = randomNumberGen.nextInt(21);
        if (num < 3) return 1;
        if (num < 7) return 2;
        if (num < 9) return 3;
        if (num < 11) return 4;
        if (num < 13) return 5;
        if (num < 15) return 6;
        if (num < 17) return 7;
        if (num < 19) return 8;
        return 9;
    }

    private boolean isGone() {
        return position.y < 0-radius;
    }

    // top left corner
    public float getX() {
        return position.x - radius;
    }

    // top left corner
    public float getY() {
        return position.y - radius;
    }

    public Vector2 getCenter() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getType() {
        return type;
    }

    public Circle getBoundingCircle() {
        return boundingCircle;
    }

}


