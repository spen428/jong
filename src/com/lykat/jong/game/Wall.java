package com.lykat.jong.game;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents the wall from which tiles are drawn.
 * 
 * @author lykat
 *
 */
public class Wall {

	public static final int NUM_DEADWALL_TILES = 14;

	private final Random rand;
	private final Tile[] liveWall, deadWall;
	private final ArrayList<Integer> remainingLive;
	private final int numDeadWallDraws;
	private int remainingDeadWallDraws;
	private int dora;

	/**
	 * 
	 * @param tileSet
	 *            an array containing the set of tiles to stack the wall with.
	 * @param numDeadWallDraws
	 *            the number of dead-wall draws to allow. This value is
	 *            typically 4 in 4-player and 8 in 3-player. The value is
	 *            further increased by 4 if flower tiles are included in the
	 *            tile set.
	 */
	public Wall(Tile[] tileSet, int numDeadWallDraws) {
		if (numDeadWallDraws < 0 || numDeadWallDraws > 12) {
			throw new IllegalArgumentException("Number of dead-wall draws "
					+ "must be between 0 and 12");
		}
		rand = new Random();
		this.numDeadWallDraws = numDeadWallDraws;
		this.remainingDeadWallDraws = numDeadWallDraws;
		this.dora = 1;

		/* Construct the wall. */
		liveWall = tileSet;
		remainingLive = new ArrayList<Integer>();
		deadWall = new Tile[NUM_DEADWALL_TILES];
		this.reset();
	}

	/**
	 * Draw a random tile from the wall. The tile is removed from the wall in
	 * the process.
	 */
	public Tile draw() {
		int size = getNumRemainingDraws();
		if (size < 1) {
			return null;
		}
		int r = rand.nextInt(size);
		remainingLive.remove(r);
		return liveWall[r];
	}

	/**
	 * Draw a random tile from the dead wall. The tile is removed from the dead
	 * wall in the process, and the tile from the end of the live wall is
	 * appended.
	 */
	public Tile deadWallDraw() {
		if (getNumRemainingDeadWallDraws() > 0) {
			Tile draw = this.draw();
			if (draw != null) {
				remainingDeadWallDraws--;
				return draw;
			}
		}
		return null;
	}

	/**
	 * Resets the wall and dead wall.
	 */
	public void reset() {
		remainingLive.clear();
		remainingDeadWallDraws = numDeadWallDraws;
		dora = 1;
		for (int i = 0; i < liveWall.length; i++) {
			remainingLive.add(i);
		}
		for (int i = 0; i < deadWall.length; i++) {
			deadWall[i] = this.draw();
		}
	}

	/**
	 * Number of tiles left in the live wall.
	 */
	public int getNumRemainingDraws() {
		return remainingLive.size();
	}

	public int getNumRemainingDeadWallDraws() {
		if (getNumRemainingDraws() < remainingDeadWallDraws) {
			return getNumRemainingDraws();
		} else {
			return remainingDeadWallDraws;
		}
	}

	public int getTotalNumDeadWallDraws() {
		return numDeadWallDraws;
	}

	public int getNumDoraIndicators() {
		return dora;
	}

	public void flipDora() {
		if (dora < NUM_DEADWALL_TILES / 2) {
			dora++;
		}
	}

	public Tile[] getDoraIndicators() {
		Tile[] tiles = new Tile[dora];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = deadWall[i];
		}
		return tiles;
	}

	public Tile[] getUraDoraIndicators() {
		Tile[] tiles = new Tile[dora];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = deadWall[i + dora];
		}
		return tiles;
	}

	/* Static tile set generator methods */

	/**
	 * Returns the set of 34 unique tiles.
	 */
	public static Tile[] uniqueTileSet() {
		Tile[] tileSet = new Tile[34];
		int j = 0;
		for (TileSuit s : TileSuit.values()) {
			for (TileValue v : TileValue.values()) {
				if ((s == TileSuit.JIHAI && v.isJihai())
						|| (s != TileSuit.JIHAI && v.isNumbered())) {
					Tile tile = new Tile(s, v);
					tileSet[j] = tile;
					j++;
				}
			}
		}
		return tileSet;
	}

	/**
	 * Returns the set of tiles used in 2 player mahjong (2~8 Pinzu and Wanzu
	 * removed)
	 */
	public static Tile[] twoPlayerTileSet() {
		ArrayList<Tile> tileSet = new ArrayList<Tile>();
		for (Tile t : fourPlayerTileSet()) {
			if (t.getSuit() != TileSuit.WANZU && t.getSuit() != TileSuit.PINZU) {
				tileSet.add(t);
			} else if (t.getValue().isTermHon()) {
				tileSet.add(t);
			}
		}
		Tile[] tileSetArray = new Tile[tileSet.size()];
		int i = 0;
		for (Tile t : tileSet)
			tileSetArray[i++] = t;
		return tileSetArray;
	}

	/**
	 * Returns the set of tiles used in 3 player mahjong (2~8 Wanzu removed)
	 */
	public static Tile[] threePlayerTileSet() {
		ArrayList<Tile> tileSet = new ArrayList<Tile>();
		for (Tile t : fourPlayerTileSet()) {
			if (t.getSuit() != TileSuit.WANZU) {
				tileSet.add(t);
			} else if (t.getValue().isTermHon()) {
				tileSet.add(t);
			}
		}
		Tile[] tileSetArray = new Tile[tileSet.size()];
		int i = 0;
		for (Tile t : tileSet)
			tileSetArray[i++] = t;
		return tileSetArray;
	}

	/**
	 * Returns a standard set of 136 tiles.
	 */
	public static Tile[] fourPlayerTileSet() {
		Tile[] tileSet = new Tile[136];

		for (int i = 0; i < 4; i++) {
			int j = 0;
			for (Tile t : Wall.uniqueTileSet()) {
				tileSet[(i * 34) + j++] = t;
			}
		}

		return tileSet;
	}

}
