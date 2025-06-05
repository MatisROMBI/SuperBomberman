package com.bomberman;

import com.bomberman.model.Player;
import com.bomberman.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        SceneManager.initialize(primaryStage);
        SceneManager.switchScene("MainMenu");
    }

    public static void main(String[] args) {
        launch(args);
    }

}