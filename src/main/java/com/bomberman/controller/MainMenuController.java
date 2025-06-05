package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainMenuController {
    @FXML
    private Button playButton;
    @FXML
    private Button quitButton;
    @FXML
    private ImageView backgroundImage;

    @FXML
    private void initialize() {
        try {
            // Remplace "menu_bg.png" par le vrai nom de ton image (attention à la casse et aux espaces !)
            Image img = new Image(getClass().getResourceAsStream("/images/menu_bg.png"));
            backgroundImage.setImage(img);
        } catch (Exception e) {
            backgroundImage.setImage(null);
            System.err.println("Erreur chargement image fond menu : " + e.getMessage());
        }

        playButton.setOnAction(e -> startGame());
        quitButton.setOnAction(e -> System.exit(0));
    }

    private void startGame() {
        SceneManager.switchScene("Game");
    }
}
