package com.lykat.jong.calc;

/**
 * Enumeration of Yaku and Minipoints conditions.
 * 
 * @author lykat
 *
 */
public enum Yaku {
	/* Fu */
	FU_HONOUR_PAIR(2,0,0),
	FU_SINGLE_WAIT(2,0,0),
	FU_OPEN_PINFU(2,0,0),
	FU_CLOSED_RON(30,0,0),
	FU_OPEN_RON(20,0,0),
	FU_CHIITOITSU(25,0,0),
	FU_TSUMO(22,0,0),
	FU_KOUTSU_MID_OPEN(2,0,0),
	FU_KOUTSU_MID_CLOSED(4,0,0),
	FU_KOUTSU_HON_OPEN(4,0,0),
	FU_KOUTSU_HON_CLOSED(8,0,0),
	FU_KANTSU_MID_OPEN(8,0,0),
	FU_KANTSU_MID_CLOSED(16,0,0),
	FU_KANTSU_HON_OPEN(16,0,0),
	FU_KANTSU_HON_CLOSED(32,0,0),

	/* 1/2 Han */
	RIICHI(1),
	DOUBLE_RIICHI(2),
	IPPATSU(1),
	MENZEN_TSUMO(1),
	PINFU(1),
	IIPEIKOU(1),
	TANYAO(1),
	SANSHOKU_DOUJUN(1),
	ITTSUU(1),
	YAKUHAI_CHUN(1),
	YAKUHAI_HAKU(1),
	YAKUHAI_HATSU(1),
	JIKAZE_TON(1),
	JIKAZE_NAN(1),
	JIKAZE_SHAA(1),
	JIKAZE_PEI(1),
	BAKAZE_TON(1),
	BAKAZE_NAN(1),
	BAKAZE_SHAA(1),
	BAKAZE_PEI(1),
	BAKAZE_CHUN(1),
	BAKAZE_HAKU(1),
	BAKAZE_HATSU(1),
	CHANTAIYAO(1),
	RINSHAN_KAIHOU(1),
	CHANKAN(1),
	HAITEI_RAOYUE(1),
	HOUTEI_RAOYUI(1),
	
	/* 2/3 Han */
	CHIITOITSU(2),
	SAN_ANKOU(2),
	SANSHOKU_DOUKOU(2),
	SAN_KANTSU(2),
	TOITOI(2),
	HONITSU(2),
	MENZEN_HONITSU(3),
	SHOUSANGEN(2),
	HONROUTOU(2),
	JUN_CHANTAIYAO(2),
	MENZEN_JUNCHAN(3),
	RYANPEIKOU(3),
	
	/* 5/6 Han */
	RENHOU(5),
	CHINITSU(5),
	MENZEN_CHINITSU(6),

	/* Yakuman */
	YM_KOKUSHI_MUSOU(0, 0, 1),
	YM_KOKUSHI_MUSOU_13_MAN_MACHI(0, 0, 2),
	YM_CHUUREN_POUTOU(0,0,1),
	YM_JUNSEI_CHUUREN_POUTOU(0,0,2),
	YM_TENHOU(0,0,1),
	YM_CHIIHOU(0,0,1),
	YM_SUU_ANKOU(0,0,1),
	YM_SUU_ANKOU_TANKI(0,0,2),
	YM_SUU_KANTSU(0,0,1),
	YM_RYUUIISOU(0,0,1),
	YM_CHINROUTOU(0,0,1),
	YM_TSUUIISOU(0,0,1),
	YM_DAICHIISEI(0,0,2),
	YM_DANSANGEN(0,0,1),
	YM_SHOUSUUSHII(0,0,1),
	YM_DAISUUSHII(0,0,2);

	private final int fu, han, yakuman;

	Yaku(int fu, int han, int yakuman) {
		if (fu < 0 || han < 0 || yakuman < 0) {
			throw new IllegalArgumentException(
					"Fu, han, and yakuman values cannot be less than zero.");
		}

		this.fu = fu;
		this.han = han;
		this.yakuman = yakuman;
	}

	Yaku(int han) {
		this(0, han, 0);
	}

	public int getFu() {
		return fu;
	}

	public int getHan() {
		return han;
	}

	public int getYakuman() {
		return yakuman;
	}

}
