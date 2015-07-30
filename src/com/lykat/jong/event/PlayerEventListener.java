package com.lykat.jong.event;

import java.util.EventListener;

public interface PlayerEventListener extends EventListener {

	public void handleEvent(PlayerEvent event);

}
