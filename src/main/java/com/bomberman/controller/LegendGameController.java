package com.bomberman.controller;

import com.bomberman.model.Legend1v1Board;
import com.bomberman.model.Music;
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
 * Contrôleur du mode LEGEND 1v1 (2 joueurs sur le même PC)
 * Gère le plateau, le rendu, la musique et les entrées clavier.
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

    private final Music music = new Music(); // Gestion de la musique et des sons

    @FXML
    private void initialize() {
        board = new Legend1v1Board();
        renderer = new GameRenderer(gameCanvas);

        setupKeyboardHandling();
        music.demarrerLegendMusic(); // Lance la musique LEGEND au démarrage
        startRenderLoop();
    }

    // Gère le focus et la récupération du clavier pour les deux joueurs
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
        gameCanvas.setOnMouseClicked(e -> gameContainer.requestFocus());
    }

    // Mapping des touches : J1 = ZQSD+R, J2 = IJKL+P
    private void handleKeyPressed(KeyEvent event) {
        String keyCode = event.getCode().toString();
        if (pressedKeys.contains(keyCode)) return; // Ignore la répétition de touche
        pressedKeys.add(keyCode);

        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        switch (keyCode) {
            // Joueur 1 (ZQSDR)
            case "Z": p1.moveUp(board);    break;
            case "S": p1.moveDown(board);  break;
            case "Q": p1.moveLeft(board);  break;
            case "D": p1.moveRight(board); break;
            case "R": p1.placeBomb(board); break;
            // Joueur 2 (IJKLP)
            case "I": p2.moveUp(board);    break;
            case "K": p2.moveDown(board);  break;
            case "J": p2.moveLeft(board);  break;
            case "L": p2.moveRight(board); break;
            case "P": p2.placeBomb(board); break;
        }
        event.consume();
    }

    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode().toString());
    }

    // Boucle principale : met à jour le jeu et rend la scène
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

    // Vérifie si la partie est terminée (game over ou victoire)
    private void checkGameState() {
        // Game Over : les deux joueurs sont morts
        if (!board.getPlayer1().isAlive() && !board.getPlayer2().isAlive()) {
            gameOver = true;
            music.arreterLegendMusic(); // Arrête la musique LEGEND
            com.bomberman.controller.GameOverController.setLastScore(
                    Math.max(board.getPlayer1().getScore(), board.getPlayer2().getScore()));
            SceneManager.switchScene("GameOver");
            stopRenderLoop();
        }

        // Victoire (décommente si tu veux activer la victoire quand tous les ennemis sont morts)
        /*
        if (board.getBomberEnemies().isEmpty() && board.getYellowEnemies().isEmpty()) {
            victory = true;
            music.arreterLegendMusic();
            com.bomberman.controller.VictoryController.LAST_SCORE =
                Math.max(board.getPlayer1().getScore(), board.getPlayer2().getScore());
            SceneManager.switchScene("Victory");
            stopRenderLoop();
        }
        */
    }

    // Arrête la boucle de rendu
    private void stopRenderLoop() {
        if (renderLoop != null) renderLoop.stop();
    }
}
