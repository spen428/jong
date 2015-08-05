package com.lykat.jong.test.main;

import static com.lykat.jong.main.GraphicsConstants.OVERHEAD_CAMERA_Z_OFFSET_MM;

import java.util.Observable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Vector3;
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
public class GameplayTest extends GameScene implements InputProcessor {

	private boolean overheadView;
	private Vector3 prevCamPos = new Vector3();
	private final GameManager gameManager;

	private final Vector3 OVERHEAD_CAM_POS = new Vector3(0, 0,
			OVERHEAD_CAMERA_Z_OFFSET_MM);
	private InputProcessor playerController;

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

	private void toggleCamera() {
		if (!overheadView) {
			prevCamPos.set(cam.position);
		}
		cam.position.set(overheadView ? prevCamPos : OVERHEAD_CAM_POS);
		overheadView = !overheadView;
		cam.lookAt(0, 0, 0);
		cam.update();
	}

	private void cyclePlayerCams(boolean right) {
		boolean didOverhead = false;
		if (overheadView) {
			toggleCamera();
			didOverhead = true;
		}
		cam.rotateAround(super.CENTER_POS, super.UP, right ? 90 : -90);
		cam.lookAt(0, 0, 0);
		cam.update();
		if (didOverhead) {
			toggleCamera();
		}
	}

	private void resetCamera() {
		overheadView = false;
		cam.position.set(super.PLAYER_CAM_POS);
		cam.up.set(super.UP);
		cam.lookAt(0, 0, 0);
		cam.update();
	}

	@Override
	public boolean keyDown(int key) {
		switch (key) {
		case Keys.F:
			toggleCamera();
			break;
		case Keys.D:
			cyclePlayerCams(true);
			break;
		case Keys.A:
			cyclePlayerCams(false);
			break;
		case Keys.R:
			resetCamera();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char key) {
		return false;
	}

	@Override
	public boolean keyUp(int key) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
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