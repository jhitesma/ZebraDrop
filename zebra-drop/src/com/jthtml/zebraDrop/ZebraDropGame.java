package com.jthtml.zebraDrop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jthtml.zebraDrop.GoogleInterface;

public class ZebraDropGame extends Game {

	private GoogleInterface platformInterface;
	
	SpriteBatch batch;
	BitmapFont font;
	
	public ZebraDropGame(GoogleInterface aInterface){
		platformInterface = aInterface;
	}
	
	public GoogleInterface getGameInterface() {
		return platformInterface;	
	}
	
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/bell_goth_64.fnt"),
		         Gdx.files.internal("data/bell_goth_64_0.png"), false);


		this.setScreen(new MainMenuScreen(this));
	}
	
	public void render() {
		super.render();
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}