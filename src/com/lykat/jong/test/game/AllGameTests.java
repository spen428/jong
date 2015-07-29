package com.lykat.jong.test.game;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A test suite encompassing all of the JUnit tests in the
 * <code>test.game</code> package.
 * 
 * @author lykat
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ TileSuitTest.class, TileTest.class, TileValueTest.class,
		WallTest.class })
public class AllGameTests {
}
