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
import com.mygdx.JumpyHelper.PlayerResult;
import com.mygdx.Platform;
import com.mygdx.PlatformHandler;
import com.mygdx.PowerUp;
import com.mygdx.appwarp.WarpController;

import org.json.JSONObject;


/**
 * Created by acer on 16/2/2016.
 */
public class Player implements ContactFilter, ContactListener {
    private String name;
    private int avatarID;
    private Vector2 position;

    private float width = 5;
    private float height = 5;
    private float gameWidth;
    private float gameHeight;

    private float jumpSpeed;
    private final float baseJumpSpeed = -55;
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

    private float radius = 2.5f;

    private OrthographicCamera cam;

    private Circle boundingCircle;
    private World world;
    private Body body;
    private CircleShape shape;

    private PlayerResult result;

    public Player(String name, int avatarID, OrthographicCamera cam, World world, PowerUp powerUp, float gameWidth, float gameHeight) {
        this.name = name;
        this.avatarID = avatarID;
        position = new Vector2(gameWidth/2, gameHeight*0.8f);
        this.cam = cam;
        this.powerUp = powerUp;

        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        jumpSpeed = baseJumpSpeed;

        canJump = true;
        alive = false;
        powerUpState = 0;

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

        initialHeight = (int) body.getPosition().y;

        result = null;
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
        if (body.getPosition().y < cam.position.y - gameHeight/4 && powerUpState!=-9)
            cam.position.y = body.getPosition().y + gameHeight/4;

        // Map position on screen with world position
        position.x = body.getPosition().x;
        position.y = body.getPosition().y - (cam.position.y - gameHeight/2);
        boundingCircle.set(position, radius);

        // Handling death
        if (isDead()) {
            lives -= 1;
            alive = false;
            if (lives > 0)
                respawn();
        }

        sendPlayerUpdate();


    }

    public void respawn() {
        Platform respawnPlatform = platformHandler.getPlatforms().get(0);
        for (Platform p : platformHandler.getPlatforms())
            if (p.getY() < gameHeight/2 && p.getY() > respawnPlatform.getY())
                respawnPlatform = p;
        if (respawnPlatform.getY() > gameHeight)
            for (Platform p : platformHandler.getPlatforms())
                if (p.getY() < respawnPlatform.getY())
                    respawnPlatform = p;


        position = new Vector2(respawnPlatform.getX() + respawnPlatform.getWidth()/2,
                respawnPlatform.getY() - 4);
        jumpSpeed = baseJumpSpeed;
        canJump = true;
        powerUpState = 0;

        body.setTransform(respawnPlatform.getX() + respawnPlatform.getWidth()/2, respawnPlatform.getWorldHeight() - 4, 0);
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
        if (powerUpState == 0) {
            jumpSpeed = baseJumpSpeed;
            if (radius!=2.5f) {
                body.destroyFixture(body.getFixtureList().first());

                radius = 2.5f;
                height = 5;
                width = 5;

                shape = new CircleShape();
                shape.setRadius(radius);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = 1;
                fixtureDef.friction = 0.5f;
                body.createFixture(fixtureDef);
            }

        } else if (powerUpState == 1) {
            jumpSpeed = baseJumpSpeed * 2;
        } else if (powerUpState == -2) {
            jumpSpeed = baseJumpSpeed*0.75f;
        } else if (powerUpState==3) { // umbrella
            body.setLinearVelocity(body.getLinearVelocity().x, -35);
        } else if (powerUpState == 4) {
            if (lives < 3) lives++;
            powerUpState = 0;
        } else if (powerUpState == 5) {
            if (radius!=4f) {
                body.destroyFixture(body.getFixtureList().first());

                radius = 4f;
                height = 8;
                width = 8;

                shape = new CircleShape();
                shape.setRadius(radius);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = 0.75f;
                fixtureDef.friction = 0.5f;
                body.createFixture(fixtureDef);
            }
        } else if (powerUpState == 6) {
            while (powerUpState == 6)
                powerUpState = powerUp.typeGenerator();
        } else if (powerUpState == -7) {
            // increase world scroll speed
        } else if (powerUpState == -8) {
            body.setLinearVelocity(0, 0);
        } else if (powerUpState == -9) {
            jumpSpeed = -baseJumpSpeed;
        }

        if ((powerUpState==8 || powerUpState==-8) && timer > 0.7f) {
            if (powerUpState==-7)
                lives--;
            powerUpState = 0;
        }

        if ((powerUpState==2 || powerUpState==-2 || powerUpState==7 || powerUpState==-7 ||
                powerUpState==9 || powerUpState==-9) && timer > 2.5f) {
            powerUpState = 0;
        }

        if (timer > 5f) // power up effective for 5s
            powerUpState = 0;
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
            body.setLinearVelocity(body.getLinearVelocity().x*12, 0);
        } else if (platformType==2) {
            canJump = false;
            body.setLinearVelocity(body.getLinearVelocity().x, baseJumpSpeed*1.5f);
        }
    }

    public void getHit(int powerUp) {
        jumpSpeed = baseJumpSpeed;
        if (radius!=2.5f) {
            body.destroyFixture(body.getFixtureList().first());

            radius = 2.5f;
            height = 5;
            width = 5;

            shape = new CircleShape();
            shape.setRadius(radius);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1;
            fixtureDef.friction = 0.5f;
            body.createFixture(fixtureDef);
        }

        powerUpState = -powerUp;
        timer = 0;
    }

