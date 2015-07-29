package com.lykat.jong.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lykat.jong.test.game.AllGameTests;

/**
 * A test suite encompassing all of this project's JUnit tests.
 * 
 * @author lykat
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ AllGameTests.class })
public class AllTests {
}
