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


public class GameOverScreen implements Screen {
	final ZebraDropGame game;
	private GoogleInterface platformInterface;
	
	TextureRegion playControllerImage;
	TextureRegion backgroundImage;
	TextureRegion tapitImage;
	TextureRegion gameOverImage;
	Rectangle tapItBounds;
	Rectangle achivementsBounds;
	Rectangle highLevelBounds;
	Rectangle highScoreBounds;
	Rectangle loginBounds;	
	Rectangle touchSpot;
	Vector3 touchPos;
	Boolean newPref;

	OrthographicCamera camera;
	
	public GameOverScreen(final ZebraDropGame gam) {
		game = gam;
		platformInterface = game.getGameInterface();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, game.maxW, game.maxH);

		// Load textures for this screen
		playControllerImage = game.atlas.findRegion("ic_play_games_badge_green");
		backgroundImage = game.atlas.findRegion("background");
		tapitImage = game.atlas.findRegion("tapit");
		gameOverImage = game.atlas.findRegion("gameover");
		newPref = false;
		
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

		game.stateTime = 0f;  	

		if (game.points > game.highScore) {
			game.highScore = game.points;
			game.prefs.putInteger("highScore", game.highScore);
			newPref = true;
			if (platformInterface.getSignedIn()) {
				platformInterface.submitScore(game.highScore);
			}
		}
		
		
		if (game.level > game.highLevel) {
			game.highLevel = game.level;
			game.prefs.putInteger("highLevel", game.highLevel);
			newPref = true;
			if (platformInterface.getSignedIn()) {
				platformInterface.submitLevel(game.highLevel);
			}
		}

		if (platformInterface.getSignedIn()) {

			platformInterface.incrementAchievement("CgkIx7_-lMMSEAIQAg",1);

			if (game.points == 1337) {
				platformInterface.unlockAchievement("CgkIx7_-lMMSEAIQAQ");
			}
			
			if (game.points >= 6826) {
				platformInterface.unlockAchievement("CgkIx7_-lMMSEAIQBg");			
			}
			
			if (game.level >= 15) {
				platformInterface.unlockAchievement("CgkIx7_-lMMSEAIQBg");			
			}

			if (game.level == 1) {
				platformInterface.unlockAchievement("CgkIx7_-lMMSEAIQBw");
			}			
		}
		
		if (newPref) game.prefs.flush();
	
	}
	
	
	@Override
	public void render(float delta) {		
		// GAME OVER
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
				game.bucketBounds.height = 212;
				game.dropRate = game.minDropRate;
				game.dropSpeed = game.minDropSpeed;
				game.neededDrops = 10;
				game.level = 1;
				game.dropDir = 1;
				game.ptVal = 1;
				game.points = 0;
				game.bonus = 0;
				game.buckets = 3;	
				game.zebras = new Array<Zebra>();
				game.gameState = game.gameState.Normal;	
				game.dropCount = 0;
				game.numDropped = 0;
				game.setScreen(new GameScreen(game));
				dispose();
			}
			if (touchSpot.overlaps(highScoreBounds)) {
				platformInterface.getScores();
			}
			if (touchSpot.overlaps(highLevelBounds)) {
				platformInterface.getLevels();
			}
			if (touchSpot.overlaps(achivementsBounds)) {
				platformInterface.getAchievements();
			}
			if (touchSpot.overlaps(loginBounds)) {
				if (platformInterface.getSignedIn()) {
					platformInterface.LogOut();
				} else {
					platformInterface.Login();
				}
			}
		}

		// begin a new batch and draw the bucket and
		// all drops
		game.batch.begin();
		game.font.draw(game.batch, Long.toString(game.points), 20, game.lineH);
		game.font.draw(game.batch, "HS: " + Long.toString(game.highScore) + " HL: " + Long.toString(game.highLevel), game.maxW/2 - 160, game.lineH);
		game.font.draw(game.batch, "Level: " + Integer.toString(game.level), game.maxW-(8*30), game.lineH);	
		game.batch.draw(playControllerImage, achivementsBounds.x, achivementsBounds.y);
		game.batch.draw(playControllerImage, highScoreBounds.x, highScoreBounds.y);
		game.batch.draw(playControllerImage, loginBounds.x, loginBounds.y);
		game.batch.draw(playControllerImage, highLevelBounds.x, highLevelBounds.y);
		if (newPref) {
			game.font.draw(game.batch,"^^NEW RECORDS^^", loginBounds.x+40, loginBounds.y + loginBounds.height - 115);
		}
		game.font.draw(game.batch, "ZEBRA DROP!!!", tapItBounds.x + tapItBounds.width, game.maxH-100);
		game.font.draw(game.batch, "Achievements", achivementsBounds.x + 70, achivementsBounds.y + achivementsBounds.height - 12);
		game.font.draw(game.batch, "High Scores", highScoreBounds.x + 70, highScoreBounds.y + highScoreBounds.height - 12);		
		game.font.draw(game.batch, "High Levels", highLevelBounds.x + 70, highLevelBounds.y + highLevelBounds.height - 12);		
		if (platformInterface.getSignedIn()) {
			game.font.draw(game.batch, "Logout", loginBounds.x + 70, loginBounds.y + loginBounds.height - 12);		
		} else {
			game.font.draw(game.batch, "Login", loginBounds.x + 70, loginBounds.y + loginBounds.height - 12);		
		}			
		game.batch.draw(gameOverImage, (game.maxW/2) - 182, (game.maxH/2) - 97);
		game.batch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
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
		
	}

}
