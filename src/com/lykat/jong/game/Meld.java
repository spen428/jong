package com.lykat.jong.game;

import com.lykat.jong.util.Sorter;

public class Meld {

	public enum MeldType {
		SHUNTSU_OPEN, KOUTSU_OPEN, KANTSU_OPEN, KANTSU_EXTENDED, KANTSU_CLOSED, SHUNTSU_CLOSED, KOUTSU_CLOSED, TOITSU, INVALID, BONUS_NORTH;

		public boolean isOpen() {
			switch (this) {
			case KANTSU_EXTENDED:
			case KANTSU_OPEN:
			case KOUTSU_OPEN:
			case SHUNTSU_OPEN:
				return true;
			default:
				return false;
			}
		}

		public boolean isKan() {
			switch (this) {
			case KANTSU_EXTENDED:
			case KANTSU_OPEN:
			case KANTSU_CLOSED:
				return true;
			default:
				return false;
			}
		}
	}

	public enum MeldSource {
		LEFT, ACROSS, RIGHT, SELF;
	}

	private final Tile[] tiles;
	private final Tile callTile;
	private final MeldType type;
	private final MeldSource meldSource;

	/**
	 * Represents a declared meld.
	 * 
	 * @param tiles
	 *            the tiles that make up this meld
	 * @param callTile
	 *            the tile that was called to form the meld
	 * @param meldSource
	 *            the source of the meld
	 * @param type
	 *            the meld type
	 */
	public Meld(Tile[] tiles, Tile callTile, MeldSource meldSource,
			MeldType type) {
		super();

		if (tiles == null) {
			throw new IllegalArgumentException(
					"Meld tile array cannot be null.");
		} else if (tiles.length < 3 || tiles.length > 4) {
			throw new IllegalArgumentException(
					"Meld tile array must contain at between 3 and 4 tiles.");
		} else if (callTile == null && type.isOpen()) {
			throw new IllegalArgumentException(
					"Call tile cannot be null with an open meld.");
		}

		this.tiles = tiles;
		this.callTile = callTile;
		this.type = type;
		this.meldSource = MeldSource.SELF;
		Sorter.sort(tiles);
	}

	public Meld(Tile[] tiles, MeldType type) {
		this(tiles, null, MeldSource.SELF, type);
	}

	@Override
	public Meld clone() {
		Tile[] newTiles = new Tile[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			newTiles[i] = tiles[i].clone();
		}
		return new Meld(newTiles, this.callTile, this.meldSource, this.type);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Meld) {
			Meld meld = (Meld) obj;
			if (meld.getTiles().length == this.tiles.length
					&& meld.getType() == this.type) {
				for (int i = 0; i < this.tiles.length; i++) {
					if (!this.tiles[i].equals(meld.getTiles()[i])) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Meld : " + this.type.toString() + " {\n");
		for (Tile t : this.tiles) {
			sb.append("  " + t.toString() + "\n");
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public Tile[] getTiles() {
		return tiles;
	}

	public Tile getCallTile() {
		return callTile;
	}

	public MeldType getType() {
		return type;
	}

	public boolean isOpen() {
		return type.isOpen();
	}

	public MeldSource getMeldSource() {
		return meldSource;
	}

	/**
	 * Returns a new Meld that is an extended version of this one. Only valid
	 * for extending an open Koutsu into an extended Kantsu, or for adding a new
	 * bonus tile to a "meld" of declared bonus tiles.
	 * 
	 * @param newTile
	 *            the Tile to extend the meld with
	 * @return the new meld
	 * @throws IllegalStateException
	 *             if trying to extend an incompatible meld, or extending a meld
	 *             with an incompatible tile.
	 */
	public Meld extend(Tile newTile) {
		if (!newTile.equals(tiles[0])) {
			throw new IllegalStateException(
					"Tile cannot be used to extend the meld.");
		}

		MeldType newType;
		if (type == MeldType.BONUS_NORTH) {
			newType = type;
		} else if (type == MeldType.KOUTSU_OPEN) {
			newType = MeldType.KANTSU_EXTENDED;
		} else {
			throw new IllegalStateException("MeldType cannot be extended.");
		}

		Tile[] newTiles = new Tile[tiles.length + 1];
		newTiles[newTiles.length - 1] = newTile;
		for (int i = 0; i < newTiles.length - 1; i++) {
			newTiles[i] = tiles[i];
		}

		return new Meld(newTiles, this.callTile, this.meldSource, newType);
	}

}
