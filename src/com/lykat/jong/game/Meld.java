package com.lykat.jong.game;

import com.lykat.jong.util.Sorter;

public class Meld {

	public enum MeldType {
		SHUNTSU_OPEN, KOUTSU_OPEN, KANTSU_OPEN, KANTSU_EXTENDED, KANTSU_CLOSED;

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
	}

	private final Tile[] tiles;
	private final Tile callTile;
	private final MeldType type;

	public Meld(Tile[] tiles, Tile callTile, MeldType type) {
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
		Sorter.sort(tiles);
	}

	@Override
	public Meld clone() {
		Tile[] newTiles = new Tile[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			newTiles[i] = tiles[i].clone();
		}
		return new Meld(newTiles, this.callTile, this.type);
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
}
