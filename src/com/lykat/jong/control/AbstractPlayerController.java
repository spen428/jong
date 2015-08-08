package com.lykat.jong.control;

import java.util.EventListener;
import java.util.Observable;

import com.badlogic.gdx.InputProcessor;
import com.lykat.jong.game.GameEvent;
import com.lykat.jong.game.GameManager;
import com.lykat.jong.game.Player;
import com.lykat.jong.game.GameEvent.GameEventType;

public abstract class AbstractPlayerController extends Observable implements
		EventListener, InputProcessor {

	protected Player player;
	protected final String name;
	protected final GameManager gameManager;

	/**
	 * A means of controlling a player in a game.
	 * 
	 * @param player
	 *            the player under control
	 * @param gameManager
	 *            the game manager of the game
	 */
	public AbstractPlayerController(String name, GameManager gameManager) {
		super();
		this.name = name;
		this.gameManager = gameManager;
	}

	protected final void fireEvent(GameEventType eventType) {
		fireEvent(eventType, null);
	}

	protected final void fireEvent(GameEventType eventType, Object eventData) {
		Player source = player;

		GameEvent event = new GameEvent(source, eventType, eventData,
				System.currentTimeMillis());
		gameManager.handleEvent(event);
	}

	public abstract void handleEvent(GameEvent event);

	public void connect() {
		fireEvent(GameEventType.PLAYER_CONNECT, this);
	}

	public String getName() {
		return name;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean setPlayer(Player player) {
		if (this.player == null) {
			this.player = player;
			return true;
		}
		return false;
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
