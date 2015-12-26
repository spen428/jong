package com.lykat.jong.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.Input.Keys;
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
                    if (state == GameState.MUST_DRAW_LIVE) {
                        super.fireEvent(GameEventType.DRAW_FROM_LIVE_WALL);
                    } else if (state == GameState.MUST_DRAW_DEAD) {
                        super.fireEvent(GameEventType.DRAW_FROM_DEAD_WALL);
                    }
                }
            }
            super.tsumoKiri();
        }

        super.notifyObservers(event);
    }

    @Override
    public boolean keyDown(int keycode) {
        LOGGER.log(Level.FINEST, "keyDown event: " + keycode);
        switch (keycode) {
        case Keys.NUM_1:
            LOGGER.log(Level.FINER, "Firing event: "
                    + GameEventType.PLAYER_CONNECT.toString());
            super.fireEvent(GameEventType.PLAYER_CONNECT, this);
            break;
        case Keys.F:
            super.discard(-1);
            break;
        default:
            break;
        }
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
