package com.lykat.jong.main;

public final class GameConstants {

	public static final int WALL_WIDTH_TILES = 17;
	public static final int WALL_HEIGHT_TILES = 2;

	public static final int DISCARD_WIDTH_TILES = 6;
	public static final int DISCARD_HEIGHT_TILES = 3;

	/**
	 * The minimum number of tiles in the player's hand for the Tsumohai to be
	 * placed on top of the hand, instead of to its side. (Should at least be
	 * greater than 1).
	 */
	public static final int MIN_TILES_TSUMOHAI_ONTOP = 8;

	private GameConstants() {
	}

}
