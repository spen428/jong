package com.lykat.jong.test.main;

import java.util.Observable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lykat.jong.control.PlayerController;
import com.lykat.jong.control.TsumokiriAI;
import com.lykat.jong.game.Game;
import com.lykat.jong.game.GameManager;
import com.lykat.jong.game.RuleSet;
import com.lykat.jong.main.GameScene;

/**
 * A test scene displaying the table and tiles.
 * 
 * @author lykat
 *
 */
public class GameplayTest extends GameScene {

	private final GameManager gameManager;
	private final InputProcessor playerController;

	public GameplayTest() {
		RuleSet ruleSet = new RuleSet(RuleSet.GameType.RIICHI_FOUR_PLAYER, 2);
		Game game = new Game("GameplayTest Game", ruleSet);

		gameManager = new GameManager(game);
		playerController = new PlayerController("Dave", gameManager);
		((Observable) playerController).addObserver(this);
		
		for (int i = 1; i < 4; i++) {
			TsumokiriAI ai = new TsumokiriAI("AI " + i, gameManager);
			ai.connect();
		}

		super.setGame(game);
	}

	@Override
	public void create() {
		super.create();
		Gdx.input.setInputProcessor(playerController);
	}

	@Override
	public void render() {
		super.render();
	}

	public static void main(String[] args) {
		initLoggers();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Jong Gameplay Test";
		config.width = 1280;
		config.height = 720;
		config.samples = 8;
		config.useGL30 = true;
		config.vSyncEnabled = true;
		config.fullscreen = false;
		new LwjglApplication(new GameplayTest(), config);
	}

	private static void initLoggers() {
		Logger logger = PlayerController.LOGGER;
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new SimpleFormatter());
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
		logger.log(Level.FINER, "PlayerController logger initialised.");

		logger = GameManager.LOGGER;
		handler = new ConsoleHandler();
		handler.setFormatter(new SimpleFormatter());
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
		logger.log(Level.FINER, "GameManager logger initialised.");

		logger = GameScene.LOGGER;
		handler = new ConsoleHandler();
		handler.setFormatter(new SimpleFormatter());
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.setLevel(Level.FINER);
		logger.log(Level.FINER, "Graphics logger initialised.");
	}

}