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
        setChanged();

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
                        fireEvent(GameEventType.DRAW_FROM_LIVE_WALL);
                    } else if (state == GameState.MUST_DRAW_DEAD) {
                        fireEvent(GameEventType.DRAW_FROM_DEAD_WALL);
                    } else if (state == GameState.MUST_DISCARD) {
                        LOGGER.log(Level.INFO, "You must now discard a tile.");
                    } else if (state == GameState.WAITING) {
                        LOGGER.log(Level.INFO, "It is now your turn.");
                    }
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
                // TODO: Handle call event
                LOGGER.info("Call available: " + call.toString());
            } else {
                LOGGER.log(Level.FINER,
                        "Received GameEvent: " + type.toString());
            }
        }

        notifyObservers(event);
    }

    private void handleConfirmation(GameEvent event) {
        GameEventType type = event.getEventType();
        if (type == GameEventType.ABORT_4_KAN) {
        } else if (type == GameEventType.ABORT_ALL_RIICHI) {
        } else if (type == GameEventType.ABORT_5_KAN) {
        } else if (type == GameEventType.ABORT_CHOMBO) {
        } else if (type == GameEventType.ABORT_KYUUSHU_KYUUHAI) {
        } else if (type == GameEventType.CALL_AVAILABLE) {
        } else if (type == GameEventType.CALL_CHII) {
        } else if (type == GameEventType.CALL_KAN) {
        } else if (type == GameEventType.CALL_PON) {
        } else if (type == GameEventType.CALL_RON) {
        } else if (type == GameEventType.DECLARE_BONUS_TILE) {
        } else if (type == GameEventType.DECLARE_KAN) {
        } else if (type == GameEventType.DECLARE_RIICHI) {
        } else if (type == GameEventType.DECLARE_TSUMO) {
        } else if (type == GameEventType.DISCARD) {
        } else if (type == GameEventType.DRAW_FROM_DEAD_WALL) {
        } else if (type == GameEventType.DRAW_FROM_LIVE_WALL) {
        } else if (type == GameEventType.EXHAUSTIVE_DRAW) {
        } else if (type == GameEventType.SKIP_CALL) {
        } else if (type == GameEventType.TURN_FINISHED) {
        } else if (type == GameEventType.TURN_STARTED) {
        } else {
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        LOGGER.log(Level.FINEST, "keyDown event: " + keycode);
        switch (keycode) {
        case Keys.F:
            discard(-1);
            break;
        case Keys.D:
            skipCall();
            break;
        case Keys.S:
            fireEvent(GameEventType.CALL_PON);
            break;
        case Keys.O:
            fireEvent(GameEventType.OK);
            break;
        case Keys.NUM_1:
            discard(0);
            break;
        case Keys.NUM_2:
            discard(1);
            break;
        case Keys.NUM_3:
            discard(2);
            break;
        case Keys.NUM_4:
            discard(3);
            break;
        case Keys.NUM_5:
            discard(4);
            break;
        case Keys.NUM_6:
            discard(5);
            break;
        case Keys.NUM_7:
            discard(6);
            break;
        case Keys.NUM_8:
            discard(7);
            break;
        case Keys.NUM_9:
            discard(8);
            break;
        case Keys.NUM_0:
            discard(9);
            break;
        case Keys.MINUS:
            discard(10);
            break;
        case Keys.EQUALS:
            discard(11);
            break;
        case Keys.BACKSPACE:
            discard(12);
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
