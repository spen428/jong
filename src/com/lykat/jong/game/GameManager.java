package com.lykat.jong.game;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lykat.jong.calc.Hand;
import com.lykat.jong.calc.Yaku;
import com.lykat.jong.control.AbstractPlayerController;
import com.lykat.jong.game.GameEvent.GameEventType;
import com.lykat.jong.game.Meld.MeldType;

public class GameManager implements EventListener {

	public static final Logger LOGGER = Logger.getLogger("GameManager");

	public enum GameState {
		MUST_DRAW_LIVE, MUST_DRAW_DEAD, MUST_DISCARD, WAITING, WAITING_FOR_CALLERS, END_OF_ROUND, EXTENDED_KAN_DECLARED, CLOSED_KAN_DECLARED, BONUS_TILE_DECLARED, GAME_OVER, WAITING_FOR_PLAYERS;
	}

	private int toFlip;
	private ArrayList<Call> canCall, called;

	private final Game game;
	private final AbstractPlayerController[] players;

	public GameManager(Game game) {
		super();
		this.game = game;
		this.players = new AbstractPlayerController[game.getRuleSet()
				.getNumPlayers()];
		this.toFlip = 0;
		this.game.setGameState(GameState.WAITING_FOR_PLAYERS);
		this.canCall = new ArrayList<Call>();
		this.called = new ArrayList<Call>();
	}

