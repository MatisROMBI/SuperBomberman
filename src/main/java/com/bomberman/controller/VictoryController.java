/**
 * Contrôleur pour l'écran de victoire
 * Affiche le score final et gère les différents types de victoire
 * Support spécial pour le mode Legend avec affichage du gagnant
 */
package com.bomberman.controller;

import com.bomberman.model.Music;
import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class VictoryController {
    // Variables statiques pour partager les données entre contrôleurs
    public static int LAST_SCORE = 0;
    public static String WINNER_NAME = ""; // Nom du gagnant pour le mode Legend

    @FXML private Label victoryLabel;      // Titre de victoire (dynamique)
    @FXML private Label scoreLabel;        // Affichage du score final
    @FXML private Button playAgainButton;  // Bouton rejouer
    @FXML private Button mainMenuButton;   // Bouton menu principal

    private final Music music = new Music();

    /**
     * Initialisation du contrôleur de victoire
     */
    @FXML
    private void initialize() {
        // Lancement de la musique de victoire
        music.demarrerMusiqueDeVictoire();

        // Configuration de l'affichage selon le type de victoire
        if (victoryLabel != null) {
            if (WINNER_NAME != null && !WINNER_NAME.isBlank()) {
                // Mode Legend avec gagnant spécifique
                victoryLabel.setText(WINNER_NAME + " a gagné !");
                playAgainButton.setOnAction(e -> rejouerLegend());
            } else {
                // Mode classique
                victoryLabel.setText("VICTOIRE !");
                playAgainButton.setOnAction(e -> rejouer());
            }
        }

        // Affichage du score final
        if (scoreLabel != null) {
            scoreLabel.setText("SCORE FINAL : " + LAST_SCORE);
        }

        // Configuration du bouton menu principal
        mainMenuButton.setOnAction(e -> retourMenu());
    }

    /**
     * Rejouer en mode classique
     */
    private void rejouer() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("Game");
    }

    /**
     * Rejouer en mode Legend
     */
    private void rejouerLegend() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("LegendGame");
    }

    /**
     * Retour au menu principal
     */
    private void retourMenu() {
        music.arreterMusiqueDeVictoire();
        SceneManager.switchScene("MainMenu");
    }
}
