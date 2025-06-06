package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameOverController {

    @FXML
    private Label gameOverLabel;

    @FXML
    private Label lastScoreLabel;

    @FXML
    private Button playAgainButton;

    @FXML
    private Button mainMenuButton;

    // Champ statique pour stocker le score à afficher
    public static int lastScore = 0;

    public static void setLastScore(int score) {
        lastScore = score;
    }

    @FXML
    private void initialize() {
        playAgainButton.setOnAction(e -> playAgain());
        mainMenuButton.setOnAction(e -> goToMainMenu());

        // Texte "GAME OVER"
        gameOverLabel.setText("GAME OVER");

        // Affiche le score dans le Label prévu
        lastScoreLabel.setText(String.valueOf(lastScore));
    }

    private void playAgain() {
        SceneManager.switchScene("Game");
    }

    private void goToMainMenu() {
        SceneManager.switchScene("MainMenu");
    }
}
