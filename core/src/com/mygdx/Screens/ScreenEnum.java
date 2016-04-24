package com.mygdx.Screens;

import com.mygdx.JumpyHelper.PlayerResult;
import com.mygdx.Sprites.OtherPlayer;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by user on 11/3/2016.
 */

/**Enum java type ScreenEnum defines a list of available screens
 * Each element of ScreenEnum has a getScreen method
 * Which allow us to get an screen instance of each enum type*/
public enum ScreenEnum {

    /**Each getScreen method has a Object... params parsed in as an argument
     * It allows us to send as many Objects as we want from one screen to another*/

    LOGIN {
        public AbstractScreen getScreen(Object... params) {
            return new LoginScreen();
        }
    },
    ROOMSELECTION {
        public AbstractScreen getScreen(Object... params) {
            // pass warpcontroller
//            return new RoomSelectionScreen((WarpController) params[0]);
            return new RoomSelectionScreen();
        }
    },
    LOBBY {
        public AbstractScreen getScreen(Object... params) {
            return new LobbyScreen();
        }
    },
    AVATAR {
        public AbstractScreen getScreen(Object... params) {
            return new AvatarScreen();
        }
    },
    WIN {
        public AbstractScreen getScreen(Object... params) {
            /**2 Objects are being parsed to Win Screen, PlayerResult and CopyOnWriteArrayList*/
            return new WinScreen((PlayerResult) params[0],(CopyOnWriteArrayList<OtherPlayer>) params[1]);
        }
    },
    PLAY {
        public AbstractScreen getScreen(Object... params) {
            return new PlayScreen();
        }
    };

    public abstract AbstractScreen getScreen(Object... params);
}
