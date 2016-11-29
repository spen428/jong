package com.lykat.jong.test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.lykat.jong.game.TileSuit;

public class TileSuitTest {

	private TileSuit wan, pin, sou, jihai;

	@Before
	public void setUp() throws Exception {
		wan = TileSuit.WANZU;
		pin = TileSuit.PINZU;
		sou = TileSuit.SOUZU;
		jihai = TileSuit.JIHAI;
	}

	@Test
	public void testIsJihai() {
		assertTrue(jihai.isJihai());
		assertFalse(wan.isJihai());
		assertFalse(pin.isJihai());
		assertFalse(sou.isJihai());
	}

	@Test
	public void testToString() {
		assertEquals("", jihai.toString());
		assertEquals("Pin", pin.toString());
	}

	@Test
	public void testCompareTo() {
		assertEquals(0, wan.compareTo(wan));
		assertNotEquals(0, wan.compareTo(pin));
		assertNotEquals(0, wan.compareTo(sou));
		assertNotEquals(0, wan.compareTo(jihai));
	}
}
