package com.lykat.jong.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.lykat.jong.game.Tile;

/**
 * Static sorting methods.
 * 
 * @author lykat
 *
 */
public class Sorter {

	/* Sorting methods */

	public static void sort(Tile[] tiles) {
		Arrays.sort(tiles, tileComparator);
	}

	public static void sort(ArrayList<Tile> tiles) {
		Collections.sort(tiles, tileComparator);
	}

	/* Comparator declarations */

	private static final Comparator<Tile> tileComparator = new Comparator<Tile>() {

		@Override
		public int compare(Tile t1, Tile t2) {
			if (t1 == t2) {
				return 0;
			} else if (t1 == null) {
				return 1;
			} else if (t2 == null) {
				return -1;
			}

			int c = t1.getSuit().compareTo(t2.getSuit());
			if (c == 0) {
				return t1.getValue().compareTo(t2.getValue());
			}
			return c;
		}

	};

}
