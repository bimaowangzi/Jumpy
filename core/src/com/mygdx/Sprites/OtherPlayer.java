package com.mygdx.Sprites;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Circle;
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
import com.mygdx.JumpyHelper.PlayerResult;


/**
 * Created by acer on 16/2/2016.
 */
public class OtherPlayer implements ContactFilter, ContactListener {
    private String name;
    private int avatarID;
    private volatile Vector2 position;

    private volatile float width = 5;
    private volatile float height = 5;
    private volatile float worldHeight;
    private volatile float gameWidth;
    private volatile float gameHeight;

    private volatile int score = 0;

    private volatile int lives = 3;
    private volatile int powerUpState; //(-1)-nothing; 0-high jump; 1-weight; 2-balloon; 3-swap Position;
    private volatile boolean inAir;

    private volatile float radius = 2.5f;

    private OrthographicCamera cam;

    private Circle boundingCircle;
    private World world;
    private Body body;
    private CircleShape shape;

    private volatile PlayerResult result;

    public OtherPlayer(String name, int avatarID, OrthographicCamera cam, World world, float gameWidth, float gameHeight) {
        this.name = name;
        this.avatarID = avatarID;
        position = new Vector2(gameWidth/6*(avatarID+1), gameHeight*0.8f);
        this.cam = cam;

        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        powerUpState = -1;

        boundingCircle = new Circle();

        this.world = world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);
        body = world.createBody(bodyDef);

        shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape=shape;
        fixtureDef.density=1;
        fixtureDef.friction=0.5f;
        body.createFixture(fixtureDef);

        result = new PlayerResult(false, -1, -1, name);
    }

    // This update method is called when a new data set is received
    public synchronized void update(float x, float y, float vx, float vy, float width, float height,
                                    int powerUpState, boolean inAir, float worldHeight, int lives) {
        this.body.setTransform(x, y, 0);
        this.body.setLinearVelocity(vx, vy);
        this.width = width;
        this.height = height;
        this.powerUpState = powerUpState;
        this.inAir = inAir;
        this.lives = lives;
        this.worldHeight = worldHeight;
    }

    // This update method is called by the GameWorld
    public synchronized void update() {
        if (result.getTime()>0)
            body.setLinearVelocity(0, -0.2f);

        // Map position on screen with world position
        position.x = body.getPosition().x;
        position.y = body.getPosition().y - (cam.position.y - gameHeight / 2);
        boundingCircle.set(position, radius);

    }

    public void setResult(PlayerResult result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public PlayerResult getResult() {
        return result;
    }

    //upper left corner
    public float getX() {
        return position.x - width/2;
    }

    //upper left corner
    public float getY() {
        return position.y - height/2;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getWorldHeight() {
        return this.worldHeight;
    }

    public int getLives() {
        return lives;
    }

    public boolean inAir() {
        return inAir;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public int getPowerUpState() {
        return powerUpState;
    }

    public boolean MovingRight(){
        return body.getLinearVelocity().x > 0;
    }

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        return false;
    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}


