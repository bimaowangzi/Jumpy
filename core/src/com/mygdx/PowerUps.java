package com.mygdx;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

/**
 * Created by Admin on 2/23/2016.
 */
public class PowerUps{

    // vectors for position and velocity
    public Body powerupBody;
    private World world;
    private CircleShape frame;
    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private int type = 0;

    public PowerUps(int worldWidth, float y, World world) {
        this.world = world;

        type = MathUtils.random(1, 3);

        position.x = MathUtils.random(0, worldWidth);
        position.y = y;

        velocity.x = 0;
        velocity.y = 17;

        frame = new CircleShape();
        frame.setRadius(2);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.KinematicBody;
        powerupBody = world.createBody(bdef);

        powerupBody.createFixture(frame, 1);
        powerupBody.setUserData("Powerup");

    }


    public int getPosX() {
        return (int) powerupBody.getPosition().x;
    }

    public int getPosY() {
        return (int) powerupBody.getPosition().y;
    }

    public int getType() {
        return type;
    }

 /*   public Rectangle getFrame() {
        Rectangle frame = new Rectangle();
        frame.x = (int) position.x;
        frame.y = (int) position.y;
        frame.width = 64;
        frame.height = 64;
        return frame;
    }*/



    // the circles will modify their direction every 3 seconds
    // changing this to an instance variable (remove static) would allow different circles to
    // change directions at different frequencies, making some more erratic than others
    private static float directionChangeFrequency=0.3f;

    // these determine how much of a direction change can occur per directionChangeFrequency
    private static float minDirectionChangeAmount=0;
    private static float maxDirectionChangeAmount=4;

    private static Random randomNumberGen=new Random();

    // timer to keep track of when this moving circle should change direction
    float directionChangetimer=0;

    public void update(float delta){
        //position.x+=velocity.x*delta;
        //position.y+=velocity.y*delta;

        directionChangetimer+=delta;

        if(directionChangetimer>=directionChangeFrequency){
            directionChangetimer-=directionChangeFrequency;
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
        if (getPosX()<0 | getPosX()>Jumpy.WIDTH)
            velocity.x = -velocity.x;

        powerupBody.setLinearVelocity(velocity);
    }

}

