package com.bomberman.controller;

import com.bomberman.model.Legend1v1Board;
import com.bomberman.model.Music;
import com.bomberman.model.Player;
import com.bomberman.utils.SceneManager;
import com.bomberman.view.GameRenderer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Contrôleur du mode LEGEND 1v1 - AVEC MENU DE PAUSE
 */
public class LegendGameController implements PauseOverlayController.PauseActionListener {
    @FXML private VBox gameContainer;
    @FXML private Canvas gameCanvas;

    private Legend1v1Board board;
    private GameRenderer renderer;
    private final Set<String> pressedKeys = new HashSet<>();
    private AnimationTimer renderLoop;
    private boolean gameOver = false;
    private boolean victory = false;
    private final Music music = new Music();

    // Gestion du menu de pause
    private StackPane pauseOverlay;
    private PauseOverlayController pauseController;
    private boolean isPaused = false;

    private long lastRenderTime = 0;
    private static final long RENDER_INTERVAL = 16_666_666L; // ~60 FPS

    @FXML
    private void initialize() {
        board = new Legend1v1Board();
        renderer = new GameRenderer(gameCanvas);

        setupKeyboardHandling();
        setupPauseOverlay();
        music.demarrerLegendMusic();
        startRenderLoop();
    }

    /**
     * Configuration du menu de pause
     */
    private void setupPauseOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PauseOverlay.fxml"));
            pauseOverlay = loader.load();
            pauseController = loader.getController();
            pauseController.setActionListener(this);

            // Ajouter l'overlay au conteneur principal
            if (gameContainer.getParent() instanceof StackPane) {
                StackPane parent = (StackPane) gameContainer.getParent();
                parent.getChildren().add(pauseOverlay);
            } else {
                // Créer un nouveau StackPane si nécessaire
                StackPane stackPane = new StackPane();
                VBox originalParent = (VBox) gameContainer.getParent();
                originalParent.getChildren().remove(gameContainer);
                stackPane.getChildren().addAll(gameContainer, pauseOverlay);
                originalParent.getChildren().add(stackPane);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du menu de pause : " + e.getMessage());
        }
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

        // Gestion spéciale de la touche ESCAPE pour la pause
        if ("ESCAPE".equals(keyCode)) {
            togglePause();
            event.consume();
            return;
        }

        // Ne traiter les autres touches que si le jeu n'est pas en pause
        if (!isPaused && !pressedKeys.contains(keyCode)) {
            pressedKeys.add(keyCode);
            processKeyAction(keyCode);
        }
        event.consume();
    }

    /**
     * Basculer entre pause et jeu
     */
    private void togglePause() {
        if (pauseController != null) {
            if (isPaused) {
                resumeGame();
            } else {
                pauseGame();
            }
        }
    }

    /**
     * Mettre le jeu en pause
     */
    private void pauseGame() {
        isPaused = true;
        music.arreterLegendMusic();
        pauseController.showPause();
    }

    /**
     * Reprendre le jeu
     */
    private void resumeGame() {
        isPaused = false;
        music.demarrerLegendMusic();
        pauseController.hidePause();
        gameContainer.requestFocus();
    }

    private void processKeyAction(String keyCode) {
        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        switch (keyCode) {
            // Joueur 1 (ZQSD + R)
            case "Z":
                p1.moveUp(board);
                break;
            case "S":
                p1.moveDown(board);
                break;
            case "Q":
                p1.moveLeft(board);
                break;
            case "D":
                p1.moveRight(board);
                break;
            case "R":
                p1.placeBomb(board);
                break;

            // Joueur 2 (IJKL + P)
            case "I":
                p2.moveUp(board);
                break;
            case "K":
                p2.moveDown(board);
                break;
            case "J":
                p2.moveLeft(board);
                break;
            case "L":
                p2.moveRight(board);
                break;
            case "P":
                p2.placeBomb(board);
                break;
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode().toString());
    }

    private void startRenderLoop() {
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver && !victory && !isPaused) {
                    if (now - lastRenderTime >= RENDER_INTERVAL) {
                        board.update();
                        renderer.renderLegend1v1(board);
                        lastRenderTime = now;
                    }
                    checkGameState();
                }
            }
        };
        renderLoop.start();
    }

    private void checkGameState() {
        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        if (!p1.isAlive() && !p2.isAlive()) {
            gameOver = true;
            music.arreterLegendMusic();
            com.bomberman.controller.GameOverController.setLastScore(
                    Math.max(p1.getScore(), p2.getScore()));
            SceneManager.switchScene("GameOver");
            stopRenderLoop();
            return;
        }

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
    }

    // ======================================================================
    // Implémentation des actions du menu de pause
    // ======================================================================

    @Override
    public void onResume() {
        resumeGame();
    }

    @Override
    public void onRestart() {
        // Arrêter le jeu actuel
        cleanup();

        // Redémarrer une nouvelle partie Legend
        board = new Legend1v1Board();
        music.demarrerLegendMusic();
        startRenderLoop();

        gameOver = false;
        victory = false;
        isPaused = false;
        gameContainer.requestFocus();
    }

    @Override
    public void onMainMenu() {
        cleanup();
        SceneManager.switchScene("MainMenu");
    }
}