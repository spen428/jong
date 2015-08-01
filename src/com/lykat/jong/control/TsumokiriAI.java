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
	
	public void connect() {
		super.fireEvent(GameEventType.PLAYER_CONNECTED, this);
	}

	@Override
	public void handleEvent(GameEvent event) {
		super.hasChanged();
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
				super.tsumoKiri();
			}
		}

		super.notifyObservers();
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
		case Keys.NUM_1:
			LOGGER.log(Level.FINER, "Firing event: "
					+ GameEventType.PLAYER_CONNECTED.toString());
			super.fireEvent(GameEventType.PLAYER_CONNECTED, this);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
