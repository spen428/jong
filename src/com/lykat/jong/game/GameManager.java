package com.lykat.jong.game;

import java.util.ArrayList;
import java.util.EventListener;

import com.lykat.jong.calc.Hand;
import com.lykat.jong.calc.Yaku;
import com.lykat.jong.control.AbstractPlayerController;
import com.lykat.jong.game.GameEvent.GameEventType;
import com.lykat.jong.game.Meld.MeldType;

public class GameManager implements EventListener {

	public enum GameState {
		MUST_DRAW_LIVE, MUST_DRAW_DEAD, MUST_DISCARD, WAITING, WAITING_FOR_CALLERS, END_OF_ROUND, EXTENDED_KAN_DECLARED, CLOSED_KAN_DECLARED, BONUS_TILE_DECLARED, GAME_OVER;
	}

	private int toFlip;
	private boolean deadDraw;
	private GameState gameState;
	private ArrayList<Call> canCall, called;

	private final Game game;
	private final AbstractPlayerController[] players;

	public GameManager(Game game, AbstractPlayerController[] players) {
		super();
		this.game = game;
		this.players = players;
		this.toFlip = 0;
		this.deadDraw = false;
		this.gameState = GameState.END_OF_ROUND;
		this.canCall = new ArrayList<Call>();
		this.called = new ArrayList<Call>();
	}

	private void fireEventAllPlayers(GameEventType eventType, Object eventData) {
		for (AbstractPlayerController player : players) {
			fireEvent(player, eventType, eventData);
		}
	}

	private void fireEvent(Player target, GameEventType eventType,
			Object eventData) {
		fireEvent(getPlayerController(target), eventType, eventData);
	}

	private void fireEvent(AbstractPlayerController target,
			GameEventType eventType, Object eventData) {
		GameEvent event = new GameEvent(null, eventType, eventData,
				System.currentTimeMillis());
		target.handleEvent(event);
	}

