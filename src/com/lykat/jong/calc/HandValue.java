package com.lykat.jong.calc;

public class HandValue {

	/* Multipliers */
	public static final int MULTIPLIER_OYA_RON = 6;
	public static final int MULTIPLIER_KO_RON = 4;
	public static final int MULTIPLIER_OYA_TSUMO = 2;
	public static final int MULTIPLIER_KO_TSUMO_OYA = 2;
	public static final int MULTIPLIER_KO_TSUMO_KO = 1;

	/* Limits */
	public static final int BASE_VALUE_MANGAN = 2000;
	public static final int BASE_VALUE_HANEMAN = 3000;
	public static final int BASE_VALUE_BAIMAN = 4000;
	public static final int BASE_VALUE_SANBAIMAN = 6000;
	public static final int BASE_VALUE_YAKUMAN = 8000;

	private final int fu;
	private final int han;
	private final int baseValue;
	private final boolean isLimitHand;

	public HandValue(int fu, int han) {
		super();
		this.fu = ((fu + 9) / 10) * 10; // Round up 10
		this.han = han;
		if ((han == 4 && fu >= 40) || (han == 3 && fu >= 70)) {
			this.isLimitHand = true;
			this.baseValue = BASE_VALUE_MANGAN;
		} else if (han > 4) {
			this.isLimitHand = true;
			if (han > 12) {
				this.baseValue = BASE_VALUE_YAKUMAN * (han / 13);
			} else if (han > 9) {
				this.baseValue = BASE_VALUE_SANBAIMAN;
			} else if (han > 7) {
				this.baseValue = BASE_VALUE_BAIMAN;
			} else if (han > 5) {
				this.baseValue = BASE_VALUE_HANEMAN;
			} else {
				this.baseValue = BASE_VALUE_MANGAN;
			}
		} else {
			this.isLimitHand = false;
			this.baseValue = (int) (fu * Math.pow(2, 2 + han));
		}
	}

	public int getFu() {
		return fu;
	}

	public int getHan() {
		return han;
	}

	public boolean isLimitHand() {
		return isLimitHand;
	}

	public int getOyaRonValue() {
		return roundUpHundred(baseValue * MULTIPLIER_OYA_RON);
	}

	public int getKoRonValue() {
		return roundUpHundred(baseValue * MULTIPLIER_KO_RON);
	}

	public int getOyaTsumoValue() {
		return roundUpHundred(baseValue * MULTIPLIER_OYA_TSUMO);
	}

	public int getKoTsumoValueBig() {
		return roundUpHundred(baseValue * MULTIPLIER_KO_TSUMO_OYA);
	}

	public int getKoTsumoValueSmall() {
		return roundUpHundred(baseValue * MULTIPLIER_KO_TSUMO_KO);
	}

	private int roundUpHundred(int num) {
		return ((num + 99) / 100) * 100;
	}

}
