package com.lykat.jong.game;

import java.util.EventObject;

public class GameEvent extends EventObject {

    private static final long serialVersionUID = -5522214486399537195L;

    public enum GameEventType {
        DRAW_FROM_LIVE_WALL,
        DRAW_FROM_DEAD_WALL,
        DISCARD,
        DECLARE_RIICHI,
        DECLARE_BONUS_TILE,
        ABORT_KYUUSHU_KYUUHAI,
        DECLARE_TSUMO,
        DECLARE_KAN,
        CALL_PON,
        CALL_CHII,
        CALL_RON,
        CALL_KAN,
        SKIP_CALL,
        CALL_AVAILABLE,
        TURN_STARTED,
        TURN_FINISHED,
        ABORT_ALL_RIICHI,
        ABORT_4_KAN,
        ABORT_CHOMBO,
        EXHAUSTIVE_DRAW,
        ABORT_4_WINDS,
        ABORT_5_KAN,
        ABORT_RON,
        PLAYER_CONNECT,
        ROUND_STARTED,
        SENT_MESSAGE,
        OK,
        /* "Past-tense" game events */
        DREW_FROM_DEAD_WALL,
        DREW_FROM_LIVE_WALL,
        DISCARDED,
        DECLARED_RIICHI,
        CALLED_KAN,
        CALLED_CHII,
        CALLED_PON,
        DECLARED_BONUS_TILE,
        DECLARED_KAN,
        FLIPPED_DORA_HYOUJI;

        /**
         * Returns true if the event requires it to be the player's turn.
         */
        public boolean requiresControl() {
            switch (this) {
            case DECLARE_BONUS_TILE:
            case DECLARE_KAN:
            case ABORT_KYUUSHU_KYUUHAI:
            case DECLARE_RIICHI:
            case DECLARE_TSUMO:
            case DISCARD:
            case DRAW_FROM_DEAD_WALL:
            case DRAW_FROM_LIVE_WALL:
                return true;
            default:
                return false;
            }
        }

        /**
         * Returns true if this is a Chii, Kan, Pon, or Ron event
         */
        public boolean isCall() {
            switch (this) {
            case CALL_CHII:
            case CALL_KAN:
            case CALL_PON:
            case CALL_RON:
                return true;
            default:
                return false;
            }
        }

        public boolean isAbort() {
            switch (this) {
            default:
                return false;
            }
        }
    }

    private final GameEventType eventType;
    private final Object eventData;
    private final long timeStamp;

    /**
     * A game event.
     * 
     * @param source
     *            the {@link Player} that triggered the event. If this value is
     *            set to null, it will be passed with a value of -1 to the
     *            EventObject superclass, however the method
     *            <code>GameEvent.getSource()</code> will return null.
     *            <p>
     *            Passing null as the source may be necessary if the source of
     *            the GameEvent was, for example, the {@link GameManager}.
     * @param eventType
     *            the {@link GameEventType} of the event
     * @param eventData
     *            data associated with the event
     * @param timeStamp
     *            the UTC timestamp of when the event was fired
     */
    public GameEvent(Player source, GameEventType eventType, Object eventData,
            long timeStamp) {
        super(source == null ? -1 : source); // TODO: NullPlayer subclass?
        this.source = source;
        this.eventType = eventType;
        this.eventData = eventData;
        this.timeStamp = timeStamp;
    }

    @Override
    public Player getSource() {
        return (Player) this.source;
    }

    public GameEventType getEventType() {
        return this.eventType;
    }

    public Object getEventData() {
        return this.eventData;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

}
