package com.lykat.jong.game;

public class RuleSet {

	public enum GameType {
		RIICHI_FOUR_PLAYER, RIICHI_THREE_PLAYER, RIICHI_TWO_PLAYER, WASHIZU_FOUR_PLAYER, MINEFIELD_TWO_PLAYER;
	}

	private final GameType gameType;
	private final int numPlayers;
	private final int numDeadWallDraws;

	public RuleSet(GameType gameType) {
		super();

		if (gameType == null) {
			throw new IllegalArgumentException(
					"RuleSet's GameType cannot be null.");
		}

		this.gameType = gameType;

		switch (gameType) {
		case MINEFIELD_TWO_PLAYER:
		case RIICHI_TWO_PLAYER:
			this.numDeadWallDraws = 4;
			this.numPlayers = 2;
			break;
		case WASHIZU_FOUR_PLAYER:
		case RIICHI_FOUR_PLAYER:
			this.numDeadWallDraws = 4;
			this.numPlayers = 4;
			break;
		case RIICHI_THREE_PLAYER:
			this.numDeadWallDraws = 8;
			this.numPlayers = 3;
			break;
		default:
			this.numDeadWallDraws = 0;
			this.numPlayers = 0;
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

}
