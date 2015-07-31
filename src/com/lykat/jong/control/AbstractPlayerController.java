package com.lykat.jong.control;

import java.util.EventListener;

import com.lykat.jong.game.GameEvent;
import com.lykat.jong.game.GameManager;
import com.lykat.jong.game.Player;
import com.lykat.jong.game.GameEvent.GameEventType;

public abstract class AbstractPlayerController implements EventListener {

	protected final Player player;
	protected final GameManager gameManager;

	/**
	 * A means of controlling a player in a game.
	 * 
	 * @param player
	 *            the player under control
	 * @param gameManager
	 *            the game manager of the game
	 */
	public AbstractPlayerController(Player player, GameManager gameManager) {
		super();
		this.player = player;
		this.gameManager = gameManager;
	}

	protected final void fireEvent(GameEventType eventType) {
		fireEvent(eventType, null);
	}

	protected final void fireEvent(GameEventType eventType, Object eventData) {
		GameEvent event = new GameEvent(player, eventType, eventData,
				System.currentTimeMillis());
		gameManager.handleEvent(event);
	}
	
	public abstract void handleEvent(GameEvent event);

	public Player getPlayer() {
		return player;
	}

	/* Controls */

	public final void discard(int index) {
		fireEvent(GameEventType.DISCARD, index);
	}

	public final void tsumoKiri() {
		fireEvent(GameEventType.DISCARD, -1);
	}

	public final void skipCall() {
		fireEvent(GameEventType.SKIP_CALL);
	}

}
