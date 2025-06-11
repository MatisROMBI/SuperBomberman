package com.bomberman.controller;

/**
 * Contrôleur du menu principal.
 * Gère les boutons, la musique et les animations du menu.
 */

import com.bomberman.utils.SceneManager;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;

public class MainMenuController {
    @FXML private ImageView backgroundImage;
    @FXML private ImageView balloon1, balloon2, balloon3;
    @FXML private ImageView robotSurvivorButtonImg, legend1v1ButtonImg, levelEditorButtonImg, themesButtonImg, quitButtonImg;

    private MediaPlayer menuMusicPlayer;

    @FXML
    private void initialize() {
        loadImages();
        playMenuMusic();
        setupButtonActions();
        animateBalloons();
    }

    // Charge les images du menu
    private void loadImages() {
        try {
            backgroundImage.setImage(new Image(getClass().getResourceAsStream("/images/menu_bg.png")));
            balloon1.setImage(new Image(getClass().getResourceAsStream("/images/dirigeable_bleu.png")));
            balloon2.setImage(new Image(getClass().getResourceAsStream("/images/ballon_rouge.png")));
            balloon3.setImage(new Image(getClass().getResourceAsStream("/images/fire_dirigeable.png")));
            robotSurvivorButtonImg.setImage(new Image(getClass().getResourceAsStream("/images/ROBOT-SURVIVOR.png")));
            legend1v1ButtonImg.setImage(new Image(getClass().getResourceAsStream("/images/1V1_LEGEND.png")));
            levelEditorButtonImg.setImage(new Image(getClass().getResourceAsStream("/images/LEVEL_EDITOR.png")));
            themesButtonImg.setImage(new Image(getClass().getResourceAsStream("/images/THEMES.png")));
            quitButtonImg.setImage(new Image(getClass().getResourceAsStream("/images/QUIT_text.png")));
        } catch (Exception e) {
            System.err.println("Erreur chargement images menu : " + e.getMessage());
        }
    }

    // Gère les clics sur les boutons du menu
    private void setupButtonActions() {
        robotSurvivorButtonImg.setOnMouseClicked(e -> {
            stopMenuMusic();
            SceneManager.switchScene("MapSelection");
        });

        legend1v1ButtonImg.setOnMouseClicked(e -> {
            stopMenuMusic();
            SceneManager.switchScene("LegendGame");
        });

        levelEditorButtonImg.setOnMouseClicked(e -> {
            stopMenuMusic();
            SceneManager.switchScene("LevelEditor");
        });

        themesButtonImg.setOnMouseClicked(e -> {
            stopMenuMusic();
            SceneManager.switchScene("ThemeSelection");
        });

        quitButtonImg.setOnMouseClicked(e -> System.exit(0));
    }

    // Lance la musique du menu
    private void playMenuMusic() {
        try {
            URL resource = getClass().getResource("/sons/menu_music.mp3");
            if (resource == null) return;
            Media media = new Media(resource.toString());
            menuMusicPlayer = new MediaPlayer(media);
            menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            menuMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Erreur musique menu : " + e.getMessage());
        }
    }

    // Arrête la musique du menu
    private void stopMenuMusic() {
        if (menuMusicPlayer != null) menuMusicPlayer.stop();
    }

    // Anime les ballons du menu principal
    private void animateBalloons() {
        double width = 799;

        balloon1.setTranslateX(-balloon1.getFitWidth());
        balloon1.setTranslateY(100);
        TranslateTransition ttBleu = new TranslateTransition(Duration.seconds(7), balloon1);
        ttBleu.setFromX(-balloon1.getFitWidth());
        ttBleu.setToX(width);

        balloon3.setTranslateX(width + balloon3.getFitWidth());
        balloon3.setTranslateY(170);
        TranslateTransition ttFire = new TranslateTransition(Duration.seconds(9), balloon3);
        ttFire.setFromX(width + balloon3.getFitWidth());
        ttFire.setToX(-balloon3.getFitWidth());

        balloon2.setTranslateX(width + balloon2.getFitWidth());
        balloon2.setTranslateY(320);
        TranslateTransition ttRouge = new TranslateTransition(Duration.seconds(8), balloon2);
        ttRouge.setFromX(width + balloon2.getFitWidth());
        ttRouge.setToX(-balloon2.getFitWidth());

        ttBleu.play();

        PauseTransition pauseFire = new PauseTransition(Duration.seconds(2));
        pauseFire.setOnFinished(e -> ttFire.play());
        pauseFire.play();

        PauseTransition pauseRouge = new PauseTransition(Duration.seconds(4));
        pauseRouge.setOnFinished(e -> ttRouge.play());
        pauseRouge.play();
    }
}
