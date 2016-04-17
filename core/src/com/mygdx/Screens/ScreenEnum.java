package com.mygdx.Screens;

import com.mygdx.GameWorld.GameWorld;
import com.mygdx.JumpyHelper.PlayerResult;
import com.mygdx.Sprites.OtherPlayer;

import java.util.ArrayList;

/**
 * Created by user on 11/3/2016.
 */
public enum ScreenEnum {

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
            return new WinScreen((PlayerResult) params[0],(ArrayList<OtherPlayer>) params[1], (GameWorld) params[2]);
//            return new WinScreen();
        }
    },
    PLAY {
        public AbstractScreen getScreen(Object... params) {
//            return new PlayScreen((Integer) params[0]);
            return new PlayScreen();
        }
    };

    public abstract AbstractScreen getScreen(Object... params);
}
