package com.lykat.jong.game;

import java.util.ArrayList;
import java.util.EventListener;

import com.lykat.jong.calc.Hand;
import com.lykat.jong.calc.Yaku;
import com.lykat.jong.control.AbstractPlayerController;
import com.lykat.jong.game.GameEvent.GameEventType;
import com.lykat.jong.game.Meld.MeldType;

public class GameManager implements EventListener {

	private enum GameState {
		MUST_DRAW_LIVE, MUST_DRAW_DEAD, MUST_DISCARD, WAITING, WAITING_FOR_CALLERS, END_OF_ROUND, EXTENDED_KAN_DECLARED, CLOSED_KAN_DECLARED, BONUS_TILE_DECLARED;
	}

	private class Call {
		private final Player player;
		private final CallType callType;
		private final Meld meld;

		public Call(Player player, CallType callType, Meld meld) {
			this.player = player;
			this.callType = callType;
			this.meld = meld;

			if (meld == null && callType != CallType.RON) {
				throw new IllegalArgumentException(
						"Cannot have a null meld unless the CallType is Ron.");
			}
		}

		public Call(Player player, CallType callType) {
			this(player, callType, null);
		}

		public Player getPlayer() {
			return player;
		}

		public CallType getCallType() {
			return callType;
		}

		public Meld getMeld() {
			return meld;
		}
	}

	private enum CallType {
		RON, PON, CHII, KAN;
	}

	private int toFlip;
	private boolean deadDraw;
	private GameState gameState;
	private ArrayList<Call> canCall, called;

	private final Game game;
	private final AbstractPlayerController[] players;
	private Call c;

	public GameManager(Game game, AbstractPlayerController[] players) {
		super();
		this.game = game;
		this.players = players;
		this.toFlip = 0;
		this.deadDraw = false;
		this.gameState = GameState.MUST_DRAW_LIVE;
		this.canCall = new ArrayList<Call>();
		this.called = new ArrayList<Call>();
	}

	private boolean fireEvent(Player target, GameEventType eventType,
			Object eventData) {
		for (AbstractPlayerController p : players) {
			if (p.getPlayer() == target) {
				GameEvent event = new GameEvent(null, eventType, eventData,
						System.currentTimeMillis());
				p.handleEvent(event);
				return true;
			}
		}
		return false;
	}

	public void handleEvent(GameEvent event) {
		// TODO: Timeouts
		GameEventType eventType = event.getEventType();
		Player player = event.getSource();
		boolean isTurn = (player == game.getTurn());

		if (gameState == GameState.CLOSED_KAN_DECLARED
				|| gameState == GameState.EXTENDED_KAN_DECLARED
				|| gameState == GameState.BONUS_TILE_DECLARED
				|| gameState == GameState.WAITING_FOR_CALLERS) {
			if (eventType.isCall()) { // Pon, Chii, Kan, or Ron
				called(event);
			} else if (eventType == GameEventType.SKIP_CALL) {
				removeCaller(player);
			}
		} else if (gameState == GameState.MUST_DISCARD) {
			if (eventType == GameEventType.DISCARD && isTurn) {
				discard(event);
			}
		} else if (gameState == GameState.MUST_DRAW_DEAD) {
			if (eventType == GameEventType.DRAW_FROM_DEAD_WALL && isTurn) {
				player.deal(game.getWall().deadWallDraw());
				deadDraw = true;
				gameState = GameState.WAITING;
			}
		} else if (gameState == GameState.MUST_DRAW_LIVE) {
			if (eventType == GameEventType.DRAW_FROM_LIVE_WALL && isTurn) {
				player.deal(game.getWall().draw());
				gameState = GameState.WAITING;
			}
		} else if (gameState == GameState.WAITING) {
			if (isTurn) {
				if (eventType == GameEventType.DISCARD) {
					discard(event);
				} else if (eventType == GameEventType.DECLARE_RIICHI) {
					declareRiichi(event);
				} else if (eventType == GameEventType.DECLARE_KAN) {
					declareKan(event);
				} else if (eventType == GameEventType.DECLARE_BONUS_TILE) {
					declareBonusTile(event);
				} else if (eventType == GameEventType.DECLARE_TSUMO) {
					declareTsumo(event);
				} else if (eventType == GameEventType.DECLARE_REDEAL) {
					declareRedeal(event);
				}
			}
		} else if (gameState == GameState.END_OF_ROUND) {
			// TODO: Players must click to proceed.
		} else {
		}
	}

