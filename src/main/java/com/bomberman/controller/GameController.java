package com.bomberman.controller;

import com.bomberman.controller.GameOverController;
import com.bomberman.model.*;
import com.bomberman.model.enums.PowerUpType;
import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;
import com.bomberman.utils.SceneManager;
import com.bomberman.view.GameRenderer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;

public class GameController implements GameOverListener {
    @FXML
    private VBox gameContainer;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Label bonusLabel; // <-- POUR LE TEXTE BONUS

    private Game game;
    private GameRenderer renderer;
    private Set<String> pressedKeys;
    private AnimationTimer renderLoop;

    private Music music = new Music();

    @FXML
    private void initialize() {
        game = new Game();
        music.arreterGameOverMusique();
        music.demarrerMusique();
        // On connecte le listener (important!)
        game.getPlayer().setGameOverListener(this);
        renderer = new GameRenderer(gameCanvas);
        pressedKeys = new HashSet<>();

        setupSoundHandling();
        setupKeyboardHandling();
        startRenderLoop();

        // Vide le label au début
        if (bonusLabel != null) bonusLabel.setText("");
    }

    private void setupSoundHandling() {
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
        if (!pressedKeys.contains(keyCode)) {
            pressedKeys.add(keyCode);

            switch (keyCode) {
                case "UP":
                case "Z":
                    game.getPlayer().move(Direction.UP, game.getBoard(), game.getGameState());
                    break;
                case "DOWN":
                case "S":
                    game.getPlayer().move(Direction.DOWN, game.getBoard(), game.getGameState());
                    break;
                case "LEFT":
                case "Q":
                    game.getPlayer().move(Direction.LEFT, game.getBoard(), game.getGameState());
                    break;
                case "RIGHT":
                case "D":
                    game.getPlayer().move(Direction.RIGHT, game.getBoard(), game.getGameState());
                    break;
                case "SPACE":
                    game.getPlayer().placeBomb(game.getBoard(), game.getGameState());
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

    // Affiche le nom du bonus en bas de page
    public void showBonusText(PowerUpType type) {
        if (bonusLabel == null) return;
        String text = getBonusName(type);
        bonusLabel.setText(text);
        // Efface après 2s
        new Thread(() -> {
            try { Thread.sleep(2000); } catch (Exception e) {}
            Platform.runLater(() -> bonusLabel.setText(""));
        }).start();
    }

    // Méthode utilitaire
    public static String getBonusName(PowerUpType type) {
        switch(type) {
            case EXTRA_BOMB: return "Bonus : Bombe supplémentaire !";
            case RANGE_UP:   return "Bonus : Portée augmentée !";
            case LIFE:       return "Bonus : Vie supplémentaire !";
            case SPEED:      return "Bonus : Vitesse augmentée !";
            default:         return "";
        }
    }

    // === À APPELER quand le joueur ramasse un bonus ===
    // Exemple : dans Player.applyPowerUp(PowerUp powerUp), ajoute :
    // ((GameController) SceneManager.getCurrentController()).showBonusText(powerUp.getType());

    @Override
    public void onGameOver(int score) {
        music.arreterMusique();
        GameOverController.setLastScore(score);
        music.demarrerGameOverMusique();
        SceneManager.switchScene("GameOver");
    }
}
