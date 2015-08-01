package com.lykat.jong.main;

import static com.lykat.jong.main.GameConstants.DISCARD_HEIGHT_TILES;
import static com.lykat.jong.main.GameConstants.DISCARD_WIDTH_TILES;
import static com.lykat.jong.main.GameConstants.MIN_TILES_TSUMOHAI_ONTOP;
import static com.lykat.jong.main.GameConstants.WALL_HEIGHT_TILES;
import static com.lykat.jong.main.GameConstants.WALL_WIDTH_TILES;
import static com.lykat.jong.main.GraphicsConstants.DISCARD_TILES_Y_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.HAND_TILES_Y_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.MODEL_TILE;
import static com.lykat.jong.main.GraphicsConstants.PLAYER_CAMERA_Y_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.PLAYER_CAMERA_Z_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.PLAYING_SURFACE_RADIUS_MM;
import static com.lykat.jong.main.GraphicsConstants.PLAYING_SURFACE_THICKNESS_MM;
import static com.lykat.jong.main.GraphicsConstants.TILE_GAP_MM;
import static com.lykat.jong.main.GraphicsConstants.TILE_HEIGHT_MM;
import static com.lykat.jong.main.GraphicsConstants.TILE_THICKNESS_MM;
import static com.lykat.jong.main.GraphicsConstants.TILE_WIDTH_MM;
import static com.lykat.jong.main.GraphicsConstants.WALL_TILES_Y_OFFSET_MM;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;
import com.lykat.jong.game.Game;
import com.lykat.jong.game.Player;
import com.lykat.jong.game.Tile;

/**
 * Renders the table and tiles based on an instance of {@link Game}.
 * 
 * @author lykat
 *
 */
public class GameScene implements ApplicationListener, Observer {

	public static final Logger LOGGER = Logger.getLogger("Graphics");

	protected Game game, setGame;
	protected Environment environment;
	protected PerspectiveCamera cam;
	protected ModelBatch modelBatch;
	protected AssetManager assets;
	protected Array<ModelInstance> instances = new Array<ModelInstance>();
	protected IdentityMap<Tile, ModelInstance> tiles = new IdentityMap<Tile, ModelInstance>();

	private boolean loading, changed;

	protected final String[] MODELS = new String[] {};

	protected final Vector3 PLAYER_CAM_POS = new Vector3(0,
			-PLAYER_CAMERA_Y_OFFSET_MM, PLAYER_CAMERA_Z_OFFSET_MM);
	protected final Vector3 CENTER_POS = new Vector3(0, 0, 0);
	protected final Vector3 UP = new Vector3(0, 0, 1);
	protected final Vector3 RIGHT = new Vector3(1, 0, 0);
	protected final Vector3 FORWARD = new Vector3(0, 1, 0);

	@Override
	public void create() {
		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f,
				0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(2.8f, 0.8f, 0.8f, -1f,
				-0.8f, -0.2f));

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.near = 1f;
		cam.far = 15000f;
		cam.position.set(PLAYER_CAM_POS);
		cam.lookAt(0, 0, 0);
		cam.update();

