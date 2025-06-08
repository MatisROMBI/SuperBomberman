package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameOverController {
    public static int lastScore = 0;

    @FXML
    private Label lastScoreLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;

    public static void setLastScore(int score) { lastScore = score; }

    @FXML
    private void initialize() {
        lastScoreLabel.setText(String.valueOf(lastScore));
        playAgainButton.setOnAction(e -> SceneManager.switchScene("Game"));
        mainMenuButton.setOnAction(e -> SceneManager.switchScene("MainMenu"));
    }
}
