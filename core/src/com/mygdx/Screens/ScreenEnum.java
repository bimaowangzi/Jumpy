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
            // pass username
            return new RoomSelectionScreen((String) params[0]);
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
    GAME {
        public AbstractScreen getScreen(Object... params) {
//            return new GameScreen((Integer) params[0]);
            return new GameScreen();
        }
    };

    public abstract AbstractScreen getScreen(Object... params);
}