	private AbstractPlayerController getPlayerController(Player player) {
		for (AbstractPlayerController p : players) {
			if (p.getPlayer() == player) {
				return p;
			}
		}
		return null;
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
				fireEvent(game.getTurn(), GameEventType.TURN_STARTED, gameState);
			}
		} else if (gameState == GameState.MUST_DRAW_LIVE) {
			if (eventType == GameEventType.DRAW_FROM_LIVE_WALL && isTurn) {
				player.deal(game.getWall().draw());
				gameState = GameState.WAITING;
				fireEvent(game.getTurn(), GameEventType.TURN_STARTED, gameState);
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
					declareTsumo(event.getSource());
				} else if (eventType == GameEventType.ABORT_KYUUSHU_KYUUHAI) {
					declareRedeal(event);
				}
			}
		} else if (gameState == GameState.END_OF_ROUND) {
			// TODO: Players must click to proceed.

			/* Set up */
			Wall wall = game.getWall();
			wall.reset();
			for (Player p : game.getPlayers()) {
				p.nextRound();
				p.deal(wall.haipai());
			}
			gameState = GameState.MUST_DRAW_LIVE;
			fireEvent(game.getTurn(), GameEventType.TURN_STARTED, gameState);
		} else {
			System.err.printf("Unhandled event: %s%n", eventType.toString());
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
				if (eventType == c.getCallEvent()) {
					canCall.remove(c);
					called.add(c);
					break;
				}
			}
		}
	}

	private boolean containsPlayer(ArrayList<Call> callArray, Player player) {
		for (Call c : callArray) {
			if (c.getPlayer() == player) {
				return true;
			}
		}
		return false;
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
				GameEventType type = c.getCallEvent();
				if (type == GameEventType.CALL_KAN
						|| type == GameEventType.CALL_PON) {
					ponKan++;
				} else if (type == GameEventType.CALL_RON) {
					ron++;
				}
			}

			/* Prioritise */
			ArrayList<Call> callz = new ArrayList<Call>(called);
			callz.addAll(canCall);
			boolean removePonKan = (ron > 0);
			boolean removeChii = (ponKan > 0);
			for (Call c : callz) {
				GameEventType type = c.getCallEvent();
				if (((type == GameEventType.CALL_KAN || type == GameEventType.CALL_PON) && removePonKan)
						|| (type == GameEventType.CALL_CHII && removeChii)) {
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
				fireEvent(game.getTurn(), GameEventType.TURN_STARTED, gameState);
			} else if (gameState == GameState.WAITING_FOR_CALLERS) {
				game.nextTurn();
				gameState = GameState.MUST_DRAW_LIVE;
				fireEvent(game.getTurn(), GameEventType.TURN_STARTED, gameState);
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

		fireEvent(discarder, GameEventType.TURN_FINISHED, null);
		game.setTurn(caller);

		if (call.getCallEvent() == GameEventType.CALL_KAN) {
			if (game.isMaxKan()
					&& game.getWall().getNumRemainingDeadWallDraws() == 0) {
				gameState = GameState.END_OF_ROUND;
				fireEventAllPlayers(GameEventType.ABORT_5_KAN, null);
				return;
			}
			toFlip++;
			// TODO: Kan Pao
			gameState = GameState.MUST_DRAW_DEAD;
		} else {
			gameState = GameState.MUST_DISCARD;
		}

		fireEvent(caller, GameEventType.TURN_STARTED, gameState);
	}

	private void declareRon(Player winner, boolean changeGameState) {
		// TODO
		boolean isDealer = (winner == game.getDealer());
		boolean houtei = (game.getWall().getNumRemainingDraws() == 0);
		boolean chankan = (gameState == GameState.EXTENDED_KAN_DECLARED || gameState == GameState.CLOSED_KAN_DECLARED);

		int payment = -1;
		// TODO: Calculate ron payment

		fireEventAllPlayers(GameEventType.CALL_RON, winner);

		if (changeGameState) {
			boolean buttobi = game.ron(winner, game.getTurn(), payment);
			if (buttobi && game.getRuleSet().isButtobiEnds()) {
				gameState = GameState.GAME_OVER;
			} else {
				if (isDealer) {
					game.incrementBonusCounter();
					gameState = GameState.END_OF_ROUND;
				} else {
					game.resetBonusCounter();
					if (game.rotateDealers()) {
						/* Round wind changed, check for end condition */
						// TODO: Bonus round winds
						// gameState = GameState.GAME_OVER;
						gameState = GameState.END_OF_ROUND;
					}
				}
			}
		}
	}

	/**
	 * Handle a multi-Ron situation. Priorised so that the dealer stays seated
	 * if they are one of the callers.
	 */
	private void multiRon() {
		RuleSet ruleSet = game.getRuleSet();
		if (called.size() > ruleSet.getMaxSimultanousRon()) {
			if (ruleSet.isHeadBump()) {
				/* Closest to discarder in turn wins */
				Player winner = null;
				Player[] inTurn = game.getPlayersInTurnStartingAt(game
						.getTurn());
				for (int i = 1; i < inTurn.length; i++) {
					if (containsPlayer(called, inTurn[i])) {
						winner = inTurn[i];
						break;
					}
				}
				if (winner != null) {
					declareRon(winner, true);
				}
			} else {
				game.incrementBonusCounter();
				gameState = GameState.END_OF_ROUND;
				fireEventAllPlayers(GameEventType.ABORT_RON, null);
			}
		} else {
			/* Multi-ron: Dealer ron is processed last. */
			Player dealer = game.getDealer();
			if (containsPlayer(called, dealer)) {
				for (Call c : called) {
					Player winner = c.getPlayer();
					if (winner != dealer) {
						declareRon(winner, false);
					}
				}
				declareRon(dealer, true);
			} else {
				int numCallers = called.size();
				for (int i = 0; i < numCallers; i++) {
					boolean last = (i == numCallers - 1);
					declareRon(called.get(i).getPlayer(), last);
				}
			}
		}
	}

	private void declareTsumo(Player winner) {
		// TODO
		boolean haitei = false;
		boolean rinshan = deadDraw;
		gameState = GameState.END_OF_ROUND;
		fireEventAllPlayers(GameEventType.DECLARE_TSUMO, winner);
	}

	private void declareBonusTile(GameEvent event) {
		Player player = event.getSource();
		Tile tile = (Tile) event.getEventData();
		player.declareBonusTile(tile);
		gameState = GameState.BONUS_TILE_DECLARED;
		fireEventAllPlayers(GameEventType.DECLARE_BONUS_TILE, player);
		if (!hasCallers(event)) {
			gameState = GameState.MUST_DRAW_DEAD;
			fireEvent(player, GameEventType.TURN_STARTED, gameState);
		}
	}

	private void declareRedeal(GameEvent event) {
		Player player = event.getSource();
		if (game.getTurn() == player
				&& game.isFirstGoAround()
				&& Hand.isKyuushuKyuuhai(player.getHand(), player.getTsumoHai())) {
			game.incrementBonusCounter();
			gameState = GameState.END_OF_ROUND;
			fireEventAllPlayers(GameEventType.ABORT_KYUUSHU_KYUUHAI, null);
		}
	}

	private void declareRiichi(GameEvent event) {
		Player player = event.getSource();
		if (player.declareRiichi()) {
			game.incrementNumRiichiSticks();
			gameState = GameState.MUST_DISCARD;
			fireEvent(player, GameEventType.TURN_STARTED, gameState);
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
			fireEvent(player, GameEventType.TURN_FINISHED, null);
			RuleSet rs = game.getRuleSet();
			if (game.getWall().getNumRemainingDraws() == 0) {
				gameState = GameState.END_OF_ROUND;
				fireEventAllPlayers(GameEventType.EXHAUSTIVE_DRAW, null);
			} else if (rs.isAllRiichiAbort()
					&& game.getNumPlayersRiichi() == game.getPlayers().length) {
				gameState = GameState.END_OF_ROUND;
				fireEventAllPlayers(GameEventType.ABORT_ALL_RIICHI, null);
			} else if (game.isFourWindsAbort()) {
				Tile discard = player.getLatestDiscard();
				Tile first = game.getFirstDiscard();
				if (!discard.equals(first) || !discard.isWind()
						|| !game.isFirstGoAround()) {
					game.setFourWindsAbort(false);
				}
				if (game.getTurnCounter() == 3) {
					gameState = GameState.END_OF_ROUND;
					fireEventAllPlayers(GameEventType.ABORT_4_WINDS, null);
				}
			} else if (rs.isFourKanAbort() && game.isMaxKan()) {
				gameState = GameState.END_OF_ROUND;
				fireEventAllPlayers(GameEventType.ABORT_4_KAN, null);
			} else {
				game.nextTurn();
				gameState = GameState.MUST_DRAW_LIVE;
				fireEvent(game.getTurn(), GameEventType.TURN_STARTED, gameState);
			}
		}
	}

	private void declareKan(GameEvent event) {
		if (game.isMaxKan()) {
			// TODO: 4 kan abort
			return;
		}

		Meld meld = (Meld) event.getEventData();
		Player player = event.getSource();
		if (meld.getType() == MeldType.KANTSU_CLOSED) {
			player.addMeld(meld);
			game.getWall().flipDora();
			gameState = GameState.CLOSED_KAN_DECLARED;
			fireEventAllPlayers(GameEventType.DECLARE_KAN, player);
			if (!hasCallers(event)) {
				gameState = GameState.MUST_DRAW_DEAD;
				fireEvent(player, GameEventType.TURN_STARTED, gameState);
			}
		} else if (meld.getType() == MeldType.KANTSU_EXTENDED) {
			player.addMeld(meld);
			toFlip++;
			gameState = GameState.EXTENDED_KAN_DECLARED;
			fireEventAllPlayers(GameEventType.DECLARE_KAN, player);
			if (!hasCallers(event)) {
				gameState = GameState.MUST_DRAW_DEAD;
				fireEvent(player, GameEventType.TURN_STARTED, gameState);
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
		for (Player player : game.getPlayers()) {

			if (game.getTurn() == player) {
				continue;
			}

			ArrayList<Tile> hand = player.getHand();
			ArrayList<Meld> melds = player.getMelds();

			/* Chii/Pon/Kan calls */
			ArrayList<Meld> callableMelds = Hand.getCallableMelds(hand, tile);
			for (Meld meld : callableMelds) {
				addCall(new Call(player, meld));
			}

			/* Ron calls */
			if (Hand.getWaits(hand, melds).contains(tile)) {
				Call ronCall = new Call(player, GameEventType.CALL_RON);
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

			/* Notify player */
			for (Call c : canCall) {
				if (c.getPlayer() == player) {
					fireEvent(player, c.getCallEvent(), c);
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
