package com.jthtml.zebraDrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jthtml.zebraDrop.GameScreen;


public class PauseScreen implements Screen {
	final ZebraDropGame game;

	OrthographicCamera camera;
	
	public PauseScreen(final ZebraDropGame gam) {
		game = gam;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
	
		game.batch.begin();
		game.font.draw(game.batch, "Catch your breath!", 100, 150);
		game.font.draw(game.batch, "Tap Anywhere to begin", 100, 100);
		game.batch.end();
		
		if(Gdx.input.isTouched()){
			game.setScreen(new GameScreen(game));
			dispose();
		}
		
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
