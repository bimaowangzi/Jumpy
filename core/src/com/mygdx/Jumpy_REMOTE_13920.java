package com.mygdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.JumpyHelper.AssetLoader;
import com.mygdx.Screens.PlayScreen;
import com.mygdx.Screens.ScreenEnum;
import com.mygdx.Screens.ScreenManager;

public class Jumpy extends Game {

	public SpriteBatch batch;

	@Override
	public void create () {
		AssetLoader.load();

		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen( ScreenEnum.LOGIN );
	}

	@Override
	public void render () {
		super.render();
	}
}