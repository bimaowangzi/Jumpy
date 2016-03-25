package com.mygdx.Sprites;

import com.badlogic.gdx.Gdx;
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
import com.mygdx.Platform;
import com.mygdx.PlatformHandler;
import com.mygdx.PowerUp;


/**
 * Created by acer on 16/2/2016.
 */
public class Player implements ContactFilter, ContactListener {
    private Vector2 position;

    private final float width = 5;
    private final float height = 5;
    private float gameWidth;
    private float gameHeight;

    private float jumpSpeed;
    private final float baseJumpSpeed = -50;
    private float accelX;
    private boolean canJump; //determine whether to respond to the touch, to avoid multiple jumps
    private int score = 0;
    private int initialHeight;

    private int lives = 3;
    private boolean alive;
    private int powerUpState; //(-1)-nothing; 0-high jump; 1-weight; 2-balloon; 3-swap Position;

    private PowerUp powerUp;
    private PlatformHandler platformHandler;
    private float timer;

    private OrthographicCamera cam;

    private Circle boundingCircle;
    private World world;
    private Body body;
    private CircleShape shape;

    public Player(OrthographicCamera cam, World world, PowerUp powerUp, float gameWidth, float gameHeight) {
        position = new Vector2(gameWidth/2, gameHeight*0.8f);
        this.cam = cam;
        this.powerUp = powerUp;

        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        jumpSpeed = baseJumpSpeed;

        canJump = true;
        alive = false;
        powerUpState = -1;

        boundingCircle = new Circle();

        this.world = world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);
        body = world.createBody(bodyDef);

        shape = new CircleShape();
        shape.setRadius(2.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape=shape;
        fixtureDef.density=1;
        fixtureDef.friction=0.5f;
        body.createFixture(fixtureDef);

        initialHeight = (int) body.getPosition().y;

    }

    public void update(float delta) {
        timer += delta;

        // Set x-velocity according to accelerometer value
        accelX = Gdx.input.getAccelerometerX();
        body.setLinearVelocity(0.1f * accelX * jumpSpeed, body.getLinearVelocity().y);

        detectPowerUp();
        detectPlatform();

        // If a player goes off an edge, he comes back on the other edge
        if (body.getPosition().x<0)
            body.setTransform(gameWidth, body.getPosition().y, 0);
        if (body.getPosition().x>gameWidth)
            body.setTransform(0, body.getPosition().y, 0);

        // If a player is too far up the screen, the camera should follow him
        if (body.getPosition().y < cam.position.y - gameHeight/4)
            cam.position.y = body.getPosition().y + gameHeight/4;

        // Map position on screen with world position
        position.x = body.getPosition().x;
        position.y = body.getPosition().y - (cam.position.y - gameHeight/2);
        boundingCircle.set(position, 2.5f);

        // Handling death
        if (isDead()) {
            lives -= 1;
            alive = false;
            respawn();
        }
    }

    public void respawn() {
        position = new Vector2(gameWidth/2, gameHeight*3/2);
        jumpSpeed = baseJumpSpeed;
        canJump = true;
        powerUpState = -1;

        body.setTransform(cam.position.x, cam.position.y + gameHeight/6, 0);
    }

    // Call this function when responding to screen touch
    public void onJump() {
        if (!alive) alive = true;   // if a player was not alive, a touch revives him
        if (canJump)
            body.setLinearVelocity(new Vector2(0, jumpSpeed));
        canJump = false;
    }

    private void detectPowerUp() {
        if (boundingCircle.overlaps(powerUp.getBoundingCircle())) {
            powerUpState = powerUp.getType();
            timer = 0;
        }

        // Set jump speed based on power ups
        if (powerUpState == -1) {
            jumpSpeed = baseJumpSpeed;
        } else if (powerUpState == 0) {
            jumpSpeed = baseJumpSpeed * 2;
        } else if (powerUpState == 1) {
            jumpSpeed = baseJumpSpeed*0.8f;
        } else if (powerUpState==2) { // umbrella
            body.setLinearVelocity(body.getLinearVelocity().x, -35);
        } else if (powerUpState == 3 && timer != 0) {
            if (lives < 3) lives++;
            powerUpState = -1;
        }

        if (timer > 5f) // power up effective for 5s
            powerUpState = -1;
    }

    private void detectPlatform() {
        if (!canJump) return;
        Platform platform = null;
        int platformType = 0;
        for (Platform p: platformHandler.getPlatforms()) {
            if (Math.abs(p.getY() - (getY()+height)) < 0.1f) {
                platform = p;
                break;
            }
        }
        if (platform!=null) {
            platformType = platform.getType();
        }

        if (platformType==1) {
            body.setLinearVelocity(body.getLinearVelocity().x*50, 0);
        } else if (platformType==2) {
            body.setLinearVelocity(body.getLinearVelocity().x, baseJumpSpeed*1.5f);
        }
    }

    public void reset() {
        position = new Vector2(gameWidth/2, gameHeight*3/2);
        jumpSpeed = baseJumpSpeed;
        canJump = true;
        alive = false;
        score = 0;
        powerUpState = -1;
        lives = 4;

        body.setTransform(position, 0);
    }

    public void setPlatformHandler(PlatformHandler p) {
        platformHandler = p;
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

    public boolean isDead() {
        return position.y > gameHeight;
    }

    public boolean isAlive(){
        return alive;
    }


    public int getPowerUpState() {
        return powerUpState + 1;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        if (score < (initialHeight - (int) body.getPosition().y)/10)
            score = (initialHeight - (int) body.getPosition().y)/10;
        return score;
    }


    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        return (body.getLinearVelocity().y > 0 && body.getPosition().y+height/2 < fixtureB.getBody().getPosition().y+0.1);
    }

    @Override
    public void beginContact(Contact contact) {
        canJump = true;
    }

    @Override
    public void endContact(Contact contact) {
        canJump = false;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

