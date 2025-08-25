package com.jthtml.zebraDrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameOverScreen extends ScreenAdapter {
	final ZebraDropGame game;
	private GoogleInterface platformInterface;
	
	private Stage stage;
	private Skin skin;
	private Table mainTable;
	private Image backgroundImage;
	private Image gameOverImage;
	private Image logoImage;
	
	private TextButton achievementsButton;
	private TextButton highScoreButton;
	private TextButton highLevelButton;
	private TextButton loginButton;
	
	private Label scoreLabel;
	private Label highScoreLabel;
	private Label levelLabel;
	private Label newRecordLabel;
	private Label titleLabel;
	
	// Dynamic login label for updating text
	private Label loginLabel;
	
	private Boolean newPref;
	
	public GameOverScreen(final ZebraDropGame game) {
		this.game = game;
		platformInterface = game.getGameInterface();
		newPref = false;
		
		// Create stage with ExtendViewport to fill screen without letterboxing
		stage = new Stage(new ExtendViewport(game.maxW, game.maxH));
		Gdx.input.setInputProcessor(stage);
		
		// Game over logic (preserve original behavior)
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
			platformInterface.incrementAchievement(GameConstants.ACHIEVEMENT_GAMES_PLAYED, 1);

			if (game.points == GameConstants.ACHIEVEMENT_SCORE_1337) {
				platformInterface.unlockAchievement(GameConstants.ACHIEVEMENT_ELITE_SCORE);
			}
			
			if (game.points >= GameConstants.ACHIEVEMENT_SCORE_6826) {
				platformInterface.unlockAchievement(GameConstants.ACHIEVEMENT_HIGH_SCORE);			
			}
			
			if (game.level >= GameConstants.ACHIEVEMENT_LEVEL_15) {
				platformInterface.unlockAchievement(GameConstants.ACHIEVEMENT_HIGH_SCORE);			
			}

			if (game.level == GameConstants.ACHIEVEMENT_LEVEL_1) {
				platformInterface.unlockAchievement(GameConstants.ACHIEVEMENT_FIRST_LEVEL);
			}			
		}
		
		if (newPref) game.prefs.flush();
		
		// Create UI
		createSkin();
		createUI();
	}
	
	private void createSkin() {
		// Create skin for buttons
		skin = new Skin();
		skin.add("font", game.font);
		
		// Create menu button style (no background - icon is handled in Table)
		TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle();
		menuButtonStyle.font = game.font;
		skin.add("menu", menuButtonStyle);
		
		// Create restart button style (with tapit background)
		TextButton.TextButtonStyle restartButtonStyle = new TextButton.TextButtonStyle();
		restartButtonStyle.up = new TextureRegionDrawable(game.atlas.findRegion("tapit"));
		restartButtonStyle.font = game.font;
		skin.add("restart", restartButtonStyle);
		
		// Create label style
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = game.font;
		skin.add("default", labelStyle);
	}
	
	private void createUI() {
		// Create blurred background
		backgroundImage = new Image(game.atlas.findRegion("blurred_bg"));
		backgroundImage.setFillParent(true);
		stage.addActor(backgroundImage);
		
		// Create main table for layout
		mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);
		
		// Create game over image as a separate display element
		gameOverImage = new Image(game.atlas.findRegion("gameover"));
		
		// Create logo image
		logoImage = new Image(game.atlas.findRegion("logo"));
		
		// No separate restart button - the game over image itself is clickable
		
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
		
		// Create labels
		scoreLabel = new Label("", skin);
		highScoreLabel = new Label("", skin);
		levelLabel = new Label("", skin);
		titleLabel = new Label("ZEBRA DROP!!!", skin);
		newRecordLabel = new Label("^^NEW RECORDS^^", skin);
		
		// Layout the UI
		layoutUI();
	}
	
	private Table createMenuButtonTable(TextButton button, String text) {
		// Create table with consistent alignment for all menu items
		Table table = new Table();
		
		// Add controller icon with fixed positioning
		Image icon = new Image(game.atlas.findRegion("ic_play_games_badge_green"));
		table.add(icon).width(64).height(64).padRight(10).left();
		
		// Add text label with consistent left alignment
		Label textLabel = new Label(text, skin);
		table.add(textLabel).left().expandX();
		
		// Ensure the table itself is left-aligned
		table.left();
		
		// Make the whole table clickable
		if (button.getListeners().size > 0) {
			table.addListener(button.getListeners().first());
		}
		
		return table;
	}
	
	private Table createLoginButtonTable(TextButton button) {
		// Create table with consistent alignment matching other menu items
		Table table = new Table();
		
		// Add controller icon with fixed positioning
		Image icon = new Image(game.atlas.findRegion("ic_play_games_badge_green"));
		table.add(icon).width(64).height(64).padRight(10).left();
		
		// Add text label with consistent left alignment
		loginLabel = new Label("Login", skin);
		table.add(loginLabel).left().expandX();
		
		// Ensure the table itself is left-aligned
		table.left();
		
		// Make the whole table clickable
		if (button.getListeners().size > 0) {
			table.addListener(button.getListeners().first());
		}
		
		return table;
	}
	
	private void layoutUI() {
		// Create layout similar to original GameOverScreen
		
		// Create split layout: left side for menu, center for game over
		Table leftSide = new Table();
		Table centerArea = new Table();
		
		// Menu buttons on left side - ensure perfect left alignment
		Table achievementsTable = createMenuButtonTable(achievementsButton, "Achievements");
		Table highScoreTable = createMenuButtonTable(highScoreButton, "High Scores");
		Table highLevelTable = createMenuButtonTable(highLevelButton, "High Levels");
		Table loginTable = createLoginButtonTable(loginButton);
		
		// All menu buttons with identical layout - perfect left alignment
		leftSide.add(achievementsTable).width(400).height(64).left().padTop(80).row();
		leftSide.add(highScoreTable).width(400).height(64).left().padTop(15).row();
		leftSide.add(highLevelTable).width(400).height(64).left().padTop(15).row();
		leftSide.add(loginTable).width(400).height(64).left().padTop(15);
		leftSide.top().left().padLeft(50);
		
		// Game over button centered horizontally, positioned to align with top of menu
		Table gameOverTable = new Table();
		gameOverTable.add(gameOverImage).center().padTop(80); // Match menu's padTop
		gameOverTable.top().setFillParent(true);
		
		// Make the game over image clickable for restart (like original)
		// Use InputListener for better touch/click handling on images
		gameOverImage.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
			@Override
			public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
				return true; // Consume the event
			}
			
			@Override
			public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
				// Reset game state (same as playButton)
				game.bucketBounds.height = GameConstants.BUCKET_HEIGHT;
				game.dropRate = game.minDropRate;
				game.dropSpeed = game.minDropSpeed;
				game.neededDrops = GameConstants.INITIAL_DROPS_NEEDED;
				game.level = GameConstants.INITIAL_LEVEL;
				game.dropDir = GameConstants.INITIAL_DROP_DIRECTION;
				game.ptVal = GameConstants.INITIAL_POINT_VALUE;
				game.points = 0;
				game.bonus = 0;
				game.buckets = GameConstants.INITIAL_BUCKETS;	
				game.zebras = new Array<Zebra>();
				game.gameState = ZebraDropGame.State.Normal;	
				game.dropCount = 0;
				game.numDropped = 0;
				game.setScreen(new GameScreen(game));
				dispose();
			}
		});
		
		stage.addActor(gameOverTable);
		
		// New records label positioned below game over button when there are new records
		if (newPref) {
			Table recordsTable = new Table();
			recordsTable.add(newRecordLabel).center().padTop(80 + gameOverImage.getHeight() + 20);
			recordsTable.top().setFillParent(true);
			stage.addActor(recordsTable);
		}
		
		// Bottom score info like original
		Table bottomTable = new Table();
		bottomTable.add(scoreLabel).left().padLeft(20);
		bottomTable.add(highScoreLabel).center().expandX();
		bottomTable.add(levelLabel).right().padRight(20);
		
		// Main table layout - now just the left menu (game over button positioned independently)
		Table contentTable = new Table();
		contentTable.add(leftSide).width(450).fillY().top().padLeft(20);
		contentTable.top().left().setFillParent(true);
		stage.addActor(contentTable);
		
		// Logo positioned independently in top-right corner with fixed 10px margins
		// Use stage dimensions (actual screen area) instead of game virtual dimensions
		float logoX = stage.getWidth() - logoImage.getWidth() - 10; // Right edge 10px from screen edge
		float logoY = stage.getHeight() - logoImage.getHeight() - 10; // Top edge 10px from screen edge
		logoImage.setPosition(logoX, logoY);
		stage.addActor(logoImage);
		
		// Bottom score info positioned independently
		bottomTable.bottom().setFillParent(true);
		bottomTable.padBottom(20);
		stage.addActor(bottomTable);
	}
	
	
	@Override
	public void render(float delta) {		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Update labels with current game state
		scoreLabel.setText(Long.toString(game.points));
		highScoreLabel.setText("HS: " + Long.toString(game.highScore) + " HL: " + Long.toString(game.highLevel));
		levelLabel.setText("Level: " + Integer.toString(game.level));
		
		// Update login label text
		if (platformInterface.getSignedIn()) {
			loginLabel.setText("Logout");
		} else {
			loginLabel.setText("Login");
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
	public void hide() {
		// Font color is now managed by scene2d labels, no need to manually reset
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}
}