package com.mygdx.JumpyHelper;

/**
 * Created by user on 11/4/2016.
 */
public class PlayerResult {
    private boolean dead ;
    private float time;
    private int height;
    private String userName;

    public PlayerResult(boolean dead, float time, int height, String userName) {
        this.dead = dead;
        this.time = time;
        this.height = height;
        this.userName = userName;
    }

    public boolean isDead() {
        return dead;
    }

    public float getTime() {
        return time;
    }

    public int getHeight() {
        return height;
    }

    public String getUserName() {
        return userName;
    }
}
