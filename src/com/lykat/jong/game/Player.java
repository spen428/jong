package com.lykat.jong.game;

import java.util.ArrayList;
import java.util.Collections;

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
	boolean discard(int index) {
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
	boolean tsumoKiri() {
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
	boolean deal(Tile tile) {
		if (tsumoHai == null) {
			this.tsumoHai = tile;
			return true;
		}
		return false;
	}

	/**
	 * Deals the given tiles to the player's hand. Can only be done if the
	 * player's hand is currently empty (as at the start of a new round). The
	 * number of tiles dealt must be equal to 13.
	 * 
	 * @return false if the player's hand already contains tiles.
	 */
	boolean deal(Tile[] tiles) {
		if (hand.size() > 0) {
			return false;
		}

		if (tiles == null) {
			throw new IllegalArgumentException("Cannot deal a null tile array.");
		} else if (tiles.length != 13) {
			throw new IllegalArgumentException(
					"The number of tiles dealt to a player's hand at "
							+ "the start of a round must be exactly 13.");
		}

		for (Tile t : tiles) {
			if (t == null) {
				throw new IllegalArgumentException(
						"Cannot deal a null tile to the player's hand.");
			}
			hand.add(t);
		}
		return true;
	}

	/**
	 * Declares that a new round has started, resetting any round-specific flags
	 * such as riichi declaration and clearing the player's hand.
	 * <p>
	 * Does not advance the player's seat wind as this does not necessarily
	 * change every round.
	 */
	void nextRound() {
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
	void discard(Tile tile) {
		if (tile == null) {
			throw new IllegalArgumentException("Discard tile cannot be null.");
		}
		discards.add(tile);
	}

	/**
	 * Sorts the players hand.
	 */
	void sortHand() {
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
	boolean declareRiichi() {
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

	void addPoints(int points) {
		if (points < 0) {
			throw new IllegalArgumentException(
					"Cannot add less than zero points.");
		}
		this.points += points;
	}

	void removePoints(int points) {
		if (points < 0) {
			throw new IllegalArgumentException(
					"Cannot remove less than zero points.");
		}
		this.points -= points;
	}

	public TileValue getSeatWind() {
		return seatWind;
	}

	void setSeatWind(TileValue seatWind) {
		this.seatWind = seatWind;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Tile> getHand() {
		return (ArrayList<Tile>) Collections.unmodifiableList(hand);
	}

	public ArrayList<Meld> getMelds() {
		return (ArrayList<Meld>) Collections.unmodifiableList(melds);
	}

	public ArrayList<Tile> getDiscards() {
		return (ArrayList<Tile>) Collections.unmodifiableList(discards);
	}

}
