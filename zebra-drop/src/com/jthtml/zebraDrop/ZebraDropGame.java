package com.jthtml.zebraDrop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.jthtml.zebraDrop.GoogleInterface;

public class ZebraDropGame extends Game {

	private GoogleInterface platformInterface;
	
	enum State {
		Paused,
		Normal,
		Reduced
	}

	int maxW;
	int maxH;
	int points;
	int level;
	int lineH;
	int highScore;
	int highLevel;

	SpriteBatch batch;
	BitmapFont font;
	State gameState;
	Sound dropSound;
	Music rainMusic;
	Preferences prefs;

	Array<Rectangle> raindrops;

	
	public ZebraDropGame(GoogleInterface aInterface){
		// interface for google play game services
		platformInterface = aInterface;

		// Screen size
		maxW = 1280;
		maxH = 720;

		// gameplay variables
		points = 0;
		level = 1;
		
		// Set Line height
		lineH = 70;
		
		// Array for our "raindrops"
		raindrops = new Array<Rectangle>();

	}
	
	public GoogleInterface getGameInterface() {
		return platformInterface;	
	}
	
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/bell_goth_64.fnt"),
		         Gdx.files.internal("data/bell_goth_64_0.png"), false);
		gameState = State.Paused;		

		// load some sounds
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rainMusic.setLooping(true);		

		// Load high Score and high level
		prefs = Gdx.app.getPreferences("My Preferences");
		highScore = prefs.getInteger("highScore");
		highLevel = prefs.getInteger("highLevel");
		
		this.setScreen(new MainMenuScreen(this));
	}
	
	public void render() {
		super.render();
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}
}