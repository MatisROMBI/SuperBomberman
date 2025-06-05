package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameOverController {
    @FXML
    private Label gameOverLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;

    @FXML
    private void initialize() {
        playAgainButton.setOnAction(e -> playAgain());
        mainMenuButton.setOnAction(e -> goToMainMenu());
    }

    private void playAgain() {
        SceneManager.switchScene("Game");
    }

    private void goToMainMenu() {
        SceneManager.switchScene("MainMenu");
    }

    public void setGameResult(String result) {
        gameOverLabel.setText(result);
    }
}
