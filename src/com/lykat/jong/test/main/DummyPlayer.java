package com.lykat.jong.test.main;

import com.lykat.jong.game.Player;
import com.lykat.jong.game.Tile;

/**
 * Exposes the protected methods of {@link Player} for testing purposes.
 * 
 * @author lykat
 *
 */
public class DummyPlayer extends Player {

    public DummyPlayer(String name) {
        super(name);
    }

    public void deal(Tile tile) {
        super.deal(tile);
    }

    public void deal(Tile[] tiles) {
        super.deal(tiles);
    }

    public void addToDiscardPond(Tile tile) {
        super.addToDiscardPond(tile);
    }

    public Tile tsumoKiri() {
        return super.tsumoKiri();
    }

    public void addPoints(int points) {
        super.addPoints(points);
    }

    public void removePoints(int points) {
        super.removePoints(points);
    }

    public boolean declareRiichi() {
        return super.declareRiichi();
    }

}
