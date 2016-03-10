package com.mygdx;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.Screens.PlayScreen;

/**
 * Created by user on 1/3/2016.
 */
public class InputHandlerClass {

    private InputProcessor inputProcessor;
    private PlayScreen playScreen;

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public InputHandlerClass(final PlayScreen playScreen) {
        this.playScreen = playScreen;
        inputProcessor = new InputProcessor() {
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
                if (playScreen.isAccelMode()){
                    try{
                        int state = (Integer) playScreen.getPlayer().b2body.getUserData();

                        if(state==0) {
                            playScreen.getPlayer().b2body.setLinearVelocity(playScreen.getPlayer().b2body.getLinearVelocity().x, 35*PlayScreen.velocityScale);
                        }
                    }
                    catch(NullPointerException e){}
                }
                return true;
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
        };



    }
}
