/**
 * Gestionnaire de scènes JavaFX
 * Navigation entre les écrans avec redimensionnement automatique
 */
package com.bomberman.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private static Stage primaryStage;

    /**
     * Initialise le gestionnaire avec la fenêtre principale
     */
    public static void initialize(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Bomberman");
        primaryStage.setResizable(false);
    }

    /**
     * Change de scène avec chargement FXML
     */
    public static void switchScene(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/" + fxmlName + ".fxml")
            );
            Scene scene = new Scene(loader.load());

            // Chargement CSS avec vérification
            loadStylesheet(scene);

            // Ajustement de la taille de fenêtre
            adjustWindowSize(fxmlName);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Erreur chargement scène " + fxmlName + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge la feuille de style avec plusieurs tentatives
     */
    private static void loadStylesheet(Scene scene) {
        String[] cssPaths = {
                "/css/style.css",
                "/style.css",
                "css/style.css",
                "style.css"
        };

        boolean cssLoaded = false;

        for (String cssPath : cssPaths) {
            try {
                URL cssUrl = SceneManager.class.getResource(cssPath);
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS chargé depuis : " + cssPath);
                    cssLoaded = true;
                    break;
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement CSS " + cssPath + " : " + e.getMessage());
            }
        }

        if (!cssLoaded) {
            System.err.println("ATTENTION: Aucun fichier CSS trouvé. Utilisation des styles par défaut.");
            applyFallbackStyles(scene);
        }
    }

    /**
     * Applique des styles de base si le CSS n'est pas trouvé
     */
    private static void applyFallbackStyles(Scene scene) {
        String fallbackCSS =
                ".root { -fx-font-family: 'Arial'; -fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e); }" +
                        ".menu-button { -fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b); -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand; }" +
                        ".menu-button:hover { -fx-background-color: linear-gradient(to bottom, #ec7063, #e74c3c); }" +
                        ".pause-button-primary { -fx-background-color: linear-gradient(to bottom, #3498db, #2980b9); -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; }" +
                        ".pause-button-primary:hover { -fx-background-color: linear-gradient(to bottom, #5dade2, #3498db); }" +
                        ".pause-button-secondary { -fx-background-color: linear-gradient(to bottom, #f39c12, #e67e22); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; }" +
                        ".pause-button-secondary:hover { -fx-background-color: linear-gradient(to bottom, #f7dc6f, #f39c12); }" +
                        ".pause-button-danger { -fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; }" +
                        ".pause-button-danger:hover { -fx-background-color: linear-gradient(to bottom, #ec7063, #e74c3c); }";

        scene.getRoot().setStyle(fallbackCSS);
        System.out.println("Styles de fallback appliqués.");
    }

    /**
     * Ajuste la taille de la fenêtre selon la scène
     */
    private static void adjustWindowSize(String fxmlName) {
        switch (fxmlName) {
            case "Game":
                primaryStage.setWidth(Constants.WINDOW_WIDTH + 16);
                primaryStage.setHeight(Constants.WINDOW_HEIGHT + 39);
                break;
            case "LegendGame":
                primaryStage.setWidth(900);
                primaryStage.setHeight(700);
                break;
            case "LevelEditor":
                primaryStage.setWidth(820);
                primaryStage.setHeight(720);
                break;
            case "MapSelection":
                primaryStage.setWidth(920);
                primaryStage.setHeight(720);
                break;
            case "ThemeSelection":
                primaryStage.setWidth(920);
                primaryStage.setHeight(720);
                break;
            case "MainMenu":
            case "GameOver":
            case "Victory":
            default:
                primaryStage.setWidth(816);
                primaryStage.setHeight(639);
                break;
        }

        // Centre la fenêtre après redimensionnement
        primaryStage.centerOnScreen();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}