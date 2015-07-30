package com.lykat.jong.main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Jong";
		config.width = 1280;
		config.height = 720;
		config.samples = 8;
		config.useGL30 = true;
		config.vSyncEnabled = true;
		config.fullscreen = false;
		new LwjglApplication(new GameScene(), config);
	}

}
