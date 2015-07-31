package com.lykat.jong.calc;

public enum Yaku {
	KOKUSHI_MUSOU(-1), KOKUSHI_MUSOU_13_MAN_MACHI(-2);

	private final int han;

	Yaku(int han) {
		this.han = han;
	}

	public int getHan() {
		return han;
	}

}
