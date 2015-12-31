package com.lykat.jong.main;

public final class GameConstants {

    public static final int TURN_DELAY_MS = 150;

    public static final int WALL_WIDTH_TILES = 17;
    public static final int WALL_HEIGHT_TILES = 2;

    public static final int DISCARD_WIDTH_TILES = 6;
    public static final int DISCARD_HEIGHT_TILES = 3;

    /**
     * The minimum number of tiles in the player's hand for the Tsumohai to be
     * placed on top of the hand, instead of to its side. (Should at least be
     * greater than 1).
     */
    public static final int MIN_TILES_TSUMOHAI_ONTOP = 14; // 8;
    public static final int MAX_DISCARDS_PER_PLAYER = 22;
    public static final int NUM_HAND_TILES = 13;
    public static final int MAX_OPEN_MELDS = 5;

    private GameConstants() {
    }

}
