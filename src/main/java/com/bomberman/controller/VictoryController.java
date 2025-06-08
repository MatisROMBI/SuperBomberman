package com.bomberman.controller;

import com.bomberman.model.Music;
import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class VictoryController {
    public static int LAST_SCORE = 0;

    @FXML
    private Label victoryLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;

    Music music = new Music();

    @FXML
    private void initialize() {
        music.demarrerMusiqueDeVictoire();

        if (victoryLabel != null)
            victoryLabel.setText("VICTOIRE !");

        if (scoreLabel != null)
            scoreLabel.setText("SCORE FINAL : " + LAST_SCORE);

        playAgainButton.setOnAction(e -> playAgain());
        mainMenuButton.setOnAction(e -> goToMainMenu());
    }

    private void playAgain() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("Game");
    }

    private void goToMainMenu() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("MainMenu");
    }
}
