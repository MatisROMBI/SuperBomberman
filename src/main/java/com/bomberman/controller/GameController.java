package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;
import com.bomberman.utils.SceneManager;
import com.bomberman.view.GameRenderer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;

public class GameController {
    @FXML
    private VBox gameContainer;
    @FXML
    private Canvas gameCanvas;

    private Game game;
    private GameRenderer renderer;
    private Set<String> pressedKeys;
    private AnimationTimer renderLoop;

    @FXML
    private void initialize() {
        game = new Game();
        renderer = new GameRenderer(gameCanvas);
        pressedKeys = new HashSet<>();

        setupKeyboardHandling();
        startRenderLoop();
    }

    private void setupKeyboardHandling() {
        gameContainer.setFocusTraversable(true);

        javafx.application.Platform.runLater(() -> {
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
        if (!pressedKeys.contains(keyCode)) {
            pressedKeys.add(keyCode);

            switch (keyCode) {
                case "UP":
                case "Z":
                    game.getPlayer().move(Direction.UP, game.getBoard());
                    break;
                case "DOWN":
                case "S":
                    game.getPlayer().move(Direction.DOWN, game.getBoard());
                    break;
                case "LEFT":
                case "Q":
                    game.getPlayer().move(Direction.LEFT, game.getBoard());
                    break;
                case "RIGHT":
                case "D":
                    game.getPlayer().move(Direction.RIGHT, game.getBoard());
                    break;
                case "SPACE":
                    game.getPlayer().placeBomb(game.getBoard());
                    break;
                case "ESCAPE":
                    game.pause();
                    break;
            }
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
                renderer.render(game);
                if (game.getGameState() == GameState.GAME_OVER ||
                        game.getGameState() == GameState.VICTORY) {
                    stop();
                    SceneManager.switchScene("GameOver");
                }
            }
        };
        renderLoop.start();
    }

    public void cleanup() {
        if (renderLoop != null) {
            renderLoop.stop();
        }
        if (game != null) {
            game.stop();
        }
    }

}
