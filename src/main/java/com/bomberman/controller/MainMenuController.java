package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;

public class MainMenuController {
    @FXML private ImageView backgroundImage;
    @FXML private ImageView balloon1;
    @FXML private ImageView balloon2;
    @FXML private ImageView balloon3;

    // Nouveaux boutons FXML
    @FXML private Button robotSurvivorButton;
    @FXML private Button legend1v1Button;
    @FXML private Button levelEditorButton;
    @FXML private Button themesButton;
    @FXML private Button quitButton;

    private MediaPlayer menuMusicPlayer;

    @FXML
    private void initialize() {
        try {
            // Chargement des images de fond et ballons
            backgroundImage.setImage(new Image(getClass().getResourceAsStream("/images/menu_bg.png")));
            balloon1.setImage(new Image(getClass().getResourceAsStream("/images/dirigeable_bleu.png")));
            balloon2.setImage(new Image(getClass().getResourceAsStream("/images/ballon_rouge.png")));
            balloon3.setImage(new Image(getClass().getResourceAsStream("/images/fire_dirigeable.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        playMenuMusic();
        setupButtonActions();
        animateBalloons();
    }

    private void setupButtonActions() {
        // ===== MODE ROBOT SURVIVOR =====
        // Redirige vers la sélection de map pour choisir la map puis jouer en mode classique
        robotSurvivorButton.setOnAction(e -> {
            System.out.println("🤖 Mode Robot Survivor sélectionné -> Sélection de map");
            stopMenuMusic();
            SceneManager.switchScene("MapSelection");
        });

        // ===== MODE LEGEND 1V1 =====
        // Redirige DIRECTEMENT vers LegendGame (2 joueurs humains)
        legend1v1Button.setOnAction(e -> {
            System.out.println("⚔️ Mode Legend 1v1 sélectionné -> LegendGame DIRECT (2 joueurs humains)");
            stopMenuMusic();
            SceneManager.switchScene("LegendGame");
        });

        // ===== ÉDITEUR DE NIVEAU =====
        levelEditorButton.setOnAction(e -> {
            System.out.println("✏️ Éditeur de niveau sélectionné");
            stopMenuMusic();
            SceneManager.switchScene("LevelEditor");
        });

        // ===== SÉLECTION DE THÈMES =====
        themesButton.setOnAction(e -> {
            System.out.println("🎨 Sélection de thèmes");
            stopMenuMusic();
            SceneManager.switchScene("ThemeSelection");
        });

        // ===== QUITTER =====
        quitButton.setOnAction(e -> {
            System.out.println("👋 Fermeture du jeu");
            System.exit(0);
        });

        // Ajout d'effets visuels supplémentaires sur les boutons
        addButtonEffects();
    }

    /**
     * Ajoute des effets visuels supplémentaires aux boutons
     */
    private void addButtonEffects() {
        Button[] buttons = {robotSurvivorButton, legend1v1Button, levelEditorButton, themesButton, quitButton};

        for (Button button : buttons) {
            // Effet de focus au survol
            button.setOnMouseEntered(e -> button.requestFocus());

            // Animation légère au clic
            button.setOnMousePressed(e -> {
                button.setScaleX(0.95);
                button.setScaleY(0.95);
            });

            button.setOnMouseReleased(e -> {
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            });
        }
    }

    private void playMenuMusic() {
        try {
            URL resource = getClass().getResource("/sons/menu_music.mp3");
            if (resource == null) {
                System.err.println("Musique menu introuvable !");
                return;
            }
            Media media = new Media(resource.toString());
            menuMusicPlayer = new MediaPlayer(media);
            menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            menuMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Erreur de lecture musique menu : " + e.getMessage());
        }
    }

    private void stopMenuMusic() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.stop();
        }
    }

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