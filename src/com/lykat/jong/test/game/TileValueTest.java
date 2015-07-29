package com.lykat.jong.test.game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.lykat.jong.game.TileValue;

public class TileValueTest {

	private TileValue ii, ryan, chuu, haku;

	@Before
	public void setUp() throws Exception {
		ii = TileValue.II;
		ryan = TileValue.RYAN;
		chuu = TileValue.CHUU;
		haku = TileValue.HAKU;
	}

	@Test
	public void testToString() {
		assertEquals("Haku", haku.toString());
		assertEquals("1", ii.toString());
		assertNotEquals(1, ii.toString());
	}

	@Test
	public void testIsJihai() {
		assertTrue(haku.isJihai());
		assertFalse(ryan.isJihai());
		assertFalse(chuu.isJihai());
	}

	@Test
	public void testIsNumbered() {
		assertTrue(ryan.isNumbered());
		assertTrue(chuu.isNumbered());
		assertFalse(haku.isNumbered());
	}

	@Test
	public void testIsTermHon() {
		assertTrue(haku.isTermHon());
		assertTrue(ii.isTermHon());
		assertFalse(ryan.isTermHon());
	}

	@Test
	public void testToInteger() {
		assertEquals(1, ii.toInteger());
		assertEquals(9, chuu.toInteger());
		assertEquals(15, haku.toInteger());
	}

}
