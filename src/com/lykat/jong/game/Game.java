package com.lykat.jong.game;


/**
 * Represents a game of Mahjong. Responsible for distributing tiles to players
 * and handling tile calls.
 * 
 * @author lykat
 *
 */
public class Game {

	private class Round {
		private TileValue roundWind;
		private int round;
		private int bonus;

		public Round() {
			this.roundWind = TileValue.TON;
			this.round = 1;
			this.bonus = 0;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(roundWind.toString());
			sb.append(" ");
			sb.append(round);
			if (bonus > 0) {
				sb.append("Bonus ");
				sb.append(bonus);
			}
			return sb.toString();
		}

		public TileValue getRoundWind() {
			return roundWind;
		}

		void setRoundWind(TileValue roundWind) {
			this.roundWind = roundWind;
		}

		public int getRound() {
			return round;
		}

		public int getBonus() {
			return bonus;
		}

		public void incrementRound() {
			round++;
		}

		public void incrementBonus() {
			bonus++;
		}

		public void resetBonus() {
			bonus = 0;
		}

	}

	private final String name;
	private final RuleSet ruleSet;
	private final Wall wall;
	private final Player[] players;
	private final Round round;
	private int numRiichiSticks;
	private int turn;

	public Game(String name, RuleSet ruleSet, Player... players) {
		this.name = name;
		this.ruleSet = ruleSet;
		this.players = players;
		this.wall = new Wall(ruleSet.getTileSet(),
				ruleSet.getNumDeadWallDraws());
		this.round = new Round();
		this.numRiichiSticks = 0;
		this.turn = 0;
	}

	/**
	 * Returns the next wind in turn given the current wind. For rounds that go
	 * past PEI, the dragons are used as round wind indicators, starting at CHUN
	 * and ending at HATSU. If HATSU is entered as the current wind, it is
	 * assumed this is the point at which it wraps around, so TON is returned.
	 * 
	 * @param currentWind
	 *            the current wind indicator. If this is a dragon tile, it is
	 *            assumed that a round indicator is desired, and so the
	 *            'seatWind' parameter is ignored.
	 * @param seatWind
	 *            whether to treat the current wind as a round indicator or a
	 *            seat indicator. If it is a seat indicator, PEI wraps around to
	 *            TON, otherwise PEI continues onto CHUN.
	 * @return the next wind indicator in sequence, or null if an invalid input
	 *         was given.
	 */
	public TileValue nextWind(TileValue currentWind, boolean seatWind) {
		switch (currentWind) {
		case TON:
			return TileValue.NAN;
		case NAN:
			return TileValue.SHAA;
		case SHAA:
			return TileValue.PEI;
		case PEI:
			if (seatWind) {
				return TileValue.TON;
			} else {
				return TileValue.CHUN;
			}
		case CHUN:
			return TileValue.HAKU;
		case HAKU:
			return TileValue.HATSU;
		case HATSU:
			return TileValue.TON;
		default:
			return null;
		}
	}

	public String getName() {
		return name;
	}

	public RuleSet getRuleSet() {
		return ruleSet;
	}

	public Wall getWall() {
		return wall;
	}

	public Player[] getPlayers() {
		return players;
	}

	public Round getRound() {
		return round;
	}

	public int getNumRiichiSticks() {
		return numRiichiSticks;
	}

	void incrementNumRiichiSticks() {
		numRiichiSticks++;
	}

	void decrementNumRiichiSticks() {
		numRiichiSticks--;
	}

	public Player getTurn() {
		return players[turn];
	}

	public int getTurnIndex(Player player) {
		for (int i = 0; i < players.length; i++) {
			if (players[i] == player) {
				return i;
			}
		}
		return -1;
	}

	boolean setTurn(Player player) {
		int turn = getTurnIndex(player);
		if (turn > -1) {
			this.turn = turn;
			return true;
		}
		return false;
	}

	void nextTurn() {
		turn = (turn + 1) % players.length;
	}

}
