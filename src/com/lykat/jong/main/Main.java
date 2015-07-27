package com.lykat.jong.main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lykat.jong.test.SceneTest;

public class Main {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Jong";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new SceneTest(), config);
	}

}
