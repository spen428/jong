package com.lykat.jong.game;

public class RuleSet {

	// TODO: Builder class

	public enum GameType {
		RIICHI_FOUR_PLAYER, RIICHI_THREE_PLAYER, RIICHI_TWO_PLAYER, WASHIZU_FOUR_PLAYER, MINEFIELD_TWO_PLAYER;
	}

	private final GameType gameType;
	private final int numPlayers, numDeadWallDraws, maxSimultanousRon;
	private final boolean buttobiEnds, fourWindsAbort, allRiichiAbort,
			fourKanAbort;

	public RuleSet(GameType gameType) {
		super();

		if (gameType == null) {
			throw new IllegalArgumentException(
					"RuleSet's GameType cannot be null.");
		}

		this.gameType = gameType;
		this.buttobiEnds = true;

		switch (gameType) {
		case MINEFIELD_TWO_PLAYER:
		case RIICHI_TWO_PLAYER:
			this.numDeadWallDraws = 4;
			this.numPlayers = 2;
			this.maxSimultanousRon = 1;
			this.fourKanAbort = false;
			this.fourWindsAbort = false;
			this.allRiichiAbort = false;
			break;
		case WASHIZU_FOUR_PLAYER:
			this.maxSimultanousRon = 1;
			this.numDeadWallDraws = 4;
			this.numPlayers = 4;
			this.fourKanAbort = true;
			this.fourWindsAbort = true;
			this.allRiichiAbort = true;
			break;
		case RIICHI_FOUR_PLAYER:
			this.numDeadWallDraws = 4;
			this.numPlayers = 4;
			this.maxSimultanousRon = 2;
			this.fourKanAbort = true;
			this.fourWindsAbort = true;
			this.allRiichiAbort = true;
			break;
		case RIICHI_THREE_PLAYER:
			this.numDeadWallDraws = 8;
			this.numPlayers = 3;
			this.maxSimultanousRon = 2;
			this.fourKanAbort = false;
			this.fourWindsAbort = false;
			this.allRiichiAbort = false;
			break;
		default:
			this.numDeadWallDraws = 0;
			this.numPlayers = 0;
			this.maxSimultanousRon = 0;
			this.fourKanAbort = false;
			this.fourWindsAbort = false;
			this.allRiichiAbort = false;
			break;
		}
	}

	public Tile[] getTileSet() {
		switch (gameType) {
		case MINEFIELD_TWO_PLAYER:
		case WASHIZU_FOUR_PLAYER:
		case RIICHI_FOUR_PLAYER:
			return Wall.fourPlayerTileSet();
		case RIICHI_THREE_PLAYER:
			return Wall.threePlayerTileSet();
		case RIICHI_TWO_PLAYER:
			return Wall.twoPlayerTileSet();
		default:
			return null;
		}
	}

	public GameType getGameType() {
		return gameType;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public int getNumDeadWallDraws() {
		return numDeadWallDraws;
	}

	public int getMaxSimultanousRon() {
		return maxSimultanousRon;
	}

	public boolean isHeadBump() {
		return (maxSimultanousRon == 1);
	}

	public boolean isButtobiEnds() {
		return buttobiEnds;
	}

	public boolean isFourWindsAbort() {
		return fourWindsAbort;
	}

	public boolean isAllRiichiAbort() {
		return allRiichiAbort;
	}

	public boolean isFourKanAbort() {
		return fourKanAbort;
	}

}
