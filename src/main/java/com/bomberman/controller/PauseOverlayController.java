/**
 * Contrôleur pour l'overlay de menu pause
 * Interface commune utilisée par tous les modes de jeu
 * Communique avec le contrôleur parent via l'interface PauseActionListener
 */
package com.bomberman.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class PauseOverlayController {

    // ===== ÉLÉMENTS DE L'INTERFACE FXML =====
    @FXML private StackPane pauseOverlay;     // Conteneur principal de l'overlay
    @FXML private Button resumeButton;        // Bouton reprendre
    @FXML private Button restartButton;       // Bouton relancer la partie
    @FXML private Button mainMenuButton;      // Bouton menu principal

    // ===== COMMUNICATION AVEC LE PARENT =====
    private PauseActionListener actionListener; // Interface de callback

    /**
     * Interface pour communiquer avec le contrôleur parent
     * Permet au menu pause d'envoyer des actions au contrôleur de jeu
     */
    public interface PauseActionListener {
        void onResume();    // Action reprendre le jeu
        void onRestart();   // Action relancer une nouvelle partie
        void onMainMenu();  // Action retour au menu principal
    }

    /**
     * Initialisation du contrôleur du menu pause
     */
    @FXML
    private void initialize() {
        setupButtons();
    }

    /**
     * Configuration des actions des boutons
     */
    private void setupButtons() {

        // ===== BOUTON REPRENDRE =====
        resumeButton.setOnAction(e -> {
            hidePause();  // Cache le menu pause
            if (actionListener != null) {
                actionListener.onResume(); // Notifie le parent
            }
        });

        // ===== BOUTON RELANCER =====
        restartButton.setOnAction(e -> {
            hidePause();  // Cache le menu pause
            if (actionListener != null) {
                actionListener.onRestart(); // Notifie le parent
            }
        });

        // ===== BOUTON MENU PRINCIPAL =====
        mainMenuButton.setOnAction(e -> {
            hidePause();  // Cache le menu pause
            if (actionListener != null) {
                actionListener.onMainMenu(); // Notifie le parent
            }
        });

        // ===== CLIC SUR L'OVERLAY POUR REPRENDRE =====
        // Permet de reprendre en cliquant à côté des boutons
        pauseOverlay.setOnMouseClicked(e -> {
            // Vérifie que le clic n'est pas sur un bouton
            if (e.getTarget() == pauseOverlay) {
                hidePause();
                if (actionListener != null) {
                    actionListener.onResume();
                }
            }
        });
    }

    /**
     * Affiche le menu de pause avec animation d'apparition
     */
    public void showPause() {
        pauseOverlay.setVisible(true);
        pauseOverlay.setOpacity(0); // Commence transparent

        // Animation de fondu d'entrée
        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(200), pauseOverlay);
        fadeIn.setFromValue(0);    // De transparent
        fadeIn.setToValue(1);      // À opaque
        fadeIn.play();

        // Focus sur le bouton reprendre pour navigation clavier
        resumeButton.requestFocus();
    }

    /**
     * Cache le menu de pause avec animation de sortie
     */
    public void hidePause() {
        // Animation de fondu de sortie
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(150), pauseOverlay);
        fadeOut.setFromValue(1);   // D'opaque
        fadeOut.setToValue(0);     // À transparent

        // Cache complètement l'overlay à la fin de l'animation
        fadeOut.setOnFinished(e -> pauseOverlay.setVisible(false));
        fadeOut.play();
    }

    /**
     * Vérifie si le menu de pause est actuellement visible
     * @return true si le menu pause est affiché
     */
    public boolean isPauseVisible() {
        return pauseOverlay.isVisible();
    }

    /**
     * Définit le listener pour recevoir les actions du menu pause
     * @param listener L'objet qui implémente PauseActionListener
     */
    public void setActionListener(PauseActionListener listener) {
        this.actionListener = listener;
    }
}