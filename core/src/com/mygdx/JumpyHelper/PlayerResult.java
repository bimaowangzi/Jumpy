package com.mygdx.JumpyHelper;

import java.util.Comparator;

/**
 * Created by user on 11/4/2016.
 */
public class PlayerResult implements Comparable<PlayerResult>{
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

//    The compare method compares its two arguments, returning a negative integer, 0, or a positive integer depending on whether the first argument is less than, equal to,
//    or greater than the second. If either of the arguments has an inappropriate type for the Comparator, the compare method throws a ClassCastException.
    @Override
    public int compareTo(PlayerResult o) {
        // one dead and one alive, alive priority
        if (this.isDead()!=o.isDead()){
            if (this.isDead()){
                return 1;
            } else {
                return -1;
            }
            // both dead, higher height priority
        } else if (this.isDead()){
            if (this.getHeight()>o.getHeight()){
                return -1;
            } else if (this.getHeight()<o.getHeight()){
                return 1;
            } else {
                // tie breaker by timing, smaller timing priority, if tie again not no special implementation yet, o2 gets priority this case
                if (this.getTime()<o.getTime()){
                    return -1;
                } else {
                    return 1;
                }
            }
            //both alive, smaller timing priority
        } else {
            // tie breaker by timing, smaller timing priority, if tie again not no special implementation yet, o2 gets priority this case
            if (this.getTime()<o.getTime()){
                return -1;
            } else {
                return 1;
            }
        }
    }
}
