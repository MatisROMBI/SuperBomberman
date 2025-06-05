package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController {
    @FXML
    private Button playButton;

    @FXML
    private Button quitButton;

    @FXML
    private void initialize() {
        playButton.setOnAction(e -> startGame());
        quitButton.setOnAction(e -> System.exit(0));
    }

    private void startGame() {
        SceneManager.switchScene("Game");
    }
}