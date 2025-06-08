package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class VictoryController {
    public static int LAST_SCORE = 0;

    @FXML
    private Label scoreLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;

    @FXML
    private void initialize() {
        scoreLabel.setText(String.valueOf(LAST_SCORE));
        playAgainButton.setOnAction(e -> SceneManager.switchScene("Game"));
        mainMenuButton.setOnAction(e -> SceneManager.switchScene("MainMenu"));
    }
}
