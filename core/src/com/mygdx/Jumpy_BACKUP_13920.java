package com.mygdx;

import com.badlogic.gdx.Game;
<<<<<<< HEAD
||||||| merged common ancestors
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
=======
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.JumpyHelper.AssetLoader;
>>>>>>> upstream/master
import com.mygdx.Screens.PlayScreen;
import com.mygdx.JumpyHelper.AssetLoader;

public class Jumpy extends Game {
<<<<<<< HEAD
||||||| merged common ancestors
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
=======

	public SpriteBatch batch;
>>>>>>> upstream/master

	@Override
	public void create () {
<<<<<<< HEAD
		AssetLoader.load();
		setScreen(new PlayScreen());
||||||| merged common ancestors
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen( ScreenEnum.LOGIN );
=======
		AssetLoader.load();

		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen( ScreenEnum.LOGIN );
>>>>>>> upstream/master
	}

	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}