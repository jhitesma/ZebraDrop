package com.jthtml.zebraDrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jthtml.zebraDrop.GameScreen;


public class MainMenuScreen implements Screen {
	final ZebraDropGame game;
	private GoogleInterface platformInterface;

	TextureAtlas atlas;
	TextureRegion playControllerImage;
	TextureRegion backgroundImage;
	TextureRegion tapitImage;
	Rectangle tapItBounds;
	Rectangle achivementsBounds;
	Rectangle highLevelBounds;
	Rectangle highScoreBounds;
	Rectangle loginBounds;
	Rectangle touchSpot;
	Vector3 touchPos;

	OrthographicCamera camera;
	
	public MainMenuScreen(final ZebraDropGame gam) {
		game = gam;
		platformInterface = game.getGameInterface();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, game.maxW, game.maxH);

		// Load textures for this screen
		atlas = new TextureAtlas(Gdx.files.internal("zdImages.atlas"));
		playControllerImage = atlas.findRegion("ic_play_games_badge_green");
		backgroundImage = atlas.findRegion("background");
		tapitImage = atlas.findRegion("tapit");

		// Setup our Bounds
		tapItBounds = new Rectangle();
		tapItBounds.width = 365;
		tapItBounds.height = 195;
		tapItBounds.x = (game.maxW/2) - 182 ;
		tapItBounds.y = (game.maxH/2) - 97 ;

		achivementsBounds = new Rectangle();
		achivementsBounds.width = 400;
		achivementsBounds.height = 64;
		achivementsBounds.x = 35;
		achivementsBounds.y = game.maxH-150;		

		highScoreBounds = new Rectangle();
		highScoreBounds.width = 400;
		highScoreBounds.height = 64;
		highScoreBounds.x = achivementsBounds.x ;
		highScoreBounds.y = achivementsBounds.y - 75;

		highLevelBounds = new Rectangle();
		highLevelBounds.width = 400;
		highLevelBounds.height = 64;
		highLevelBounds.x = achivementsBounds.x ;
		highLevelBounds.y = highScoreBounds.y-75;
		
		loginBounds = new Rectangle();
		loginBounds.width = 200;
		loginBounds.height = 64;
		loginBounds.x = achivementsBounds.x ;
		loginBounds.y = highLevelBounds.y-75;	
		
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
	
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		if(Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			touchSpot.x = touchPos.x;
			touchSpot.y = touchPos.y;
			if (touchSpot.overlaps(tapItBounds)) {
				game.setScreen(new GameScreen(game));
				dispose();
			}
			if (touchSpot.overlaps(highScoreBounds)) {
				platformInterface.getScores();
				dispose();
			}
			if (touchSpot.overlaps(highLevelBounds)) {
				platformInterface.getLevels();
				dispose();
			}
			if (touchSpot.overlaps(achivementsBounds)) {
				platformInterface.getAchievements();
				dispose();
			}
			if (touchSpot.overlaps(loginBounds)) {
				if (platformInterface.getSignedIn()) {
					platformInterface.LogOut();
				} else {
					platformInterface.Login();
				}
			}
		}
		
		
		game.batch.begin();
		game.batch.draw(backgroundImage, 0, 0);
		game.font.draw(game.batch, Long.toString(game.points), 20, game.lineH);
		game.font.draw(game.batch, "HS: " + Long.toString(game.highScore) + " HL: " + Long.toString(game.highLevel),game.maxW/2 - 160, game.lineH);
		game.font.draw(game.batch, "Level: " + Integer.toString(game.level), game.maxW-(8*30), game.lineH);		
		game.batch.draw(tapitImage, tapItBounds.x, tapItBounds.y);				
		game.batch.draw(playControllerImage, achivementsBounds.x, achivementsBounds.y);
		game.batch.draw(playControllerImage, highScoreBounds.x, highScoreBounds.y);
		game.batch.draw(playControllerImage, loginBounds.x, loginBounds.y);
		game.batch.draw(playControllerImage, highLevelBounds.x, highLevelBounds.y);
		game.font.draw(game.batch, "ZEBRA DROP!!!", tapItBounds.x + tapItBounds.width, game.maxH-100);
		game.font.draw(game.batch, "Achievements", achivementsBounds.x + 70, achivementsBounds.y + achivementsBounds.height - 12);
		game.font.draw(game.batch, "High Scores", highScoreBounds.x + 70, highScoreBounds.y + highScoreBounds.height - 12);		
		game.font.draw(game.batch, "High Levels", highLevelBounds.x + 70, highLevelBounds.y + highLevelBounds.height - 12);		
		if (platformInterface.getSignedIn()) {
			game.font.draw(game.batch, "Logout", loginBounds.x + 70, loginBounds.y + loginBounds.height - 12);		
		} else {
			game.font.draw(game.batch, "Login", loginBounds.x + 70, loginBounds.y + loginBounds.height - 12);		
		}	
		game.batch.end();
			
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		game.rainMusic.stop();		
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
		atlas.dispose();

	}

}
