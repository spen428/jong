package com.lykat.jong.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.Input.Keys;
import com.lykat.jong.game.Call;
import com.lykat.jong.game.GameEvent;
import com.lykat.jong.game.GameEvent.GameEventType;
import com.lykat.jong.game.GameManager;
import com.lykat.jong.game.GameManager.GameState;
import com.lykat.jong.game.Player;

public class PlayerController extends AbstractPlayerController {

    public static final Logger LOGGER = Logger.getLogger("PlayerController");

    public PlayerController(String name, GameManager gameManager) {
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
                    if (state == GameState.MUST_DISCARD) {
                        LOGGER.log(Level.INFO, "You must now discard a tile.");
                    } else if (state == GameState.WAITING) {
                        LOGGER.log(Level.INFO, "It is now your turn.");
                    }
                } else if (type == GameEventType.TURN_FINISHED) {
                    LOGGER.log(Level.INFO, "You turn is over.");
                } else if (state == GameState.END_OF_ROUND) {
                    LOGGER.log(Level.INFO, "End of round. Press OK.");
                } else if (state == GameState.GAME_OVER) {
                    LOGGER.log(Level.INFO, "Game over.");
                }
            } else if (data instanceof Player) {
                /* Notification of player action */
                Player player = (Player) data;
                LOGGER.log(
                        Level.FINE,
                        String.format("%d: Player %s did %s%n",
                                event.getTimeStamp(), player.getName(),
                                type.toString()));
            } else if (data instanceof Call) {
                /* Available meld call */
                Call call = (Call) data;
                LOGGER.info("Call available: " + call.toString());
            } else {
                LOGGER.log(Level.FINER,
                        "Received GameEvent: " + type.toString());
                if (data != null) {
                    LOGGER.log(Level.FINER, "Data: " + data.toString());
                }
            }
        }

        super.notifyObservers(event);
    }

    @Override
    public boolean keyDown(int keycode) {
        LOGGER.log(Level.FINEST, "keyDown event: " + keycode);
        switch (keycode) {
        case Keys.F:
            super.discard(-1);
            break;
        case Keys.D:
            super.skipCall();
            break;
        case Keys.S:
            super.fireEvent(GameEventType.CALL_PON);
            break;
        case Keys.O:
            super.fireEvent(GameEventType.OK);
            break;
        case Keys.NUM_1:
            super.discard(0);
            break;
        case Keys.NUM_2:
            super.discard(1);
            break;
        case Keys.NUM_3:
            super.discard(2);
            break;
        case Keys.NUM_4:
            super.discard(3);
            break;
        case Keys.NUM_5:
            super.discard(4);
            break;
        case Keys.NUM_6:
            super.discard(5);
            break;
        case Keys.NUM_7:
            super.discard(6);
            break;
        case Keys.NUM_8:
            super.discard(7);
            break;
        case Keys.NUM_9:
            super.discard(8);
            break;
        case Keys.NUM_0:
            super.discard(9);
            break;
        case Keys.MINUS:
            super.discard(10);
            break;
        case Keys.EQUALS:
            super.discard(11);
            break;
        case Keys.BACKSPACE:
            super.discard(12);
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
