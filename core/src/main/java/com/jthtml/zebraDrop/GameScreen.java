package com.jthtml.zebraDrop;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.ScreenAdapter;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Pool;

public class GameScreen extends ScreenAdapter {

	private GoogleInterface platformInterface;
	
	private static final float ZEBRA_FRAME_DURATION = 0.02f;
	private static final float UFO_FRAME_DURATION = 0.08f;
	
	ZebraDropGame game;
	TextureRegion bucketImage;
	TextureRegion backgroundImage;
	OrthographicCamera camera;
	Rectangle dropper;
	Rectangle touchSpot;
	long lastDropTime;
	
	// Cached strings to avoid allocation in render loop
	private StringBuilder fpsStringBuilder;
	private StringBuilder levelStringBuilder;
	private int lastFps = -1;
	private int lastLevel = -1;
	
	// Cached random values to avoid expensive random calls every frame
	private float cachedDropperSpeed = 0f;
	private long lastDirectionChange = 0;
	private static final long DIRECTION_CHANGE_INTERVAL = 100_000_000L; // 0.1 seconds in nanoseconds

	Vector3 touchPos;
	
	
	TextureRegion zebraFrame;
	private Animation<TextureRegion> zebraAnimation;
	
	TextureRegion ufoFrame;
	private Animation<TextureRegion> ufoAnimation;

	// Rectangle pool used for drops - good to avoid instantiation each frame
	private Pool<Zebra> zebraPool = new Pool<Zebra>() {
		@Override
		protected Zebra newObject() {
			return new Zebra();
		}
		
		@Override
		protected void reset(Zebra zebra) {
			zebra.reset();
		}
	};
	
	
	public GameScreen(ZebraDropGame game){
		this.game = game;
		platformInterface = game.getGameInterface();

		// Load images through centralized atlas (managed by AssetManager)
		TextureAtlas atlas = game.getAtlas();
		bucketImage = atlas.findRegion("bucket");
		backgroundImage = atlas.findRegion("background");
		
		game.stateTime = 0f;  
		Array<TextureRegion> zebraFrames = new Array<TextureRegion>();
		for (int i = 0 ; i < 16  ; i++) {
			if (i+1 < 10) {
				zebraFrames.add(atlas.findRegion("hero0" + (i+1)));	
			}
			else {
				zebraFrames.add(atlas.findRegion("hero" + (i+1)));
			}
		}
		zebraAnimation = new Animation<TextureRegion>(ZEBRA_FRAME_DURATION, zebraFrames);
		
		Array<TextureRegion> ufoFrames = new Array<TextureRegion>();
		for (int i = 0 ; i < 12  ; i++) {
			ufoFrames.add(atlas.findRegion("ufo" + (i+1)));
			Gdx.app.log("texture_load", i + " - ufo" + (i+1));
		}
		ufoAnimation = new Animation<TextureRegion>(UFO_FRAME_DURATION, ufoFrames);
		
		
		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, game.maxW, game.maxH);		

		// setup our touchspot
		touchSpot = new Rectangle();
		touchSpot.width = GameConstants.TOUCH_SPOT_SIZE;
		touchSpot.height = GameConstants.TOUCH_SPOT_SIZE;

		touchPos = new Vector3();
		
		// create the dropper
		dropper = new Rectangle();
		dropper.x = game.maxW / 2 - GameConstants.BUCKET_SIZE / 2; // start out centered
		dropper.y = game.maxH - GameConstants.UFO_DROP_HEIGHT; 
		dropper.width = GameConstants.BUCKET_SIZE;
		dropper.height = GameConstants.BUCKET_SIZE;
		
