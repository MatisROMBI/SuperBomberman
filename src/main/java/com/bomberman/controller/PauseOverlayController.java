package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * Contrôleur pour l'overlay de pause
 */
public class PauseOverlayController {

    @FXML private StackPane pauseOverlay;
    @FXML private Button resumeButton;
    @FXML private Button restartButton;
    @FXML private Button mainMenuButton;

    private PauseActionListener actionListener;

    /**
     * Interface pour communiquer avec le contrôleur parent
     */
    public interface PauseActionListener {
        void onResume();
        void onRestart();
        void onMainMenu();
    }

    @FXML
    private void initialize() {
        setupButtons();
    }

    private void setupButtons() {
        // Bouton Reprendre
        resumeButton.setOnAction(e -> {
            hidePause();
            if (actionListener != null) {
                actionListener.onResume();
            }
        });

        // Bouton Relancer
        restartButton.setOnAction(e -> {
            hidePause();
            if (actionListener != null) {
                actionListener.onRestart();
            }
        });

        // Bouton Menu Principal
        mainMenuButton.setOnAction(e -> {
            hidePause();
            if (actionListener != null) {
                actionListener.onMainMenu();
            }
        });

        // Clic sur l'overlay pour reprendre (optionnel)
        pauseOverlay.setOnMouseClicked(e -> {
            // Vérifier que le clic n'est pas sur un bouton
            if (e.getTarget() == pauseOverlay) {
                hidePause();
                if (actionListener != null) {
                    actionListener.onResume();
                }
            }
        });
    }

    /**
     * Affiche le menu de pause avec animation
     */
    public void showPause() {
        pauseOverlay.setVisible(true);
        pauseOverlay.setOpacity(0);

        // Animation d'apparition
        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(200), pauseOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Focus sur le bouton reprendre
        resumeButton.requestFocus();
    }

    /**
     * Cache le menu de pause avec animation
     */
    public void hidePause() {
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(150), pauseOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> pauseOverlay.setVisible(false));
        fadeOut.play();
    }

    /**
     * Vérifie si le menu de pause est affiché
     */
    public boolean isPauseVisible() {
        return pauseOverlay.isVisible();
    }

    /**
     * Définit le listener pour les actions de pause
     */
    public void setActionListener(PauseActionListener listener) {
        this.actionListener = listener;
    }
}