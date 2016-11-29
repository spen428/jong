package com.lykat.jong.test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.lykat.jong.game.Tile;
import com.lykat.jong.game.TileSuit;
import com.lykat.jong.game.TileValue;

public class TileTest {

	private Tile hatsu1, hatsu2, man1, man2, man9, pin2;

	@Before
	public void setUp() throws Exception {
		hatsu1 = new Tile(TileSuit.JIHAI, TileValue.HATSU);
		hatsu2 = new Tile(TileSuit.JIHAI, TileValue.HATSU);
		man1 = new Tile(TileSuit.WANZU, TileValue.II);
		man2 = new Tile(TileSuit.WANZU, TileValue.RYAN);
		man9 = new Tile(TileSuit.WANZU, TileValue.CHUU);
		pin2 = new Tile(TileSuit.PINZU, TileValue.RYAN);
	}

	@Test
	public void testContructor() {
		/* Null suit/value test */
		boolean nullArgumentException = false;
		try {
			new Tile(null, null);
		} catch (IllegalArgumentException e) {
			nullArgumentException = true;
		}
		assertTrue(nullArgumentException);

		/* Illegal value for JIHAI */
		boolean illegalArgumentException1 = false;
		try {
			new Tile(TileSuit.JIHAI, TileValue.II);
		} catch (IllegalArgumentException e) {
			illegalArgumentException1 = true;
		}
		assertTrue(illegalArgumentException1);

		/* Illegal value for suit */
		boolean illegalArgumentException2 = false;
		try {
			new Tile(TileSuit.PINZU, TileValue.HATSU);
		} catch (IllegalArgumentException e) {
			illegalArgumentException2 = true;
		}
		assertTrue(illegalArgumentException2);

		/* Normal construction test */
		Tile t = new Tile(TileSuit.WANZU, TileValue.CHUU);
		assertEquals(TileSuit.WANZU, t.getSuit());
		assertEquals(TileValue.CHUU, t.getValue());
	}

	@Test
	public void testHashCode() {
		assertTrue(hatsu1.hashCode() == hatsu2.hashCode());
		assertFalse(man9.hashCode() == hatsu1.hashCode());
		assertFalse(man1.hashCode() == man2.hashCode());
		assertFalse(pin2.hashCode() == man2.hashCode());
	}

	@Test
	public void testEqualsObject() {
		assertTrue(hatsu1.equals(hatsu2));
		assertFalse(man2.equals(pin2));
		assertTrue(man9.equals(man9));
	}

	@Test
	public void testClone() {
		assertTrue(man9.equals(man9.clone()));
		assertFalse(man9 == man9.clone());
		assertTrue(man9.hashCode() == man9.clone().hashCode());
		assertFalse(man2.equals(pin2.clone()));
	}

	@Test
	public void testToString() {
		assertEquals("Hatsu", hatsu1.toString());
		assertEquals("9 Wan", man9.toString());
	}

	@Test
	public void testGetSuit() {
		assertEquals(TileSuit.WANZU, man1.getSuit());
		assertNotEquals(TileSuit.PINZU, hatsu1.getSuit());
	}

	@Test
	public void testGetValue() {
		assertEquals(TileValue.CHUU, man9.getValue());
		assertNotEquals(TileValue.CHII, hatsu1.getValue());
	}

	@Test
	public void testIsJihai() {
		assertTrue(hatsu1.isJihai());
		assertFalse(man2.isJihai());
		assertFalse(man9.isJihai());
	}

	@Test
	public void testIsNumbered() {
		assertTrue(man2.isNumbered());
		assertTrue(man9.isNumbered());
		assertFalse(hatsu1.isNumbered());
	}

	@Test
	public void testIsTermHon() {
		assertTrue(hatsu1.isYaochuuhai());
		assertTrue(man9.isYaochuuhai());
		assertFalse(man2.isYaochuuhai());
	}

	@Test
	public void testIsAdjacentTo() {
		assertTrue(man1.isAdjacentTo(man2));
		assertTrue(man2.isAdjacentTo(man1));
		assertFalse(man1.isAdjacentTo(pin2));
	}

}
