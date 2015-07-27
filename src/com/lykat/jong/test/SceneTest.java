package com.lykat.jong.test;

import static com.lykat.jong.main.GameConstants.DISCARD_HEIGHT_TILES;
import static com.lykat.jong.main.GameConstants.DISCARD_WIDTH_TILES;
import static com.lykat.jong.main.GameConstants.WALL_HEIGHT_TILES;
import static com.lykat.jong.main.GameConstants.WALL_WIDTH_TILES;
import static com.lykat.jong.main.GraphicsConstants.DISCARD_TILES_Y_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.HAND_TILES_Y_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.MODEL_RIICHI_STICK;
import static com.lykat.jong.main.GraphicsConstants.MODEL_TILE;
import static com.lykat.jong.main.GraphicsConstants.OVERHEAD_CAMERA_Z_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.PLAYER_CAMERA_Y_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.PLAYER_CAMERA_Z_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.PLAYING_SURFACE_RADIUS_MM;
import static com.lykat.jong.main.GraphicsConstants.PLAYING_SURFACE_THICKNESS_MM;
import static com.lykat.jong.main.GraphicsConstants.RIICHI_HEIGHT_MM;
import static com.lykat.jong.main.GraphicsConstants.RIICHI_STICK_Y_OFFSET_MM;
import static com.lykat.jong.main.GraphicsConstants.RIICHI_THICKNESS_MM;
import static com.lykat.jong.main.GraphicsConstants.RIICHI_WIDTH_MM;
import static com.lykat.jong.main.GraphicsConstants.TILE_GAP_MM;
import static com.lykat.jong.main.GraphicsConstants.TILE_HEIGHT_MM;
import static com.lykat.jong.main.GraphicsConstants.TILE_THICKNESS_MM;
import static com.lykat.jong.main.GraphicsConstants.TILE_WIDTH_MM;
import static com.lykat.jong.main.GraphicsConstants.WALL_TILES_Y_OFFSET_MM;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * A test scene displaying the table and tiles.
 * 
 * @author lykat
 *
 */
public class SceneTest implements ApplicationListener, InputProcessor {

	private Environment environment;
	private PerspectiveCamera cam;
	private ModelBatch modelBatch;
	private AssetManager assets;
	private Array<ModelInstance> instances = new Array<ModelInstance>();
	private boolean loading;
	private boolean toggle;

	private final String[] models = new String[] {}; // "res/Table/Table.obj" };

	private final Vector3 PLAYER_CAM_POS = new Vector3(0,
			-PLAYER_CAMERA_Y_OFFSET_MM, PLAYER_CAMERA_Z_OFFSET_MM);
	private final Vector3 OVERHEAD_CAM_POS = new Vector3(0, 0,
			OVERHEAD_CAMERA_Z_OFFSET_MM);
	private final Vector3 CENTER_POS = new Vector3(0, 0, 0);

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
		cam.position.set(PLAYER_CAM_POS);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 15000f;
		cam.update();

		Gdx.input.setInputProcessor(this);

