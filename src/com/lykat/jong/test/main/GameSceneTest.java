package com.lykat.jong.test.main;

import static com.lykat.jong.main.GraphicsConstants.OVERHEAD_CAMERA_Z_OFFSET_MM;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.lykat.jong.game.Game;
import com.lykat.jong.game.RuleSet;
import com.lykat.jong.game.Tile;
import com.lykat.jong.game.Wall;
import com.lykat.jong.main.GameScene;

/**
 * A test scene displaying the table and tiles.
 * 
 * @author lykat
 *
 */
public class GameSceneTest extends GameScene implements InputProcessor {

	private boolean overheadView;
	private Vector3 prevCamPos = new Vector3();
	private CameraInputController camCont;
	private long shortestRenderTime = Long.MAX_VALUE;
	private long longestRenderTime = Long.MIN_VALUE;
	private long startTime, finishTime;

	private final Vector3 OVERHEAD_CAM_POS = new Vector3(0, 0,
			OVERHEAD_CAMERA_Z_OFFSET_MM);

	public GameSceneTest() {
		RuleSet ruleSet = new RuleSet(RuleSet.GameType.RIICHI_FOUR_PLAYER, 2);

		DummyPlayer p1, p2, p3, p4;
		p1 = new DummyPlayer("Player 1");
		p2 = new DummyPlayer("Player 2");
		p3 = new DummyPlayer("Player 3");
		p4 = new DummyPlayer("Player 4");
		DummyPlayer[] players = new DummyPlayer[] { p1, p2, p3, p4 };
		Game game = new DummyGame("GameSceneTest Game", ruleSet, players);

		Wall w = game.getWall();
		for (DummyPlayer p : players) {
			Tile[] hand = new Tile[13];
			for (int i = 0; i < hand.length; i++) {
				hand[i] = w.draw();
			}
			p.deal(hand);
			for (int i = 0; i < 20; i++) {
				Tile tile = w.draw();
				p.deal(tile);
				p.tsumoKiri();
			}
			w.reset();
			p.addPoints(25000);
			p.declareRiichi();
			p.deal(w.draw());
		}

		super.setGame(game);
	}

	@Override
	public void create() {
		super.create();
		camCont = new CameraInputController(super.cam);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
		startTime = System.nanoTime();

		camCont.update();
		super.render();

		finishTime = System.nanoTime();
		finishTime -= startTime;

		if (finishTime < shortestRenderTime) {
			shortestRenderTime = finishTime;
		} else if (finishTime > longestRenderTime) {
			longestRenderTime = finishTime;
		}

		StringBuilder sb = new StringBuilder("Render took ");
		sb.append(finishTime).append("ns");
		LOGGER.log(Level.FINEST, sb.toString());
		LOGGER.log(Level.FINEST, "FPS: " + Gdx.graphics.getFramesPerSecond());
	}

	@Override
	public void dispose() {
		LOGGER.log(Level.INFO, "Shortest render: " + shortestRenderTime + "ns");
		LOGGER.log(Level.INFO, "Longest render: " + longestRenderTime + "ns");
		super.dispose();
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
	public boolean mouseMoved(int screenX, int screenY) {
		return camCont.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
		return camCont.scrolled(amount * 25);
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		return camCont.touchDown(0, y, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return camCont.touchDragged(0, screenY, pointer);
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		return camCont.touchUp(0, y, pointer, button);
	}

	public static void main(String[] args) {
		Logger logger = GameScene.LOGGER;
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new SimpleFormatter());
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.setLevel(Level.FINER);
		logger.log(Level.FINER, "Graphics logger initialised.");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Jong Scene Test";
		config.width = 1280;
		config.height = 720;
		config.samples = 8;
		config.useGL30 = true;
		config.vSyncEnabled = true;
		config.fullscreen = false;
		new LwjglApplication(new GameSceneTest(), config);
	}

}