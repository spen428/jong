package com.lykat.jong.game;

/**
 * Represents a Mahjong tile with a suit and value.
 * 
 * @author lykat
 *
 */
public class Tile {

	private final TileSuit suit;
	private final TileValue value;
	private final boolean red;

	/**
	 * A Mahjong tile.
	 * 
	 * @param suit
	 *            The tile's {@link TileSuit}
	 * @param value
	 *            the tile's {@link TileValue}
	 * @param red
	 *            Whether this tile is a 'red' dora tile.
	 */
	public Tile(TileSuit suit, TileValue value, boolean red) {
		super();

		if (suit == null) {
			throw new IllegalArgumentException("Tile suit cannot be null.");
		} else if (value == null) {
			throw new IllegalArgumentException("Tile value cannot be null.");
		} else if (suit.isJihai() && value.isNumbered()) {
			throw new IllegalArgumentException(
					"Illegal suit/value combo: JIHAI + NUMBERED");
		} else if (!suit.isJihai() && value.isJihai()) {
			throw new IllegalArgumentException(
					"Illegal suit/value combo: NUMBERED + JIHAI");
		}

		this.suit = suit;
		this.value = value;
		this.red = red;
	}

	/**
	 * A Mahjong tile.
	 * 
	 * @param suit
	 *            The tile's {@link TileSuit}
	 * @param value
	 *            The tile's {@link TileValue}
	 */
	public Tile(TileSuit suit, TileValue value) {
		this(suit, value, false);
	}

	@Override
	public Tile clone() {
		return new Tile(this.suit, this.value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tile) {
			Tile tile = (Tile) obj;
			return (this.suit == tile.getSuit() && this.value == tile.value);
		}
		return false;
	}

	@Override
	public String toString() {
		// TODO: It is worth memoizing this method if it is to be heavily used.
		return String.format("%s %s %s", value.toString(), suit.toString(),
				this.red ? "(Red)" : "").trim();
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public TileSuit getSuit() {
		return this.suit;
	}

	public TileValue getValue() {
		return this.value;
	}

	public boolean isJihai() {
		return this.value.isJihai();
	}

	public boolean isNumbered() {
		return !this.isJihai();
	}

	public boolean isTermHon() {
		return this.value.isTermHon();
	}

	/**
	 * Returns true if the tile shares the same suit and has a value that is
	 * adjacent to this tile's in sequence. Only applies to numbered tiles.
	 */
	public boolean isAdjacentTo(Tile tile) {
		if (this.suit != TileSuit.JIHAI && this.suit == tile.getSuit()) {
			int diff = this.value.toInteger() - tile.getValue().toInteger();
			return (Math.abs(diff) == 1);
		}
		return false;
	}

}