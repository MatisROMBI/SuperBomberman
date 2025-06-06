package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class MainMenuController {
    @FXML private Button playButton;
    @FXML private Button quitButton;
    @FXML private ImageView backgroundImage;
    @FXML private ImageView balloon1; // Dirigeable bleu (bleu)
    @FXML private ImageView balloon2; // Ballon rouge (rouge)
    @FXML private ImageView balloon3; // Dirigeable FIRE (fire)

    @FXML
    private void initialize() {
        try {
            backgroundImage.setImage(new Image(getClass().getResourceAsStream("/images/menu_bg.png")));
            balloon1.setImage(new Image(getClass().getResourceAsStream("/images/dirigeable_bleu.png")));
            balloon2.setImage(new Image(getClass().getResourceAsStream("/images/ballon_rouge.png")));
            balloon3.setImage(new Image(getClass().getResourceAsStream("/images/fire_dirigeable.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        playButton.setOnAction(e -> startGame());
        quitButton.setOnAction(e -> System.exit(0));
        animateBalloons();
    }

    private void animateBalloons() {
        double width = 800;
        // --- Placement initial et Y adaptés à la taille de l'écran et des images ---
        // BLEU : de gauche à droite, en haut
        balloon1.setTranslateX(-balloon1.getFitWidth());
        balloon1.setTranslateY(100);

        // FIRE : de droite à gauche, milieu-haut
        balloon3.setTranslateX(width + balloon3.getFitWidth());
        balloon3.setTranslateY(230);

        // ROUGE : de droite à gauche, un peu plus haut que le bas
        balloon2.setTranslateX(width + balloon2.getFitWidth());
        balloon2.setTranslateY(380);

        // --- Transitions ---
        TranslateTransition ttBleu = new TranslateTransition(Duration.seconds(10), balloon1);
        ttBleu.setFromX(-balloon1.getFitWidth());
        ttBleu.setToX(width);

        TranslateTransition ttFire = new TranslateTransition(Duration.seconds(11), balloon3);
        ttFire.setFromX(width + balloon3.getFitWidth());
        ttFire.setToX(-balloon3.getFitWidth());

        TranslateTransition ttRouge = new TranslateTransition(Duration.seconds(9), balloon2);
        ttRouge.setFromX(width + balloon2.getFitWidth());
        ttRouge.setToX(-balloon2.getFitWidth());

        // --- Séquencement ---
        ttBleu.setOnFinished(e -> ttFire.play());
        ttFire.setOnFinished(e -> ttRouge.play());

        ttBleu.play();
    }

    private void startGame() {
        SceneManager.switchScene("Game");
    }
}
