package com.lykat.jong.game;

import java.util.ArrayList;

import com.lykat.jong.event.GameEvent;
import com.lykat.jong.event.PlayerController;
import com.lykat.jong.event.PlayerEvent;
import com.lykat.jong.event.PlayerEventListener;
import com.lykat.jong.event.GameEvent.GameEventType;
import com.lykat.jong.event.PlayerEvent.PlayerEventType;
import com.lykat.jong.game.Meld.MeldType;

public class GameManager implements PlayerEventListener {

	private enum GameState {
		MUST_DRAW_LIVE, MUST_DRAW_DEAD, MUST_DISCARD, WAITING, WAITING_FOR_CALLERS, WIN_DECLARED, EXTENDED_KAN_DECLARED, CLOSED_KAN_DECLARED, BONUS_TILE_DECLARED;
	}

	private class Call {

		private final Player player;
		private final CallType call;

		public Call(Player player, CallType call) {
			this.player = player;
			this.call = call;
		}

		public Player getPlayer() {
			return player;
		}

		public CallType getCall() {
			return call;
		}
	}

	private enum CallType {
		RON, PON, CHII, KAN_OPEN;
	}

	private int toFlip;
	private boolean deadDraw;
	private GameState gameState;
	private ArrayList<Call> canCall, called;

	private final Game game;
	private final PlayerController[] players;

	public GameManager(Game game, PlayerController[] players) {
		super();
		this.game = game;
		this.players = players;
		this.toFlip = 0;
		this.deadDraw = false;
		this.gameState = GameState.MUST_DRAW_LIVE;
		this.canCall = new ArrayList<Call>();
		this.called = new ArrayList<Call>();
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
		Player player = event.getSource();
		Player turn = game.getTurn();
		Wall wall = game.getWall();
		boolean isTurn = (player == turn);

		if (wall.getNumRemainingDraws() == 0) {
			// TODO: Exhaustive draw
		}
		if (gameState == GameState.CLOSED_KAN_DECLARED
				|| gameState == GameState.EXTENDED_KAN_DECLARED
				|| gameState == GameState.BONUS_TILE_DECLARED
				|| gameState == GameState.WAITING_FOR_CALLERS) {
			if (eventType.isCall()) { // Pon, Chii, Kan, or Ron
				called(player);
			} else if (eventType == PlayerEventType.SKIP_CALL) {
				removeCaller(player);
			}
			// TODO: Timeout
		} else if (gameState == GameState.MUST_DISCARD) {
			if (eventType == PlayerEventType.DISCARD && isTurn) {
				discard(event);
			}
		} else if (gameState == GameState.MUST_DRAW_DEAD) {
			if (eventType == PlayerEventType.DRAW_FROM_DEAD_WALL && isTurn) {
				player.deal(wall.deadWallDraw());
				deadDraw = true;
				gameState = GameState.WAITING;
			}
		} else if (gameState == GameState.MUST_DRAW_LIVE) {
			if (eventType == PlayerEventType.DRAW_FROM_LIVE_WALL && isTurn) {
				player.deal(wall.draw());
				gameState = GameState.WAITING;
			}
		} else if (gameState == GameState.WAITING) {
			if (isTurn) {
				if (eventType == PlayerEventType.DISCARD) {
					discard(event);
				} else if (eventType == PlayerEventType.DECLARE_RIICHI) {
					declareRiichi(event);
				} else if (eventType == PlayerEventType.DECLARE_KAN) {
					declareKan(event);
				} else if (eventType == PlayerEventType.DECLARE_BONUS_TILE) {
					declareBonusTile(event);
				} else if (eventType == PlayerEventType.DECLARE_TSUMO) {
					declareTsumo(event);
				}
			}
		} else if (gameState == GameState.WIN_DECLARED) {
		} else {
		}
	}

	/**
	 * Signal that the given player has called. If they are the only valid
	 * caller remaining, this will change the game state.
	 */
	private void called(Player player) {
		// TODO Auto-generated method stub
		if (canCall.size() > 0) {
			// TODO: Prioritise calls
			if (canCall.size() == 0) {
				if (called.size() == 1) {
					// TODO: Apply call
				} else if (called.size() > 1) {
					// TODO: Multi-Ron
				} else {
					throw new IllegalStateException("Called list is empty! "
							+ "Concurrent modification?");
				}
				called.clear();
			}
		}
	}

