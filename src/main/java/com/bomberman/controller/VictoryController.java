package com.bomberman.controller;

import com.bomberman.model.Music;
import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Contrôleur de l'écran de victoire
 * Affiche le score final et, en 1v1, le joueur gagnant.
 */
public class VictoryController {
    public static int LAST_SCORE = 0;
    public static String WINNER_NAME = ""; // <--- Ajoute cette variable pour le gagnant (Joueur 1 / Joueur 2 / pseudo...)

    @FXML
    private Label victoryLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;

    private final Music music = new Music();

    @FXML
    private void initialize() {
        music.demarrerMusiqueDeVictoire();

        // Si un gagnant est renseigné, on l’affiche, sinon "VICTOIRE !"
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

        // Boutons
        mainMenuButton.setOnAction(e -> retourMenu());
    }

    private void rejouer() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("Game");
    }

    private void rejouerLegend() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("LegendGame");
    }

    private void retourMenu() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("MainMenu");
    }
}
