package com.mygdx.JumpyHelper;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.GameWorld.GameWorld;
import com.mygdx.Sprites.Player;

/**
 * Created by Admin on 3/21/2016.
 */
public class InputHandler implements InputProcessor {
    private Player myPlayer;
    private GameWorld myWorld;

    public InputHandler(GameWorld myWolrd) {
        this.myWorld =myWolrd;
        myPlayer = myWolrd.getPlayer();
    }
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        myPlayer.onJump();

        if (myWorld.isGameOver())
            myWorld.restart();
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
