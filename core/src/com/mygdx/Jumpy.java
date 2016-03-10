package com.mygdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.Screens.PlayScreen;

public class Jumpy extends Game {
	public static final int WIDTH = 38;
//	public static final float WIDTH = Gdx.graphics.getWidth();
	public static final int HEIGHT = 64;
//	public static final float HEIGHT = Gdx.graphics.getHeight();

	public SpriteBatch batch;
	//Texture img;

	@Override
	public void create () {
		batch = new SpriteBatch();
		//img = new Texture("badlogic.jpg");
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
}


//public class Jumpy extends ApplicationAdapter {
//	SpriteBatch batch;
//	Texture img;
//
//	@Override
//	public void create () {
//		batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
//	}
//
//	@Override
//	public void render () {
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
//	}
//}
