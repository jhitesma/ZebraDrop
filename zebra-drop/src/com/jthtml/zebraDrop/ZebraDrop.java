package com.jthtml.zebraDrop;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class ZebraDrop implements ApplicationListener {
	   Texture dropImage;
	   Texture bucketImage;
	   Texture dropperImage;
	   Sound dropSound;
	   Music rainMusic;
	   SpriteBatch batch;
	   OrthographicCamera camera;
	   Rectangle bucket;
	   Rectangle dropper;
	   Array<Rectangle> raindrops;
	   long lastDropTime;
	   long neededDrops;
	   long dropRate;
	   int dropCount;
	   int level;
	   BitmapFont font;
	   int dropDir;
	   
	   
	   @Override
	   public void create() {
	      // load the images for the droplet and the bucket, 64x64 pixels each
	      dropImage = new Texture(Gdx.files.internal("droplet.png"));
	      bucketImage = new Texture(Gdx.files.internal("bucket.png"));
	      dropperImage = new Texture(Gdx.files.internal("bucket.png"));      
	      
	      // load the drop sound effect and the rain background "music"
	      dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
	      rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
	      
	      // start the playback of the background music immediately
	      rainMusic.setLooping(true);
	      rainMusic.play();
	      
	      // create the camera and the SpriteBatch
	      camera = new OrthographicCamera();
	      camera.setToOrtho(false, 800, 480);
	      batch = new SpriteBatch();
	      
	      // create a Rectangle to logically represent the bucket
	      bucket = new Rectangle();
	      bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
	      bucket.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
	      bucket.width = 64;
	      bucket.height = 64;
	      
	      // create the dropper
	      dropper = new Rectangle();
	      dropper.x = 800 / 2 - 64 /2; // start out centered
	      dropper.y = 480 - 74; // 
	      dropper.width = 64;
	      dropper.height = 64;
	      	      
	      font = new BitmapFont();
	      
          // create the raindrops array and spawn the first raindrop
	      dropRate = 1000000000;
	      neededDrops = 10;
	      level = 1;
	      dropDir = 1;
	      raindrops = new Array<Rectangle>();
	      spawnRaindrop();
	   }
	   
	   private void newLevel() {
		   level = level + 1;
		   dropRate = dropRate / 2 ;
		   dropCount = 0;
		   neededDrops = neededDrops + (neededDrops * 2);
	   }
	   
	   private void dropLevel() {
		   dropRate = dropRate * 2;
		   dropCount = 0;
		   neededDrops = neededDrops - (neededDrops /2);
	   }
	   
	   
	   private void spawnRaindrop() {
	      Rectangle raindrop = new Rectangle();
//	      raindrop.x = MathUtils.random(0, 800-64);
//	      raindrop.y = 480;
	      raindrop.x = dropper.x;
	      raindrop.y = dropper.y;	      
	      raindrop.width = 64;
	      raindrop.height = 64;
	      raindrops.add(raindrop);
	      lastDropTime = TimeUtils.nanoTime();
	   }

	   @Override
	   public void render() {
	      // clear the screen with a dark blue color. The
	      // arguments to glClearColor are the red, green
	      // blue and alpha component in the range [0,1]
	      // of the color to be used to clear the screen.
	      Gdx.gl.glClearColor(0, 0, 0.2f, 1);
	      Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	      
	      // tell the camera to update its matrices.
	      camera.update();
	      
	      // tell the SpriteBatch to render in the
	      // coordinate system specified by the camera.
	      batch.setProjectionMatrix(camera.combined);
	      
	      // begin a new batch and draw the bucket and
	      // all drops
	      batch.begin();
	      batch.draw(bucketImage, bucket.x, bucket.y);
	      batch.draw(dropperImage, dropper.x, dropper.y);
	      for(Rectangle raindrop: raindrops) {
	         batch.draw(dropImage, raindrop.x, raindrop.y);
	      }
	      font.draw(batch, "Level: " + Integer.toString(level), 10, 15);
	      font.draw(batch, "dropCount: " + Integer.toString(dropCount),10,30);
	      font.draw(batch, "neededDrops: " + Long.toString(neededDrops),10,45);
	      font.draw(batch,  "dropRate: " + Long.toString(dropRate), 10, 60);
	      batch.end();
	      
	      // process user input
	      if(Gdx.input.isTouched()) {
	         Vector3 touchPos = new Vector3();
	         touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	         camera.unproject(touchPos);
	         bucket.x = touchPos.x - 64 / 2;
	      }
	      if(Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
	      if(Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();
	      
	      // make sure the bucket stays within the screen bounds
	      if(bucket.x < 0) bucket.x = 0;
	      if(bucket.x > 800 - 64) bucket.x = (800 - 64);
	      
	      // check if we need to create a new raindrop
	      if(TimeUtils.nanoTime() - lastDropTime > dropRate) spawnRaindrop();
	      
	      // move the raindrops, remove any that are beneath the bottom edge of
	      // the screen or that hit the bucket. In the later case we play back
	      // a sound effect as well.

	      
	      if (MathUtils.random(0,10) > 7) {
	    	  if (dropDir==1) dropDir = 0; else dropDir = 1;
	      }
	      
	    	      
	      if (dropDir==1) {
	    	  dropper.x -= MathUtils.random(200 * level, 250 * level) * Gdx.graphics.getDeltaTime();
	      } else {
	    	  dropper.x += MathUtils.random(200 * level, 250 * level) * Gdx.graphics.getDeltaTime();	      
	      }

	      // make sure the dropper stays within the screen bounds
	      if(dropper.x < 0) dropper.x = 0;
	      if(dropper.x > 800 - 64) dropper.x = (800 - 64);
	      
	      Iterator<Rectangle> iter = raindrops.iterator();
	      while(iter.hasNext()) {
	         Rectangle raindrop = iter.next();
	         raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
	         if(raindrop.y + 64 < 0) {
	        	 iter.remove();
	        	 dropLevel();
	         }
	         if(raindrop.overlaps(bucket)) {
	        
	        	dropSound.play();
	            iter.remove();
	            dropCount++;
	            if (dropCount >= neededDrops) {
	            	newLevel();
	            }
	         }
	      }
	   }
	   
	   @Override
	   public void dispose() {
	      // dispose of all the native resources
	      dropImage.dispose();
	      bucketImage.dispose();
	      dropSound.dispose();
	      rainMusic.dispose();
	      batch.dispose();
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
	}