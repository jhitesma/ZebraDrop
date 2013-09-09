package com.jthtml.zebraDrop;

import com.badlogic.gdx.Screen;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Pool;
import com.jthtml.zebraDrop.GoogleInterface;


public class GameScreen implements Screen {

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

	Vector3 touchPos;
	
	TextureRegion zebraFrame;
	private Animation zebraAnimation;
	
	TextureRegion ufoFrame;
	private Animation ufoAnimation;

	// Rectangle pool used for drops
	// good to avoid instantiation each frame
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	
	
	public GameScreen(ZebraDropGame gam){
		game = gam;
		platformInterface = game.getGameInterface();

		// load the images for the droplet and the bucket, 64x64 pixels each
		bucketImage = game.atlas.findRegion("bucket");
		backgroundImage = game.atlas.findRegion("background");
		
		game.stateTime = 0f;  
		TextureRegion[] zebraFrames = new TextureRegion[16];
		for (int i = 0 ; i < 16  ; i++) {
			if (i+1 < 10) {
				zebraFrames[i] = game.atlas.findRegion("hero0" + (i+1));	
			}
			else {
				zebraFrames[i] = game.atlas.findRegion("hero" + (i+1));
			}
		}
		zebraAnimation = new Animation(ZEBRA_FRAME_DURATION, zebraFrames);
		
		TextureRegion[] ufoFrames = new TextureRegion[12];
		for (int i = 0 ; i < 12  ; i++) {
			ufoFrames[i] = game.atlas.findRegion("ufo" + (i+1));
			Gdx.app.log("texture_load", i + " - ufo" + (i+1));
		}
		ufoAnimation = new Animation(UFO_FRAME_DURATION, ufoFrames);
		
		
		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, game.maxW, game.maxH);		

		// setup our touchspot
		touchSpot = new Rectangle();
		touchSpot.width = 16;
		touchSpot.height = 16;

		touchPos = new Vector3();
		
