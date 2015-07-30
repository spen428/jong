package com.lykat.jong.event;

import java.util.EventObject;

/**
 * A GameEvent is any event triggered by the game manager during a game.
 * 
 * @author lykat
 *
 */
public class GameEvent extends EventObject {

	public enum GameEventType {
		TURN_STARTED, TURN_ENDED, CALL_AVAILABLE;
	}

	private final Object source;
	private final GameEventType eventType;
	private final Object eventData;
	private final long timeStamp;

	/**
	 * A game-driven game event.
	 * 
	 * @param source
	 *            the source of the event (currently unused)
	 * @param eventType
	 *            the {@link GameEventType} of the event
	 * @param eventData
	 *            data associated with the event
	 * @param timeStamp
	 *            the UTC timestamp of when the event was fired
	 */
	public GameEvent(Object source, GameEventType eventType, Object eventData,
			long timeStamp) {
		super(source);
		this.source = source;
		this.eventType = eventType;
		this.eventData = eventData;
		this.timeStamp = timeStamp;
	}

	/**
	 * A game-driven game event.
	 * 
	 * @param eventType
	 *            the {@link GameEventType} of the event
	 * @param eventData
	 *            data associated with the event
	 * @param timeStamp
	 *            the UTC timestamp of when the event was fired
	 */
	public GameEvent(GameEventType eventType, Object eventData, long timeStamp) {
		this(null, eventType, eventData, timeStamp);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public Object getSource() {
		return source;
	}

	public GameEventType getEventType() {
		return eventType;
	}

	public Object getEventData() {
		return eventData;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

}
