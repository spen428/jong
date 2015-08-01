package com.lykat.jong.game;

class Round {

	private TileValue roundWind;
	private int round;
	private int roundCounter;
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

	/**
	 * Advance to the next round, incrementing the round counter and changing
	 * the round wind if necessary.
	 * 
	 * @return Returns true if the round wind changed.
	 */
	public boolean nextRound() {
		roundCounter++;
		round = (round + 1) % 4;
		if (round == 1) {
			roundWind = Game.nextWind(roundWind, false);
			return true;
		}
		return false;
	}

	public void incrementBonus() {
		bonus++;
	}

	public void resetBonus() {
		bonus = 0;
	}

	public int getRoundCounter() {
		return roundCounter;
	}

}