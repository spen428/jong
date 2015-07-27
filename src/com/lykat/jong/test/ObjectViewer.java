package com.lykat.jong.test;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;
import com.lykat.jong.main.GraphicsConstants;

/**
 * Simple object viewer.
 * 
 * @author lykat
 *
 */
public class ObjectViewer implements ApplicationListener {

	private Environment environment;
	private PerspectiveCamera cam;
	private ModelBatch modelBatch;
	private AssetManager assets;
	private Array<ModelInstance> instances = new Array<ModelInstance>();
	private boolean loading;

	private final String[] models = new String[] {}; // "res/Table/Table.obj" };
	private CameraInputController camCont;

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
		cam.position.set(50, 50, 50);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 15000f;
		cam.update();
		camCont = new CameraInputController(cam);

		Gdx.input.setInputProcessor(camCont);

		assets = new AssetManager();
		for (String path : models) {
			assets.load(path, Model.class);
		}
		loading = true;
	}

	private void loadModels() {
		ModelInstance i = new ModelInstance(GraphicsConstants.MODEL_TILE);
		Texture t = new Texture(
				Gdx.files.internal("res/test.png"));
		setFaceTexture(i, t);
		instances.add(i);

		loading = false;
	}

	private void setFaceTexture(ModelInstance i, Texture t) {
		TextureAttribute mat = new TextureAttribute(TextureAttribute.Diffuse, t);
		Iterator<MeshPart> it = i.model.meshParts.iterator();
		int idx = 0;
		while (it.hasNext()) {
			if (it.next().id.equals("face")) {
				break; // Found the face meshPart index
			}
			idx++;
		}
		i.materials.get(idx).set(mat);
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

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Jong Object Viewer";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new ObjectViewer(), config);
	}

}