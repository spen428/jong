package com.lykat.jong.test;

import static com.lykat.jong.main.GameConstants.*;
import static com.lykat.jong.main.GraphicsConstants.*;

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
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
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
	private boolean overheadView;
	private Vector3 prevCamPos = new Vector3();

	private final String[] models = new String[] {}; // "res/Table/Table.obj" };

	private CameraInputController camCont;

	private final Vector3 PLAYER_CAM_POS = new Vector3(0,
			-PLAYER_CAMERA_Y_OFFSET_MM, PLAYER_CAMERA_Z_OFFSET_MM);
	private final Vector3 OVERHEAD_CAM_POS = new Vector3(0, 0,
			OVERHEAD_CAMERA_Z_OFFSET_MM);
	private final Vector3 CENTER_POS = new Vector3(0, 0, 0);

	private final Vector3 up = new Vector3(0, 0, 1);
	// private final Vector3 right = new Vector3(1, 0, 0);
	private final Vector3 forward = new Vector3(0, 1, 0);

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

		camCont = new CameraInputController(cam);

		Gdx.input.setInputProcessor(this);

		assets = new AssetManager();
		for (String path : models) {
			assets.load(path, Model.class);
		}
		loading = true;
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

	private void loadModels() {
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

		for (String path : models) {
			Model m = assets.get(path, Model.class);
			ModelInstance i = new ModelInstance(m);
			i.transform.translate(0, 0, 0); // -196.937500f
			instances.add(i);
		}

		Texture tex8sou = new Texture(Gdx.files.internal("res/test.png"));
		Texture texChun = new Texture(Gdx.files.internal("res/test2.png"));

		int numPlayers = 4;
		int numHandTiles = 13;
		float tileWG = TILE_WIDTH_MM + TILE_GAP_MM;
		float tileHG = TILE_HEIGHT_MM + TILE_GAP_MM;
		float tileTG = TILE_THICKNESS_MM + TILE_GAP_MM;

		/* Hands */
		for (int p = 0; p < numPlayers; p++) {
			/* Changes per player so has to be inside loop */
			float halfWidth = ((numHandTiles * (tileWG)) - TILE_GAP_MM) / 2;
			for (int x = 0; x < numHandTiles; x++) {
				ModelInstance instance = new ModelInstance(MODEL_TILE);

				/* Move into position */
				float xPos = x * (tileWG) - halfWidth;
				float yPos = -HAND_TILES_Y_OFFSET_MM;
				float zPos = 0;
				instance.transform.setToWorld(new Vector3(xPos, yPos, zPos),
						forward, up);

				/* Rotate so it ends up in front of #p, then rotate to face */
				rotateAboutCenter(instance.transform, p * 90);
				instance.transform.rotate(0, -1, 0, 90).rotate(-1, 0, 0, 90);

				instances.add(instance);
				setTileFace(instance, tex8sou);
			}
			/* Tsumo-hai */
			{
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
						forward, up);

				/* Rotate into place */
				rotateAboutCenter(instance.transform, p * 90);
				instance.transform.rotate(0, -1, 0, 90).rotate(-1, 0, 0, 90);
				if (placeOntop) {
					instance.transform.rotate(0, 0, -1, 90);
				}

				instances.add(instance);
				setTileFace(instance, texChun);
			}
			numHandTiles -= 4;
		}

		/* Riichi Sticks */
		for (int p = 0; p < numPlayers; p++) {
			ModelInstance instance = new ModelInstance(MODEL_RIICHI_STICK);

			/* Move into position */
			float xPos = 0;
			float yPos = -RIICHI_STICK_Y_OFFSET_MM;
			float zPos = RIICHI_HEIGHT_MM;
			instance.transform.setToWorld(new Vector3(xPos, yPos, zPos),
					forward, up);

			/* Rotate into place */
			rotateAboutCenter(instance.transform, p * 90);

			instances.add(instance);
		}

		/* Walls */
		float halfWidth = ((WALL_WIDTH_TILES * tileWG) - TILE_GAP_MM) / 2;
		for (int p = 0; p < numPlayers; p++) {
			for (int z = 0; z < WALL_HEIGHT_TILES; z++) {
				for (int x = 0; x < WALL_WIDTH_TILES; x++) {
					ModelInstance instance = new ModelInstance(MODEL_TILE);

					/* Move into position */
					float xPos = ((x + 1) * tileWG) - halfWidth;
					float yPos = -WALL_TILES_Y_OFFSET_MM;
					float zPos = z * tileTG;
					instance.transform.setToWorld(
							new Vector3(xPos, yPos, zPos), forward, up);

					/* Rotate into place */
					rotateAboutCenter(instance.transform, p * 90);
					instance.transform.rotate(0, 0, 1, 90);

					instances.add(instance);
				}
			}
		}

		/* Discards */
		halfWidth = ((DISCARD_WIDTH_TILES * tileWG) - TILE_GAP_MM) / 2;
		for (int p = 0; p < numPlayers; p++) {
			for (int i = 0; i < DISCARD_HEIGHT_TILES; i++) {
				for (int x = 0; x < DISCARD_WIDTH_TILES; x++) {
					ModelInstance instance = new ModelInstance(MODEL_TILE);

					/* Move into position */
					float xPos = (x * tileWG) - halfWidth;
					float yPos = -DISCARD_TILES_Y_OFFSET_MM - (i * tileHG);
					float zPos = TILE_THICKNESS_MM;
					instance.transform.setToWorld(
							new Vector3(xPos, yPos, zPos), forward, up);

					/* Rotate into place */
					rotateAboutCenter(instance.transform, p * 90);
					instance.transform.rotate(1, 0, 0, 180)
							.rotate(0, 0, -1, 90);

					instances.add(instance);
					setTileFace(instance, texChun);
				}
			}
		}

		loading = false;
	}

	@Override
	public void render() {
		if (loading && assets.update())
			loadModels();

		camCont.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();

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
		cam.rotateAround(CENTER_POS, up, right ? 90 : -90);
		cam.lookAt(0, 0, 0);
		cam.update();
		if (didOverhead) {
			toggleCamera();
		}
	}

	private void resetCamera() {
		overheadView = false;
		cam.position.set(PLAYER_CAM_POS);
		cam.up.set(up);
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
	public boolean keyTyped(char arg0) {

		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
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
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Jong Scene Test";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new SceneTest(), config);
	}

}