package com.bomberman.model;

import com.bomberman.model.enums.GameState;
import com.bomberman.utils.Constants;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private Player player;
    private List<Player> players;
    private GameState gameState;
    private AnimationTimer gameLoop;
    private long lastUpdate;

    // Réduire la fréquence de mise à jour
    private static final long UPDATE_INTERVAL = Constants.GAME_SPEED * 1_000_000L; // Utilise la constante optimisée

    public Game() {
        initialize();
    }

    public Game(boolean isMultiplayer) {
        if (isMultiplayer) {
            initializeMultiplayer();
        } else {
            initialize();
        }
    }

    private void initializeMultiplayer() {
        board = new Board();
        players = new ArrayList<>();
        gameState = GameState.PLAYING;

        // Positions de départ des 4 joueurs
        players.add(new Player(1, 1));
        players.add(new Player(board.getWidth() - 2, 1));
        players.add(new Player(1, board.getHeight() - 2));
        players.add(new Player(board.getWidth() - 2, board.getHeight() - 2));

        // Place les joueurs sur le plateau
        for (Player p : players) {
            board.getCell(p.getX(), p.getY()).setHasPlayer(true);
        }

        startGameLoopMultiplayer();
    }

    private void initialize() {
        board = new Board();
        player = board.getPlayer(); // Utilise le joueur du Board
        gameState = GameState.PLAYING;
        board.getCell(player.getX(), player.getY()).setHasPlayer(true);
        startGameLoop();
    }

    // Limitation de la fréquence de mise à jour
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= UPDATE_INTERVAL) {
                    update();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void startGameLoopMultiplayer() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= UPDATE_INTERVAL) {
                    updateMultiplayer();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    public List<Player> getPlayers() {
        return players;
    }

    private void update() {
        if (gameState != GameState.PLAYING) return;
        board.update(player);
        checkGameState();
    }

    private void updateMultiplayer() {
        if (gameState != GameState.PLAYING) return;

        for (Player p : players) {
            if (p.isAlive()) {
                board.update(p);
            }
        }

        checkGameStateMultiplayer();
    }

    private void checkGameState() {
        if (!player.isAlive()) {
            gameState = GameState.GAME_OVER;
            gameLoop.stop();
            // (le reste : passage à l'écran Game Over)
        } else if (board.getBots().stream().noneMatch(PlayerBot::isAlive)) {
            gameState = GameState.VICTORY;
            gameLoop.stop();
            player.addScore(1000); // +1000 points pour la victoire
            com.bomberman.controller.VictoryController.LAST_SCORE = player.getScore();
            com.bomberman.utils.SceneManager.switchScene("Victory");
        }
    }

    private void checkGameStateMultiplayer() {
        boolean anyAlive = players.stream().anyMatch(Player::isAlive);

        if (!anyAlive) {
            gameState = GameState.GAME_OVER;
            gameLoop.stop();
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