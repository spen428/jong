package com.lykat.jong.control;

import com.lykat.jong.game.Call;
import com.lykat.jong.game.GameEvent;
import com.lykat.jong.game.GameEvent.GameEventType;
import com.lykat.jong.game.GameManager;
import com.lykat.jong.game.GameManager.GameState;
import com.lykat.jong.game.Player;

public class PlayerController extends AbstractPlayerController {

	public PlayerController(Player player, GameManager gameManager) {
		super(player, gameManager);
	}

	@Override
	public void handleEvent(GameEvent event) {
		GameEventType type = event.getEventType();
		Object data = event.getEventData();

		if (data instanceof GameState) {
			GameState state = (GameState) data;
			if (type == GameEventType.TURN_STARTED) {
				if (state == GameState.MUST_DRAW_LIVE) {

				} else if (state == GameState.MUST_DRAW_DEAD) {

				} else if (state == GameState.MUST_DISCARD) {

				} else if (state == GameState.WAITING) {

				}
			}
		} else if (data instanceof Player) {
			/* Notification of player action */
			Player player = (Player) data;
			System.out.printf("%d: Player %s did %s%n", event.getTimeStamp(),
					player.getName(), type.toString());
		} else if (data instanceof Call) {
			/* Available meld call */
			Call call = (Call) data;
			// TODO: Handle call event
		}
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
}