	/**
	 * Removes the given called from the call list. If they were the last
	 * remaining caller, this will change the game state.
	 */
	private void removeCaller(Player player) {
		canCall.remove(player);
		if (canCall.size() == 0) {
			if (gameState == GameState.BONUS_TILE_DECLARED
					|| gameState == GameState.CLOSED_KAN_DECLARED
					|| gameState == GameState.EXTENDED_KAN_DECLARED) {
				gameState = GameState.MUST_DRAW_DEAD;
			} else if (gameState == GameState.WAITING_FOR_CALLERS) {
				game.nextTurn();
				gameState = GameState.MUST_DRAW_LIVE;
			}
		}
	}

	private void declareRon(PlayerEvent event) {
		// TODO
		throw new UnsupportedOperationException("Unimplemented");
	}

	private void declareTsumo(PlayerEvent event) {
		// TODO
		throw new UnsupportedOperationException("Unimplemented");
	}

	private void declareBonusTile(PlayerEvent event) {
		// TODO
		throw new UnsupportedOperationException("Unimplemented");
	}

	private void declareRiichi(PlayerEvent event) {
		if (event.getSource().declareRiichi()) {
			game.incrementNumRiichiSticks();
			gameState = GameState.MUST_DISCARD;
		}
	}

	/**
	 * Discards the player's desired tile, moving the game into the next state.
	 * 
	 * @param event
	 *            the discard event
	 */
	private void discard(PlayerEvent event) {
		Player player = event.getSource();
		int index = (int) event.getEventData();
		player.discard(index);

		deadDraw = false;
		while (toFlip > 0) {
			game.getWall().flipDora();
			toFlip--;
		}
		if (hasCallers(event)) {
			gameState = GameState.WAITING_FOR_CALLERS;
		} else {
			game.nextTurn();
			gameState = GameState.MUST_DRAW_LIVE;
		}
	}

	private void declareKan(PlayerEvent event) {
		Meld meld = (Meld) event.getEventData();
		if (meld.getType() == MeldType.KANTSU_CLOSED) {
			event.getSource().addMeld(meld);
			game.getWall().flipDora();
			if (hasCallers(event)) {
				gameState = GameState.CLOSED_KAN_DECLARED;
			} else {
				gameState = GameState.MUST_DRAW_DEAD;
			}
		} else if (meld.getType() == MeldType.KANTSU_EXTENDED) {
			event.getSource().addMeld(meld);
			toFlip++;
			if (hasCallers(event)) {
				gameState = GameState.EXTENDED_KAN_DECLARED;
			} else {
				gameState = GameState.MUST_DRAW_DEAD;
			}
		}
	}

	/**
	 * ...
	 * 
	 * @param event
	 *            the discard event
	 */
	private boolean hasCallers(PlayerEvent event) {
		Tile tile = event.getSource().getLatestDiscard();
		PlayerEventType eventType = event.getEventType();
		if (eventType == PlayerEventType.CALL_CHII) {
		} else if (eventType == PlayerEventType.CALL_KAN) {
		} else if (eventType == PlayerEventType.CALL_PON) {
		} else if (eventType == PlayerEventType.CALL_RON) {
		} else if (eventType == PlayerEventType.DECLARE_BONUS_TILE) {
		} else if (eventType == PlayerEventType.DECLARE_KAN) {
		} else if (eventType == PlayerEventType.DECLARE_REDEAL) {
		} else if (eventType == PlayerEventType.DECLARE_RIICHI) {
		} else if (eventType == PlayerEventType.DECLARE_TSUMO) {
		} else if (eventType == PlayerEventType.DISCARD) {
		} else if (eventType == PlayerEventType.DRAW_FROM_DEAD_WALL) {
		} else if (eventType == PlayerEventType.DRAW_FROM_LIVE_WALL) {
		} else if (eventType == PlayerEventType.SKIP_CALL) {
		} else {
		}
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
