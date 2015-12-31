package com.lykat.jong.game;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lykat.jong.calc.Hand;
import com.lykat.jong.calc.Yaku;
import com.lykat.jong.control.AbstractPlayerController;
import com.lykat.jong.game.GameEvent.GameEventType;
import com.lykat.jong.game.Meld.MeldSource;
import com.lykat.jong.game.Meld.MeldType;

public class GameManager implements GameEventListener {

    public enum GameState {
        BONUS_TILE_DECLARED,
        CLOSED_KAN_DECLARED,
        END_OF_ROUND,
        EXTENDED_KAN_DECLARED,
        GAME_OVER,
        MUST_DISCARD,
        WAITING,
        WAITING_FOR_CALLERS,
        WAITING_FOR_PLAYERS;
    }

    public static final Logger LOGGER = Logger.getLogger("GameManager"); //$NON-NLS-1$

    private static boolean containsPlayer(ArrayList<Call> callArray,
            Player player) {
        for (Call c : callArray) {
            if (c.getPlayer() == player) {
                return true;
            }
        }
        return false;
    }

    private static void fireEvent(AbstractPlayerController target,
            GameEvent event) {
        if (target != null) {
            target.handleEvent(event);
        }
    }

    private static void fireEvent(AbstractPlayerController target,
            GameEventType eventType, Object eventData) {
        GameEvent event = new GameEvent(null, eventType, eventData,
                System.currentTimeMillis());
        fireEvent(target, event);
    }

    private final ArrayList<Call> canCall, called;
    private final Game game;

    final BlockingQueue<GameEvent> myQueue;
    private final Thread myThread;

    private final AbstractPlayerController[] players;

    private int toFlip;

    private final ArrayList<Player> waitingForOk;