	private boolean connect(AbstractPlayerController player) {
		// TODO: no duplicates
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) {
				players[i] = player;
				return true;
			}
		}
		return false;
	}

	public Game getGame() {
		return game;
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
		if (target != null) {
			GameEvent event = new GameEvent(null, eventType, eventData,
					System.currentTimeMillis());
			target.handleEvent(event);
		}
	}

	private AbstractPlayerController getPlayerController(Player player) {
		for (AbstractPlayerController p : players) {
			if (p != null && p.getPlayer() == player) {
				return p;
			}
		}
		return null;
	}

	public void handleEvent(GameEvent event) {
		// TODO: Timeouts
		final GameEventType eventType = event.getEventType();
		final Player player = event.getSource();
		final boolean isTurn = (player == game.getTurn());
		final GameState gameState = game.getGameState();

		if (gameState == GameState.WAITING_FOR_PLAYERS) {
			if (eventType == GameEventType.PLAYER_CONNECT) {
				AbstractPlayerController conn = (AbstractPlayerController) event
						.getEventData();
				if (connect(conn)) {
					fireEventAllPlayers(GameEventType.PLAYER_CONNECT, conn);
					if (numConnectedPlayers() == players.length) {
						TileValue seatWind = TileValue.TON;
						for (int i = 0; i < players.length; i++) {
							game.newPlayer(players[i].getName());
							Player p = game.getPlayers()[i];
							p.setSeatWind(seatWind);
							players[i].setPlayer(p);
							seatWind = Game.nextWind(seatWind, true);
						}
						setUpNewRound();
					}
				} else {
					// TODO Player could not connect
				}
			}
		} else if (gameState == GameState.CLOSED_KAN_DECLARED
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
				game.setDeadDraw(true);
				game.setGameState(GameState.WAITING);
				fireEvent(game.getTurn(), GameEventType.TURN_STARTED, gameState);
			}
		} else if (gameState == GameState.MUST_DRAW_LIVE) {
			if (eventType == GameEventType.DRAW_FROM_LIVE_WALL && isTurn) {
				player.deal(game.getWall().draw());
				game.setGameState(GameState.WAITING);
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
			setUpNewRound();
		} else {
			LOGGER.log(Level.FINE, "Unhandled event: " + eventType.toString());
		}

		LOGGER.log(Level.FINER, "Current gamestate: " + gameState.toString());
	}

	private void setUpNewRound() {
		Wall wall = game.getWall();
		wall.reset();
		game.resetFourWindsAbort();
		game.setDeadDraw(false);
		for (Player p : game.getPlayers()) {
			p.nextRound();
			p.deal(wall.haipai());
		}
		game.setGameState(GameState.MUST_DRAW_LIVE);
		fireEventAllPlayers(GameEventType.ROUND_STARTED, null);
		fireEvent(game.getTurn(), GameEventType.TURN_STARTED,
				game.getGameState());
	}

	private int numConnectedPlayers() {
		int num = 0;
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				num++;
			}
		}
		return num;
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
			if (game.getGameState() == GameState.BONUS_TILE_DECLARED
					|| game.getGameState() == GameState.CLOSED_KAN_DECLARED
					|| game.getGameState() == GameState.EXTENDED_KAN_DECLARED) {
				game.setGameState(GameState.MUST_DRAW_DEAD);
				fireEvent(game.getTurn(), GameEventType.TURN_STARTED,
						game.getGameState());
			} else if (game.getGameState() == GameState.WAITING_FOR_CALLERS) {
				game.nextTurn();
				game.setGameState(GameState.MUST_DRAW_LIVE);
				fireEvent(game.getTurn(), GameEventType.TURN_STARTED,
						game.getGameState());
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
		game.interruptPlayers();
		game.setTurn(caller);

		if (call.getCallEvent() == GameEventType.CALL_KAN) {
			if (game.isMaxKan()
					&& game.getWall().getNumRemainingDeadWallDraws() == 0) {
				game.setGameState(GameState.END_OF_ROUND);
				fireEventAllPlayers(GameEventType.ABORT_5_KAN, null);
				return;
			}
			toFlip++;
			// TODO: Kan Pao
			game.setGameState(GameState.MUST_DRAW_DEAD);
		} else {
			game.setGameState(GameState.MUST_DISCARD);
		}

		fireEvent(caller, GameEventType.TURN_STARTED, game.getGameState());
	}

	private void declareRon(Player winner, boolean changeGameState) {
		ArrayList<Yaku> yaku = Hand.getYaku(winner.getHand(),
				winner.getMelds(), game.getTurn().getLatestDiscard());

		// TODO
		boolean isDealer = (winner == game.getDealer());
		boolean chankan = (game.getGameState() == GameState.EXTENDED_KAN_DECLARED || game
				.getGameState() == GameState.CLOSED_KAN_DECLARED);
		boolean houtei = !chankan
				&& (game.getGameState() != GameState.BONUS_TILE_DECLARED)
				&& (game.getWall().getNumRemainingDraws() == 0);
		boolean riichi = winner.isRiichi();
		boolean ippatsu = riichi && !winner.isInterrupted();

		int payment = Hand.countFuHan(yaku);

		fireEventAllPlayers(GameEventType.CALL_RON, winner);

		if (changeGameState) {
			boolean buttobi = game.ron(winner, game.getTurn(), payment);
			if (buttobi && game.getRuleSet().isButtobiEnds()) {
				game.setGameState(GameState.GAME_OVER);
			} else {
				if (isDealer) {
					game.incrementBonusCounter();
					game.setGameState(GameState.END_OF_ROUND);
				} else {
					game.resetBonusCounter();
					if (game.isGameOver()) {
						game.setGameState(GameState.GAME_OVER);
					} else {
						game.rotateDealers();
						game.setGameState(GameState.END_OF_ROUND);
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
				game.setGameState(GameState.END_OF_ROUND);
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
		boolean rinshan = game.isDeadDraw();
		game.setGameState(GameState.END_OF_ROUND);
		fireEventAllPlayers(GameEventType.DECLARE_TSUMO, winner);
	}

	private void declareBonusTile(GameEvent event) {
		Player player = event.getSource();
		Tile tile = (Tile) event.getEventData();
		player.declareBonusTile(tile);
		game.interruptPlayers();
		game.setGameState(GameState.BONUS_TILE_DECLARED);
		fireEventAllPlayers(GameEventType.DECLARE_BONUS_TILE, player);
		if (!hasCallers(event)) {
			game.setGameState(GameState.MUST_DRAW_DEAD);
			fireEvent(player, GameEventType.TURN_STARTED, game.getGameState());
		}
	}

	private void declareRedeal(GameEvent event) {
		Player player = event.getSource();
		if (game.getTurn() == player
				&& game.isFirstGoAround()
				&& Hand.isKyuushuKyuuhai(player.getHand(), player.getTsumoHai())) {
			game.incrementBonusCounter();
			game.setGameState(GameState.END_OF_ROUND);
			fireEventAllPlayers(GameEventType.ABORT_KYUUSHU_KYUUHAI, null);
		}
	}

	private void declareRiichi(GameEvent event) {
		Player player = event.getSource();
		if (player.declareRiichi()) {
			game.incrementNumRiichiSticks();
			game.setGameState(GameState.MUST_DISCARD);
			fireEvent(player, GameEventType.TURN_STARTED, game.getGameState());
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
		game.setDeadDraw(false);

		while (toFlip > 0) {
			game.getWall().flipDora();
			toFlip--;
		}
		game.setGameState(GameState.WAITING_FOR_CALLERS);
		if (!hasCallers(event)) {
			LOGGER.log(Level.FINE, "Discard had no callers.");
			fireEventAllPlayers(GameEventType.TURN_FINISHED, player);
			RuleSet rs = game.getRuleSet();
			if (game.isFourWindsAbort()) {
				Tile discard = player.getLatestDiscard();
				Tile first = game.getFirstDiscard();
				if (!discard.equals(first) || !discard.isWind()
						|| !game.isFirstGoAround()) {
					game.setFourWindsAbort(false);
				}
				if (game.getTurnCounter() == 3) {
					game.setGameState(GameState.END_OF_ROUND);
					fireEventAllPlayers(GameEventType.ABORT_4_WINDS, null);
					return;
				}
			}
			if (game.getWall().getNumRemainingDraws() == 0) {
				game.setGameState(GameState.END_OF_ROUND);
				fireEventAllPlayers(GameEventType.EXHAUSTIVE_DRAW, null);
			} else if (rs.isAllRiichiAbort()
					&& game.getNumPlayersRiichi() == game.getPlayers().length) {
				game.setGameState(GameState.END_OF_ROUND);
				fireEventAllPlayers(GameEventType.ABORT_ALL_RIICHI, null);
			} else if (rs.isFourKanAbort() && game.isMaxKan()) {
				game.setGameState(GameState.END_OF_ROUND);
				fireEventAllPlayers(GameEventType.ABORT_4_KAN, null);
			} else {
				game.nextTurn();
				game.setGameState(GameState.MUST_DRAW_LIVE);
				fireEvent(game.getTurn(), GameEventType.TURN_STARTED,
						game.getGameState());
			}
		}
	}

	private void declareKan(GameEvent event) {
		if (game.isMaxKan()) {
			// TODO: 4 kan abort
			return;
		}

		game.interruptPlayers();
		Meld meld = (Meld) event.getEventData();
		Player player = event.getSource();
		if (meld.getType() == MeldType.KANTSU_CLOSED) {
			player.addMeld(meld);
			game.getWall().flipDora();
			game.setGameState(GameState.CLOSED_KAN_DECLARED);
			fireEventAllPlayers(GameEventType.DECLARE_KAN, player);
			if (!hasCallers(event)) {
				game.setGameState(GameState.MUST_DRAW_DEAD);
				fireEvent(player, GameEventType.TURN_STARTED,
						game.getGameState());
			}
		} else if (meld.getType() == MeldType.KANTSU_EXTENDED) {
			player.addMeld(meld);
			toFlip++;
			game.setGameState(GameState.EXTENDED_KAN_DECLARED);
			fireEventAllPlayers(GameEventType.DECLARE_KAN, player);
			if (!hasCallers(event)) {
				game.setGameState(GameState.MUST_DRAW_DEAD);
				fireEvent(player, GameEventType.TURN_STARTED,
						game.getGameState());
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
				if (game.getGameState() == GameState.BONUS_TILE_DECLARED
						|| game.getGameState() == GameState.EXTENDED_KAN_DECLARED
						|| game.getGameState() == GameState.WAITING_FOR_CALLERS) {
					addCall(ronCall);
				} else if (game.getGameState() == GameState.CLOSED_KAN_DECLARED) {
					ArrayList<Yaku> yaku = Hand.getYaku(hand, melds, tile);
					if (yaku.contains(Yaku.YM_KOKUSHI_MUSOU)
							|| yaku.contains(Yaku.YM_KOKUSHI_MUSOU_13_MAN_MACHI)) {
						addCall(ronCall);
					}
				}
			}

			/* Notify player */
			for (Call c : canCall) {
				Player caller = c.getPlayer();
				if (caller == player) {
					GameEventType callEvent = c.getCallEvent();
					fireEvent(player, callEvent, c);
					LOGGER.log(Level.FINE, "Player " + player.getName()
							+ " has a " + callEvent.toString() + " call");
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
