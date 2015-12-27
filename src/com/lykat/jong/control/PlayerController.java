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
                    if (state == GameState.MUST_DRAW_LIVE) {
                        super.fireEvent(GameEventType.DRAW_FROM_LIVE_WALL);
                    } else if (state == GameState.MUST_DRAW_DEAD) {
                        super.fireEvent(GameEventType.DRAW_FROM_DEAD_WALL);
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
            } else {
                LOGGER.log(Level.FINER,
                        "Received GameEvent: " + type.toString());
            }
        }

        super.notifyObservers(event);
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
            super.discard(-1);
            break;
        case Keys.NUM_1:
            super.fireEvent(GameEventType.OK);
            break;
        case Keys.J:
            super.fireEvent(GameEventType.DECLARE_KAN);
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
