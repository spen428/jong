package com.lykat.jong.game;

import java.util.ArrayList;

import com.lykat.jong.game.Meld.MeldType;
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
	protected boolean riichi;
	protected int points;
	private TileValue seatWind;
	protected boolean madeCall;
	protected boolean interrupted;

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
		this.madeCall = false;
		this.interrupted = false;
	}

	/**
	 * Discard the tile at the given index of the player's hand. Subsequently
	 * moves the 'tsumoHai' into the hand and sets tsumoHai to null. Only
	 * allowed if the player has a tsumoHai or a meld call has just been made.
	 * 
	 * @param index
	 *            the index of the tile to discard from the hand. If set to -1,
	 *            the player will tsumokiri instead.
	 */
	void discard(int index) {
		if (index == -1) {
			if (tsumoHai == null) {
				throw new IllegalStateException("The player has not drawn a "
						+ "tile, so is not allowed to tsumokiri.");
			}
			addToDiscardPond(tsumoHai);
			tsumoHai = null;
		} else {
			if (tsumoHai == null) {
				if (!madeCall) {
					throw new IllegalStateException(
							"The player has not drawn a "
									+ "tile, so is not allowed to discard one.");
				}
				madeCall = false;
			} else {
				hand.add(tsumoHai);
				tsumoHai = null;
			}
			Tile tile = hand.remove(index);
			addToDiscardPond(tile);
		}
		if (isRiichi() && !isInterrupted()) {
			setInterrupted(true);
		}
	}

	/**
	 * Discard the player's 'tsumoHai'
	 */
	protected void tsumoKiri() {
		discard(-1);
	}

	/**
	 * Deals the given tile to the player, setting it as their 'tsumoHai'. Can
	 * only be done if the player is not currently holding a tsumoHai.
	 * 
	 * @param tile
	 *            the tile to deal to the player.
	 * 
	 */
	protected void deal(Tile tile) {
		if (tsumoHai != null) {
			throw new IllegalStateException("Cannot deal a tile to a "
					+ "player when they already have a tsumohai.");
		}
		this.tsumoHai = tile;
	}

	/**
	 * Deals the given tiles to the player's hand. Can only be done if the
	 * player's hand is currently empty (as at the start of a new round). The
	 * number of tiles dealt must be equal to 13.
	 */
	protected void deal(Tile[] tiles) {
		if (hand.size() > 0) {
			throw new IllegalStateException("Cannot deal a hand to a player "
					+ "that already has more than 0 tiles in their hand. ");
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
		interrupted = false;
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
	protected void addToDiscardPond(Tile tile) {
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
	protected boolean declareRiichi() {
		if (!isRiichi() && getPoints() >= 1000) {
			removePoints(1000);
			riichi = true;
			interrupted = false;
			return true;
		}
		return false;
	}

	public int getPoints() {
		return points;
	}

	protected void addPoints(int points) {
		if (points < 0) {
			throw new IllegalArgumentException(
					"Cannot add less than zero points.");
		}
		this.points += points;
	}

	protected void removePoints(int points) {
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

	public boolean isInterrupted() {
		return interrupted;
	}

	void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Tile> getHand() {
		return new ArrayList<Tile>(hand);
	}

	public ArrayList<Meld> getMelds() {
		return new ArrayList<Meld>(melds);
	}

	public ArrayList<Tile> getDiscards() {
		return new ArrayList<Tile>(discards);
	}

	/**
	 * Adds the given meld to the player's melds. Removes any tiles that are
	 * part of the meld from the player's hand.
	 * 
	 * @param meld
	 */
	public void addMeld(Meld meld) {
		if (madeCall) {
			throw new IllegalStateException("The player has not yet "
					+ "discarded from a previous meld call.");
		}

		/* How many of the meld's tiles were in the player's hand? */
		int inHand = 0;
		Tile[] meldTiles = meld.getTiles();
		for (Tile tile : meldTiles) {
			if (hand.remove(tile)) {
				inHand++;
			}
		}

		/* Check validity based on inHand count */
		MeldType type = meld.getType();
		if (type == MeldType.KANTSU_CLOSED && inHand == 4) {
			melds.add(meld);
		} else if (type == MeldType.KANTSU_EXTENDED && inHand == 1) {
			/* Must not add this meld to melds; must update existing meld. */
			Tile tile = meld.getCallTile();
			extendKan(tile);
		} else if (type == MeldType.KANTSU_OPEN && inHand == 3) {
			madeCall = true;
			melds.add(meld);
		} else if (type == MeldType.KOUTSU_OPEN && inHand == 2) {
			madeCall = true;
			melds.add(meld);
		} else if (type == MeldType.SHUNTSU_OPEN && inHand == 2) {
			madeCall = true;
			melds.add(meld);
		} else {
			throw new IllegalStateException("Attempted to add an invalid "
					+ "meld to the player's melds: " + meld.toString());
		}
	}

	private void extendMeld(Tile newTile, MeldType meldType) {
		/* Find the meld to update */
		int index = 0;
		for (Meld m : melds) {
			if (m.getType() == meldType && m.getCallTile() == newTile) {
				break;
			}
			index++;
		}
		if (index >= melds.size()) {
			throw new IllegalStateException("Attempted to extend a kan "
					+ "that does not exist: " + newTile.toString());
		} else {
			Meld oldMeld = melds.get(index);
			Meld newMeld = oldMeld.extend(newTile);
			melds.remove(oldMeld);
			melds.add(index, newMeld);
		}
	}

	private void extendKan(Tile newTile) {
		extendMeld(newTile, MeldType.KOUTSU_OPEN);
	}

	/**
	 * Declare the given tile as a bonus tile, removing it from play and adding
	 * it to the player's "bonus" meld if it exists (creating it if it doesn't).
	 * 
	 * @param tile
	 *            the tile to declare
	 */
	public void declareBonusTile(Tile tile) {
		if (tile.getValue() == TileValue.PEI) {
			extendMeld(tile, MeldType.BONUS_NORTH);
		} else {
			throw new IllegalStateException("Cannot declare as bonus tile: "
					+ tile.toString());
		}
	}

	public Tile getLatestDiscard() {
		return discards.get(discards.size() - 1);
	}

	public void removeLatestDiscard() {
		discards.remove(getLatestDiscard());
	}

}
