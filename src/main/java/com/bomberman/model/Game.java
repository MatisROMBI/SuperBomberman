/**
 * Gestionnaire principal de partie pour le mode classique
 * Boucle de jeu avec AnimationTimer et gestion des états
 */
package com.bomberman.model;

import com.bomberman.model.enums.GameState;
import com.bomberman.utils.Constants;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;                 // Plateau de jeu
    private Player player;               // Joueur principal
    private List<Player> players;        // Liste des joueurs (mode multijoueur)
    private GameState gameState;         // État actuel du jeu
    private AnimationTimer gameLoop;     // Boucle de jeu principale
    private long lastUpdate;             // Dernière mise à jour

    // OPTIMISATION: Réduction de la fréquence de mise à jour
    private static final long UPDATE_INTERVAL = Constants.GAME_SPEED * 1_000_000L;

    /**
     * Constructeur par défaut (mode solo)
     */
    public Game() {
        initialize();
    }

    /**
     * Constructeur avec choix du mode
     */
    public Game(boolean isMultiplayer) {
        if (isMultiplayer) {
            initializeMultiplayer();
        } else {
            initialize();
        }
    }

    /**
     * Initialisation du mode multijoueur (4 joueurs)
     */
    private void initializeMultiplayer() {
        board = new Board();
        players = new ArrayList<>();
        gameState = GameState.PLAYING;

        // Positions de départ des 4 joueurs
        players.add(new Player(1, 1));
        players.add(new Player(board.getWidth() - 2, 1));
        players.add(new Player(1, board.getHeight() - 2));
        players.add(new Player(board.getWidth() - 2, board.getHeight() - 2));

        // Placement sur le plateau
        for (Player p : players) {
            board.getCell(p.getX(), p.getY()).setHasPlayer(true);
        }

        startGameLoopMultiplayer();
    }

    /**
     * Initialisation du mode solo (1 joueur vs 3 bots)
     */
    private void initialize() {
        board = new Board();
        player = board.getPlayer(); // Utilise le joueur du plateau
        gameState = GameState.PLAYING;
        board.getCell(player.getX(), player.getY()).setHasPlayer(true);
        startGameLoop();
    }

    /**
     * OPTIMISATION: Boucle de jeu avec limitation de fréquence
     */
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

    /**
     * Boucle de jeu multijoueur
     */
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

    /**
     * Mise à jour du mode solo
     */
    private void update() {
        if (gameState != GameState.PLAYING) return;
        board.update(player);
        checkGameState();
    }

    /**
     * Mise à jour du mode multijoueur
     */
    private void updateMultiplayer() {
        if (gameState != GameState.PLAYING) return;

        for (Player p : players) {
            if (p.isAlive()) {
                board.update(p);
            }
        }

        checkGameStateMultiplayer();
    }

    /**
     * Vérification des conditions de fin de partie (mode solo)
     */
    private void checkGameState() {
        if (!player.isAlive()) {
            gameState = GameState.GAME_OVER;
            gameLoop.stop();
        } else if (board.getBots().stream().noneMatch(PlayerBot::isAlive)) {
            // Victoire : tous les bots sont morts
            gameState = GameState.VICTORY;
            gameLoop.stop();
            player.addScore(1000); // Bonus de victoire
            com.bomberman.controller.VictoryController.LAST_SCORE = player.getScore();
            com.bomberman.utils.SceneManager.switchScene("Victory");
        }
    }

    /**
     * Vérification des conditions de fin de partie (mode multijoueur)
     */
    private void checkGameStateMultiplayer() {
        boolean anyAlive = players.stream().anyMatch(Player::isAlive);

        if (!anyAlive) {
            gameState = GameState.GAME_OVER;
            gameLoop.stop();
        }
    }

    /**
     * Basculement pause/jeu
     */
    public void pause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
        }
    }

    /**
     * Arrêt du jeu
     */
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    // Accesseurs
    public List<Player> getPlayers() { return players; }
    public Board getBoard() { return board; }
    public Player getPlayer() { return player; }
    public GameState getGameState() { return gameState; }
}