		// Initialize string builders for performance
		fpsStringBuilder = new StringBuilder("FPS: ");
		levelStringBuilder = new StringBuilder("Level: ");
	}
	
	private void drawHUD() {
		// Draw HUD text within existing batch (no separate batch needed)
		// Use original game.lineH height to match PauseScreen
		float textY = game.lineH; // Match original height
		
		// Update FPS with caching to avoid allocations
		int currentFps = Gdx.graphics.getFramesPerSecond();
		if (currentFps != lastFps) {
			fpsStringBuilder.setLength(5); // Reset to "FPS: "
			fpsStringBuilder.append(currentFps);
			lastFps = currentFps;
		}
		
		// Level display on far LEFT
		if (game.level != lastLevel) {
			levelStringBuilder.setLength(7); // Reset to "Level: "
			levelStringBuilder.append(game.level);
			lastLevel = game.level;
		}
		game.font.draw(game.batch, levelStringBuilder.toString(), 20, textY);
		
		// FPS in center-left
		game.font.draw(game.batch, fpsStringBuilder.toString(), game.maxW/2 - 200, textY);
		
		// Score next to FPS with proper spacing
		game.font.draw(game.batch, "Score: " + Long.toString(game.points), game.maxW/2 - 50, textY);
		
		// Drops needed display on far RIGHT
		game.font.draw(game.batch, Long.toString(game.neededDrops) + " to drop", game.maxW-(8*30), textY);
	}

	private void newLevel() {
		game.gameState = ZebraDropGame.State.Paused;		   
		if (game.neededDrops == ((game.level -1) *10) /2) {
		} else {
			game.level = game.level + 1;
		}
		game.dropRate = game.dropRate / 2 ;
		if (game.dropRate < game.maxDropRate) {game.dropRate = game.maxDropRate;}
		game.dropSpeed = game.minDropSpeed + (game.level * GameConstants.LEVEL_SPEED_INCREMENT);
		if (game.dropSpeed > game.maxDropSpeed) {game.dropSpeed = game.maxDropSpeed;}
		game.dropCount = 0;
		game.neededDrops = game.level * GameConstants.DROPS_PER_LEVEL_BASE;
		game.ptVal = game.ptVal + 1;
		if (game.ptVal > GameConstants.MAX_POINT_VALUE) {game.ptVal = GameConstants.MAX_POINT_VALUE;}
		game.numDropped = 0;
	}

	private void dropLevel() {
		game.gameState = ZebraDropGame.State.Paused;
		game.buckets = game.buckets - 1;
		if (game.buckets < 1) { game.buckets = 0;}

		game.dropRate = game.dropRate * 2;
		if (game.dropRate > game.minDropRate) { game.dropRate = game.minDropRate;}

		game.dropSpeed = game.minDropSpeed;
		if (game.dropSpeed < game.minDropSpeed) { game.dropSpeed = game.minDropSpeed;}

		game.bucketBounds.height = game.bucketBounds.height - GameConstants.BUCKET_STACK_HEIGHT;
		game.ptVal = game.ptVal - 1;
		if (game.ptVal < 1) game.ptVal = 1;
		game.dropCount = 0;
		game.neededDrops = GameConstants.DROPS_PER_LEVEL_BASE;
		if (game.neededDrops < GameConstants.DROPS_PER_LEVEL_BASE) { game.neededDrops = GameConstants.DROPS_PER_LEVEL_BASE;}
		game.numDropped = 0;
	}


	private void spawnRaindrop() {
		if (game.numDropped < game.neededDrops) {
			Zebra zebra = zebraPool.obtain();;
			zebra.position.x = dropper.x;
			zebra.position.y = dropper.y;
			zebra.bounds.width = 52;
			zebra.bounds.height = 42;
			zebra.stateTime = game.stateTime;
			zebra.setTweenManager(game.tweenManager);
			zebra.startRotation();
			game.zebras.add(zebra);
			lastDropTime = TimeUtils.nanoTime();
			game.numDropped++;
		}
	}


	@Override
	public void render(float delta) {
		game.tweenManager.update(Gdx.graphics.getDeltaTime());
		if (game.buckets > 0) {
			// We still have lives available so run the game loop
			
			if (game.gameState == ZebraDropGame.State.Paused) {
				game.setScreen(new PauseScreen(game));
				dispose();
			} else {
				// clear the screen with a dark blue color
				Gdx.gl.glClearColor(0, 0, 0.2f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

				ufoFrame = ufoAnimation.getKeyFrame(game.stateTime, true);
				
				// tell the camera to update its matrices.
				camera.update();

				// tell the SpriteBatch to render in the coordinate system specified by the camera.
				game.batch.setProjectionMatrix(camera.combined);

				// begin a new batch and draw the bucket and all drops
				game.batch.begin();
				game.batch.draw(backgroundImage, 0, 0);
				game.batch.draw(bucketImage, game.bucket.x, game.bucket.y);

				if (game.buckets >= 2) {
					game.batch.draw(bucketImage, game.bucket.x, game.bucket.y + GameConstants.BUCKET_STACK_HEIGHT);
				}
				if (game.buckets >= 3) {
					game.batch.draw(bucketImage, game.bucket.x, game.bucket.y + GameConstants.BUCKET_STACK_HEIGHT * 2);
				}
				
				game.batch.draw(ufoFrame, dropper.x, dropper.y);
				for(Zebra zebra: game.zebras) {
					zebra.update(Gdx.graphics.getDeltaTime());
					zebraFrame = zebraAnimation.getKeyFrame(zebra.stateTime, true);					
					game.batch.draw(zebraFrame, zebra.position.x, zebra.position.y, 32, 25, 64, 51, 1, 1, zebra.rotation);
				}

				// Draw HUD in same batch to avoid font rendering issues
				drawHUD();

				game.batch.end();

				// process user input
				if(Gdx.input.isTouched()) {
					touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
					camera.unproject(touchPos);
					game.bucket.x = touchPos.x - GameConstants.BUCKET_SIZE / 2;
				}
				if(Gdx.input.isKeyPressed(Keys.LEFT)) game.bucket.x -= (GameConstants.BUCKET_MOVEMENT_SPEED * game.level) * Gdx.graphics.getDeltaTime();
				if(Gdx.input.isKeyPressed(Keys.RIGHT)) game.bucket.x += (GameConstants.BUCKET_MOVEMENT_SPEED * game.level) * Gdx.graphics.getDeltaTime();

				// make sure the bucket stays within the screen bounds
				if(game.bucket.x < 0) game.bucket.x = 0;
				if(game.bucket.x > game.maxW - GameConstants.BUCKET_SIZE) game.bucket.x = (game.maxW - GameConstants.BUCKET_SIZE);

				game.bucketBounds.x = game.bucket.x;
				
				// check if we need to create a new raindrop
				if(TimeUtils.nanoTime() - lastDropTime > game.dropRate) spawnRaindrop();

				// move the raindrops, remove any that are beneath the bottom edge of
				// the screen or that hit the bucket. In the later case we play back
				// a sound effect as well.

				// Cached direction changes - only check every 0.1 seconds instead of every frame
				long currentTime = TimeUtils.nanoTime();
				if (currentTime - lastDirectionChange > DIRECTION_CHANGE_INTERVAL) {
					if (MathUtils.random(0,10) > 7) {
						if (game.dropDir==1) game.dropDir = 0; else game.dropDir = 1;
						// Recalculate speed when direction changes
						cachedDropperSpeed = MathUtils.random(GameConstants.BASE_MOVEMENT_SPEED * game.level, GameConstants.MOVEMENT_SPEED_RANGE * game.level);
					}
					lastDirectionChange = currentTime;
				}
				
				// Use cached speed instead of calculating random speed every frame
				if (cachedDropperSpeed == 0f) {
					// Initialize speed on first run
					cachedDropperSpeed = MathUtils.random(GameConstants.BASE_MOVEMENT_SPEED * game.level, GameConstants.MOVEMENT_SPEED_RANGE * game.level);
				}

				if (game.dropDir==1) {
					dropper.x -= cachedDropperSpeed * Gdx.graphics.getDeltaTime();
				} else {
					dropper.x += cachedDropperSpeed * Gdx.graphics.getDeltaTime();	      
				}

				// make sure the dropper stays within the screen bounds
				if(dropper.x < 0) dropper.x = 0;
				if(dropper.x > game.maxW - 64) dropper.x = (game.maxW - 64);

				Iterator<Zebra> iter = game.zebras.iterator();
				while(iter.hasNext()) {
					Zebra zebra = iter.next();
					zebra.position.y -= game.dropSpeed * Gdx.graphics.getDeltaTime();
					Vector2 pos = zebra.position;
					zebra.setPosition(pos);
					
					if(zebra.position.y + 64 < 0) {
						iter.remove();
						zebraPool.free(zebra);
						dropLevel();
					}
					
					// Optimized collision detection: only check zebras near the entire bucket stack
					// Skip expensive overlap check if zebra is clearly not near any bucket
					if (zebra.position.y <= game.bucketBounds.y + game.bucketBounds.height + 10 && 
						zebra.position.y + 64 >= game.bucket.y - 10) {  // Check against bottom bucket position
						
						// Quick horizontal bounding box pre-check before expensive overlaps() call
						if (zebra.position.x + 64 >= game.bucketBounds.x && 
							zebra.position.x <= game.bucketBounds.x + game.bucketBounds.width &&
							zebra.bounds.overlaps(game.bucketBounds)) {
							
							game.dropSound.play();
							iter.remove();
							zebraPool.free(zebra);
							game.dropCount++;
							game.points = game.points + game.ptVal;
							game.bonus = game.bonus + game.ptVal;

							if (platformInterface.getSignedIn()) {
								if (game.points >= GameConstants.SCORE_THRESHOLD_3000) {
									platformInterface.unlockAchievement(GameConstants.ACHIEVEMENT_SCORE_3000);
								}
		
								if (game.points >= GameConstants.SCORE_THRESHOLD_10000) {
									platformInterface.unlockAchievement(GameConstants.ACHIEVEMENT_SCORE_10000);
								}
							}
								
							if (game.bonus >= GameConstants.BONUS_THRESHOLD) {
								if (game.buckets < GameConstants.MAX_BUCKETS) {
									if (platformInterface.getSignedIn()) {
										platformInterface.unlockAchievement(GameConstants.ACHIEVEMENT_BUCKET_BONUS);
									}
									game.buckets++;
									game.bucketBounds.height = game.bucketBounds.height + GameConstants.BUCKET_STACK_HEIGHT;
									if (game.bucketBounds.height > GameConstants.DEFAULT_BUCKET_BOUNDS_HEIGHT) {
										game.bucketBounds.height = GameConstants.DEFAULT_BUCKET_BOUNDS_HEIGHT;
									}
								}
								game.bonus = 0;
							}
							if (game.dropCount >= game.neededDrops) {
								newLevel();
							}
						}
					}
				}
			}

		} else {
			game.setScreen(new GameOverScreen(game));
			dispose();
		}	
	}

	@Override
	public void show() {
		game.rainMusic.play();
	}

	@Override
	public void hide() {
		game.rainMusic.stop();
	}
	
	@Override
	public void dispose() {
		// No HUD resources to dispose of - using original manual drawing
	}
}