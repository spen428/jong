package com.lykat.jong.calc;

import java.util.ArrayList;

import com.lykat.jong.game.Meld;
import com.lykat.jong.game.Tile;
import com.lykat.jong.game.Meld.MeldType;

public class Hand {

	/**
	 * Returns true if a Kokushi Musou hand can be constructed from the given
	 * set of tiles.
	 */
	public static boolean canKokushiMusou(Tile[] tiles) {
		/* Keep track of orphans */
		ArrayList<Tile> got = new ArrayList<Tile>();

		/* Pass 1 - Find 13 unique */
		for (int i = 0; i < tiles.length; i++)
			if (tiles[i].isTermHon())
				if (!Hand.containsTile(tiles[i], got))
					got.add(tiles[i]);

		/* Pass 2 - Ensure there are 13 unique */
		if (got.size() != 13)
			return false;

		/* Pass 3 - Find the pair */
		for (int i = 0; i < tiles.length; i++) {
			if (!got.contains(tiles[i]) && tiles[i].isJihai()) {
				// System.out.println("Kokushi Musou");
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if a Chiitoitsu hand can be constructed from the given set
	 * of tiles.
	 */
	public static boolean canChiitoitsu(Tile[] tiles) {
		/* Keep track of pairs */
		ArrayList<Tile> got = new ArrayList<Tile>(7);

		/* Pass 1 - Find unique pairs */
		for (int i = 0; i < tiles.length; i++)
			for (int j = i; j < tiles.length; j++)
				if (i != j && tiles[i].equals(tiles[j]))
					if (!Hand.containsTile(tiles[i], got))
						got.add(tiles[i]);

		/* Pass 2 - Ensure there are 7 unique */
		if (got.size() >= 7) {
			// System.out.println("Chiitoitsu");
			return true;
		}

		return false;
	}

	/**
	 * Returns true if a standard mahjong hand can be constructed from the given
	 * set of tiles.
	 */
	public static boolean canRegularHand(Tile[] tiles) {
		ArrayList<Meld> melds = new ArrayList<Meld>();
		boolean[] used = new boolean[tiles.length];

		for (int i = 0; i < tiles.length; i++) {
			/* Check if already used */
			if (used[i])
				continue;
			/* Find suited runs */
			Tile t1 = tiles[i];
			if (!t1.isJihai()) {
				for (int j = i + 1; j < tiles.length; j++) {
					Tile t2 = tiles[j];
					if (!used[j] && t1.isAdjacentTo(t2)) {
						for (int k = i + 1; k < tiles.length; k++) {
							Tile t3 = tiles[k];
							if (!used[k]
									&& k != j
									&& !t2.equals(t3)
									&& !t1.equals(t3)
									&& (t2.isAdjacentTo(t3) || t1
											.isAdjacentTo(t3))) {
								used[i] = true;
								used[j] = true;
								used[k] = true;
								melds.add(new Meld(new Tile[] { t1, t2, t3 },
										MeldType.SHUNTSU_CLOSED));
								break;
							}
						}
						break;
					}
				}
			}

			/* Find tuples */
			if (!used[i]) {
				ArrayList<Tile> meld = new ArrayList<Tile>(3);
				meld.add(t1);
				for (int j = i + 1; j < tiles.length; j++) {
					Tile t2 = tiles[j];
					if (!used[j] && t1.equals(t2)) {
						used[j] = true;
						meld.add(t2);
						for (int k = i + 1; k < tiles.length; k++) {
							Tile t3 = tiles[k];
							if (!used[k] && k != j && t2.equals(t3)) {
								used[k] = true;
								meld.add(t3);
								break;
							}
						}
						break;
					}
				}
				if (meld.size() >= 2) {
					MeldType type = meld.size() == 2 ? MeldType.TOITSU
							: MeldType.KOUTSU_CLOSED;
					melds.add(new Meld(meld.toArray(new Tile[] {}), type));
				}
			}
		}

		/* Count melds */
		int[] count = new int[4];
		for (Meld m : melds)
			count[m.getType().ordinal()]++;
		// System.out.printf("Shuntsu: %2d  Koutsu: %2d  Toitsu: %2d  Total: %2d%n",count[0],
		// count[1], count[3], melds.size());

		/* Find legal hands */
		if (count[0] + count[1] > 3 && count[3] > 0) { // Standard pinfu or mix
			return true;
		} else if (count[0] + count[1] > 4 && count[1] > 0) { // Use Kou as Toi
			return true;
		}
		/*
		 * There could be more possibilities still, but they would be so
		 * unlikely that their effect on the results is negligable.
		 */

		return false;
	}

	/**
	 * Returns true if any legal hand can be constructed from the given tile
	 * set.
	 */
	public static HandType canConstructHand(Tile[] tiles) {
		if (Hand.canKokushiMusou(tiles))
			return HandType.KOKUSHI_MUSOU;
		if (Hand.canChiitoitsu(tiles))
			return HandType.CHIITOITSU;
		if (Hand.canRegularHand(tiles))
			return HandType.REGULAR;
		else
			return HandType.INVALID;
	}

	public enum HandType {
		INVALID, KOKUSHI_MUSOU, CHIITOITSU, REGULAR;
	}

	/**
	 * Returns true if the given ArrayList contains a tile with the same suit
	 * and value as the given tile.
	 */
	private static boolean containsTile(Tile tile, ArrayList<Tile> tiles) {
		for (Tile t : tiles)
			if (tile.equals(t))
				return true;
		return false;
	}

	/**
	 * Returns true if any of the given melds share the same tile instance.
	 */
	public static boolean containsIdentical(Meld... melds) {
		for (int i = 0; i < melds.length; i++)
			for (int j = i; j < melds.length; j++)
				if (i != j && containsIdentical(melds[i], melds[j]))
					return true;
		return false;
	}

	/**
	 * Returns an ArrayList of the given hand's possible waits. If the hand is
	 * not valid or is not tenpai, the returned ArrayList will be empty.
	 */
	public static ArrayList<Tile> getWaits(ArrayList<Tile> hand,
			ArrayList<Meld> melds) {
		ArrayList<Tile> waits = new ArrayList<Tile>();
		// TODO
		return waits;
	}

	/**
	 * Returns an ArrayList of the given hand's yaku. If the hand is not valid
	 * or has no yaku, the returned ArrayList will be empty.
	 *
	 * @param hand
	 * @param melds
	 * @param tsumohai
	 * @return
	 */
	public static ArrayList<Yaku> getYaku(ArrayList<Tile> hand,
			ArrayList<Meld> melds, Tile tsumohai) {
		ArrayList<Yaku> yaku = new ArrayList<Yaku>();
		// TODO
		return yaku;
	}

	/**
	 * Returns an ArrayList of melds that can be formed from the given hand,
	 * using the given tile in every meld.
	 * 
	 * @param hand
	 *            hand of tiles
	 * @param tile
	 *            tile to use
	 * @return empty arraylist if none possible
	 */
	public static ArrayList<Meld> getCallableMelds(ArrayList<Tile> hand,
			Tile tile) {
		ArrayList<Meld> melds = new ArrayList<Meld>();
		// TODO Auto-generated method stub
		return melds;
	}

}
