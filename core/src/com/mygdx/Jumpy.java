package com.mygdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.JumpyHelper.AssetLoader;
import com.mygdx.Screens.PlayScreen;
import com.mygdx.Screens.ScreenEnum;
import com.mygdx.Screens.ScreenManager;

public class Jumpy extends Game {
	public static final int WIDTH = 38;
//	public static final float WIDTH = Gdx.graphics.getWidth();
	public static final int HEIGHT = 64;
//	public static final float HEIGHT = Gdx.graphics.getHeight();

	public SpriteBatch batch;
	//Texture img;

//	@Override
//	public void create () {
//		batch = new SpriteBatch();
//		//img = new Texture("badlogic.jpg");
//		setScreen(new PlayScreen(this));
//	}

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

