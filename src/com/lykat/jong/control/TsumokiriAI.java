package com.lykat.jong.control;

import java.util.logging.Logger;

import com.lykat.jong.game.Call;
import com.lykat.jong.game.GameEvent;
import com.lykat.jong.game.GameEvent.GameEventType;
import com.lykat.jong.game.GameManager;
import com.lykat.jong.game.GameManager.GameState;

public class TsumokiriAI extends AbstractPlayerController {

    public static final Logger LOGGER = Logger.getLogger("AI");

    public TsumokiriAI(String name, GameManager gameManager) {
        super(name, gameManager);
    }

    @Override
    public void handleEvent(GameEvent event) {
        super.setChanged();

        /* Echoed event */
        if (event.getSource() == this.getPlayer()) {
            // Do nothing
        } else {
            GameEventType type = event.getEventType();
            Object data = event.getEventData();

            if (data instanceof GameState) {
                GameState state = (GameState) data;
                if (type == GameEventType.TURN_STARTED) {
                    if (state == GameState.WAITING) {
                        sleep();
                        super.tsumoKiri();
                    }
                } else if (type == GameEventType.DREW_FROM_LIVE_WALL) {
                    //
                } else if (type == GameEventType.TURN_FINISHED) {
                    //
                } else if (state == GameState.END_OF_ROUND) {
                    super.ok();
                } else if (state == GameState.GAME_OVER) {
                    //
                }
            } else if (data instanceof Call) {
                // Call call = (Call) data;
                super.skipCall();
            } else {
                if (type == GameEventType.ROUND_STARTED) {
                    super.ok();
                } else {
                    //
                }
            }
        }

        super.notifyObservers(event);
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
