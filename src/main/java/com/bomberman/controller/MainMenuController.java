package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;

/**
 * Contrôleur du menu principal de Bomberman
 * Gère l'affichage du menu, les animations et la navigation vers les différents modes de jeu
 */
public class MainMenuController {
    public interface MainMenuActionListener {
        void onPlay();
        void onEditor();
        void onLegend();
        void onThemes();
        void onQuit();
    }

    private MainMenuActionListener actionListener;

    // ==================== ÉLÉMENTS DE L'INTERFACE ====================

    @FXML private ImageView backgroundImage;  // Image de fond du menu
    @FXML private ImageView balloon1;         // Premier ballon animé
    @FXML private ImageView balloon2;         // Deuxième ballon animé
    @FXML private ImageView balloon3;         // Troisième ballon animé

    // Boutons du menu principal
    @FXML private Button robotSurvivorButton; // Mode Robot Survivor (1 joueur vs IA)
    @FXML private Button legend1v1Button;     // Mode Legend 1v1 (2 joueurs humains)
    @FXML private Button levelEditorButton;   // Éditeur de niveau
    @FXML private Button themesButton;        // Sélection de thèmes
    @FXML private Button quitButton;          // Quitter le jeu

    @FXML private StackPane mainMenu;

    // Lecteur de musique pour le menu
    private MediaPlayer menuMusicPlayer;

    /**
     * Méthode d'initialisation appelée automatiquement après le chargement du FXML
     * Configure l'interface, démarre la musique et lance les animations
     */
    @FXML
    private void initialize() {
        try {
            // Chargement des images de fond et des ballons décoratifs
            backgroundImage.setImage(new Image(getClass().getResourceAsStream("/images/menu_bg.png")));
            balloon1.setImage(new Image(getClass().getResourceAsStream("/images/dirigeable_bleu.png")));
            balloon2.setImage(new Image(getClass().getResourceAsStream("/images/ballon_rouge.png")));
            balloon3.setImage(new Image(getClass().getResourceAsStream("/images/fire_dirigeable.png")));
        } catch (Exception e) {
            // En cas d'erreur de chargement d'image, afficher l'erreur dans la console
            e.printStackTrace();
        }

        // Démarrage de la musique de fond du menu
        playMenuMusic();

        // Configuration des actions des boutons
        setupButtonActions();

        // Lancement des animations des ballons
        animateBalloons();
    }

    /**
     * Configure les actions de tous les boutons du menu principal
     */
    private void setupButtonActions() {
        // ===== MODE ROBOT SURVIVOR =====
        // Lance le mode classique : 1 joueur humain contre des robots IA
        // Redirige d'abord vers la sélection de map pour choisir le terrain
        robotSurvivorButton.setOnAction(e -> {
            System.out.println("🤖 Mode Robot Survivor sélectionné -> Sélection de map");
            stopMenuMusic(); // Arrêter la musique du menu
            SceneManager.switchScene("MapSelection"); // Aller à la sélection de map
        });

        // ===== MODE LEGEND 1V1 =====
        // Lance directement le mode 2 joueurs humains (mode Legend)
        // Pas de sélection de map, utilise la map par défaut
        legend1v1Button.setOnAction(e -> {
            System.out.println("⚔️ Mode Legend 1v1 sélectionné -> LegendGame DIRECT (2 joueurs humains)");
            stopMenuMusic(); // Arrêter la musique du menu
            SceneManager.switchScene("LegendGame"); // Aller directement au jeu Legend
        });

        // ===== ÉDITEUR DE NIVEAU =====
        // Ouvre l'éditeur pour créer/modifier des maps personnalisées
        levelEditorButton.setOnAction(e -> {
            System.out.println("✏️ Éditeur de niveau sélectionné");
            stopMenuMusic(); // Arrêter la musique du menu
            SceneManager.switchScene("LevelEditor"); // Aller à l'éditeur
        });

        // ===== SÉLECTION DE THÈMES =====
        // Ouvre l'interface de gestion des thèmes visuels
        themesButton.setOnAction(e -> {
            System.out.println("🎨 Sélection de thèmes");
            stopMenuMusic(); // Arrêter la musique du menu
            SceneManager.switchScene("ThemeSelection"); // Aller à la sélection de thèmes
        });

        // ===== QUITTER =====
        // Ferme complètement l'application
        quitButton.setOnAction(e -> {
            System.out.println("👋 Fermeture du jeu");
            System.exit(0); // Fermer l'application
        });

        // Ajouter des effets visuels supplémentaires aux boutons
        addButtonEffects();
    }