		assets = new AssetManager();
		for (String path : models) {
			assets.load(path, Model.class);
		}
		loading = true;
	}

	/**
	 * Returns a Vector3 relative to the bottom-left corner of the playing
	 * surface.
	 */
	private Vector3 rel(float x, float y, float z, float width, float height,
			float thickness) {
		return new Vector3(x + width / 2 - PLAYING_SURFACE_RADIUS_MM, y
				+ height / 2 - PLAYING_SURFACE_RADIUS_MM, z + thickness / 2
				+ PLAYING_SURFACE_THICKNESS_MM);
	}

	private void rotateAboutCenter(ModelInstance instance, float degrees) {
		Vector3 pos = new Vector3();
		instance.transform.getTranslation(pos);
		instance.transform.setTranslation(CENTER_POS);
		instance.transform.rotate(new Vector3(0, 0, 1), degrees);
		instance.transform.translate(pos);
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

	private void loadModels() {
		for (String path : models) {
			Model m = assets.get(path, Model.class);
			ModelInstance i = new ModelInstance(m);
			i.transform.translate(0, 0, 0); // -196.937500f
			instances.add(i);
		}

		Texture tex8sou = new Texture(Gdx.files.internal("res/test.png"));
		Texture texChun = new Texture(Gdx.files.internal("res/test2.png"));

		/* Hands */
		for (int p = 0; p < 4; p++) {
			float totalWidth = (13 * (TILE_WIDTH_MM + TILE_GAP_MM))
					- TILE_GAP_MM;
			for (int x = 0; x < 14; x++) {
				ModelInstance instance = new ModelInstance(MODEL_TILE);
				float xPos = x * (TILE_WIDTH_MM + TILE_GAP_MM)
						+ PLAYING_SURFACE_RADIUS_MM - totalWidth / 2;
				float yPos = PLAYING_SURFACE_RADIUS_MM - HAND_TILES_Y_OFFSET_MM;
				instance.transform.setTranslation(rel(xPos, yPos, 0,
						TILE_THICKNESS_MM, TILE_HEIGHT_MM, TILE_WIDTH_MM));
				rotateAboutCenter(instance, p * 90);
				/* Rotate towards player from face-down position */
				instance.transform.rotate(0, 0, -1, 90);
				instance.transform.rotate(0, -1, 0, 90);
				setTileFace(instance, tex8sou);
				instances.add(instance);
			}
			/* Tsumo-hai */
			{
				ModelInstance instance = new ModelInstance(MODEL_TILE);
				instance.transform.setTranslation(rel(
						(totalWidth - (0.75f * TILE_WIDTH_MM)
								+ PLAYING_SURFACE_RADIUS_MM - totalWidth / 2),
						PLAYING_SURFACE_RADIUS_MM - HAND_TILES_Y_OFFSET_MM,
						TILE_HEIGHT_MM + TILE_WIDTH_MM + TILE_GAP_MM,
						TILE_THICKNESS_MM, TILE_HEIGHT_MM, TILE_WIDTH_MM));
				rotateAboutCenter(instance, p * 90);
				/* Rotate towards player from face-down position */
				instance.transform.rotate(-1, 0, 0, 90);
				setTileFace(instance, tex8sou);
				instances.add(instance);
			}
		}

		/* Riichi Sticks */
		for (int p = 0; p < 4; p++) {
			ModelInstance instance = new ModelInstance(MODEL_RIICHI_STICK);
			instance.transform.setTranslation(rel(PLAYING_SURFACE_RADIUS_MM
					- (RIICHI_WIDTH_MM / 2.0f), PLAYING_SURFACE_RADIUS_MM
					- RIICHI_STICK_Y_OFFSET_MM, 0, RIICHI_WIDTH_MM,
					RIICHI_THICKNESS_MM, RIICHI_HEIGHT_MM));
			rotateAboutCenter(instance, p * 90);
			instances.add(instance);
		}

		/* Walls */
		for (int p = 0; p < 4; p++) {
			float totalWidth = (WALL_WIDTH_TILES * (TILE_WIDTH_MM + TILE_GAP_MM))
					- TILE_GAP_MM;
			for (int z = 0; z < WALL_HEIGHT_TILES; z++) {
				for (int x = 0; x < WALL_WIDTH_TILES; x++) {
					ModelInstance instance = new ModelInstance(MODEL_TILE);
					float xPos = x * (TILE_WIDTH_MM + TILE_GAP_MM)
							+ PLAYING_SURFACE_RADIUS_MM - totalWidth / 2;
					float yPos = PLAYING_SURFACE_RADIUS_MM
							+ WALL_TILES_Y_OFFSET_MM
							+ (TILE_HEIGHT_MM + TILE_GAP_MM);
					instance.transform.setTranslation(rel(xPos, yPos, z
							* (TILE_THICKNESS_MM + TILE_GAP_MM),
							TILE_THICKNESS_MM, TILE_HEIGHT_MM, TILE_WIDTH_MM));
					rotateAboutCenter(instance, p * 90);
					instance.transform.rotate(0, 0, 1, 90); // Rotate clockwise
					instances.add(instance);
				}
			}
		}

		/* Discards */
		for (int p = 0; p < 4; p++) {
			float totalWidth = (DISCARD_WIDTH_TILES * (TILE_WIDTH_MM + TILE_GAP_MM))
					- TILE_GAP_MM;
			for (int i = 0; i < DISCARD_HEIGHT_TILES; i++) {
				for (int x = 0; x < DISCARD_WIDTH_TILES; x++) {
					ModelInstance instance = new ModelInstance(MODEL_TILE);
					float xPos = x * (TILE_WIDTH_MM + TILE_GAP_MM)
							+ PLAYING_SURFACE_RADIUS_MM - totalWidth / 2;
					float yPos = PLAYING_SURFACE_RADIUS_MM
							+ DISCARD_TILES_Y_OFFSET_MM
							+ (i * (TILE_HEIGHT_MM + TILE_GAP_MM));
					instance.transform.setTranslation(rel(xPos, yPos, 0,
							TILE_THICKNESS_MM, TILE_HEIGHT_MM, TILE_WIDTH_MM));
					rotateAboutCenter(instance, p * 90);
					/* Rotate face-up from face-down position */
					instance.transform.rotate(1, 0, 0, 180);
					instance.transform.rotate(0, 0, 1, 90); // Rotate clockwise
					setTileFace(instance, texChun);
					instances.add(instance);
				}
			}
		}

		/* Surface */
		ModelBuilder mb = new ModelBuilder();
		Model playingSurface = mb.createBox(PLAYING_SURFACE_RADIUS_MM * 2,
				PLAYING_SURFACE_RADIUS_MM * 2, PLAYING_SURFACE_THICKNESS_MM,
				new Material(ColorAttribute.createDiffuse(Color.NAVY)),
				Usage.Position | Usage.Normal);
		ModelInstance instance = new ModelInstance(playingSurface);
		instances.add(instance);

		loading = false;
	}

	@Override
	public void render() {
		if (loading && assets.update())
			loadModels();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();

	}

	private void toggleCamera() {
		cam.position.set(toggle ? PLAYER_CAM_POS : OVERHEAD_CAM_POS);
		toggle = !toggle;
		cam.lookAt(0, 0, 0);
		cam.update();
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		instances.clear();
		assets.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyDown(int key) {
		switch (key) {
		case Keys.F:
			toggleCamera();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char arg0) {

		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Jong Scene Test";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new SceneTest(), config);
	}

}