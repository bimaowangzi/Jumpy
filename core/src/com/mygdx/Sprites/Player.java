package com.mygdx.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by acer on 16/2/2016.
 */
public class Player extends Sprite implements ContactFilter, ContactListener
{
    public World world;
    public Body b2body;
    public Fixture fixture;
    boolean collide = false;
    private long startTime;
    float position;
    Vector2 local = new Vector2(0,-100);
    public int y;
    public int x;

    public Player(World world, int x,int y){
        this.world = world;
        this.y = y;
        this.x = x;
        definePlayer(y);
    }

    public void definePlayer(int y){
        BodyDef bdef = new BodyDef();
        bdef.position.set(x,y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(1.5f);

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }

    public float getPosY(){
        return b2body.getPosition().y;
    }
    public float getPosX(){
        return b2body.getPosition().x;
    }

    public void deleteBody(){
        world.destroyBody(b2body);
    }

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
            return (b2body.getLinearVelocity().y < 0);
    }
    @Override
    public void beginContact(Contact contact) {
        startTime = TimeUtils.millis();
    }

    @Override
    public void endContact(Contact contact) {
        b2body.setUserData(1);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        b2body.setUserData(0);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        try {
            int type = (Integer) contact.getFixtureA().getUserData();
            //System.out.println(type);
            if (type == 1) {
                contact.setFriction(10);
            }
            if(type ==2){
                long elapsedTime = TimeUtils.timeSinceMillis(startTime);
                if (elapsedTime > 500) {
                    contact.getFixtureA().setSensor(true);
                }
            }
        }
        catch(NullPointerException e){

        }
    }
}