    /**
     * Ajoute des effets visuels aux boutons pour améliorer l'expérience utilisateur
     */
    private void addButtonEffects() {
        // Tableau contenant tous les boutons pour appliquer les effets
        Button[] buttons = {robotSurvivorButton, legend1v1Button, levelEditorButton, themesButton, quitButton};

        // Appliquer les effets à chaque bouton
        for (Button button : buttons) {
            // Effet de focus au survol de la souris
            button.setOnMouseEntered(e -> button.requestFocus());

            // Animation de "pressage" quand on clique
            button.setOnMousePressed(e -> {
                button.setScaleX(0.95); // Réduire légèrement la taille horizontale
                button.setScaleY(0.95); // Réduire légèrement la taille verticale
            });

            // Retour à la taille normale quand on relâche
            button.setOnMouseReleased(e -> {
                button.setScaleX(1.0); // Taille normale horizontale
                button.setScaleY(1.0); // Taille normale verticale
            });
        }
    }

    /**
     * Démarre la lecture de la musique de fond du menu
     */
    private void playMenuMusic() {
        try {
            // Chercher le fichier de musique dans les ressources
            URL resource = getClass().getResource("/sons/menu_music.mp3");
            if (resource == null) {
                System.err.println("Musique menu introuvable !");
                return; // Sortir si le fichier n'existe pas
            }

            // Créer et configurer le lecteur de musique
            Media media = new Media(resource.toString());
            menuMusicPlayer = new MediaPlayer(media);
            menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Répéter en boucle
            menuMusicPlayer.play(); // Démarrer la lecture
        } catch (Exception e) {
            // Afficher l'erreur si la musique ne peut pas être lue
            System.err.println("Erreur de lecture musique menu : " + e.getMessage());
        }
    }

    /**
     * Arrête la musique du menu (appelé avant de changer de scène)
     */
    private void stopMenuMusic() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.stop(); // Arrêter la lecture
        }
    }

    /**
     * Lance les animations des ballons qui traversent l'écran
     * Crée un effet visuel dynamique pour rendre le menu plus vivant
     */
    private void animateBalloons() {
        double width = 799; // Largeur de l'écran

        // ===== ANIMATION DU BALLON BLEU (balloon1) =====
        // Positionner le ballon hors écran à gauche
        balloon1.setTranslateX(-balloon1.getFitWidth());
        balloon1.setTranslateY(100); // Hauteur verticale

        // Créer l'animation de translation de gauche à droite
        TranslateTransition ttBleu = new TranslateTransition(Duration.seconds(7), balloon1);
        ttBleu.setFromX(-balloon1.getFitWidth()); // Commencer hors écran à gauche
        ttBleu.setToX(width); // Finir hors écran à droite

        // ===== ANIMATION DU BALLON DE FEU (balloon3) =====
        // Positionner le ballon hors écran à droite
        balloon3.setTranslateX(width + balloon3.getFitWidth());
        balloon3.setTranslateY(170); // Hauteur verticale différente

        // Créer l'animation de translation de droite à gauche
        TranslateTransition ttFire = new TranslateTransition(Duration.seconds(9), balloon3);
        ttFire.setFromX(width + balloon3.getFitWidth()); // Commencer hors écran à droite
        ttFire.setToX(-balloon3.getFitWidth()); // Finir hors écran à gauche

        // ===== ANIMATION DU BALLON ROUGE (balloon2) =====
        // Positionner le ballon hors écran à droite
        balloon2.setTranslateX(width + balloon2.getFitWidth());
        balloon2.setTranslateY(320); // Hauteur verticale encore différente

        // Créer l'animation de translation de droite à gauche
        TranslateTransition ttRouge = new TranslateTransition(Duration.seconds(8), balloon2);
        ttRouge.setFromX(width + balloon2.getFitWidth()); // Commencer hors écran à droite
        ttRouge.setToX(-balloon2.getFitWidth()); // Finir hors écran à gauche

        // ===== DÉMARRAGE DES ANIMATIONS AVEC DÉLAIS =====

        // Démarrer immédiatement le ballon bleu
        ttBleu.play();

        // Démarrer le ballon de feu après 2 secondes de délai
        PauseTransition pauseFire = new PauseTransition(Duration.seconds(2));
        pauseFire.setOnFinished(e -> ttFire.play());
        pauseFire.play();

        // Démarrer le ballon rouge après 4 secondes de délai
        PauseTransition pauseRouge = new PauseTransition(Duration.seconds(4));
        pauseRouge.setOnFinished(e -> ttRouge.play());
        pauseRouge.play();
    }

    public void setActionListener(MainMenuActionListener listener) {
        this.actionListener = listener;
    }

    public void showMainMenu() {
        mainMenu.setVisible(true);
        robotSurvivorButton.requestFocus();
    }

    public void hideMainMenu() {
        mainMenu.setVisible(false);
    }

    public boolean isMainMenuVisible() {
        return mainMenu.isVisible();
    }
}