package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.model.GameOverListener;
import com.bomberman.model.Music;
import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;
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

public class GameController implements GameOverListener, PauseOverlayController.PauseActionListener {
    @FXML private VBox gameContainer;
    @FXML private Canvas gameCanvas;

    private Game game;
    private GameRenderer renderer;
    private Set<String> pressedKeys;
    private AnimationTimer renderLoop;
    private final Music music = new Music();

    // NOUVEAUTÉ: Gestion du menu de pause
    private StackPane pauseOverlay;
    private PauseOverlayController pauseController;
    private boolean isPaused = false;

    // OPTIMISATION: Limiter le taux de rafraichissement
    private long lastRenderTime = 0;
    private static final long RENDER_INTERVAL = 16_666_666L; // ~60 FPS en nanosecondes

    @FXML
    private void initialize() {
        game = new Game();
        music.arreterGameOverMusique();
        music.demarrerMusique();
        game.getPlayer().setGameOverListener(this);
        renderer = new GameRenderer(gameCanvas);
        pressedKeys = new HashSet<>();

        setupKeyboardHandling();
        setupPauseOverlay();
        startRenderLoop();
    }

    /**
     * NOUVEAUTÉ: Configuration du menu de pause
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

    /**
     * Gestion des événements clavier et focus.
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
        gameCanvas.setOnMouseClicked(e -> gameContainer.requestFocus());
    }

    // OPTIMISATION: Gestion plus efficace des touches
    private void handleKeyPressed(KeyEvent event) {
        String keyCode = event.getCode().toString();

        // NOUVEAUTÉ: Gestion spéciale de la touche ESCAPE pour la pause
        if ("ESCAPE".equals(keyCode)) {
            togglePause();
            event.consume();
            return;
        }

        // Ne traiter les autres touches que si le jeu n'est pas en pause
        if (!isPaused && pressedKeys.add(keyCode)) {
            processKeyAction(keyCode);
        }
        event.consume();
    }

    /**
     * NOUVEAUTÉ: Basculer entre pause et jeu
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
     * NOUVEAUTÉ: Mettre le jeu en pause
     */
    private void pauseGame() {
        isPaused = true;
        game.pause();
        music.arreterMusique();
        pauseController.showPause();
    }

    /**
     * NOUVEAUTÉ: Reprendre le jeu
     */
    private void resumeGame() {
        isPaused = false;
        game.pause(); // Bascule l'état de pause
        music.demarrerMusique();
        pauseController.hidePause();
        gameContainer.requestFocus();
    }

    // NOUVELLE MÉTHODE: Séparer le traitement des touches
    private void processKeyAction(String keyCode) {
        switch (keyCode) {
            case "UP": case "Z":
                game.getPlayer().move(Direction.UP, game.getBoard(), game.getGameState());
                break;
            case "DOWN": case "S":
                game.getPlayer().move(Direction.DOWN, game.getBoard(), game.getGameState());
                break;
            case "LEFT": case "Q":
                game.getPlayer().move(Direction.LEFT, game.getBoard(), game.getGameState());
                break;
            case "RIGHT": case "D":
                game.getPlayer().move(Direction.RIGHT, game.getBoard(), game.getGameState());
                break;
            case "SPACE":
                game.getPlayer().placeBomb(game.getBoard(), game.getGameState());
                break;
            default:
                break;
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode().toString());
    }

    // OPTIMISATION: Limiter le taux de rendu
    private void startRenderLoop() {
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Limiter à ~60 FPS
                if (now - lastRenderTime >= RENDER_INTERVAL) {
                    renderer.render(game);
                    lastRenderTime = now;
                }

                if (game.getGameState() == GameState.GAME_OVER || game.getGameState() == GameState.VICTORY) {
                    music.arreterMusique();
                    stop();
                }
            }
        };
        renderLoop.start();
    }

    public void cleanup() {
        if (renderLoop != null) renderLoop.stop();
        if (game != null) game.stop();
    }

    // Callback fin de partie
    @Override
    public void onGameOver(int score) {
        music.arreterMusique();
        com.bomberman.controller.GameOverController.setLastScore(score);
        music.demarrerGameOverMusique();
        SceneManager.switchScene("GameOver");
    }

    // ======================================================================
    // NOUVEAUTÉ: Implémentation des actions du menu de pause
    // ======================================================================

    @Override
    public void onResume() {
        resumeGame();
    }

    @Override
    public void onRestart() {
        // Arrêter le jeu actuel
        cleanup();

        // Redémarrer une nouvelle partie
        game = new Game();
        game.getPlayer().setGameOverListener(this);
        music.demarrerMusique();
        startRenderLoop();

        isPaused = false;
        gameContainer.requestFocus();
    }

    @Override
    public void onMainMenu() {
        cleanup();
        music.arreterMusique();
        SceneManager.switchScene("MainMenu");
    }
}