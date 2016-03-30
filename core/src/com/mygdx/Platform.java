package com.mygdx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Admin on 3/21/2016.
 */
public class Platform {

    private Vector2 position;
    private float width;
    private float height;
    private int type; //0-normal; 1-slippery; 2-bouncy; 3-moving
    private boolean isScrolledDown;
    private float gameWidth, gameHeight;

    private float horizontalV=10; //moving velocity for type 3


    private World world;
    private OrthographicCamera cam;
    private Body body;
    private PolygonShape shape;

    private Rectangle boundingRect;

    public Platform(OrthographicCamera cam, World world, float x, float y, float width, float height, int type, float gameWidth, float gameHeight) {
        this.cam = cam;
        position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.type = type;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        isScrolledDown = false;

        boundingRect = new Rectangle();

        this.world = world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position.x+width/2, position.y+height/2);
        body = world.createBody(bodyDef);

        shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        switch (type) {
            case 1: fixture.friction=0;
                    break;
            case 2: fixture.friction=0.2f;
                    break;
            default: fixture.friction=0.4f;
        }
        body.createFixture(fixture);
    }

    public void update(float delta) {
        if (type==3) {
            if (body.getPosition().x > gameWidth-width/2 || body.getPosition().x < width/2)
                horizontalV*=-1;
            body.setTransform(body.getPosition().x+horizontalV*delta, body.getPosition().y, 0);
        }

        position.x = body.getPosition().x - width/2;
        position.y = body.getPosition().y - (cam.position.y - gameHeight/2) - height/2;



        // If the Platform object is no longer visible:
        if (position.y - height > gameHeight) {
            isScrolledDown = true;
        }
        boundingRect.set(position.x, position.y, width, height);
    }

    public void reset(float x, float y, int width, int height, int type, float worldHeight) {
        position.x = x;
        position.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        body.setTransform(x+width/2, worldHeight - gameHeight / 2 + height/2, 0);

        isScrolledDown = false;
    }

    // Getters for instance variables
    public boolean isScrolledDown() {
        return isScrolledDown;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getType() {
        return type;
    }

    public Rectangle getBoundingRect() {
        return boundingRect;
    }

}
