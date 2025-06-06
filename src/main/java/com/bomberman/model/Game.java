package com.bomberman.model;

import com.bomberman.model.enums.GameState;
import com.bomberman.utils.Constants;
import javafx.animation.AnimationTimer;

public class Game {
    private Board board;
    private Player player;
    private GameState gameState;
    private AnimationTimer gameLoop;
    private long lastUpdate;

    public Game() {
        initialize();
    }

    private void initialize() {
        board = new Board();
        player = board.getPlayer(); // Utilise le joueur du Board
        gameState = GameState.PLAYING;
        board.getCell(player.getX(), player.getY()).setHasPlayer(true);
        startGameLoop();
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= Constants.GAME_SPEED * 1_000_000) {
                    update();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void update() {
        if (gameState != GameState.PLAYING) return;
        board.update(player);
        checkGameState();
    }

    private void checkGameState() {
        if (!player.isAlive()) {
            gameState = GameState.GAME_OVER;
            gameLoop.stop();
            // (le reste : passage à l'écran Game Over)
        } else if (board.getBots().stream().noneMatch(PlayerBot::isAlive)) {
            gameState = GameState.VICTORY;
            gameLoop.stop();
            // AJOUTE CETTE LIGNE :
            player.addScore(1000); // +1000 points pour la victoire
            com.bomberman.controller.VictoryController.LAST_SCORE = player.getScore();
            com.bomberman.utils.SceneManager.switchScene("Victory");
        }
    }

    public void pause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
        }
    }

    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public Board getBoard() { return board; }
    public Player getPlayer() { return player; }
    public GameState getGameState() { return gameState; }
}