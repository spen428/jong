package com.lykat.jong.test.main;

import com.lykat.jong.game.Game;
import com.lykat.jong.game.Player;
import com.lykat.jong.game.RuleSet;

public class DummyGame extends Game {

	public DummyGame(String name, RuleSet ruleSet, Player[] players) {
		super(name, ruleSet);

		Player[] playerz = super.getPlayers();
		for (int i = 0; i < playerz.length; i++) {
			playerz[i] = players[i];
		}
	}

}
