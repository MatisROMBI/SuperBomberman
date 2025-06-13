/**
 * Contrôleur pour l'écran Game Over
 * Affiche le score final et permet de rejouer ou retourner au menu
 */
package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameOverController {
    // Variable statique pour partager le score entre les contrôleurs
    public static int lastScore = 0;

    @FXML private Label lastScoreLabel;     // Affichage du score final
    @FXML private Button playAgainButton;   // Bouton pour rejouer
    @FXML private Button mainMenuButton;    // Bouton retour menu principal

    /**
     * Définit le dernier score à afficher
     * @param score Le score final du joueur
     */
    public static void setLastScore(int score) {
        lastScore = score;
    }

    /**
     * Initialisation du contrôleur Game Over
     */
    @FXML
    private void initialize() {
        // Affichage du score final
        lastScoreLabel.setText(String.valueOf(lastScore));

        // Configuration des boutons
        playAgainButton.setOnAction(e -> SceneManager.switchScene("Game"));
        mainMenuButton.setOnAction(e -> SceneManager.switchScene("MainMenu"));
    }
}