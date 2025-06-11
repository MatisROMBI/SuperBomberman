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
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;

/**
 * Contrôleur principal du jeu Bomberman.
 * Gère le déroulement de la partie, les entrées clavier, le rendu graphique, et la fin de partie.
 */

public class GameController implements GameOverListener {

    @FXML private VBox gameContainer;
    @FXML private Canvas gameCanvas;

    private Game game;
    private GameRenderer renderer;
    private final Set<String> pressedKeys = new HashSet<>();
    private AnimationTimer renderLoop;
    private final Music music = new Music();

    @FXML
    private void initialize() {
        // Initialise le jeu et la musique
        game = new Game();
        renderer = new GameRenderer(gameCanvas);
        music.arreterGameOverMusique();
        music.demarrerMusique();
        game.getPlayer().setGameOverListener(this);

        setupKeyboardHandling();
        startRenderLoop();
    }

    // Gère les touches clavier et le focus
    private void setupKeyboardHandling() {
        gameContainer.setFocusTraversable(true);
        Platform.runLater(() -> {
            gameContainer.requestFocus();
            var scene = gameContainer.getScene();
            if (scene != null) {
                scene.setOnKeyPressed(this::handleKeyPressed);
                scene.setOnKeyReleased(this::handleKeyReleased);
            }
            gameContainer.setOnKeyPressed(this::handleKeyPressed);
            gameContainer.setOnKeyReleased(this::handleKeyReleased);
        });
        gameCanvas.setOnMouseClicked(e -> gameContainer.requestFocus());
    }

    // Appui sur une touche
    private void handleKeyPressed(KeyEvent event) {
        String key = event.getCode().toString();
        if (!pressedKeys.add(key)) return;

        switch (key) {
            case "UP", "Z"    -> game.getPlayer().move(Direction.UP,    game.getBoard(), game.getGameState());
            case "DOWN", "S"  -> game.getPlayer().move(Direction.DOWN,  game.getBoard(), game.getGameState());
            case "LEFT", "Q"  -> game.getPlayer().move(Direction.LEFT,  game.getBoard(), game.getGameState());
            case "RIGHT", "D" -> game.getPlayer().move(Direction.RIGHT, game.getBoard(), game.getGameState());
            case "SPACE"      -> game.getPlayer().placeBomb(game.getBoard(), game.getGameState());
            case "ESCAPE"     -> game.pause();
        }
        event.consume();
    }

    // Relâchement d'une touche
    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode().toString());
    }

    // Boucle de rendu
    private void startRenderLoop() {
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderer.render(game);
                var state = game.getGameState();
                if (state == GameState.GAME_OVER || state == GameState.VICTORY) {
                    music.arreterMusique();
                    stop();
                }
            }
        };
        renderLoop.start();
    }

    // Nettoyage à la fermeture
    public void cleanup() {
        if (renderLoop != null) renderLoop.stop();
        if (game != null) game.stop();
    }

    // Fin de partie
    @Override
    public void onGameOver(int score) {
        music.arreterMusique();
        GameOverController.setLastScore(score);
        music.demarrerGameOverMusique();
        SceneManager.switchScene("GameOver");
    }
}
