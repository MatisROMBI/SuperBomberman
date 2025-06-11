package com.bomberman.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void initialize(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Bomberman");
        primaryStage.setResizable(false);
    }

    public static void switchScene(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/" + fxmlName + ".fxml")
            );
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add("/css/style.css");

            // Adapter la taille de la fenêtre selon la scène
            adjustWindowSize(fxmlName);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la scène " + fxmlName + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ajuste la taille de la fenêtre selon la scène chargée
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

        // Centrer la fenêtre après redimensionnement
        primaryStage.centerOnScreen();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}