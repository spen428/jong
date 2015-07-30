package com.lykat.jong.game;

import java.util.ArrayList;

import com.lykat.jong.event.GameEvent;
import com.lykat.jong.event.PlayerController;
import com.lykat.jong.event.PlayerEvent;
import com.lykat.jong.event.PlayerEventListener;
import com.lykat.jong.event.GameEvent.GameEventType;
import com.lykat.jong.event.PlayerEvent.PlayerEventType;

public class GameManager implements PlayerEventListener {

	private enum GameState {
		MUST_DRAW_LIVE, MUST_DRAW_DEAD, MUST_DISCARD, WAITING, WAITING_FOR_CALLERS, WIN_DECLARED, EXTENDED_KAN_DECLARED, CLOSED_KAN_DECLARED, NORTH_DECLARED;
	}

	private class Call {
		final Player player;
		final Meld meld;

		Call(Player player, Meld meld) {
			this.player = player;
			this.meld = meld;
		}
	}

	private int toFlip;
	private boolean deadDraw;
	private GameState gameState;
	private ArrayList<Call> calls;

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

	private final ArrayList<PlayerEvent> waiting = new ArrayList<PlayerEvent>();

	@Override
	public void handleEvent(PlayerEvent event) {
		PlayerEventType eventType = event.getEventType();
		Player source = event.getSource();
		Player turn = game.getTurn();
		Wall wall = game.getWall();
		boolean isTurn = (source == turn);

		if (wall.getNumRemainingDraws() == 0) {
			// TODO: Exhaustive draw
		}
		if (gameState == GameState.CLOSED_KAN_DECLARED) {
		} else if (gameState == GameState.EXTENDED_KAN_DECLARED) {
		} else if (gameState == GameState.MUST_DISCARD) {
			if (eventType == PlayerEventType.DISCARD && isTurn) {
				onDiscard();
			}
		} else if (gameState == GameState.MUST_DRAW_DEAD) {
			if (eventType == PlayerEventType.DRAW_FROM_DEAD_WALL && isTurn) {
				source.deal(wall.deadWallDraw());
				deadDraw = true;
				gameState = GameState.WAITING;
			}
		} else if (gameState == GameState.MUST_DRAW_LIVE) {
			if (eventType == PlayerEventType.DRAW_FROM_LIVE_WALL && isTurn) {
				source.deal(wall.draw());
				gameState = GameState.WAITING;
			}
		} else if (gameState == GameState.NORTH_DECLARED) {
		} else if (gameState == GameState.WAITING) {
			if (isTurn) {
				// TODO: Incomplete
				if (eventType == PlayerEventType.DISCARD) {
					onDiscard();
				}
			}
		} else if (gameState == GameState.WAITING_FOR_CALLERS) {
			// TODO: Timeout
		} else if (gameState == GameState.WIN_DECLARED) {
		} else {
		}
	}

	private void onDiscard() {
		deadDraw = false;
		while (toFlip > 0) {
			game.getWall().flipDora();
			toFlip--;
		}
		if (hasCallers()) {
			gameState = GameState.WAITING_FOR_CALLERS;
		} else {
			game.nextTurn();
			gameState = GameState.MUST_DRAW_LIVE;
		}
	}

	private boolean hasCallers() {
		return false;
	}

	private PlayerEvent waiting(PlayerEvent event) {
		for (PlayerEvent wait : waiting) {
			if (wait.getEventType() == event.getEventType()) {
				if (wait.getSource() == event.getSource()) {
					return wait;
				}
			}
		}
		return null;
	}
}
