package com.lykat.jong.game;

import com.lykat.jong.event.GameEvent;
import com.lykat.jong.event.PlayerController;
import com.lykat.jong.event.PlayerEvent;
import com.lykat.jong.event.PlayerEventListener;
import com.lykat.jong.event.GameEvent.GameEventType;
import com.lykat.jong.event.PlayerEvent.PlayerEventType;

public class GameManager implements PlayerEventListener {

	private final Game game;
	private final PlayerController[] players;

	public GameManager(Game game, PlayerController[] players) {
		super();
		this.game = game;
		this.players = players;
	}

	private boolean fireEvent(Player target, GameEventType eventType) {
		return fireEvent(target, eventType, null);
	}

	private boolean fireEvent(Player target, GameEventType eventType,
			Object eventData) {
		for (PlayerController p : players) {
			if (p.getPlayer() == target) {
				GameEvent event = new GameEvent(eventType, eventData,
						System.currentTimeMillis());
				p.handleEvent(event);
				return true;
			}
		}
		return false;
	}

	@Override
	public void handleEvent(PlayerEvent event) {
		PlayerEventType eventType = event.getEventType();
		if (eventType.requiresControl() && game.getTurn() != event.getSource()) {
			return;
		}
		switch (eventType) {
		case CALL_CHII:
			break;
		case CALL_PON:
			break;
		case CALL_RON:
			break;
		case DECLARE_BONUS_TILE:
			break;
		case DECLARE_KAN:
			break;
		case DECLARE_REDEAL:
			break;
		case DECLARE_RIICHI:
			break;
		case DECLARE_TSUMO:
			break;
		case DISCARD_FROM_HAND:
			break;
		case DISCARD_TSUMOHAI:
			break;
		case DRAW_FROM_DEAD_WALL:
			break;
		case DRAW_FROM_LIVE_WALL:
			break;
		case SKIP_CALL:
			break;
		default:
			break;
		}
	}

}