//    public void reset() {
//        position = new Vector2(gameWidth/2, gameHeight*3/2);
//        jumpSpeed = baseJumpSpeed;
//        canJump = true;
//        alive = false;
//        score = 0;
//        powerUpState = -1;
//        lives = 4;
//
//        body.setTransform(position, 0);
//    }

    private void sendPlayerUpdate() {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "update");
            data.put("worldX", body.getPosition().x);
            data.put("worldY", body.getPosition().y);
            data.put("velocityX", body.getLinearVelocity().x);
            data.put("velocityY", body.getLinearVelocity().y);
            data.put("width", width);
            data.put("height", height);
            data.put("powerUpState", powerUpState);
            data.put("inAir", inAir());
            data.put("worldHeight", getWorldHeight());
            data.put("lives", lives);
            WarpController.getInstance().sendGameUpdate(data.toString());
        } catch (Exception e) {
            // exception in sendLocation
        }
    }


    public void setPlatformHandler(PlatformHandler p) {
        platformHandler = p;
    }

    public String getName() {
        return name;
    }

    //upper left corner
    public float getX() {
        return position.x - width/2;
    }

    //upper left corner
    public float getY() {
        return position.y - height/2;
    }

    public float getWorldHeight() {
        return body.getPosition().y;
    }


    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public boolean isDead() {
        return (position.y > gameHeight || position.y < 0);
    }

    public boolean isAlive(){
        return alive;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public int getPowerUpState() {
        return powerUpState;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        if (score < (initialHeight - (int) body.getPosition().y)/10)
            score = (initialHeight - (int) body.getPosition().y)/10;
        return score;
    }

    public boolean MovingRight(){
        return body.getLinearVelocity().x > 0;
    }

    public void setResult(PlayerResult result) {
        this.result = result;
    }

    public PlayerResult getResult() {
        return result;
    }

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        if (powerUpState!=-9)
            return (body.getLinearVelocity().y > 0 && body.getPosition().y+height/2 < fixtureB.getBody().getPosition().y+0.1);
        else
            return (body.getLinearVelocity().y < 0 && body.getPosition().y-height/2 < fixtureB.getBody().getPosition().y+2);
    }

    @Override
    public void beginContact(Contact contact) {
//        for (Platform p: platformHandler.getPlatforms()) {
//            if (Math.abs(p.getY() - (getY() + height)) < 0.05f && p.getType() == 2) {
//                canJump = false;
//                return;
//            }
//        }
        canJump = true;
    }

    public boolean inAir() {
        return !canJump;
    }

    @Override
    public void endContact(Contact contact) {
        for (Platform p: platformHandler.getPlatforms()) {
            if (Math.abs(p.getY() - (getY() + height)) < 0.1f) {
                canJump = true;
                return;
            }
        }
        canJump = false;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
