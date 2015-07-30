package com.lykat.jong.game;

import java.util.ArrayList;

import com.lykat.jong.util.Sorter;

/**
 * Represents a player in a {@link Game}.
 * 
 * @author lykat
 *
 */
public class Player {

	private final String name;
	private final ArrayList<Tile> hand;

	private final ArrayList<Tile> discards;

	/** Visible melds. Includes open melds, closed kan, and removed bonus tiles. */
	private final ArrayList<Meld> melds;

	private Tile tsumoHai;
	private boolean riichi;
	private int points;
	private TileValue seatWind;

	public Player(String name) {
		super();

		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Player name cannot be blank.");
		}

		this.name = name.trim();
		this.hand = new ArrayList<Tile>();
		this.discards = new ArrayList<Tile>();
		this.melds = new ArrayList<Meld>();
		this.tsumoHai = null;
		this.riichi = false;
		this.points = 0;
		this.seatWind = null;
	}

	/**
	 * Discard the tile at the given index of the player's hand. Subsequently
	 * moves the 'tsumoHai' into the hand and sets tsumoHai to null. Only
	 * allowed if the player has a tsumoHai.
	 * 
	 * @return false if the player is not allowed to discard.
	 */
	public boolean discard(int index) {
		if (tsumoHai != null) {
			Tile tile = hand.remove(index);
			discard(tile);
			hand.add(tsumoHai);
			tsumoHai = null;
			return true;
		}
		return false;
	}

	/**
	 * Discard the player's 'tsumoHai'.
	 * 
	 * @return false if the player did not have a tsumoHai.
	 */
	public boolean tsumoKiri() {
		if (tsumoHai != null) {
			discard(tsumoHai);
			tsumoHai = null;
			return true;
		}
		return false;
	}

	/**
	 * Deals the given tile to the player, setting it as their 'tsumoHai'. Can
	 * only be done if the player is not currently holding a tsumoHai.
	 * 
	 * @param tile
	 *            the tile to deal to the player.
	 * @return false if the player could not be dealt the tile.
	 * 
	 */
	public boolean deal(Tile tile) {
		if (tsumoHai == null) {
			this.tsumoHai = tile;
			return true;
		}
		return false;
	}

	/**
	 * Declares that a new round has started, resetting any round-specific flags
	 * such as riichi declaration and clearing the player's hand.
	 * <p>
	 * Does not advance the player's seat wind as this does not necessarily
	 * change every round.
	 */
	public void nextRound() {
		riichi = false;
		hand.clear();
		melds.clear();
		discards.clear();
		tsumoHai = null;
	}

	/**
	 * Places the given tile into the player's discard pond.
	 * 
	 * @param tile
	 *            the tile to discard.
	 */
	private void discard(Tile tile) {
		if (tile == null) {
			throw new IllegalArgumentException("Discard tile cannot be null.");
		}
		discards.add(tile);
	}

	/**
	 * Sorts the players hand.
	 */
	public void sortHand() {
		Sorter.sort(hand);
	}

	public Tile getTsumoHai() {
		return tsumoHai;
	}

	public boolean isRiichi() {
		return riichi;
	}

	/**
	 * Declare riichi, setting the riichi flag to true and removing 1000 points
	 * from the player. Cannot be done if the player has already declared riichi
	 * or has less than 1000 points.
	 * 
	 * @return Whether declaration was successful.
	 */
	public boolean declareRiichi() {
		if (!isRiichi() && getPoints() >= 1000) {
			removePoints(1000);
			riichi = true;
			return true;
		}
		return false;
	}

	public int getPoints() {
		return points;
	}

	public void addPoints(int points) {
		if (points < 0) {
			throw new IllegalArgumentException(
					"Cannot add less than zero points.");
		}
		this.points += points;
	}

	public void removePoints(int points) {
		if (points < 0) {
			throw new IllegalArgumentException(
					"Cannot remove less than zero points.");
		}
		this.points -= points;
	}

	public TileValue getSeatWind() {
		return seatWind;
	}

	public void setSeatWind(TileValue seatWind) {
		this.seatWind = seatWind;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Tile> getHand() {
		return hand;
	}

	public ArrayList<Meld> getMelds() {
		return melds;
	}

	public ArrayList<Tile> getDiscards() {
		return discards;
	}

}
