package com.lykat.jong.game;

public enum TileValue {
	II(1), RYAN(2), SAN(3), SUU(4), UU(5), RYUU(6), CHII(7), PAA(8), CHUU(9), TON(
			10), NAN(11), SHAA(12), PEI(13), CHUN(14), HAKU(15), HATSU(16);

	private final int value;

	TileValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		if (this.isJihai()) {
			switch (this) {
			case TON:
				return "Ton";
			case NAN:
				return "Nan";
			case SHAA:
				return "Shaa";
			case PEI:
				return "Pei";
			case HAKU:
				return "Haku";
			case HATSU:
				return "Hatsu";
			case CHUN:
				return "Chun";
			default:
				return "null";
			}
		} else {
			return "" + this.value;
		}
	}

	public boolean isJihai() {
		return this.value >= 10;
	}

	public boolean isNumbered() {
		return !isJihai();
	}

	public boolean isTermHon() {
		return isJihai() || this.value == 1 || this.value == 9;
	}

	public int toInteger() {
		return this.value;
	}

	public boolean isDragon() {
		switch (this) {
		case CHUN:
		case HAKU:
		case HATSU:
			return true;
		default:
			return false;
		}
	}

	public boolean isWind() {
		switch (this) {
		case TON:
		case NAN:
		case SHAA:
		case PEI:
			return true;
		default:
			return false;
		}
	}

}