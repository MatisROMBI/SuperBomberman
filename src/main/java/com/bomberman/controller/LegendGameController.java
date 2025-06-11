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
 * Gère la logique du plateau, les entrées, le rendu et la musique.
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
    private final Music music = new Music(); // Musique et sons

    @FXML
    private void initialize() {
        board = new Legend1v1Board();
        renderer = new GameRenderer(gameCanvas);

        setupKeyboardHandling();
        music.demarrerLegendMusic(); // Démarre la musique LEGEND
        startRenderLoop();
    }

    // Prend le focus et configure les touches pour deux joueurs
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

    // Contrôle clavier : J1 = ZQSD+R, J2 = IJKL+P
    private void handleKeyPressed(KeyEvent event) {
        String keyCode = event.getCode().toString();
        if (pressedKeys.contains(keyCode)) return; // Ignore répétition
        pressedKeys.add(keyCode);

        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        switch (keyCode) {
            // Joueur 1
            case "Z": p1.moveUp(board);    break;
            case "S": p1.moveDown(board);  break;
            case "Q": p1.moveLeft(board);  break;
            case "D": p1.moveRight(board); break;
            case "R": p1.placeBomb(board); break;
            // Joueur 2
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

    // Boucle principale du jeu (update et rendu)
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

    // Détecte fin de partie et affiche le vainqueur
    private void checkGameState() {
        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        // Cas : les deux joueurs sont morts en même temps (égalité)
        if (!p1.isAlive() && !p2.isAlive()) {
            gameOver = true;
            music.arreterLegendMusic();
            com.bomberman.controller.GameOverController.setLastScore(
                    Math.max(p1.getScore(), p2.getScore()));
            SceneManager.switchScene("GameOver");
            stopRenderLoop();
            return;
        }

        // Victoire Joueur 1
        if (!p2.isAlive() && p1.isAlive()) {
            victory = true;
            music.arreterLegendMusic();
            com.bomberman.controller.VictoryController.LAST_SCORE = p1.getScore();
            com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 1";
            SceneManager.switchScene("Victory");
            stopRenderLoop();
            return;
        }

        // Victoire Joueur 2
        if (!p1.isAlive() && p2.isAlive()) {
            victory = true;
            music.arreterLegendMusic();
            com.bomberman.controller.VictoryController.LAST_SCORE = p2.getScore();
            com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 2";
            SceneManager.switchScene("Victory");
            stopRenderLoop();
            return;
        }

        // Tu peux rajouter la détection de victoire “tous les ennemis tués” ici si besoin.
    }

    // Stoppe le rendu
    private void stopRenderLoop() {
        if (renderLoop != null) renderLoop.stop();
    }
}
