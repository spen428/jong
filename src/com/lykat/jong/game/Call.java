package com.lykat.jong.game;

import java.util.EnumSet;

import com.lykat.jong.game.GameEvent.GameEventType;
import com.lykat.jong.game.Meld.MeldType;

public class Call {

    public enum CallType {
        RON, PON, CHII, KAN;
    }

    private final Player player;
    private final GameEventType callEvent;
    private final Meld meld;
    private final String toString;

    public Call(Player player, GameEventType callEvent, Meld meld) {
        this.player = player;
        this.callEvent = callEvent;
        this.meld = meld;

        if (!EnumSet.of(GameEventType.CALL_RON, GameEventType.CALL_KAN,
                GameEventType.CALL_PON, GameEventType.CALL_CHII).contains(
                callEvent)) {
            throw new IllegalArgumentException("GameEventType "
                    + callEvent.toString() + " is not a valid call event.");
        }

        if (meld == null && callEvent != GameEventType.CALL_RON) {
            throw new IllegalArgumentException(
                    "Cannot have a null meld unless the call event is Ron.");
        }

        if (meld == null) {
            this.toString = String.format("Call : %s", callEvent.toString());
        } else {
            this.toString = String.format("Call : %s | %s",
                    callEvent.toString(), meld.toString());
        }
    }

    public Call(Player player, GameEventType callType) {
        this(player, callType, null);
    }

    public Call(Player player, Meld meld) {
        this(player, Call.meldTypeToCallEvent(meld.getType()), meld);
    }

    private static GameEventType meldTypeToCallEvent(MeldType type) {
        switch (type) {
        case KANTSU_OPEN:
            return GameEventType.CALL_KAN;
        case KOUTSU_OPEN:
            return GameEventType.CALL_PON;
        case SHUNTSU_OPEN:
            return GameEventType.CALL_CHII;
        default:
            return null;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public GameEventType getCallEvent() {
        return callEvent;
    }

    public Meld getMeld() {
        return meld;
    }

    @Override
    public String toString() {
        return toString;
    }
}