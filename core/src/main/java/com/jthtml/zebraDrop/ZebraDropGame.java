package com.jthtml.zebraDrop;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

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

	TweenManager tweenManager;
	SpriteBatch batch;
	BitmapFont font;
	State gameState;
	AssetManager assetManager;
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
	
	public AssetManager getAssetManager() {
		return assetManager;
	}
	
	public TextureAtlas getAtlas() {
		return atlas;
	}
	
	public void create() {
		// Initialize core systems
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		tweenManager = new TweenManager();
		Tween.registerAccessor(Zebra.class, new ZebraAccessor());
		gameState = State.Paused;

		// Load all assets through AssetManager
		loadAssets();
		
		// Get references to loaded assets
		atlas = assetManager.get("zdImages.atlas", TextureAtlas.class);
		dropSound = assetManager.get("drop.wav", Sound.class);
		rainMusic = assetManager.get("rain.mp3", Music.class);
		
		// Try to load font through AssetManager, with fallback for compatibility
		try {
			font = assetManager.get("data/hvd_poster_32.fnt", BitmapFont.class);
			// Ensure font is properly configured for rendering
			font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		} catch (Exception e) {
			// Fallback to manual loading if AssetManager fails
			System.out.println("AssetManager font loading failed, using manual loading: " + e.getMessage());
			font = new BitmapFont(Gdx.files.internal("data/hvd_poster_32.fnt"),
			         Gdx.files.internal("data/hvd_poster_32_0.png"), false);
			font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		}
		
		rainMusic.setLooping(true);

		// Load high Score and high level
		prefs = Gdx.app.getPreferences("My Preferences");
		highScore = prefs.getInteger("highScore");
		highLevel = prefs.getInteger("highLevel");
		
		this.setScreen(new MainMenuScreen(this));
	}
	
	private void loadAssets() {
		// Load texture atlas
		assetManager.load("zdImages.atlas", TextureAtlas.class);
		
		// Load audio assets
		assetManager.load("drop.wav", Sound.class);
		assetManager.load("rain.mp3", Music.class);
		
		// Load font through AssetManager for better compatibility
		assetManager.load("data/hvd_poster_32.fnt", BitmapFont.class);
		
		// Block until all assets are loaded
		assetManager.finishLoading();
	}
	
	public void render() {
		super.render();
	}

		
	public void dispose() {
		batch.dispose();
		font.dispose();
		assetManager.dispose(); // This will dispose all managed assets automatically
	}
}
