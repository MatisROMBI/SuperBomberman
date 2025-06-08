package com.bomberman.controller;

import com.bomberman.model.Music;
import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class VictoryController {
    public static int LAST_SCORE = 0; // Stocke temporairement le score du joueur

    @FXML
    private Label victoryLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;
    @FXML
    private Label scoreLabel;

    Music music = new Music();

    @FXML
    private void initialize() {
        music.demarrerMusiqueDeVictoire();
        playAgainButton.setOnAction(e -> playAgain());
        mainMenuButton.setOnAction(e -> goToMainMenu());
        // Affiche le score lors de l'ouverture de la scène
        victoryLabel.setText("VICTOIRE !\n");
        scoreLabel.setText("SCORE FINAL : " + LAST_SCORE);
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