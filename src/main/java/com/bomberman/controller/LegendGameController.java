package com.bomberman.controller;

import com.bomberman.model.Legend1v1Board;
import com.bomberman.model.Player;
import com.bomberman.utils.SceneManager;
import com.bomberman.view.GameRenderer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;

/**
 * Contrôleur du mode LEGEND 1v1 (2 humains sur le même PC)
 */
public class LegendGameController {
    @FXML private VBox gameContainer;
    @FXML private Canvas gameCanvas;

    private Legend1v1Board board;
    private GameRenderer renderer;
    private final Set<String> pressedKeys = new HashSet<>();
    private AnimationTimer renderLoop;

    private boolean gameOver = false;
    private boolean victory = false;

    @FXML
    private void initialize() {
        // Initialisation du plateau et du renderer
        board = new Legend1v1Board();
        renderer = new GameRenderer(gameCanvas);

        setupKeyboardHandling();
        startRenderLoop();
    }

    /**
     * Gère le focus et les handlers clavier pour jouer à deux sur le même PC
     */
    private void setupKeyboardHandling() {
        gameContainer.setFocusTraversable(true);

        Platform.runLater(() -> {
            gameContainer.requestFocus();

            if (gameContainer.getScene() != null) {
                gameContainer.getScene().setOnKeyPressed(this::handleKeyPressed);
                gameContainer.getScene().setOnKeyReleased(this::handleKeyReleased);
            }
            gameContainer.setOnKeyPressed(this::handleKeyPressed);
            gameContainer.setOnKeyReleased(this::handleKeyReleased);
        });

        // Clique sur le canvas = focus pour la partie
        gameCanvas.setOnMouseClicked(e -> gameContainer.requestFocus());
    }

    /**
     * Mapping des touches :
     *  - J1 : ZQSD pour bouger, M pour bombe
     *  - J2 : IJKL pour bouger, P pour bombe
     */
    private void handleKeyPressed(KeyEvent event) {
        String keyCode = event.getCode().toString();

        // Ignore la répétition de touche
        if (pressedKeys.contains(keyCode)) return;
        pressedKeys.add(keyCode);

        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        switch (keyCode) {
            // JOUEUR 1 (AZERTY & QWERTY friendly)
            case "Z": p1.moveUp(board); break;
            case "S": p1.moveDown(board); break;
            case "Q": p1.moveLeft(board); break;
            case "D": p1.moveRight(board); break;
            case "R": p1.placeBomb(board); break;
            // JOUEUR 2
            case "I": p2.moveUp(board); break;
            case "K": p2.moveDown(board); break;
            case "J": p2.moveLeft(board); break;
            case "L": p2.moveRight(board); break;
            case "P": p2.placeBomb(board); break;
        }
        event.consume();
    }

    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode().toString());
    }

    /**
     * Boucle d'affichage/jeu : update ennemis, joueurs, explosions, etc
     */
    private void startRenderLoop() {
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver && !victory) {
                    board.update();
                    renderer.renderLegend1v1(board);
                    checkGameState();
                }
            }
        };
        renderLoop.start();
    }

    /**
     * Vérifie si la partie est terminée (game over ou victoire)
     */
    private void checkGameState() {
        // GAME OVER : les deux joueurs sont morts
        if (!board.getPlayer1().isAlive() && !board.getPlayer2().isAlive()) {
            gameOver = true;
            com.bomberman.controller.GameOverController.setLastScore(
                    Math.max(board.getPlayer1().getScore(), board.getPlayer2().getScore()));
            SceneManager.switchScene("GameOver");
            stopRenderLoop();
        }

        // VIC-TOIRE (à activer si tu veux : par exemple si tous les ennemis morts)
        /*
        if (board.getBomberEnemies().isEmpty() && board.getYellowEnemies().isEmpty()) {
            victory = true;
            com.bomberman.controller.VictoryController.LAST_SCORE =
                Math.max(board.getPlayer1().getScore(), board.getPlayer2().getScore());
            SceneManager.switchScene("Victory");
            stopRenderLoop();
        }
        */
    }

    /**
     * Arrête la boucle de rendu quand la partie est finie
     */
    private void stopRenderLoop() {
        if (renderLoop != null) renderLoop.stop();
    }
}
