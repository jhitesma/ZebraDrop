package com.jthtml.zebraDrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen extends ScreenAdapter {
	final ZebraDropGame game;
	private GoogleInterface platformInterface;

	private Stage stage;
	private Skin skin;
	private Table mainTable;
	private Image backgroundImage;
	private Image logoImage;
	
	private TextButton playButton;
	private TextButton achievementsButton;
	private TextButton highScoreButton;
	private TextButton highLevelButton;
	private TextButton loginButton;
	
	private Label scoreLabel;
	private Label highScoreLabel;
	private Label levelLabel;
	
	public MainMenuScreen(final ZebraDropGame game) {
		this.game = game;
		platformInterface = game.getGameInterface();
		
		// Create stage with FitViewport for responsive scaling
		stage = new Stage(new FitViewport(game.maxW, game.maxH));
		Gdx.input.setInputProcessor(stage);
		
		// Create simple skin for buttons
		skin = new Skin();
		skin.add("font", game.font);
		
		// Create menu button style (no background - icon is handled in Table)
		TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle();
		menuButtonStyle.font = game.font;
		skin.add("menu", menuButtonStyle);
		
		// Create play button style (with tapit background)
		TextButton.TextButtonStyle playButtonStyle = new TextButton.TextButtonStyle();
		playButtonStyle.up = new TextureRegionDrawable(game.atlas.findRegion("tapit"));
		playButtonStyle.font = game.font;
		skin.add("play", playButtonStyle);
		
		// Create label style
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = game.font;
		skin.add("default", labelStyle);
		
		createUI();
	}
	
	private void createUI() {
		// Create background
		backgroundImage = new Image(game.atlas.findRegion("blurred_bg"));
		backgroundImage.setFillParent(true);
		stage.addActor(backgroundImage);
		
		// Create main table for layout
		mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);
		
		// Create logo
		logoImage = new Image(game.atlas.findRegion("logo"));
		
		// Create play button with tapit image
		playButton = new TextButton("", skin, "play");
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new PauseScreen(game));
				dispose();
			}
		});
		
		// Create menu buttons with icons
		achievementsButton = new TextButton("Achievements", skin, "menu");
		achievementsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				platformInterface.getAchievements();
			}
		});
		
		highScoreButton = new TextButton("High Scores", skin, "menu");
		highScoreButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				platformInterface.getScores();
			}
		});
		
		highLevelButton = new TextButton("High Levels", skin, "menu");
		highLevelButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				platformInterface.getLevels();
			}
		});
		
		loginButton = new TextButton("", skin, "menu");
		loginButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (platformInterface.getSignedIn()) {
					platformInterface.LogOut();
				} else {
					platformInterface.Login();
				}
			}
		});
		
		// Create labels for score display
		scoreLabel = new Label("", skin);
		highScoreLabel = new Label("", skin);
		levelLabel = new Label("", skin);
		
		// Layout the UI
		layoutUI();
	}
	
	private Table createMenuButtonTable(TextButton button, String text) {
		// Create table with icon on left, text on right (like original)
		Table table = new Table();
		
		// Add controller icon on left
		Image icon = new Image(game.atlas.findRegion("ic_play_games_badge_green"));
		table.add(icon).width(64).height(64).padRight(10);
		
		// Add text label on right
		Label textLabel = new Label(text, skin);
		table.add(textLabel).left();
		
		// Make the whole table clickable by copying the button's click listener
		if (button.getListeners().size > 0) {
			table.addListener(button.getListeners().first());
		}
		
		return table;
	}

	private void layoutUI() {
		// Main layout structure to match original positioning
		
		// Create split layout: left side for menu, right side for logo/play button
		Table leftSide = new Table();
		Table rightSide = new Table();
		
		// Menu buttons on left side - create icon+text layout like original
		Table achievementsTable = createMenuButtonTable(achievementsButton, "Achievements");
		Table highScoreTable = createMenuButtonTable(highScoreButton, "High Scores");
		Table highLevelTable = createMenuButtonTable(highLevelButton, "High Levels");
		Table loginTable = createMenuButtonTable(loginButton, "");
		
		leftSide.add(achievementsTable).width(400).height(64).padTop(150).row();
		leftSide.add(highScoreTable).width(400).height(64).padTop(10).row();
		leftSide.add(highLevelTable).width(400).height(64).padTop(10).row();
		leftSide.add(loginTable).width(200).height(64).padTop(10);
		leftSide.top().left();
		
		// Logo and play button on right side
		rightSide.add(logoImage).padTop(20).padRight(20).row();
		rightSide.add(playButton).size(387, 485).center().expand();
		rightSide.top().right();
		
		// Bottom score info like original
		Table bottomTable = new Table();
		bottomTable.add(scoreLabel).left().padLeft(20);
		bottomTable.add(highScoreLabel).center().expandX();
		bottomTable.add(levelLabel).right().padRight(20);
		
		// Main table layout
		Table contentTable = new Table();
		contentTable.add(leftSide).width(450).fillY().top();
		contentTable.add(rightSide).expand().fillY().top();
		
		// Final layout
		mainTable.add(contentTable).expand().fill().row();
		mainTable.add(bottomTable).fillX().bottom().padBottom(20);
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Update labels with current game state
		scoreLabel.setText(Long.toString(game.points));
		highScoreLabel.setText("HS: " + Long.toString(game.highScore) + " HL: " + Long.toString(game.highLevel));
		levelLabel.setText("Level: " + Integer.toString(game.level));
		
		// Update login button text
		if (platformInterface.getSignedIn()) {
			loginButton.setText("Logout");
		} else {
			loginButton.setText("Login");
		}
		
		// Update and render stage
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void show() {
		game.rainMusic.stop();
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}
}