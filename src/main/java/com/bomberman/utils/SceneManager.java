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
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}