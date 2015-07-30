package com.lykat.jong.main;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.lykat.jong.game.Tile;
import com.lykat.jong.game.TileSuit;
import com.lykat.jong.game.TileValue;
import com.lykat.jong.game.Wall;

public final class TextureLoader {

	private static boolean loaded = false;
	private static final HashMap<Integer, Texture> textures = new HashMap<Integer, Texture>();

	public static void load() {
		ArrayList<Tile> tiles = new ArrayList<Tile>();

		/* Red Dora Tiles */
		tiles.add(new Tile(TileSuit.PINZU, TileValue.UU, true));
		tiles.add(new Tile(TileSuit.WANZU, TileValue.UU, true));
		tiles.add(new Tile(TileSuit.SOUZU, TileValue.UU, true));

		/* Regular Tiles */
		Tile[] tilez = Wall.uniqueTileSet();
		for (Tile tile : tilez) {
			tiles.add(tile);
		}

		for (Tile tile : tiles) {
			String name = tile.toString();
			String path = String.format("res/tiles/%s.png",
					name.replace(' ', '_'));
			Texture texture = new Texture(Gdx.files.internal(path));
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			textures.put(tile.hashCode(), texture);
		}
	}

	@Deprecated
	public static Texture getTextureById(int id) {
		if (!loaded) {
			load();
		}

		if (id < textures.size()) {
			for (int key : textures.keySet()) {
				if (id == 0) {
					return textures.get(key);
				}
				id--;
			}
		}
		return null;
	}

	public static Texture getTileTexture(Tile tile) {
		if (!loaded) {
			load();
		}
		return textures.get(tile.hashCode());
	}

	private TextureLoader() {
	}

}