	/**
	 * Signal that the player in the given event has called. If they are the
	 * only valid caller remaining, this will change the game state.
	 */
	private void called(GameEvent event) {
		// TODO: Handle mulitple calls from same player
		if (canCall.size() > 0) {
			doCalled(event);
			prioriseCalls();
			if (canCall.size() == 0) {
				if (called.size() == 1) {
					doCall(called.remove(0));
				} else if (called.size() > 1) {
					multiRon();
				} else {
					throw new IllegalStateException("Called list is empty! "
							+ "Concurrent modification?");
				}
				called.clear();
			}
		}
	}

	/**
	 * Responds to a 'Call' GameEvent by adding the call to the 'called' AL (so
	 * long as it existed in 'canCall' and is valid).
	 */
	private void doCalled(GameEvent event) {
		GameEventType eventType = event.getEventType();
		Player source = event.getSource();
		for (Call c : new ArrayList<Call>(canCall)) {
			if (c.getPlayer() == source) {
				CallType type = c.getCallType();
				if ((type == CallType.CHII && eventType == GameEventType.CALL_CHII)
						|| (type == CallType.KAN && eventType == GameEventType.CALL_KAN)
						|| (type == CallType.PON && eventType == GameEventType.CALL_PON)
						|| (type == CallType.RON && eventType == GameEventType.CALL_RON)) {
					// Man, this is ugly
					canCall.remove(c);
					called.add(c);
					break;
				}
			}
		}
	}

	/**
	 * Priorise tile calls. Outranked calls are removed from the 'called' and
	 * 'canCall' ArrayList.
	 */
	private void prioriseCalls() {
		if (called.size() > 0) {
			/* Count */
			int ron = 0, ponKan = 0;
			for (Call c : called) {
				CallType type = c.getCallType();
				if (type == CallType.KAN || type == CallType.PON) {
					ponKan++;
				} else if (type == CallType.RON) {
					ron++;
				}
			}

			/* Prioritise */
			ArrayList<Call> callz = new ArrayList<Call>(called);
			callz.addAll(canCall);
			boolean removePonKan = (ron > 0);
			boolean removeChii = (ponKan > 0);
			for (Call c : callz) {
				CallType type = c.getCallType();
				if (((type == CallType.KAN || type == CallType.PON) && removePonKan)
						|| (type == CallType.CHII && removeChii)) {
					canCall.remove(c);
					called.remove(c);
				}
			}
		}
	}

