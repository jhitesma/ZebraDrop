package com.jthtml.zebraDrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PauseScreen extends ScreenAdapter {
	final ZebraDropGame game;

	private Stage stage;
	private Skin skin;
	private Table mainTable;
	private Image backgroundImage;
	private TextButton continueButton;
	private Label scoreLabel;
	private Label highScoreLabel;
	private Label levelLabel;
	private Label dropsLabel;
	
	public PauseScreen(final ZebraDropGame game) {
		this.game = game;
		
		// Create stage with FitViewport for responsive scaling
		stage = new Stage(new FitViewport(game.maxW, game.maxH));
		Gdx.input.setInputProcessor(stage);
		
		// Create skin
		skin = new Skin();
		skin.add("font", game.font);
		
		// Create button style
		TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(game.atlas.findRegion("tapit"));
		buttonStyle.font = game.font;
		skin.add("default", buttonStyle);
		
		// Create label style
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = game.font;
		skin.add("default", labelStyle);
		
		createUI();
	}
	
	private void createUI() {
		// Create background
		backgroundImage = new Image(game.atlas.findRegion("background"));
		backgroundImage.setFillParent(true);
		stage.addActor(backgroundImage);
		
		// Create main table for layout
		mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);
		
		// Create continue button
		continueButton = new TextButton("", skin);
		continueButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.zebras = new Array<Zebra>();
				game.gameState = ZebraDropGame.State.Normal;			
				game.setScreen(new GameScreen(game));
				dispose();
			}
		});
		
		// Create labels
		scoreLabel = new Label("", skin);
		highScoreLabel = new Label("", skin);
		levelLabel = new Label("", skin);
		dropsLabel = new Label("", skin);
		
		// Layout the UI
		layoutUI();
	}
	
	private void layoutUI() {
		// Bottom section with score info like original
		Table bottomTable = new Table();
		bottomTable.add(levelLabel).left().padLeft(20);           // Level on LEFT
		bottomTable.add(highScoreLabel).center().expandX();
		bottomTable.add(dropsLabel).right().padRight(20);         // Drops needed on RIGHT
		
		// Main layout
		mainTable.center();
		mainTable.add(continueButton).size(387, 485).center().expand().row();
		mainTable.add(bottomTable).fillX().bottom().padBottom(20);
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Update labels with current game state
		levelLabel.setText("Level: " + Integer.toString(game.level));
		highScoreLabel.setText("HS: " + Long.toString(game.highScore) + " HL: " + Long.toString(game.highLevel));
		dropsLabel.setText(Long.toString(game.neededDrops) + " to drop");

		// Update and render stage
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}
}