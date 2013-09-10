package com.jthtml.zebraDrop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.jthtml.zebraDrop.GoogleInterface;
import com.jthtml.zebraDrop.Zebra;


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
	long dropRate;
	long maxDropRate;
	long minDropRate;
	int dropSpeed;
	int maxDropSpeed;
	int minDropSpeed;
	int ptVal;
	int bonus;
	int buckets;
	int dropDir;
	int dropCount;
	int numDropped;
	long neededDrops;
	float stateTime;

	Rectangle bucket;
	Rectangle bucketBounds;

	SpriteBatch batch;
	BitmapFont font;
	State gameState;
	TextureAtlas atlas;
	Sound dropSound;
	Music rainMusic;
	Preferences prefs;

	Boolean signedIn;
	
	Array<Zebra> zebras;

	
	public ZebraDropGame(GoogleInterface aInterface){
		// interface for google play game services
		platformInterface = aInterface;
		
		// Screen size
		maxW = 1280;
		maxH = 720;

		// gameplay variables
		points = 0;
		level = 1;
		ptVal = 1;
		bonus = 0;
		buckets = 3;
		dropRate = 1000000000;
		minDropRate = 1000000000;
		maxDropRate = 80000000;
		dropSpeed = 200;
		minDropSpeed = 200;
		maxDropSpeed = 800;
		neededDrops = 10;
		stateTime = 0f;
		dropDir = 1;
		dropCount = 0;
		numDropped = 0;
		
		// Set Line height
		lineH = 70;
		
		// Array for our "raindrops"
		zebras = new Array<Zebra>();


		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		bucket.x = maxW / 2 - 64 / 2; // center the bucket horizontally
		bucket.y = 20 + lineH; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		bucket.width = 64;
		bucket.height = 64;
		
		// Default bounds for our bucket
		bucketBounds = new Rectangle();
		bucketBounds.width = 64;
		bucketBounds.height = 212;
		bucketBounds.x = bucket.x;
		bucketBounds.y = bucket.y;
	}
	
	public GoogleInterface getGameInterface() {
		return platformInterface;	
	}
	
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/bell_goth_64.fnt"),
		         Gdx.files.internal("data/bell_goth_64_0.png"), false);
		atlas = new TextureAtlas(Gdx.files.internal("zdImages.atlas"));
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
		atlas.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}
}