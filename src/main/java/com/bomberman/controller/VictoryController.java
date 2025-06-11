package com.bomberman.controller;

/**
 * Contrôleur de l'écran de victoire.
 * Affiche le score final et le gagnant (en mode 1v1).
 */

import com.bomberman.model.Music;
import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class VictoryController {
    public static int LAST_SCORE = 0;
    public static String WINNER_NAME = "";

    @FXML private Label victoryLabel;
    @FXML private Label scoreLabel;
    @FXML private Button playAgainButton;
    @FXML private Button mainMenuButton;

    private final Music music = new Music();

    @FXML
    private void initialize() {
        music.demarrerMusiqueDeVictoire();

        // Affiche le gagnant ou "VICTOIRE !"
        if (victoryLabel != null) {
            if (WINNER_NAME != null && !WINNER_NAME.isBlank()) {
                victoryLabel.setText(WINNER_NAME + " a gagné !");
                playAgainButton.setOnAction(e -> rejouerLegend());
            } else {
                victoryLabel.setText("VICTOIRE !");
                playAgainButton.setOnAction(e -> rejouer());
            }
        }

        // Affiche le score final
        if (scoreLabel != null)
            scoreLabel.setText("SCORE FINAL : " + LAST_SCORE);

        // Retour menu principal
        mainMenuButton.setOnAction(e -> retourMenu());
    }

    // Relancer une partie classique
    private void rejouer() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("Game");
    }

    // Relancer en mode Legend 1v1
    private void rejouerLegend() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("LegendGame");
    }

    // Retour au menu principal
    private void retourMenu() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("MainMenu");
    }
}
