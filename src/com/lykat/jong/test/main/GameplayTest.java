package com.lykat.jong.test.main;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lykat.jong.control.PlayerController;
import com.lykat.jong.control.TsumokiriAI;
import com.lykat.jong.game.Game;
import com.lykat.jong.game.GameManager;
import com.lykat.jong.game.RuleSet;
import com.lykat.jong.main.GameScene;

/**
 * A playable test scene.
 * 
 * @author lykat
 *
 */
public class GameplayTest extends GameScene {

    private final boolean player = true;
    private final GameManager gameManager;
    private final PlayerController playerController;

    public GameplayTest() {
        RuleSet ruleSet = new RuleSet(RuleSet.GameType.RIICHI_FOUR_PLAYER, 2);
        this.game = new Game("GameplayTest Game", ruleSet);
        this.gameManager = new GameManager(this.game);
        this.playerController = new PlayerController("Dave", this.gameManager);
        if (this.player) {
            this.playerController.addObserver(this);
            this.playerController.connect();
        }

        for (int i = this.player ? 1 : 0; i < 4; i++) {
            TsumokiriAI ai = new TsumokiriAI("AI " + i, this.gameManager);
            if (i == 0) {
                ai.addObserver(this);
            }
            ai.connect();
        }

        super.setGame(this.game);
    }

    @Override
    public void create() {
        super.create();
        Gdx.input.setInputProcessor(this.playerController);
    }

    @Override
    public void render() {
        super.render();
    }

    public static void main(String[] args) {
        initLoggers();

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Jong Gameplay Test";
        config.width = 1280;
        config.height = 720;
        config.samples = 8;
        config.useGL30 = true;
        config.vSyncEnabled = true;
        config.fullscreen = false;
        new LwjglApplication(new GameplayTest(), config);
    }

    private static void initLoggers() {
        Logger logger = PlayerController.LOGGER;
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
        logger.log(Level.INFO, "PlayerController logger initialised.");

        logger = GameManager.LOGGER;
        handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setLevel(Level.OFF);
        logger.log(Level.INFO, "GameManager logger initialised.");

        logger = GameScene.LOGGER;
        handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setLevel(Level.OFF);
        logger.log(Level.INFO, "Graphics logger initialised.");
    }

}