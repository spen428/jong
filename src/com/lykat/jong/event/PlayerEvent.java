package com.lykat.jong.event;

import java.util.EventObject;

import com.lykat.jong.game.Player;

/**
 * A PlayerEvent is any event triggered by player action during a game.
 * 
 * @author lykat
 *
 */
public class PlayerEvent extends EventObject {

	public enum PlayerEventType {
		DRAW_FROM_LIVE_WALL, DRAW_FROM_DEAD_WALL, DISCARD_FROM_HAND, DISCARD_TSUMOHAI, DECLARE_RIICHI, DECLARE_BONUS_TILE, DECLARE_REDEAL, DECLARE_TSUMO, DECLARE_KAN, CALL_PON, CALL_CHII, CALL_RON, CALL_KAN, SKIP_CALL;

		/**
		 * Returns true if the event requires it to be the player's turn.
		 */
		public boolean requiresControl() {
			switch (this) {
			case DECLARE_BONUS_TILE:
			case DECLARE_KAN:
			case DECLARE_REDEAL:
			case DECLARE_RIICHI:
			case DECLARE_TSUMO:
			case DISCARD_FROM_HAND:
			case DISCARD_TSUMOHAI:
			case DRAW_FROM_DEAD_WALL:
			case DRAW_FROM_LIVE_WALL:
				return true;
			default:
				return false;
			}
		}
	}

	private final Player source;
	private final PlayerEventType eventType;
	private final Object eventData;
	private final long timeStamp;

	/**
	 * A player-triggered game event.
	 * 
	 * @param source
	 *            the {@link Player} that triggered the event
	 * @param eventType
	 *            the {@link PlayerEventType} of the event
	 * @param eventData
	 *            data associated with the event
	 * @param timeStamp
	 *            the UTC timestamp of when the event was fired
	 */
	public PlayerEvent(Player source, PlayerEventType eventType,
			Object eventData, long timeStamp) {
		super(source);
		this.source = source;
		this.eventType = eventType;
		this.eventData = eventData;
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public Player getSource() {
		return source;
	}

	public PlayerEventType getEventType() {
		return eventType;
	}

	public Object getEventData() {
		return eventData;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

}
