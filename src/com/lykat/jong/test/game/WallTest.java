package com.lykat.jong.test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.lykat.jong.game.Tile;
import com.lykat.jong.game.Wall;

public class WallTest {

	private Wall wall;
	private int totalDeadWallDraws;

	@Before
	public void setUp() throws Exception {
		totalDeadWallDraws = 4;
		wall = new Wall(Wall.fourPlayerTileSet(), totalDeadWallDraws);
	}

	@Test
	public void testConstructorIllegalArgumentException() {
		boolean lessThanZeroException = false;
		boolean greaterThanTwelveException = false;
		boolean normalRangeException = false;

		try {
			new Wall(Wall.fourPlayerTileSet(), -1);
		} catch (IllegalArgumentException e) {
			/* Exception was successfully thrown */
			lessThanZeroException = true;
		}

		try {
			new Wall(Wall.fourPlayerTileSet(), 13);
		} catch (IllegalArgumentException e) {
			greaterThanTwelveException = true;
		}

		try {
			new Wall(Wall.fourPlayerTileSet(), 8);
		} catch (IllegalArgumentException e) {
			normalRangeException = true;
		}

		assertTrue(lessThanZeroException);
		assertTrue(greaterThanTwelveException);
		assertFalse(normalRangeException);
	}

	@Test
	public void testDraw() {
		wall.reset();

		/* Test counter */
		assertTrue(wall.getNumRemainingDraws() > 0);
		assertFalse(null == wall.draw());

		int total = wall.getNumRemainingDraws();
		for (int i = 0; i < total; i++) {
			wall.draw();
		}
		assertEquals(0, wall.getNumRemainingDraws());
		assertEquals(null, wall.draw());

		wall.reset();

		/* Test unique */
		total = wall.getNumRemainingDraws();
		ArrayList<Tile> drawn = new ArrayList<Tile>();
		for (int i = 0; i < total; i++) {
			drawn.add(wall.draw());
		}
		for (int i = 1; i < Wall.NUM_DEADWALL_TILES / 2; i++) {
			wall.flipDora();
		}
		for (Tile t : wall.getDoraIndicators()) {
			drawn.add(t);
		}
		for (Tile t : wall.getUraDoraIndicators()) {
			drawn.add(t);
		}
		Tile[] tileSet = Wall.fourPlayerTileSet();
		for (Tile t : drawn) {
			boolean contains = false;
			for (Tile t2 : tileSet) {
				contains = false;
				if (t.equals(t2)) {
					contains = true;
					break;
				}
			}
			assertTrue("Fail: Wall does not contain " + t.toString(), contains);
		}

		wall.reset();
	}

	@Test
	public void testDeadWallDraw() {
		wall.reset();

		assertTrue(wall.getNumRemainingDeadWallDraws() > 0);
		assertFalse(null == wall.deadWallDraw());

		int total = wall.getNumRemainingDeadWallDraws();
		for (int i = 0; i < total; i++) {
			wall.deadWallDraw();
		}
		assertEquals(0, wall.getNumRemainingDeadWallDraws());
		assertEquals(null, wall.deadWallDraw());

		wall.reset();
	}

	@Test
	public void testReset() {
		wall.draw();
		wall.deadWallDraw();
		wall.reset();
		assertEquals(1, wall.getNumDoraIndicators());
		assertEquals(wall.getTotalNumDeadWallDraws(),
				wall.getNumRemainingDeadWallDraws());
	}

	@Test
	public void testGetNumRemainingDraws() {
		/* Test that it decrements correctly. */
		wall.reset();
		int before = wall.getNumRemainingDraws();
		wall.draw();
		wall.draw();
		int after = wall.getNumRemainingDraws();
		assertEquals(2, before - after);

		/* Test that it does not decrement below zero. */
		wall.reset();
		int total = wall.getNumRemainingDraws();
		for (int i = 0; i < total; i++) {
			wall.draw();
		}
		assertEquals(0, wall.getNumRemainingDraws());
		wall.draw();
		assertEquals(0, wall.getNumRemainingDraws());

		wall.reset();
	}

	@Test
	public void testGetNumRemainingDeadWallDraws() {
		/* Test that it decrements correctly. */
		wall.reset();
		int before = wall.getNumRemainingDeadWallDraws();
		wall.deadWallDraw();
		wall.deadWallDraw();
		int after = wall.getNumRemainingDeadWallDraws();
		assertEquals(2, before - after);

		/* Test that it does not decrement below zero. */
		wall.reset();
		int total = wall.getNumRemainingDeadWallDraws();
		for (int i = 0; i < total; i++) {
			wall.deadWallDraw();
		}
		assertEquals(0, wall.getNumRemainingDeadWallDraws());
		wall.deadWallDraw();
		assertEquals(0, wall.getNumRemainingDeadWallDraws());

		wall.reset();
	}

	@Test
	public void testGetTotalNumDeadWallDraws() {
		assertEquals(totalDeadWallDraws, wall.getTotalNumDeadWallDraws());
	}

	@Test
	public void testGetNumDoraIndicators() {
		wall.reset();
		assertEquals(1, wall.getNumDoraIndicators());
		wall.flipDora();
		assertEquals(2, wall.getNumDoraIndicators());
		wall.reset();
	}

	@Test
	public void testFlipDora() {
		wall.reset();
		assertEquals(1, wall.getNumDoraIndicators());
		for (int i = 0; i < Wall.NUM_DEADWALL_TILES; i++) {
			wall.flipDora();
		}
		assertEquals(Wall.NUM_DEADWALL_TILES / 2, wall.getNumDoraIndicators());
		wall.reset();
	}

	@Test
	public void testGetDoraIndicators() {
		wall.reset();
		assertEquals(1, wall.getNumDoraIndicators());
		assertEquals(1, wall.getDoraIndicators().length);
		wall.flipDora();
		wall.flipDora();
		assertEquals(3, wall.getNumDoraIndicators());
		assertEquals(3, wall.getDoraIndicators().length);
		wall.reset();
	}

	@Test
	public void testGetUraDoraIndicators() {
		wall.reset();
		assertEquals(1, wall.getNumDoraIndicators());
		assertEquals(1, wall.getUraDoraIndicators().length);
		wall.flipDora();
		wall.flipDora();
		assertEquals(3, wall.getNumDoraIndicators());
		assertEquals(3, wall.getUraDoraIndicators().length);
		assertFalse(wall.getDoraIndicators() == wall.getUraDoraIndicators());
		wall.reset();
	}

	@Test
	public void testTileSets() {
		// TODO: Wall.class tile set generation unit tests.
		assertEquals(136, Wall.fourPlayerTileSet().length);
		assertEquals(108, Wall.threePlayerTileSet().length);
		assertEquals(80, Wall.twoPlayerTileSet().length);
		assertEquals(34, Wall.uniqueTileSet().length);
	}

}
