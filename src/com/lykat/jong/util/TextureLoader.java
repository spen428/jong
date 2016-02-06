package com.lykat.jong.util;

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
    private static final HashMap<Integer, Texture> textures = new HashMap<>();

    public static void load() {
        System.out.println("Loading textures...");
        ArrayList<Tile> tiles = new ArrayList<>();

        /* Red Dora Tiles */
        tiles.add(new Tile(TileSuit.PINZU, TileValue.UU, true));
        tiles.add(new Tile(TileSuit.WANZU, TileValue.UU, true));
        tiles.add(new Tile(TileSuit.SOUZU, TileValue.UU, true));

        /* Regular Tiles */
        Tile[] tilez = Wall.uniqueTileSet();
        for (Tile tile : tilez) {
            tiles.add(tile);
        }

        String textureRoot = "res/tiles_new/Export/Regular";
        String ext = ".png";
        for (Tile tile : tiles) {
            String suit = "";
            switch (tile.getSuit()) {
            default:
            case JIHAI:
                suit = "";
                break;
            case PINZU:
                suit = "Pin";
                break;
            case SOUZU:
                suit = "Sou";
                break;
            case WANZU:
                suit = "Man";
                break;
            }
            String value = "";
            switch (tile.getValue()) {
            case CHII:
                value = "7";
                break;
            case CHUN:
                value = "Chun";
                break;
            case CHUU:
                value = "9";
                break;
            case HAKU:
                value = "Haku";
                break;
            case HATSU:
                value = "Hatsu";
                break;
            case II:
                value = "1";
                break;
            case NAN:
                value = "Nan";
                break;
            case PAA:
                value = "8";
                break;
            case PEI:
                value = "Pei";
                break;
            case RYAN:
                value = "2";
                break;
            case RYUU:
                value = "6";
                break;
            case SAN:
                value = "3";
                break;
            case SHAA:
                value = "Shaa";
                break;
            case SUU:
                value = "4";
                break;
            case TON:
                value = "Ton";
                break;
            case UU:
                value = "5";
                break;
            default:
                value = "";
                break;
            }
            if (tile.isRed()) {
                value += "-Dora";
            }
            String filename = String.format("%s%s", suit, value);
            String path = String.format("%s/%s%s", textureRoot, filename, ext);
            Texture texture = new Texture(Gdx.files.internal(path));
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            textures.put(new Integer(tile.hashCode()), texture);
        }
        /* Blank tile */
        textures.put(null, new Texture(
                Gdx.files.internal(textureRoot + "/" + "Blank" + ext)));
        loaded = true;
    }

    public static Texture getTileTexture(Tile tile) {
        if (!loaded) {
            load();
        }

        return textures.get(new Integer(tile.hashCode()));
    }

    private TextureLoader() {
    }

}
