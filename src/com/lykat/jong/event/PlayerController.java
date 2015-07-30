package com.lykat.jong.event;

import com.lykat.jong.event.PlayerEvent.PlayerEventType;
import com.lykat.jong.game.GameManager;
import com.lykat.jong.game.Player;

public class PlayerController implements GameEventListener {

	private final Player player;
	private final GameManager gameManager;

	/**
	 * A means of controlling a player in a game.
	 * 
	 * @param player
	 *            the player under control
	 * @param gameManager
	 *            the game manager of the game
	 */
	public PlayerController(Player player, GameManager gameManager) {
		super();
		this.player = player;
		this.gameManager = gameManager;
	}

	private final void fireEvent(PlayerEventType eventType) {
		fireEvent(eventType, null);
	}

	private final void fireEvent(PlayerEventType eventType, Object eventData) {
		PlayerEvent event = new PlayerEvent(player, eventType, eventData,
				System.currentTimeMillis());
		gameManager.handleEvent(event);
	}

	/* Controls */

	public final void tsumoKiri() {
		fireEvent(PlayerEventType.DISCARD_TSUMOHAI);
	}

	public final void skipCall() {
		fireEvent(PlayerEventType.SKIP_CALL);
	}

	/* Event handler */

	@Override
	public void handleEvent(GameEvent event) {
		// TODO Auto-generated method stub

	}

	public Player getPlayer() {
		return player;
	}

}