		// create the dropper
		dropper = new Rectangle();
		dropper.x = game.maxW / 2 - 64 /2; // start out centered
		dropper.y = game.maxH - 120; // 
		dropper.width = 64;
		dropper.height = 64;
		
	}

	private void newLevel() {
		game.gameState = game.gameState.Paused;		   
		if (game.neededDrops == ((game.level -1) *10) /2) {
		} else {
			game.level = game.level + 1;
		}
		game.dropRate = game.dropRate / 2 ;
		if (game.dropRate < game.maxDropRate) {game.dropRate = game.maxDropRate;}
		//dropSpeed = dropSpeed + 50;
		game.dropSpeed = game.minDropSpeed + (game.level * 50);
		if (game.dropSpeed > game.maxDropSpeed) {game.dropSpeed = game.maxDropSpeed;}
		game.dropCount = 0;
		game.neededDrops = game.level * 10;
		game.ptVal = game.ptVal + 1;
		if (game.ptVal > 8) {game.ptVal = 8;}
		game.numDropped = 0;
	}

	private void dropLevel() {
		game.gameState = game.gameState.Paused;
		game.buckets = game.buckets - 1;
		if (game.buckets < 1) { game.buckets = 0;}

		//		dropRate = minDropRate;
		game.dropRate = game.dropRate * 2;
		if (game.dropRate > game.minDropRate) { game.dropRate = game.minDropRate;}

//		dropSpeed = dropSpeed - 50;
		game.dropSpeed = game.minDropSpeed;
		if (game.dropSpeed < game.minDropSpeed) { game.dropSpeed = game.minDropSpeed;}

		game.bucketBounds.height = game.bucketBounds.height - 84;
		//		ptVal = 1;
		game.ptVal = game.ptVal - 1;
		if (game.ptVal < 1) game.ptVal = 1;
		game.dropCount = 0;
		game.neededDrops = 10;
		if (game.neededDrops < 10) { game.neededDrops = 10;}
		game.numDropped = 0;
	}


	private void spawnRaindrop() {
		if (game.numDropped < game.neededDrops) {
			Rectangle raindrop = rectPool.obtain();;
			raindrop.x = dropper.x;
			raindrop.y = dropper.y;
			raindrop.width = 52;
			raindrop.height = 42;
			game.raindrops.add(raindrop);
			lastDropTime = TimeUtils.nanoTime();
			game.numDropped++;
		}
	}

	@Override
	public void dispose() {
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void render(float delta) {

		if (game.buckets > 0) {
			// We still have lives available so run the game loop
			
			if (game.gameState == game.gameState.Paused) {
				game.setScreen(new PauseScreen(game));
				dispose();
			} else {
				// clear the screen with a dark blue color. The
				// arguments to glClearColor are the red, green
				// blue and alpha component in the range [0,1]
				// of the color to be used to clear the screen.
				Gdx.gl.glClearColor(0, 0, 0.2f, 1);
				Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

				game.stateTime += Gdx.graphics.getDeltaTime(); 
				zebraFrame = zebraAnimation.getKeyFrame(game.stateTime, true);
				ufoFrame = ufoAnimation.getKeyFrame(game.stateTime, true);
				
				// tell the camera to update its matrices.
				camera.update();

				// tell the SpriteBatch to render in the
				// coordinate system specified by the camera.
				game.batch.setProjectionMatrix(camera.combined);

				// begin a new batch and draw the bucket and
				// all drops
				game.batch.begin();
				game.batch.draw(backgroundImage, 0, 0);
				game.batch.draw(bucketImage, game.bucket.x, game.bucket.y);

				if (game.buckets >= 2) {
					game.batch.draw(bucketImage, game.bucket.x, game.bucket.y + 84);
				}
				if (game.buckets >= 3) {
					game.batch.draw(bucketImage, game.bucket.x, game.bucket.y + 168);
				}
				
				game.batch.draw(ufoFrame, dropper.x, dropper.y);
				for(Rectangle raindrop: game.raindrops) {
					game.batch.draw(zebraFrame, raindrop.x, raindrop.y, 32, 25, 64, 51, 1, 1, -60);
//					game.batch.draw(zebraFrame, raindrop.x, raindrop.y);
				}
//				font.draw(game.batch, "dropCount: " + Integer.toString(dropCount),10,game.lineH*2);
//				font.draw(game.batch, "neededDrops: " + Long.toString(neededDrops),10,game.lineH*3);
//				font.draw(game.batch, "dropRate: " + Long.toString(dropRate), 10, game.lineH*4);
//				font.draw(game.batch, "dropSpeed: " + Long.toString(dropSpeed), 10, game.lineH*5);

				game.font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), game.maxW/2 - 200, game.lineH);
				
				game.font.draw(game.batch, Long.toString(game.points), 20, game.lineH);
				game.font.draw(game.batch, "Level: " + Integer.toString(game.level), game.maxW-(8*30), game.lineH);

				game.batch.end();

				// process user input
				if(Gdx.input.isTouched()) {
					touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
					camera.unproject(touchPos);
					game.bucket.x = touchPos.x - 64 / 2;
				}
				if(Gdx.input.isKeyPressed(Keys.LEFT)) game.bucket.x -= 200 * Gdx.graphics.getDeltaTime();
				if(Gdx.input.isKeyPressed(Keys.RIGHT)) game.bucket.x += 200 * Gdx.graphics.getDeltaTime();

				// make sure the bucket stays within the screen bounds
				if(game.bucket.x < 0) game.bucket.x = 0;
				if(game.bucket.x > game.maxW - 64) game.bucket.x = (game.maxW - 64);

				game.bucketBounds.x = game.bucket.x;
				
				// check if we need to create a new raindrop
				if(TimeUtils.nanoTime() - lastDropTime > game.dropRate) spawnRaindrop();

				// move the raindrops, remove any that are beneath the bottom edge of
				// the screen or that hit the bucket. In the later case we play back
				// a sound effect as well.


				if (MathUtils.random(0,10) > 7) {
					if (game.dropDir==1) game.dropDir = 0; else game.dropDir = 1;
				}

				if (game.dropDir==1) {
					dropper.x -= MathUtils.random(200 * game.level, 250 * game.level) * Gdx.graphics.getDeltaTime();
				} else {
					dropper.x += MathUtils.random(200 * game.level, 250 * game.level) * Gdx.graphics.getDeltaTime();	      
				}

				// make sure the dropper stays within the screen bounds
				if(dropper.x < 0) dropper.x = 0;
				if(dropper.x > game.maxW - 64) dropper.x = (game.maxW - 64);

				Iterator<Rectangle> iter = game.raindrops.iterator();
				while(iter.hasNext()) {
					Rectangle raindrop = iter.next();
					raindrop.y -= game.dropSpeed * Gdx.graphics.getDeltaTime();
					if(raindrop.y + 64 < 0) {
						iter.remove();
						rectPool.free(raindrop);
						dropLevel();
					}
					if(raindrop.overlaps(game.bucketBounds)) {
						game.dropSound.play();
						iter.remove();
						rectPool.free(raindrop);
						game.dropCount++;
						game.points = game.points + game.ptVal;
						game.bonus = game.bonus + game.ptVal;

						if (platformInterface.getSignedIn()) {
							if (game.points >= 3000) {
								platformInterface.unlockAchievement("CgkIx7_-lMMSEAIQAw");
							}
	
							if (game.points >= 10000) {
								platformInterface.unlockAchievement("CgkIx7_-lMMSEAIQBA");
							}
						}
							
						if (game.bonus >= 1000) {
							if (game.buckets < 3) {
								if (platformInterface.getSignedIn()) {
									platformInterface.unlockAchievement("CgkIx7_-lMMSEAIQBQ");
								}
								game.buckets++;
								game.bucketBounds.height = game.bucketBounds.height + 84;
								if (game.bucketBounds.height > 212) {
									game.bucketBounds.height = 212;
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
}