package com.lykat.jong.event;

import java.util.EventListener;

public interface GameEventListener extends EventListener {

	public void handleEvent(GameEvent event);

}
