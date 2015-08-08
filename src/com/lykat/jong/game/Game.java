package com.lykat.jong.game;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.lykat.jong.game.GameManager.GameState;

/**
 * Represents a game of Mahjong. Responsible for distributing tiles to players
 * and handling tile calls.
 * 
 * @author lykat
 *
 */
public class Game {

	public static final Logger LOGGER = Logger.getLogger("GameManager");

	private final String name;
	private final RuleSet ruleSet;
	private final Wall wall;
	private final Player[] players;
	private final Round round;
	private int numRiichiSticks;
	private int turn;
	private int turnCounter;
	private boolean fourWindsAbort;
	private boolean deadDraw;
	private GameState gameState;

	public Game(String name, RuleSet ruleSet) {
		this.name = name;
		this.ruleSet = ruleSet;
		this.players = new Player[ruleSet.getNumPlayers()];
		this.wall = new Wall(ruleSet.getTileSet(),
				ruleSet.getNumDeadWallDraws());
		this.round = new Round();
		this.numRiichiSticks = 0;
		this.turn = 0;
		this.turnCounter = 0;
		this.fourWindsAbort = ruleSet.isFourWindsAbort();
		this.deadDraw = false;
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
	public static TileValue nextWind(TileValue currentWind, boolean seatWind) {
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

	boolean newPlayer(String name) {
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) {
				players[i] = new Player(name);
				players[i].addPoints(ruleSet.getStartingPoints());
				return true;
			}
		}
		return false;
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

	public Player getTurn() {
		return players[turn];
	}

	public Player getDealer() {
		for (Player p : players) {
			if (p.getSeatWind() == TileValue.TON) {
				return p;
			}
		}
		return null;
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
		turnCounter++;
	}

	void tsumo(Player winner, int paymentAll) {
		if (winner != this.getDealer()) {
			throw new IllegalArgumentException("Can only tsumo with an 'all' "
					+ "payment if the winner is the dealer.");
		}
		tsumo(winner, paymentAll, 0);
	}

	boolean tsumo(Player winner, int paymentKo, int paymentOya) {
		int totalWinnings = 0;
		boolean buttobi = false;
		Player dealer = this.getDealer();

		/* Apply bonus */
		int bonusPoints = round.getBonus() * 100;
		paymentKo += bonusPoints;
		paymentOya += bonusPoints;
		/* Take points */
		for (Player p : players) {
			if (p != winner) {
				if (p == dealer) {
					p.removePoints(paymentOya);
					totalWinnings += paymentOya;
				} else {
					p.removePoints(paymentKo);
					totalWinnings += paymentKo;
				}
			}
			if (!buttobi && p.getPoints() < 0) {
				buttobi = true;
			}
		}

		/* Give winner any Riichi sticks */
		while (numRiichiSticks > 0) {
			totalWinnings += 1000;
			numRiichiSticks--;
		}

		winner.addPoints(totalWinnings);
		return buttobi;
	}

	/**
	 * 
	 * @param winner
	 * @param loser
	 * @param payment
	 * @return true if any player dropped below 0 points
	 */
	boolean ron(Player winner, Player loser, int payment) {
		int totalWinnings = 0;

		/* Apply bonus and take points */
		payment += round.getBonus() * 300;
		loser.removePoints(payment);
		totalWinnings += payment;

		/* Give winner any Riichi sticks */
		while (numRiichiSticks > 0) {
			totalWinnings += 1000;
			numRiichiSticks--;
		}

		winner.addPoints(totalWinnings);
		return (loser.getPoints() < 0);
	}

	/**
	 * Called to apply a chombo penalty to the given player. Returns any Riichi
	 * payments made this round.
	 * 
	 * @param loser
	 *            the player to chombo
	 * @return true if the player dropped below 0 points
	 */
	boolean chombo(Player loser) {
		Player dealer = this.getDealer();
		int totalLosses = 0;

		for (Player p : players) {
			if (p.isRiichi()) {
				p.addPoints(1000);
				numRiichiSticks--;
			}

			if (p == loser) {
				continue;
			} else if (loser == dealer || p == dealer) {
				p.addPoints(8000);
				totalLosses += 8000;
			} else {
				p.addPoints(4000);
				totalLosses += 4000;
			}
		}

		loser.removePoints(totalLosses);
		return (loser.getPoints() < 0);
	}

	/**
	 * Returns true if it is still the first un-interrupted go-around of the
	 * hand.
	 */
	public boolean isFirstGoAround() {
		if (turnCounter < players.length) {
			for (Player p : players) {
				if (p.getMelds().size() > 0) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	void incrementBonusCounter() {
		round.incrementBonus();
	}

	void resetBonusCounter() {
		round.resetBonus();
	}

	/**
	 * Rotate dealers, incrementing the round as necessary. Does not reset the
	 * bonus counter, as a seat change could be because of no-ten.
	 * 
	 * @return Returns true if the round wind changed.
	 */
	boolean rotateDealers() {
		for (Player p : players) {
			TileValue nextWind = Game.nextWind(p.getSeatWind(), true);
			p.setSeatWind(nextWind);
		}
		this.setTurn(getDealer());
		return round.nextRound();
	}

	public int getNumPlayersRiichi() {
		int num = 0;
		for (Player p : players) {
			if (p.isRiichi()) {
				num++;
			}
		}
		return num;
	}

	public boolean isFourWindsAbort() {
		return fourWindsAbort;
	}

	void setFourWindsAbort(boolean fourWindsAbort) {
		this.fourWindsAbort = fourWindsAbort;
	}

	public Tile getFirstDiscard() {
		return this.getDealer().getDiscards().get(0);
	}

	public int getTurnCounter() {
		return turnCounter;
	}

	public boolean isMaxKan() {
		boolean onePlayerFourKan = false;
		int totalKan = 0;
		for (Player p : players) {
			int playerKan = 0;
			for (Meld m : p.getMelds()) {
				if (m.getType().isKan()) {
					playerKan++;
				}
			}
			if (playerKan == 4) {
				onePlayerFourKan = true;
			}
			totalKan += playerKan;
		}

		if (onePlayerFourKan && ruleSet.isFourKanAbort()) {
			return (totalKan > 4);
		} else {
			return (totalKan > 3);
		}
	}

	public Player[] getPlayersInTurnStartingAt(Player start) {
		Player[] inTurn = new Player[players.length];
		int idx = this.getTurnIndex(start);
		for (int i = 0; i < players.length; i++) {
			inTurn[i] = players[idx];
			idx = (idx + 1) % players.length;
		}
		return inTurn;
	}

	void resetFourWindsAbort() {
		this.fourWindsAbort = ruleSet.isFourWindsAbort();
	}

	public boolean isDeadDraw() {
		return deadDraw;
	}

	void setDeadDraw(boolean deadDraw) {
		this.deadDraw = deadDraw;
	}

	public GameState getGameState() {
		return gameState;
	}

	void setGameState(GameState gameState) {
		LOGGER.log(Level.FINER, "Gamestate change: " + this.gameState + " ==> "
				+ gameState);
		this.gameState = gameState;
	}

	void interruptPlayers() {
		for (Player p : players) {
			p.setInterrupted(true);
		}
	}

	/**
	 * Called BEFORE the start of a new round to see whether the game is over.
	 */
	public boolean isGameOver() {
		int targetPoints = ruleSet.getTargetPoints();
		boolean pointTargetHit = false;
		boolean buttobi = false;
		for (Player p : players) {
			if (!pointTargetHit && p.getPoints() >= targetPoints) {
				pointTargetHit = true;
			} else if (!buttobi && p.getPoints() < 0) {
				buttobi = true;
			} else if (buttobi && pointTargetHit) {
				break;
			}
		}

		if (buttobi && ruleSet.isButtobiEnds()) {
			return true;
		} else if (pointTargetHit && isOorasu()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOorasu() {
		int numRounds = ruleSet.getNumRounds();
		int roundWind = round.getRoundWindAsInteger();
		int currentRound = round.getRound();

		return (numRounds == roundWind && currentRound == 4)
				|| (roundWind > numRounds);
	}
}
