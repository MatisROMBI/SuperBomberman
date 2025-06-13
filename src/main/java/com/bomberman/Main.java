/**
 * Classe principale de l'application Bomberman
 * Point d'entrée qui initialise JavaFX et lance le menu principal
 */
package com.bomberman;

import com.bomberman.model.Player;
import com.bomberman.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * Méthode de démarrage de JavaFX
     * Configure la fenêtre principale et lance le menu
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialise le gestionnaire de scènes avec la fenêtre principale
        SceneManager.initialize(primaryStage);
        // Affiche le menu principal au démarrage
        SceneManager.switchScene("MainMenu");
    }

    /**
     * Point d'entrée de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