	/**
	 * Removes the given called from the call list. If they were the last
	 * remaining caller, this will change the game state.
	 */
	private void removeCaller(Player player) {
		for (Call c : new ArrayList<Call>(canCall)) {
			if (c.getPlayer() == player) {
				canCall.remove(c);
			}
		}
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

	/**
	 * Applies the given call, advancing the game state.
	 */
	private void doCall(Call call) {
		Player caller = call.getPlayer();
		Player discarder = game.getTurn();

		discarder.removeLatestDiscard();
		caller.addMeld(call.getMeld());
		game.setTurn(caller);

		if (call.getCallType() == CallType.KAN) {
			toFlip++;
			// TODO: Kan Pao
			gameState = GameState.MUST_DRAW_DEAD;
		} else {
			gameState = GameState.MUST_DISCARD;
		}
	}

	private void declareRon(GameEvent event) {
		// TODO
		boolean houtei = false;
		boolean chankan = false;
		if (gameState == GameState.EXTENDED_KAN_DECLARED
				|| gameState == GameState.CLOSED_KAN_DECLARED) {
			chankan = true;
		}
		throw new UnsupportedOperationException("Unimplemented");
	}

	/**
	 * Handle a multi-Ron situation. Priorised so that the dealer stays seated
	 * if they are one of the callers.
	 */
	private void multiRon() {
		RuleSet ruleSet = game.getRuleSet();
		if (called.size() > ruleSet.getMaxSimultanousRon()) {
			if (ruleSet.isHeadBump()) {
				// TODO: Head bump
				throw new UnsupportedOperationException("Unimplemented");
			} else {
				// TODO: Multi-ron abort
				throw new UnsupportedOperationException("Unimplemented");
			}
		} else {
			// TODO: Multi-ron
			throw new UnsupportedOperationException("Unimplemented");
		}
	}

	private void declareTsumo(GameEvent event) {
		// TODO
		boolean haitei = false;
		boolean rinshan = deadDraw;
		throw new UnsupportedOperationException("Unimplemented");
	}

	private void declareBonusTile(GameEvent event) {
		// TODO
		gameState = GameState.BONUS_TILE_DECLARED;
		if (!hasCallers(event)) {
			gameState = GameState.MUST_DRAW_DEAD;
		}
		throw new UnsupportedOperationException("Unimplemented");
	}

	private void declareRedeal(GameEvent event) {
		// TODO Auto-generated method stub

	}

	private void declareRiichi(GameEvent event) {
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
	private void discard(GameEvent event) {
		Player player = event.getSource();
		int index = (int) event.getEventData();
		player.discard(index);

		deadDraw = false;
		while (toFlip > 0) {
			game.getWall().flipDora();
			toFlip--;
		}
		gameState = GameState.WAITING_FOR_CALLERS;
		if (!hasCallers(event)) {
			if (game.getWall().getNumRemainingDraws() == 0) {
				// TODO: Exhaustive draw
				// TODO: 4 Winds abortive draw
				// TODO: 4/5 Kan abortive draw
				// TODO: 4 Riichi abortive draw
			} else {
				game.nextTurn();
				gameState = GameState.MUST_DRAW_LIVE;
			}
		}
	}

	private void declareKan(GameEvent event) {
		Meld meld = (Meld) event.getEventData();
		if (meld.getType() == MeldType.KANTSU_CLOSED) {
			event.getSource().addMeld(meld);
			game.getWall().flipDora();
			gameState = GameState.CLOSED_KAN_DECLARED;
			if (!hasCallers(event)) {
				gameState = GameState.MUST_DRAW_DEAD;
			}
		} else if (meld.getType() == MeldType.KANTSU_EXTENDED) {
			event.getSource().addMeld(meld);
			toFlip++;
			gameState = GameState.EXTENDED_KAN_DECLARED;
			if (!hasCallers(event)) {
				gameState = GameState.MUST_DRAW_DEAD;
			}
		}
	}

	/**
	 * NOTE: Clears the 'canCall' ArrayList.
	 * 
	 * @param event
	 *            the discard event
	 */
	private boolean hasCallers(GameEvent event) {
		if (event.getEventType() != GameEventType.DISCARD) {
			return false;
		}

		canCall.clear();
		Player discarder = event.getSource();
		Tile tile = discarder.getLatestDiscard();
		for (Player p : game.getPlayers()) {

			if (game.getTurn() == p) {
				continue;
			}

			ArrayList<Tile> hand = p.getHand();
			ArrayList<Meld> melds = p.getMelds();

			/* Chii/Pon/Kan calls */
			ArrayList<Meld> callableMelds = Hand.getCallableMelds(hand, tile);
			for(Meld m : callableMelds) {
				// TODO
			}

			/* Ron calls */
			if (Hand.getWaits(hand, melds).contains(tile)) {
				Call ronCall = new Call(p, CallType.RON);
				if (gameState == GameState.BONUS_TILE_DECLARED
						|| gameState == GameState.EXTENDED_KAN_DECLARED
						|| gameState == GameState.WAITING_FOR_CALLERS) {
					addCall(ronCall);
				} else if (gameState == GameState.CLOSED_KAN_DECLARED) {
					ArrayList<Yaku> yaku = Hand.getYaku(hand, melds, tile);
					if (yaku.contains(Yaku.KOKUSHI_MUSOU)
							|| yaku.contains(Yaku.KOKUSHI_MUSOU_13_MAN_MACHI)) {
						addCall(ronCall);
					}
				}
			}

		}
		return (canCall.size() > 0);
	}

	/**
	 * Adds the given call to the 'canCall' ArrayList, iff it does not already
	 * exist.
	 */
	private void addCall(Call call) {
		if (!canCall.contains(call)) {
			canCall.add(call);
		}
	}
}