		assets = new AssetManager();
		for (String path : MODELS) {
			assets.load(path, Model.class);
		}
		loading = true;
		changed = true;
	}

	private void rotateAboutCenter(Matrix4 matrix, float degrees) {
		Vector3 pos = new Vector3();
		matrix.getTranslation(pos);
		matrix.setToTranslation(CENTER_POS);
		matrix.rotate(new Vector3(0, 0, 1), degrees);
		pos = pos.sub(CENTER_POS);
		matrix.translate(pos);
	}

	private boolean setTileFace(ModelInstance tileInstance, Texture faceTexture) {
		TextureAttribute textureAttr = new TextureAttribute(
				TextureAttribute.Diffuse, faceTexture);
		/* Find the index of the "face" mesh part. */
		Iterator<MeshPart> it = tileInstance.model.meshParts.iterator();
		int idx = 0;
		boolean found = false;
		while (it.hasNext()) {
			if (it.next().id.equals("face")) {
				found = true;
				break;
			}
			idx++;
		}
		if (found) {
			tileInstance.materials.get(idx).set(textureAttr);
			return true;
		}
		return false;
	}

	private void loadGraphics() {
		/* Playing Surface */
		{
			ModelBuilder mb = new ModelBuilder();
			Model playingSurface = mb.createBox(PLAYING_SURFACE_RADIUS_MM * 2,
					PLAYING_SURFACE_RADIUS_MM * 2,
					PLAYING_SURFACE_THICKNESS_MM,
					new Material(ColorAttribute.createDiffuse(Color.NAVY)),
					Usage.Position | Usage.Normal);
			ModelInstance instance = new ModelInstance(playingSurface);
			instances.add(instance);
		}

		// for (String path : MODELS) {
		// Model m = assets.get(path, Model.class);
		// ModelInstance i = new ModelInstance(m);
		// i.transform.translate(0, 0, 0);
		// instances.add(i);
		// }

		float tileWG = TILE_WIDTH_MM + TILE_GAP_MM;
		float tileHG = TILE_HEIGHT_MM + TILE_GAP_MM;
		float tileTG = TILE_THICKNESS_MM + TILE_GAP_MM;

		Player[] players = game.getPlayers();

		/* Walls */
		// TODO
		float halfWidth = ((WALL_WIDTH_TILES * tileWG) - TILE_GAP_MM) / 2;
		for (int p = 0; p < players.length; p++) {
			for (int z = 0; z < WALL_HEIGHT_TILES; z++) {
				for (int x = 0; x < WALL_WIDTH_TILES; x++) {
					ModelInstance instance = new ModelInstance(MODEL_TILE);

					/* Move into position */
					float xPos = ((x + 1) * tileWG) - halfWidth;
					float yPos = -WALL_TILES_Y_OFFSET_MM;
					float zPos = z * tileTG;
					instance.transform.setToWorld(
							new Vector3(xPos, yPos, zPos), FORWARD, UP);

					/* Rotate into place */
					rotateAboutCenter(instance.transform, p * 90);
					instance.transform.rotate(0, 0, 1, 90);

					instances.add(instance);
				}
			}
		}

		loading = false;
	}

	private void loadTiles() {
		float tileWG = TILE_WIDTH_MM + TILE_GAP_MM;
		float tileHG = TILE_HEIGHT_MM + TILE_GAP_MM;
		float tileTG = TILE_THICKNESS_MM + TILE_GAP_MM;

		Player[] players = game.getPlayers();

		/* Hands */
		for (int p = 0; p < players.length; p++) {
			Player player = players[p];
			if (player == null) {
				LOGGER.log(Level.FINEST, "Skipping player #" + p);
				continue;
			}

			int numHandTiles = player.getHand().size();
			float halfWidth = ((numHandTiles * (tileWG)) - TILE_GAP_MM) / 2;

			for (int x = 0; x < numHandTiles; x++) {
				Tile tile = player.getHand().get(x);
				if (tiles.containsKey(tile)) {
					continue;
				}

				ModelInstance instance = new ModelInstance(MODEL_TILE);

				/* Move into position */
				float xPos = x * (tileWG) - halfWidth;
				float yPos = -HAND_TILES_Y_OFFSET_MM;
				float zPos = 0;
				instance.transform.setToWorld(new Vector3(xPos, yPos, zPos),
						FORWARD, UP);

				/* Rotate so it ends up in front of #p, then rotate to face */
				rotateAboutCenter(instance.transform, p * 90);
				instance.transform.rotate(0, -1, 0, 90).rotate(-1, 0, 0, 90);

				instances.add(instance);
				setTileFace(instance, TextureLoader.getTileTexture(tile));
			}

			/* Tsumo-hai */
			Tile tsumohai = player.getTsumoHai();
			if (tsumohai != null) {
				if (tiles.containsKey(tsumohai)) {
					continue;
				}

				ModelInstance instance = new ModelInstance(MODEL_TILE);

				/* Move into position */
				float xPos = (numHandTiles + 0.5f) * (tileWG) - halfWidth;
				float yPos = -HAND_TILES_Y_OFFSET_MM;
				float zPos = 0;
				boolean placeOntop = numHandTiles >= MIN_TILES_TSUMOHAI_ONTOP;

				if (placeOntop) { // Place ontop of hand
					xPos -= tileWG;
					zPos += tileHG;
				}

				instance.transform.setToWorld(new Vector3(xPos, yPos, zPos),
						FORWARD, UP);

				/* Rotate into place */
				rotateAboutCenter(instance.transform, p * 90);
				instance.transform.rotate(0, -1, 0, 90).rotate(-1, 0, 0, 90);
				if (placeOntop) {
					instance.transform.rotate(0, 0, -1, 90);
				}

				instances.add(instance);
				setTileFace(instance, TextureLoader.getTileTexture(tsumohai));
			}

			/* Open melds */
			// for (int y = 0; y < 4; y++) {
			// for (int x = 0; x < 4; x++) {
			// ModelInstance instance = new ModelInstance(MODEL_TILE);
			//
			// /* Move into position */
			// float xPos = OPEN_MELDS_X_OFFSET_MM - (x * tileWG);
			// float yPos = -OPEN_MELDS_Y_OFFSET_MM + (y * tileHG);
			// float zPos = TILE_THICKNESS_MM;
			// instance.transform.setToWorld(
			// new Vector3(xPos, yPos, zPos), FORWARD, UP);
			//
			// /* Rotate into place */
			// rotateAboutCenter(instance.transform, p * 90);
			// instance.transform.rotate(1, 0, 0, 180)
			// .rotate(0, 0, -1, 90);
			//
			// instances.add(instance);
			// randomTileFace(instance);
			// }
			// }

			/* Riichi Sticks */
			// if (player.isRiichi()) {
			// ModelInstance instance = new ModelInstance(MODEL_RIICHI_STICK);
			//
			// /* Move into position */
			// float xPos = 0;
			// float yPos = -RIICHI_STICK_Y_OFFSET_MM;
			// float zPos = RIICHI_HEIGHT_MM;
			// instance.transform.setToWorld(new Vector3(xPos, yPos, zPos),
			// FORWARD, UP);
			//
			// /* Rotate into place */
			// rotateAboutCenter(instance.transform, p * 90);
			//
			// instances.add(instance);
			// }

			/* Discards */
			if (player.getDiscards().size() > 0) {
				halfWidth = ((DISCARD_WIDTH_TILES * tileWG) - TILE_GAP_MM) / 2;
				for (int i = 0; i < player.getDiscards().size(); i++) {
					Tile tile = player.getDiscards().get(i);
					if (tiles.containsKey(tile)) {
						continue;
					}

					ModelInstance instance = new ModelInstance(MODEL_TILE);

					/* Move into position */
					int x = i % DISCARD_WIDTH_TILES;
					int y = i / DISCARD_WIDTH_TILES;
					if (y > DISCARD_HEIGHT_TILES - 1) {
						y = DISCARD_HEIGHT_TILES - 1;
						x += DISCARD_WIDTH_TILES;
					}

					float xPos = (x * tileWG) - halfWidth;
					float yPos = -DISCARD_TILES_Y_OFFSET_MM - (y * tileHG);
					float zPos = TILE_THICKNESS_MM;
					instance.transform.setToWorld(
							new Vector3(xPos, yPos, zPos), FORWARD, UP);

					/* Rotate into place */
					rotateAboutCenter(instance.transform, p * 90);
					instance.transform.rotate(1, 0, 0, 180)
							.rotate(0, 0, -1, 90);

					instances.add(instance);
					setTileFace(instance, TextureLoader.getTileTexture(tile));
				}
			}
		}
	}

	private long start, finish;

	@Override
	public void render() {
		start = System.nanoTime();

		if (setGame != null) {
			game = setGame;
			setGame = null;
		}

		if (game != null) {
			if (loading && assets.update()) {
				loadGraphics();
			}
			if (changed) {
				loadTiles();
				changed = false;
			}

			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight());
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			modelBatch.begin(cam);
			modelBatch.render(instances, environment);
			modelBatch.end();
		}

		finish = System.nanoTime();
		StringBuilder sb = new StringBuilder("Render took ");
		sb.append((finish - start));
		sb.append("ns");
		LOGGER.log(Level.FINEST, sb.toString());
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		instances.clear();
		assets.dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resize(int arg0, int arg1) {
	}

	@Override
	public void resume() {
	}

	/**
	 * Sets the currently rendered game. Does not update until the next start of
	 * the next render loop.
	 */
	public void setGame(Game game) {
		this.setGame = game;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		LOGGER.log(Level.FINER, "Graphical update called");
		changed = true;
	}

}