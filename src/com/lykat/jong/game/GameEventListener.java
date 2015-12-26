package com.lykat.jong.game;

import java.util.EventListener;

public interface GameEventListener extends EventListener {

    public abstract void handleEvent(GameEvent event);

}
