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
 * Contrôleur du mode LEGEND 1v1 - CORRIGÉ
 * Mode 2 joueurs humains sur le même PC
 * Joueur 1 : ZQSD + R (haut-gauche, sprite blanc)
 * Joueur 2 : IJKL + P (bas-droite, sprite noir)
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
    private final Music music = new Music();

    @FXML
    private void initialize() {
        System.out.println("=== INITIALISATION MODE LEGEND 1V1 ===");

        // Créer le plateau Legend avec 2 joueurs humains
        board = new Legend1v1Board();
        renderer = new GameRenderer(gameCanvas);

        setupKeyboardHandling();
        music.demarrerLegendMusic();
        startRenderLoop();

        // Vérification debug
        System.out.println("Joueur 1 position: (" + board.getPlayer1().getX() + "," + board.getPlayer1().getY() + ")");
        System.out.println("Joueur 2 position: (" + board.getPlayer2().getX() + "," + board.getPlayer2().getY() + ")");
        System.out.println("Nombre d'ennemis Bomber: " + board.getBomberEnemies().size());
        System.out.println("Nombre d'ennemis Yellow: " + board.getYellowEnemies().size());

        System.out.println("CONTRÔLES:");
        System.out.println("- Joueur 1 (Blanc): ZQSD + R");
        System.out.println("- Joueur 2 (Noir): IJKL + P");
        System.out.println("- ÉCHAP: Retour menu");
    }

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

    private void handleKeyPressed(KeyEvent event) {
        String keyCode = event.getCode().toString();
        if (pressedKeys.contains(keyCode)) return;
        pressedKeys.add(keyCode);

        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        // Debug des touches pressées
        System.out.println("Touche pressée: " + keyCode);

        switch (keyCode) {
            // ===== JOUEUR 1 (ZQSD + R) =====
            case "Z":
                System.out.println("J1 monte");
                p1.moveUp(board);
                break;
            case "S":
                System.out.println("J1 descend");
                p1.moveDown(board);
                break;
            case "Q":
                System.out.println("J1 gauche");
                p1.moveLeft(board);
                break;
            case "D":
                System.out.println("J1 droite");
                p1.moveRight(board);
                break;
            case "R":
                System.out.println("J1 pose bombe");
                p1.placeBomb(board);
                break;

            // ===== JOUEUR 2 (IJKL + P) =====
            case "I":
                System.out.println("J2 monte");
                p2.moveUp(board);
                break;
            case "K":
                System.out.println("J2 descend");
                p2.moveDown(board);
                break;
            case "J":
                System.out.println("J2 gauche");
                p2.moveLeft(board);
                break;
            case "L":
                System.out.println("J2 droite");
                p2.moveRight(board);
                break;
            case "P":
                System.out.println("J2 pose bombe");
                p2.placeBomb(board);
                break;

            // ===== TOUCHES COMMUNES =====
            case "ESCAPE":
                System.out.println("Retour au menu");
                music.arreterLegendMusic();
                SceneManager.switchScene("MainMenu");
                break;

            default:
                System.out.println("Touche non reconnue: " + keyCode);
                break;
        }
        event.consume();
    }

    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode().toString());
    }

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

    private void checkGameState() {
        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        // Égalité (les deux morts)
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
            p1.addScore(1000);
            com.bomberman.controller.VictoryController.LAST_SCORE = p1.getScore();
            com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 1 (Blanc)";
            SceneManager.switchScene("Victory");
            stopRenderLoop();
            return;
        }

        // Victoire Joueur 2
        if (!p1.isAlive() && p2.isAlive()) {
            victory = true;
            music.arreterLegendMusic();
            p2.addScore(1000);
            com.bomberman.controller.VictoryController.LAST_SCORE = p2.getScore();
            com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 2 (Noir)";
            SceneManager.switchScene("Victory");
            stopRenderLoop();
            return;
        }

        // Victoire coopérative (tous les ennemis éliminés)
        boolean allEnemiesDead = board.getBomberEnemies().stream().noneMatch(b -> b.isAlive()) &&
                board.getYellowEnemies().stream().noneMatch(y -> y.isAlive());

        if (allEnemiesDead && (p1.isAlive() || p2.isAlive())) {
            victory = true;
            music.arreterLegendMusic();

            if (p1.getScore() > p2.getScore()) {
                p1.addScore(500);
                com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 1 (Blanc) - Coopération";
                com.bomberman.controller.VictoryController.LAST_SCORE = p1.getScore();
            } else if (p2.getScore() > p1.getScore()) {
                p2.addScore(500);
                com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 2 (Noir) - Coopération";
                com.bomberman.controller.VictoryController.LAST_SCORE = p2.getScore();
            } else {
                int sharedScore = Math.max(p1.getScore(), p2.getScore()) + 250;
                com.bomberman.controller.VictoryController.WINNER_NAME = "Égalité - Coopération parfaite";
                com.bomberman.controller.VictoryController.LAST_SCORE = sharedScore;
            }

            SceneManager.switchScene("Victory");
            stopRenderLoop();
        }
    }

    private void stopRenderLoop() {
        if (renderLoop != null) {
            renderLoop.stop();
            renderLoop = null;
        }
    }

    public void cleanup() {
        stopRenderLoop();
        music.arreterLegendMusic();
        System.out.println("=== MODE LEGEND 1v1 TERMINÉ ===");
    }
}