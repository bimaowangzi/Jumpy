package com.mygdx.Screens;

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
            return new WinScreen();
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
