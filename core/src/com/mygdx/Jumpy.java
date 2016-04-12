package com.mygdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.JumpyHelper.AssetLoader;
import com.mygdx.Screens.ScreenEnum;
import com.mygdx.Screens.ScreenManager;

public class Jumpy extends Game {
	public SpriteBatch batch;

	@Override
	public void create () {
		AssetLoader.load();
//		setScreen(new PlayScreen());
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen( ScreenEnum.LOGIN );
	}

	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}