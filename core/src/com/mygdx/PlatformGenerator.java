package com.mygdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Daniel on 2/19/2016.
 */
public class PlatformGenerator{
    private Body environment;
    private float leftedge,rightedge, minGap, maxGap, minWidth, maxWidth, height;
    private float y;
    private float x;
    public int platformID;
    private float width;

    public float getLeftedge() {
        return leftedge;
    }

    public void setLeftedge(float leftedge) {
        this.leftedge = leftedge;
    }

    public float getRightedge() {
        return rightedge;
    }

    public void setRightedge(float rightedge) {
        this.rightedge = rightedge;
    }

    public float getMinGap() {
        return minGap;
    }

    public void setMinGap(float minGap) {
        this.minGap = minGap;
    }

    public float getMaxGap() {
        return maxGap;
    }

    public void setMaxGap(float maxGap) {
        this.maxGap = maxGap;
    }

    public float getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public PlatformGenerator(Body environment, float leftedge, float rightedge, float minGap, float maxGap, float minWidth, float maxWidth, float height){
        this.environment = environment;
        this.leftedge = leftedge;
        this.rightedge = rightedge;
        this.minGap = minGap;
        this.minWidth = minWidth;
        this.maxGap = maxGap;
        this.maxWidth = maxWidth;
        this.height = height;
    }

    public void generate(float topEdge){
        if(y+ MathUtils.random(minGap,maxGap)>topEdge){
            return;
        }
        y = topEdge;
        width = MathUtils.random(minWidth,maxWidth);
        x = MathUtils.random(leftedge,rightedge-width);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2, new Vector2(x + width / 2, y + height / 2), 0);

        Fixture fdef = environment.createFixture(shape, 0);
        platformID = MathUtils.random(0,3);
        fdef.setUserData(platformID);

        shape.dispose();
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public float getWidth(){return width;}


}
