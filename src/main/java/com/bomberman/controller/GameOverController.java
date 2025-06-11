package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Contrôleur de l'écran Game Over.
 * Affiche le score final et propose de rejouer ou revenir au menu.
 */
public class GameOverController {

    // Score de la dernière partie terminée
    private static int lastScore = 0;

    @FXML private Label lastScoreLabel;
    @FXML private Button playAgainButton;
    @FXML private Button mainMenuButton;

    // Permet de définir le score à afficher
    public static void setLastScore(int score) { lastScore = score; }

    @FXML
    private void initialize() {
        // Affiche le score
        lastScoreLabel.setText(Integer.toString(lastScore));
        // Bouton pour relancer une partie
        playAgainButton.setOnAction(e -> SceneManager.switchScene("Game"));
        // Bouton pour revenir au menu principal
        mainMenuButton.setOnAction(e -> SceneManager.switchScene("MainMenu"));
    }
}
