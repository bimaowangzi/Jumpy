package com.mygdx.JumpyHelper;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

/**
 * Created by Admin on 4/13/2016.
 */

public class Avatar {
    public ArrayList<TextureRegion> playerTextures = new ArrayList<TextureRegion>();
    public ArrayList<Animation> playerMoveR;
    public ArrayList<Animation> playerMoveL;

    public Avatar(int avatarID) {
        playerTextures = AvatarPacks.playerTexturesList.get(avatarID);
        playerMoveL = AvatarPacks.playerMoveLs.get(avatarID);
        playerMoveR = AvatarPacks.playerMoveRs.get(avatarID);
    }
}

