package com.lykat.jong.game;

import java.util.EventObject;

public class GameEvent extends EventObject {

    public enum GameEventType {
        ABORT_4_KAN,
        ABORT_4_WINDS,
        ABORT_5_KAN,
        ABORT_ALL_RIICHI,
        ABORT_CHOMBO,
        ABORT_KYUUSHU_KYUUHAI,
        ABORT_RON,
        CALL_AVAILABLE,
        CALL_CHII,
        CALL_KAN,
        CALL_PON,
        CALL_RON,
        DECLARE_BONUS_TILE,
        DECLARE_KAN,
        DECLARE_RIICHI,
        DECLARE_TSUMO,
        DISCARD,
        DREW_FROM_DEAD_WALL,
        DREW_FROM_LIVE_WALL,
        EXHAUSTIVE_DRAW,
        FLIPPED_DORA_HYOUJI,
        OK,
        PLAYER_CONNECT,
        ROUND_STARTED,
        SENT_MESSAGE,
        SKIP_CALL,
        TSUMOKIRI,
        TURN_FINISHED,
        TURN_STARTED;

        public boolean isAbort() {
            switch (this) {
            case ABORT_4_KAN:
            case ABORT_4_WINDS:
            case ABORT_5_KAN:
            case ABORT_ALL_RIICHI:
            case ABORT_CHOMBO:
            case ABORT_KYUUSHU_KYUUHAI:
            case ABORT_RON:
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

        /**
         * Returns true if this is either a DISCARD or TSUMOKIRI event.
         */
        public boolean isDiscard() {
            return (this == DISCARD || this == TSUMOKIRI);
        }

        /**
         * Returns true if the event requires it to be the player's turn.
         */
        public boolean requiresControl() {
            switch (this) {
            case ABORT_KYUUSHU_KYUUHAI:
            case DECLARE_BONUS_TILE:
            case DECLARE_KAN:
            case DECLARE_TSUMO:
            case DECLARE_RIICHI:
            case DISCARD:
            case TSUMOKIRI:
                return true;
            default:
                return false;
            }
        }
    }

    private static final long serialVersionUID = -5522214486399537195L;

    private final Object eventData;
    private final GameEventType eventType;
    private final long timeStamp;
    private final String toString;

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
        super(source == null ? new Integer(-1) : source);
        // TODO: NullPlayer subclass instead of -1 ?
        this.source = source;
        this.eventType = eventType;
        this.eventData = eventData;
        this.timeStamp = timeStamp;
        this.toString = String.format("GameEvent {%n  Type: %s%n  Source: %s%n"
                + "  Event Data: %s%n  Timestamp: %d%n}",
                eventType == null ? "null" : eventType.toString(),
                source == null ? "null" : source.getName(),
                eventData == null ? "null" : eventData.toString(), timeStamp);
    }

    public Object getEventData() {
        return this.eventData;
    }

    public GameEventType getEventType() {
        return this.eventType;
    }

    @Override
    public Player getSource() {
        return (Player) this.source;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public String toString() {
        return this.toString;
    }

}
