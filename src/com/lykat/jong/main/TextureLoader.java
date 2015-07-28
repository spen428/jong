package com.lykat.jong.main;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public final class TextureLoader {

	private static boolean loaded = false;
	private static final ArrayList<Texture> textures = new ArrayList<Texture>();

	public static void load() {
		/* Tiles */
		ArrayList<String> paths = new ArrayList<String>();
		for (String suit : new String[] { "s", "p", "w", "h" }) {
			for (int value = 1; value < 10; value++) {
				if (suit.equals("h")) {
					if (value > 7) {
						break;
					}
				} else if (value == 5) {
					paths.add(String.format("res/tiles/%d%sd.png", value, suit));
				}
				paths.add(String.format("res/tiles/%d%s.png", value, suit));
			}
		}
		for (String path : paths) {
			Texture texture = new Texture(Gdx.files.internal(path));
			textures.add(texture);
		}
	}

	public static Texture getTextureById(int id) {
		if (!loaded) {
			load();
		}
		return textures.get(id);
	}

	private TextureLoader() {
	}

}
