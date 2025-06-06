package com.bomberman.controller;

import com.bomberman.model.Music;
import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.AudioClip;

public class GameOverController {
    @FXML
    private Label gameOverLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;

    // Variable statique pour passer le score
    private static int lastScore = 0;

    public static void setLastScore(int score) {
        lastScore = score;
    }

    @FXML
    private void initialize() {
        playAgainButton.setOnAction(e -> playAgain());
        mainMenuButton.setOnAction(e -> goToMainMenu());

        // Affiche le score dès l'arrivée sur l'écran
        gameOverLabel.setText("GAME OVER !\nVotre score : " + lastScore);
    }


    private void playAgain() {
        SceneManager.switchScene("Game");
    }

    private void goToMainMenu() {
        SceneManager.switchScene("MainMenu");
    }

    // Optionnel si tu veux modifier dynamiquement le texte plus tard
    public void setGameResult(String result) {
        gameOverLabel.setText(result);
    }
}

