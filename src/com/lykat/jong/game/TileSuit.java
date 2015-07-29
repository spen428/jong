package com.lykat.jong.game;

public enum TileSuit {
	WANZU, PINZU, SOUZU, JIHAI;

	public boolean isJihai() {
		return (this == JIHAI);
	}

	@Override
	public String toString() {
		switch (this) {
		case JIHAI:
			return "";
		case PINZU:
			return "Pin";
		case SOUZU:
			return "Sou";
		case WANZU:
			return "Wan";
		default:
			return "null";
		}
	}

}