    public GameManager(Game game) {
        super();
        this.game = game;
        this.players = new AbstractPlayerController[game.getRuleSet()
                .getNumPlayers()];
        this.toFlip = 0;
        this.game.setGameState(GameState.WAITING_FOR_PLAYERS);
        this.canCall = new ArrayList<>();
        this.called = new ArrayList<>();
        this.waitingForOk = new ArrayList<>();

        this.myQueue = new ArrayBlockingQueue<>(20);
        this.myThread = new Thread(new Runnable() {
            private final BlockingQueue<GameEvent> queue = GameManager.this.myQueue;

            @Override
            public void run() {
                while (true) {
                    try {
                        asyncHandleEvent(this.queue.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.myThread.setDaemon(true);
        this.myThread.start();
    }

    /**
     * Adds the given call to the 'canCall' ArrayList, iff it does not already
     * exist.
     */
    private void addCall(Call call) {
        if (!this.canCall.contains(call)) {
            this.canCall.add(call);
        }
    }

    /**
     * Do NOT call this method.
     */
    void asyncHandleEvent(GameEvent event) {
        // TODO: Timeouts
        final GameEventType eventType = event.getEventType();
        final Player player = event.getSource();
        final boolean isTurn = (player == this.game.getTurn());
        final GameState gameState = this.game.getGameState();

        /* Echo event back to source */
        fireEvent(player, event); // TODO: Change

        if (eventType == GameEventType.PLAYER_CONNECT) {
            AbstractPlayerController conn = (AbstractPlayerController) event
                    .getEventData();
            if (gameState == GameState.WAITING_FOR_PLAYERS) {
                if (connect(conn)) {
                    fireEventAllPlayers(GameEventType.PLAYER_CONNECT, conn);
                    if (numConnectedPlayers() == this.players.length) {
                        setUpNewGame();
                        setUpNewRound();
                    }
                } else {
                    // TODO Player could not connect
                }
            } else {
                /* Connect as an observer */
                // TODO
            }
        }

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
            if (eventType == GameEventType.OK) {
                LOGGER.log(Level.INFO, "Player " + player.getName()
                        + " pressed OK.");
                this.waitingForOk.remove(player);
                if (this.waitingForOk.size() == 0) {
                    setUpNewRound();
                }
            }
        } else {
            LOGGER.log(Level.FINE, "Unhandled event: " + eventType.toString());
        }

    }

    /**
     * Signal that the player in the given event has called. If they are the
     * only valid caller remaining, this will change the game state.
     */
    private void called(GameEvent event) {
        // TODO: Handle mulitple calls from same player
        if (this.canCall.size() > 0) {
            doCalled(event);
            prioriseCalls();
            if (this.canCall.size() == 0) {
                if (this.called.size() == 1) {
                    doCall(this.called.remove(0));
                } else if (this.called.size() > 1) {
                    multiRon();
                } else {
                    throw new IllegalStateException("Called list is empty! "
                            + "Concurrent modification?");
                }
                this.called.clear();
            }
        }
    }

    private boolean connect(AbstractPlayerController player) {
        int insertIndex = -1;
        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i] == null) {
                insertIndex = i;
            } else if (this.players[i].getName().equals(player.getName())) {
                // TODO: Go by ID, not name
                fireEvent(player, GameEventType.SENT_MESSAGE,
                        "Connection refused: Player with the same name is"
                                + " already in this game.");
                return false;
            }
        }
        if (insertIndex != -1) {
            this.players[insertIndex] = player;
            fireEvent(player, GameEventType.SENT_MESSAGE,
                    "Connection accepted.");
            return true;
        }
        fireEvent(player, GameEventType.SENT_MESSAGE,
                "Connection refused: Game full.");
        return false;
    }

    private void declareBonusTile(GameEvent event) {
        Player player = event.getSource();
        Tile tile = (Tile) event.getEventData();
        player.declareBonusTile(tile);
        this.game.interruptPlayers();
        this.game.setGameState(GameState.BONUS_TILE_DECLARED);
        fireEventAllPlayers(GameEventType.DECLARE_BONUS_TILE, player);
        if (!hasCallers(event)) {
            drawFromDeadWall(player);
        }
    }

    private void declareKan(GameEvent event) {
        if (this.game.isMaxKan()) {
            // TODO: 4 kan
            return;
        }

        this.game.interruptPlayers();
        Meld meld = (Meld) event.getEventData();
        Player player = event.getSource();
        if (meld.getType() == MeldType.KANTSU_CLOSED) {
            player.addMeld(meld);
            Tile newHyouji = this.game.getWall().flipDora();
            fireEventAllPlayers(GameEventType.FLIPPED_DORA_HYOUJI, newHyouji);
            this.game.setGameState(GameState.CLOSED_KAN_DECLARED);
            fireEventAllPlayers(GameEventType.DECLARE_KAN, player);
            if (hasCallers(event)) {
                // Chankan Ron (Kokushi Musou)
                return;
            }
        } else if (meld.getType() == MeldType.KANTSU_EXTENDED) {
            player.addMeld(meld);
            this.toFlip++;
            this.game.setGameState(GameState.EXTENDED_KAN_DECLARED);
            fireEventAllPlayers(GameEventType.DECLARE_KAN, player);
            if (hasCallers(event)) {
                // Chankan Ron
                return;
            }
        } else {
            throw new IllegalStateException("Declaring Kan with illegal meld"
                    + "type: " + meld.getType().toString());
        }

        drawFromDeadWall(player);
    }

    private void declareRedeal(GameEvent event) {
        Player player = event.getSource();
        if (this.game.getTurn() == player
                && this.game.isFirstGoAround()
                && Hand.isKyuushuKyuuhai(player.getHand(), player.getTsumoHai())) {
            this.game.incrementBonusCounter();
            endOfRound();
            fireEventAllPlayers(GameEventType.ABORT_KYUUSHU_KYUUHAI,
                    this.game.getGameState());
        }
    }

    private void declareRiichi(GameEvent event) {
        Player player = event.getSource();
        if (player.declareRiichi()) {
            this.game.incrementNumRiichiSticks();
            this.game.setGameState(GameState.MUST_DISCARD);
            fireEvent(player, GameEventType.TURN_STARTED,
                    this.game.getGameState());
        }
    }

    private void declareRon(Player winner, boolean changeGameState) {
        ArrayList<Yaku> yaku = Hand.getYaku(winner.getHand(),
                winner.getMelds(), this.game.getTurn().getLatestDiscard());

        // TODO
        boolean isDealer = (winner == this.game.getDealer());
        boolean chankan = (this.game.getGameState() == GameState.EXTENDED_KAN_DECLARED || this.game
                .getGameState() == GameState.CLOSED_KAN_DECLARED);
        boolean houtei = !chankan
                && (this.game.getGameState() != GameState.BONUS_TILE_DECLARED)
                && (this.game.getWall().getNumRemainingDraws() == 0);
        boolean riichi = winner.isRiichi();
        boolean ippatsu = riichi && !winner.isInterrupted();

        int payment = Hand.countFuHan(yaku);

        fireEventAllPlayers(GameEventType.CALL_RON, winner);

        if (changeGameState) {
            boolean buttobi = this.game.ron(winner, this.game.getTurn(),
                    payment);
            if (buttobi && this.game.getRuleSet().isButtobiEnds()) {
                this.game.setGameState(GameState.GAME_OVER);
            } else {
                if (isDealer) {
                    this.game.incrementBonusCounter();
                    endOfRound();
                } else {
                    this.game.resetBonusCounter();
                    if (this.game.isGameOver()) {
                        this.game.setGameState(GameState.GAME_OVER);
                    } else {
                        this.game.rotateDealers();
                        endOfRound();
                    }
                }
            }
        }
    }

    private void declareTsumo(Player winner) {
        // TODO
        boolean haitei = false;
        boolean rinshan = this.game.isDeadDraw();
        endOfRound();
        fireEventAllPlayers(GameEventType.DECLARE_TSUMO, winner);
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
        try {
            Tile tile = player.discard(index);
            this.game.setDeadDraw(false);
            LOGGER.log(Level.FINER, String.format("Player %s"
                    + " discarded tile %s", player.getName(), tile.toString()));
            fireEventAllPlayers(GameEventType.DISCARD, player);
        } catch (IllegalStateException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
            return;
        }

        while (this.toFlip > 0) {
            Tile newHyouji = this.game.getWall().flipDora();
            fireEventAllPlayers(GameEventType.FLIPPED_DORA_HYOUJI, newHyouji);
            this.toFlip--;
        }
        this.game.setGameState(GameState.WAITING_FOR_CALLERS);
        if (!hasCallers(event)) {
            LOGGER.log(Level.FINE, "Discard had no callers.");
            fireEventAllPlayers(GameEventType.TURN_FINISHED, player);
            RuleSet rs = this.game.getRuleSet();
            if (this.game.isFourWindsAbort()) {
                Tile discard = player.getLatestDiscard();
                Tile first = this.game.getFirstDiscard();
                if (!discard.equals(first) || !discard.isWind()
                        || !this.game.isFirstGoAround()) {
                    this.game.setFourWindsAbort(false);
                }
                if (this.game.getTurnCounter() == 3) {
                    endOfRound();
                    fireEventAllPlayers(GameEventType.ABORT_4_WINDS,
                            this.game.getGameState());
                    return;
                }
            }
            if (this.game.getWall().getNumRemainingDraws() == 0) {
                endOfRound();
                fireEventAllPlayers(GameEventType.EXHAUSTIVE_DRAW,
                        this.game.getGameState());
            } else if (rs.isAllRiichiAbort()
                    && this.game.getNumPlayersRiichi() == this.game
                            .getPlayers().length) {
                endOfRound();
                fireEventAllPlayers(GameEventType.ABORT_ALL_RIICHI,
                        this.game.getGameState());
            } else if (rs.isFourKanAbort() && this.game.isMaxKan()) {
                endOfRound();
                fireEventAllPlayers(GameEventType.ABORT_4_KAN,
                        this.game.getGameState());
            } else {
                this.game.nextTurn();
                drawFromLiveWall(this.game.getTurn());
            }
        }
    }

    /**
     * Applies the given call, advancing the game state.
     */
    private void doCall(Call call) {
        Player caller = call.getPlayer();
        Player discarder = this.game.getTurn();
        Meld meld = call.getMeld();

        if (meld.getMeldSource() == MeldSource.UNKNOWN) {
            MeldSource meldSource = getMeldSource(caller, discarder);
            meld = new Meld(meld.getTiles(), meld.getCallTile(), meldSource,
                    meld.getType());
        }
        discarder.removeLatestDiscard();
        caller.addMeld(meld);

        fireEvent(discarder, GameEventType.TURN_FINISHED, null);
        fireEventAllPlayers(call.getCallEvent(), caller);
        this.game.interruptPlayers();
        this.game.setTurn(caller);

        if (call.getCallEvent() == GameEventType.CALL_KAN) {
            if (this.game.isMaxKan()
                    && this.game.getWall().getNumRemainingDeadWallDraws() == 0) {
                endOfRound();
                fireEventAllPlayers(GameEventType.ABORT_5_KAN,
                        this.game.getGameState());
                return;
            }
            this.toFlip++;
            // TODO: Kan Pao
            drawFromDeadWall(caller);
        } else {
            this.game.setGameState(GameState.MUST_DISCARD);
            fireEvent(caller, GameEventType.TURN_STARTED,
                    this.game.getGameState());
        }
    }

    /**
     * Responds to a 'Call' GameEvent by adding the call to the 'called' AL (so
     * long as it existed in 'canCall' and is valid).
     */
    private void doCalled(GameEvent event) {
        GameEventType eventType = event.getEventType();
        Player source = event.getSource();
        for (Call c : new ArrayList<>(this.canCall)) {
            if (c.getPlayer() == source) {
                if (eventType == c.getCallEvent()) {
                    this.canCall.remove(c);
                    this.called.add(c);
                    break;
                }
            }
        }
    }

    private void drawFromDeadWall(Player player) {
        Tile tile = this.game.getWall().deadWallDraw();
        player.deal(tile);
        this.game.setDeadDraw(true);
        this.game.setGameState(GameState.WAITING);
        fireEventAllPlayers(GameEventType.DREW_FROM_DEAD_WALL, player);
        fireEvent(player, GameEventType.DREW_FROM_DEAD_WALL, tile);
        fireEvent(player, GameEventType.TURN_STARTED, this.game.getGameState());
    }

    private void drawFromLiveWall(Player player) {
        Tile tile = this.game.getWall().draw();
        player.deal(tile);
        this.game.setGameState(GameState.WAITING);
        fireEventAllPlayers(GameEventType.DREW_FROM_LIVE_WALL, player);
        fireEvent(player, GameEventType.DREW_FROM_LIVE_WALL, tile);
        fireEvent(player, GameEventType.TURN_STARTED, this.game.getGameState());
    }

    private void endOfRound() {
        this.game.setGameState(GameState.END_OF_ROUND);
        for (AbstractPlayerController p : this.players) {
            this.waitingForOk.clear();
            this.waitingForOk.add(p.getPlayer());
        }
    }

    private void fireEvent(Player target, GameEvent event) {
        fireEvent(getPlayerController(target), event);
    }

    private void fireEvent(Player target, GameEventType eventType,
            Object eventData) {
        fireEvent(getPlayerController(target), eventType, eventData);
    }

    private void fireEventAllPlayers(GameEventType eventType) {
        fireEventAllPlayers(eventType, null);
    }

    private void fireEventAllPlayers(GameEventType eventType, Object eventData) {
        for (AbstractPlayerController player : this.players) {
            fireEvent(player, eventType, eventData);
        }
    }

    public Game getGame() {
        return this.game;
    }

    private MeldSource getMeldSource(Player caller, Player discarder) {
        int callerId = -1;
        int discarderId = -1;
        for (int i = 0; i < this.players.length; i++) {
            Player p = this.players[i].getPlayer();
            if (p == caller) {
                callerId = i;
            }
            if (p == discarder) {
                discarderId = i;
            }
        }

        /* */
        if (callerId == -1 || discarderId == -1) {
            return MeldSource.UNKNOWN;
        }

        // TODO: This breaks for 5-player games, but who plays 5-player jong?
        int diff = (callerId - discarderId) % this.players.length;
        if (diff == 0) {
            return MeldSource.SELF;
        } else if (diff == 1) {
            return MeldSource.LEFT;
        } else if (diff == 2) {
            return MeldSource.ACROSS;
        } else {
            return MeldSource.RIGHT;
        }
    }

    private AbstractPlayerController getPlayerController(Player player) {
        for (AbstractPlayerController p : this.players) {
            if (p != null && p.getPlayer() == player) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void handleEvent(GameEvent event) {
        try {
            this.myQueue.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        this.canCall.clear();
        Player discarder = event.getSource();
        Tile tile = discarder.getLatestDiscard();
        for (Player player : this.game.getPlayers()) {

            if (this.game.getTurn() == player) {
                continue;
            }

            ArrayList<Tile> hand = player.getHand();

            /* Chii/Pon/Kan calls */
            ArrayList<Meld> callableMelds = Hand.getCallableMelds(hand, tile);
            for (Meld meld : callableMelds) {
                addCall(new Call(player, meld));
            }

            /* Ron calls */
            ArrayList<Meld> melds = player.getMelds();
            if (Hand.getWaits(hand, melds).contains(tile)) {
                Call ronCall = new Call(player, GameEventType.CALL_RON);
                if (this.game.getGameState() == GameState.BONUS_TILE_DECLARED
                        || this.game.getGameState() == GameState.EXTENDED_KAN_DECLARED
                        || this.game.getGameState() == GameState.WAITING_FOR_CALLERS) {
                    addCall(ronCall);
                } else if (this.game.getGameState() == GameState.CLOSED_KAN_DECLARED) {
                    ArrayList<Yaku> yaku = Hand.getYaku(hand, melds, tile);
                    if (yaku.contains(Yaku.YM_KOKUSHI_MUSOU)
                            || yaku.contains(Yaku.YM_KOKUSHI_MUSOU_13_MAN_MACHI)) {
                        addCall(ronCall);
                    }
                }
            }

            /* Notify player */
            for (Call c : this.canCall) {
                Player caller = c.getPlayer();
                if (caller == player) {
                    GameEventType callEvent = c.getCallEvent();
                    fireEvent(player, callEvent, c);
                    LOGGER.log(Level.FINE, "Player " + player.getName()
                            + " has a " + callEvent.toString() + " call");
                }
            }

        }

        return (this.canCall.size() > 0);
    }

    /**
     * Handle a multi-Ron situation. Priorised so that the dealer stays seated
     * if they are one of the callers.
     */
    private void multiRon() {
        RuleSet ruleSet = this.game.getRuleSet();
        if (this.called.size() > ruleSet.getMaxSimultanousRon()) {
            if (ruleSet.isHeadBump()) {
                /* Closest to discarder in turn wins */
                Player winner = null;
                Player[] inTurn = this.game
                        .getPlayersInTurnStartingAt(this.game.getTurn());
                for (int i = 1; i < inTurn.length; i++) {
                    if (containsPlayer(this.called, inTurn[i])) {
                        winner = inTurn[i];
                        break;
                    }
                }
                if (winner != null) {
                    declareRon(winner, true);
                }
            } else {
                this.game.incrementBonusCounter();
                endOfRound();
                fireEventAllPlayers(GameEventType.ABORT_RON,
                        this.game.getGameState());
            }
        } else {
            /* Multi-ron: Dealer ron is processed last. */
            Player dealer = this.game.getDealer();
            if (containsPlayer(this.called, dealer)) {
                for (Call c : this.called) {
                    Player winner = c.getPlayer();
                    if (winner != dealer) {
                        declareRon(winner, false);
                    }
                }
                declareRon(dealer, true);
            } else {
                int numCallers = this.called.size();
                for (int i = 0; i < numCallers; i++) {
                    boolean last = (i == numCallers - 1);
                    declareRon(this.called.get(i).getPlayer(), last);
                }
            }
        }
    }

    private int numConnectedPlayers() {
        int num = 0;
        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i] != null) {
                num++;
            }
        }
        return num;
    }

    /**
     * Priorise tile calls. Outranked calls are removed from the 'called' and
     * 'canCall' ArrayList.
     */
    private void prioriseCalls() {
        if (this.called.size() > 0) {
            /* Count */
            int ron = 0, ponKan = 0;
            for (Call c : this.called) {
                GameEventType type = c.getCallEvent();
                if (type == GameEventType.CALL_KAN
                        || type == GameEventType.CALL_PON) {
                    ponKan++;
                } else if (type == GameEventType.CALL_RON) {
                    ron++;
                }
            }

            /* Prioritise */
            ArrayList<Call> callz = new ArrayList<>(this.called);
            callz.addAll(this.canCall);
            boolean removePonKan = (ron > 0);
            boolean removeChii = (ponKan > 0);
            for (Call c : callz) {
                GameEventType type = c.getCallEvent();
                if (((type == GameEventType.CALL_KAN || type == GameEventType.CALL_PON) && removePonKan)
                        || (type == GameEventType.CALL_CHII && removeChii)) {
                    this.canCall.remove(c);
                    this.called.remove(c);
                }
            }
        }
    }

    /**
     * Removes the given called from the call list. If they were the last
     * remaining caller, this will change the game state.
     */
    private void removeCaller(Player player) {
        for (Call c : new ArrayList<>(this.canCall)) {
            if (c.getPlayer() == player) {
                this.canCall.remove(c);
            }
        }
        if (this.canCall.size() == 0) {
            if (this.game.getGameState() == GameState.BONUS_TILE_DECLARED
                    || this.game.getGameState() == GameState.CLOSED_KAN_DECLARED
                    || this.game.getGameState() == GameState.EXTENDED_KAN_DECLARED) {
                drawFromDeadWall(this.game.getTurn());
            } else if (this.game.getGameState() == GameState.WAITING_FOR_CALLERS) {
                this.game.nextTurn();
                drawFromLiveWall(this.game.getTurn());
            }
        }
    }

    private void setUpNewGame() {
        TileValue seatWind = TileValue.TON;
        for (int i = 0; i < this.players.length; i++) {
            this.game.newPlayer(this.players[i].getName());
            Player p = this.game.getPlayers()[i];
            p.setSeatWind(seatWind);
            this.players[i].setPlayer(p);
            seatWind = Game.nextWind(seatWind, true);
        }
    }

    private void setUpNewRound() {
        Wall wall = this.game.getWall();
        wall.reset();
        this.game.resetFourWindsAbort();
        this.game.setDeadDraw(false);
        this.called.clear();
        this.canCall.clear();

        fireEventAllPlayers(GameEventType.ROUND_STARTED);
        /* Deal haipai */
        for (AbstractPlayerController apc : this.players) {
            Player player = apc.getPlayer();
            player.nextRound();
            Tile[] tiles = wall.haipai();
            player.deal(tiles);
            for (Tile tile : tiles) {
                fireEvent(apc, GameEventType.DREW_FROM_LIVE_WALL, tile);
            }
        }
        drawFromLiveWall(this.game.getTurn());
    }

}
