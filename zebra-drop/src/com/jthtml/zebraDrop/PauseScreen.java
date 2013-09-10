package com.jthtml.zebraDrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jthtml.zebraDrop.GameScreen;


public class PauseScreen implements Screen {
	final ZebraDropGame game;

	TextureRegion backgroundImage;
	TextureRegion tapitImage;
	Rectangle tapItBounds;
	Rectangle touchSpot;
	Vector3 touchPos;

	OrthographicCamera camera;
	
	public PauseScreen(final ZebraDropGame gam) {
		game = gam;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, game.maxW, game.maxH);

		// Load textures for this screen
		backgroundImage = game.atlas.findRegion("background");
		tapitImage = game.atlas.findRegion("tapit");

		// Setup our Bounds
		tapItBounds = new Rectangle();
		tapItBounds.width = 365;
		tapItBounds.height = 195;
		tapItBounds.x = (game.maxW/2) - 182 ;
		tapItBounds.y = (game.maxH/2) - 97 ;

		// Setup our touchspot
		touchSpot = new Rectangle();
		touchSpot.width = 16;
		touchSpot.height = 16;
		
		touchPos = new Vector3();
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.batch.setProjectionMatrix(camera.combined);

		if(Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			touchSpot.x = touchPos.x;
			touchSpot.y = touchPos.y;
			if (touchSpot.overlaps(tapItBounds)) {
				game.zebras = new Array<Zebra>();
				game.gameState = game.gameState.Normal;			
				game.setScreen(new GameScreen(game));
				dispose();
			}

		}

		// begin a new batch and draw the bucket and
		// all drops
		game.batch.begin();
		game.batch.draw(backgroundImage, 0, 0);
		game.font.draw(game.batch, Long.toString(game.points), 20, game.lineH);
		game.font.draw(game.batch, "HS: " + Long.toString(game.highScore) + " HL: " + Long.toString(game.highLevel), game.maxW/2 - 160, game.lineH);
		game.font.draw(game.batch, "Level: " + Integer.toString(game.level), game.maxW-(8*30), game.lineH);		
		game.batch.draw(tapitImage, tapItBounds.x, tapItBounds.y);				
		game.batch.end();		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
