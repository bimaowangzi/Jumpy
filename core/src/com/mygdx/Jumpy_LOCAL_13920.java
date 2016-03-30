package com.mygdx;

import com.badlogic.gdx.Game;
import com.mygdx.Screens.PlayScreen;
import com.mygdx.JumpyHelper.AssetLoader;

public class Jumpy extends Game {

	@Override
	public void create () {
		AssetLoader.load();
		setScreen(new PlayScreen());
	}

	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}

