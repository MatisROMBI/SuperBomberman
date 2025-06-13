/**
 * Contrôleur principal du mode de jeu classique (1 joueur vs 3 bots)
 * Gère les entrées clavier, la boucle de rendu et le menu pause
 * Implémente les interfaces pour la gestion des fins de partie et du menu pause
 */
package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.model.GameOverListener;
import com.bomberman.model.Music;
import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;
import com.bomberman.utils.SceneManager;
import com.bomberman.view.GameRenderer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GameController implements GameOverListener, PauseOverlayController.PauseActionListener {

    // ===== ÉLÉMENTS DE L'INTERFACE FXML =====
    @FXML private VBox gameContainer;      // Conteneur principal du jeu
    @FXML private Canvas gameCanvas;       // Canvas pour le rendu graphique

    // ===== COMPOSANTS DE JEU =====
    private Game game;                     // Instance du jeu
    private GameRenderer renderer;         // Moteur de rendu
    private Set<String> pressedKeys;       // Ensemble des touches actuellement pressées
    private AnimationTimer renderLoop;     // Boucle de rendu principal
    private final Music music = new Music(); // Gestionnaire audio

    // ===== GESTION DU MENU PAUSE =====
    private StackPane pauseOverlay;        // Overlay du menu pause
    private PauseOverlayController pauseController; // Contrôleur du menu pause
    private boolean isPaused = false;      // État de pause du jeu

    // ===== OPTIMISATION DU RENDU =====
    private long lastRenderTime = 0;       // Timestamp du dernier rendu
    private static final long RENDER_INTERVAL = 16_666_666L; // Intervalle pour 60 FPS

    public interface GameActionListener {
        void onPause();
        void onResume();
        void onRestart();
        void onMainMenu();
    }

    private GameActionListener actionListener;
    private Button pauseButton;
    private Button resumeButton;
    private Button restartButton;
    private Button mainMenuButton;
    private StackPane game;

    /**
     * Initialisation du contrôleur appelée automatiquement après chargement FXML
     */
    @FXML
    private void initialize() {
        // Création et configuration du jeu
        game = new Game();
        music.arreterGameOverMusique();  // Arrête la musique de game over si elle jouait
        music.demarrerMusique();         // Démarre la musique de jeu
        game.getPlayer().setGameOverListener(this); // Configure le callback de fin de partie

        // Initialisation du rendu et des contrôles
        renderer = new GameRenderer(gameCanvas);
        pressedKeys = new HashSet<>();

        // Configuration des systèmes
        setupKeyboardHandling();
        setupPauseOverlay();
        startRenderLoop();
    }

    /**
     * Configuration du système de menu pause
     * Charge l'overlay FXML et l'intègre dans la hiérarchie des composants
     */
    private void setupPauseOverlay() {
        try {
            // Chargement du FXML du menu pause
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PauseOverlay.fxml"));
            pauseOverlay = loader.load();
            pauseController = loader.getController();
            pauseController.setActionListener(this); // Configure les callbacks

            // Intégration dans la hiérarchie des composants
            if (gameContainer.getParent() instanceof StackPane) {
                // Si déjà dans un StackPane, ajoute directement
                StackPane parent = (StackPane) gameContainer.getParent();
                parent.getChildren().add(pauseOverlay);
            } else {
                // Crée un nouveau StackPane pour superposer les éléments
                StackPane stackPane = new StackPane();
                VBox originalParent = (VBox) gameContainer.getParent();
                originalParent.getChildren().remove(gameContainer);
                stackPane.getChildren().addAll(gameContainer, pauseOverlay);
                originalParent.getChildren().add(stackPane);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du menu de pause : " + e.getMessage());
        }
    }

    /**
     * Configuration de la gestion des événements clavier
     * Met en place les listeners pour les touches pressées et relâchées
     */
    private void setupKeyboardHandling() {
        gameContainer.setFocusTraversable(true);

        // Configuration asynchrone pour s'assurer que la scène est prête
        Platform.runLater(() -> {
            gameContainer.requestFocus();

            // Configuration des listeners au niveau de la scène si disponible
            if (gameContainer.getScene() != null) {
                gameContainer.getScene().setOnKeyPressed(this::handleKeyPressed);
                gameContainer.getScene().setOnKeyReleased(this::handleKeyReleased);
            }

            // Configuration des listeners au niveau du conteneur
            gameContainer.setOnKeyPressed(this::handleKeyPressed);
            gameContainer.setOnKeyReleased(this::handleKeyReleased);
        });

        // Clic sur le canvas pour récupérer le focus
        gameCanvas.setOnMouseClicked(e -> gameContainer.requestFocus());
    }

    /**
     * Gestion optimisée des touches pressées
     * Utilise un Set pour éviter les répétitions et traite la pause spécialement
     * @param event Événement clavier
     */
    private void handleKeyPressed(KeyEvent event) {
        String keyCode = event.getCode().toString();

        // Gestion spéciale de la touche ÉCHAP pour la pause
        if ("ESCAPE".equals(keyCode)) {
            togglePause();
            event.consume();
            return;
        }

        // Traitement des autres touches seulement si pas en pause
        if (!isPaused && pressedKeys.add(keyCode)) {  // add() renvoie true si nouvel élément
            processKeyAction(keyCode);
        }
        event.consume();
    }

    /**
     * Basculement entre l'état de pause et de jeu
     */
    private void togglePause() {
        if (pauseController != null) {
            if (isPaused) {
                resumeGame();
            } else {
                pauseGame();
            }
        }
    }

    /**
     * Met le jeu en pause
     * Arrête la musique et affiche le menu pause
     */
    private void pauseGame() {
        isPaused = true;
        game.pause();                    // Met le jeu en pause
        music.arreterMusique();          // Arrête la musique
        pauseController.showPause();     // Affiche le menu pause
    }

    /**
     * Reprend le jeu depuis la pause
     * Relance la musique et cache le menu pause
     */
    private void resumeGame() {
        isPaused = false;
        game.pause();                    // Bascule l'état de pause du jeu
        music.demarrerMusique();         // Relance la musique
        pauseController.hidePause();     // Cache le menu pause
        gameContainer.requestFocus();    // Redonne le focus pour les contrôles
    }

    /**
     * Traitement des actions correspondant aux touches pressées
     * Gère les mouvements du joueur et les actions (pose de bombe)
     * @param keyCode Code de la touche pressée
     */
    private void processKeyAction(String keyCode) {
        switch (keyCode) {
            // Mouvements - Support ZQSD et flèches directionnelles
            case "UP": case "Z":
                game.getPlayer().move(Direction.UP, game.getBoard(), game.getGameState());
                break;
            case "DOWN": case "S":
                game.getPlayer().move(Direction.DOWN, game.getBoard(), game.getGameState());
                break;
            case "LEFT": case "Q":
                game.getPlayer().move(Direction.LEFT, game.getBoard(), game.getGameState());
                break;
            case "RIGHT": case "D":
                game.getPlayer().move(Direction.RIGHT, game.getBoard(), game.getGameState());
                break;

            // Action - Pose de bombe
            case "SPACE":
                game.getPlayer().placeBomb(game.getBoard(), game.getGameState());
                break;

            default:
                // Touche non gérée, ne fait rien
                break;
        }
    }

    /**
     * Gestion du relâchement des touches
     * Retire la touche de l'ensemble des touches pressées
     * @param event Événement clavier
     */
    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode().toString());
    }

    /**
     * Démarre la boucle de rendu optimisée à 60 FPS
     * Utilise un AnimationTimer pour synchroniser avec le rafraîchissement d'écran
     */
    private void startRenderLoop() {
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Limitation à 60 FPS pour optimiser les performances
                if (now - lastRenderTime >= RENDER_INTERVAL) {
                    renderer.render(game);  // Rendu du jeu
                    lastRenderTime = now;
                }

                // Vérification des conditions d'arrêt
                if (game.getGameState() == GameState.GAME_OVER ||
                        game.getGameState() == GameState.VICTORY) {
                    music.arreterMusique();
                    stop(); // Arrête l'AnimationTimer
                }
            }
        };
        renderLoop.start();
    }

    /**
     * Nettoyage des ressources du contrôleur
     * Arrête la boucle de rendu et libère les ressources du jeu
     */
    public void cleanup() {
        if (renderLoop != null) renderLoop.stop();
        if (game != null) game.stop();
    }

    // ===== IMPLÉMENTATION DE GAMEOVERLISTENER =====

    /**
     * Callback appelé quand le joueur perd toutes ses vies
     * Déclenche la transition vers l'écran Game Over
     * @param score Score final du joueur
     */
    @Override
    public void onGameOver(int score) {
        music.arreterMusique();
        com.bomberman.controller.GameOverController.setLastScore(score);
        music.demarrerGameOverMusique();  // Musique spéciale game over
        SceneManager.switchScene("GameOver");
    }

    // ===== IMPLÉMENTATION DE PAUSEACTIONLISTENER =====

    /**
     * Action de reprise depuis le menu pause
     */
    @Override
    public void onResume() {
        resumeGame();
    }

    /**
     * Action de redémarrage depuis le menu pause
     * Recrée une nouvelle partie complètement
     */
    @Override
    public void onRestart() {
        cleanup();  // Nettoie la partie actuelle

        // Création d'une nouvelle partie
        game = new Game();
        game.getPlayer().setGameOverListener(this);
        music.demarrerMusique();
        startRenderLoop();

        // Réinitialisation des états
        isPaused = false;
        gameContainer.requestFocus();
    }

    /**
     * Action de retour au menu principal depuis le menu pause
     */
    @Override
    public void onMainMenu() {
        cleanup();
        music.arreterMusique();
        SceneManager.switchScene("MainMenu");
    }

    public void setActionListener(GameActionListener listener) {
        this.actionListener = listener;
    }

    public void showGame() {
        game.setVisible(true);
        pauseButton.requestFocus();
    }

    public void hideGame() {
        game.setVisible(false);
    }

    public boolean isGameVisible() {
        return game.isVisible();
    }
}
