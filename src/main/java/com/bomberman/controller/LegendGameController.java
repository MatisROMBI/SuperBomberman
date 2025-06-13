/**
 * Contrôleur spécialisé pour le mode Legend 1v1
 * Gère 2 joueurs humains simultanément + ennemis IA avancés
 * Contrôles : Joueur 1 (ZQSD+R) vs Joueur 2 (IJKL+P)
 */
package com.bomberman.controller;

import com.bomberman.model.Legend1v1Board;
import com.bomberman.model.Music;
import com.bomberman.model.Player;
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

public class LegendGameController implements PauseOverlayController.PauseActionListener {

    // ===== ÉLÉMENTS DE L'INTERFACE FXML =====
    @FXML private VBox gameContainer;      // Conteneur principal
    @FXML private Canvas gameCanvas;       // Canvas de rendu

    // ===== COMPOSANTS DE JEU LEGEND =====
    private Legend1v1Board board;          // Plateau spécialisé Legend
    private GameRenderer renderer;         // Moteur de rendu
    private final Set<String> pressedKeys = new HashSet<>(); // Touches pressées
    private AnimationTimer renderLoop;     // Boucle de rendu
    private boolean gameOver = false;      // État de fin de partie
    private boolean victory = false;       // État de victoire
    private final Music music = new Music(); // Gestionnaire audio

    // ===== GESTION DU MENU PAUSE =====
    private StackPane pauseOverlay;        // Overlay du menu pause
    private PauseOverlayController pauseController; // Contrôleur pause
    private boolean isPaused = false;      // État de pause

    // ===== OPTIMISATION DU RENDU =====
    private long lastRenderTime = 0;
    private static final long RENDER_INTERVAL = 16_666_666L; // 60 FPS

    // ===== IMPLÉMENTATION DE LEGENDGAMEACTIONLISTENER =====
    public interface LegendGameActionListener {
        void onStart();
        void onBack();
    }

    private LegendGameActionListener actionListener;
    private Button startButton;
    private Button backButton;
    private StackPane legendGame;

    public void setActionListener(LegendGameActionListener listener) {
        this.actionListener = listener;
    }

    public void showLegendGame() {
        legendGame.setVisible(true);
        startButton.requestFocus();
    }

    public void hideLegendGame() {
        legendGame.setVisible(false);
    }

    public boolean isLegendGameVisible() {
        return legendGame.isVisible();
    }

    /**
     * Initialisation du contrôleur Legend
     */
    @FXML
    private void initialize() {
        // Création du plateau Legend avec 2 joueurs + ennemis IA
        board = new Legend1v1Board();
        renderer = new GameRenderer(gameCanvas);

        // Configuration des systèmes
        setupKeyboardHandling();
        setupPauseOverlay();

        // Démarrage de la musique spéciale Legend
        music.demarrerLegendMusic();
        startRenderLoop();
    }

    /**
     * Configuration du menu pause (identique au mode classique)
     */
    private void setupPauseOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PauseOverlay.fxml"));
            pauseOverlay = loader.load();
            pauseController = loader.getController();
            pauseController.setActionListener(this);

            // Intégration dans la hiérarchie
            if (gameContainer.getParent() instanceof StackPane) {
                StackPane parent = (StackPane) gameContainer.getParent();
                parent.getChildren().add(pauseOverlay);
            } else {
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
     * Configuration de la gestion clavier pour 2 joueurs
     */
    private void setupKeyboardHandling() {
        gameContainer.setFocusTraversable(true);
        Platform.runLater(() -> {
            gameContainer.requestFocus();
            if (gameContainer.getScene() != null) {
                gameContainer.getScene().setOnKeyPressed(this::handleKeyPressed);
                gameContainer.getScene().setOnKeyReleased(this::handleKeyReleased);
            }
            gameContainer.setOnKeyPressed(this::handleKeyPressed);
            gameContainer.setOnKeyReleased(this::handleKeyReleased);
        });
        gameCanvas.setOnMouseClicked(e -> gameContainer.requestFocus());
    }

    /**
     * Gestion des touches pressées pour 2 joueurs simultanés
     * @param event Événement clavier
     */
    private void handleKeyPressed(KeyEvent event) {
        String keyCode = event.getCode().toString();

        // Gestion spéciale de la pause
        if ("ESCAPE".equals(keyCode)) {
            togglePause();
            event.consume();
            return;
        }

        // Traitement des contrôles de jeu si pas en pause
        if (!isPaused && !pressedKeys.contains(keyCode)) {
            pressedKeys.add(keyCode);
            processKeyAction(keyCode);
        }
        event.consume();
    }

    /**
     * Basculement pause/jeu
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
     * Met le jeu Legend en pause
     */
    private void pauseGame() {
        isPaused = true;
        music.arreterLegendMusic();      // Arrête la musique Legend
        pauseController.showPause();
    }

    /**
     * Reprend le jeu Legend
     */
    private void resumeGame() {
        isPaused = false;
        music.demarrerLegendMusic();     // Relance la musique Legend
        pauseController.hidePause();
        gameContainer.requestFocus();
    }

    /**
     * Traitement des actions pour les 2 joueurs
     * Joueur 1 : ZQSD + R (Blanc, haut-gauche)
     * Joueur 2 : IJKL + P (Noir, bas-droite)
     * @param keyCode Code de la touche
     */
    private void processKeyAction(String keyCode) {
        Player p1 = board.getPlayer1();  // Joueur Blanc
        Player p2 = board.getPlayer2();  // Joueur Noir

        switch (keyCode) {
            // ===== CONTRÔLES JOUEUR 1 (BLANC) - ZQSD + R =====
            case "Z": p1.moveUp(board); break;         // Haut
            case "S": p1.moveDown(board); break;       // Bas
            case "Q": p1.moveLeft(board); break;       // Gauche
            case "D": p1.moveRight(board); break;      // Droite
            case "R": p1.placeBomb(board); break;      // Bombe

            // ===== CONTRÔLES JOUEUR 2 (NOIR) - IJKL + P =====
            case "I": p2.moveUp(board); break;         // Haut
            case "K": p2.moveDown(board); break;       // Bas
            case "J": p2.moveLeft(board); break;       // Gauche
            case "L": p2.moveRight(board); break;      // Droite
            case "P": p2.placeBomb(board); break;      // Bombe

            default:
                // Touche non gérée
                break;
        }
    }

    /**
     * Gestion du relâchement des touches
     */
    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode().toString());
    }

    /**
     * Boucle de rendu spécialisée pour le mode Legend
     */
    private void startRenderLoop() {
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Rendu seulement si le jeu est actif
                if (!gameOver && !victory && !isPaused) {
                    if (now - lastRenderTime >= RENDER_INTERVAL) {
                        board.update();                    // Mise à jour du plateau Legend
                        renderer.renderLegend1v1(board);   // Rendu spécialisé Legend
                        lastRenderTime = now;
                    }
                    checkGameState();  // Vérification des conditions de fin
                }
            }
        };
        renderLoop.start();
    }

    /**
     * Vérification des conditions de fin de partie Legend
     * Gère les victoires individuelles et coopératives
     */
    private void checkGameState() {
        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        // ===== CAS 1: LES DEUX JOUEURS SONT MORTS =====
        if (!p1.isAlive() && !p2.isAlive()) {
            gameOver = true;
            music.arreterLegendMusic();
            // Prend le meilleur score des deux
            com.bomberman.controller.GameOverController.setLastScore(
                    Math.max(p1.getScore(), p2.getScore()));
            SceneManager.switchScene("GameOver");
            stopRenderLoop();
            return;
        }

        // ===== CAS 2: VICTOIRE DU JOUEUR 1 (BLANC) =====
        if (!p2.isAlive() && p1.isAlive()) {
            victory = true;
            music.arreterLegendMusic();
            p1.addScore(1000);  // Bonus de victoire
            com.bomberman.controller.VictoryController.LAST_SCORE = p1.getScore();
            com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 1 (Blanc)";
            SceneManager.switchScene("Victory");
            stopRenderLoop();
            return;
        }

        // ===== CAS 3: VICTOIRE DU JOUEUR 2 (NOIR) =====
        if (!p1.isAlive() && p2.isAlive()) {
            victory = true;
            music.arreterLegendMusic();
            p2.addScore(1000);  // Bonus de victoire
            com.bomberman.controller.VictoryController.LAST_SCORE = p2.getScore();
            com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 2 (Noir)";
            SceneManager.switchScene("Victory");
            stopRenderLoop();
            return;
        }

        // ===== CAS 4: VICTOIRE COOPÉRATIVE - TOUS LES ENNEMIS MORTS =====
        boolean allEnemiesDead = board.getBomberEnemies().stream().noneMatch(b -> b.isAlive()) &&
                board.getYellowEnemies().stream().noneMatch(y -> y.isAlive());

        if (allEnemiesDead && (p1.isAlive() || p2.isAlive())) {
            victory = true;
            music.arreterLegendMusic();

            // Attribution des points selon les scores
            if (p1.getScore() > p2.getScore()) {
                p1.addScore(500);
                com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 1 (Blanc) - Coopération";
                com.bomberman.controller.VictoryController.LAST_SCORE = p1.getScore();
            } else if (p2.getScore() > p1.getScore()) {
                p2.addScore(500);
                com.bomberman.controller.VictoryController.WINNER_NAME = "Joueur 2 (Noir) - Coopération";
                com.bomberman.controller.VictoryController.LAST_SCORE = p2.getScore();
            } else {
                // Égalité parfaite
                int sharedScore = Math.max(p1.getScore(), p2.getScore()) + 250;
                com.bomberman.controller.VictoryController.WINNER_NAME = "Égalité - Coopération parfaite";
                com.bomberman.controller.VictoryController.LAST_SCORE = sharedScore;
            }

            SceneManager.switchScene("Victory");
            stopRenderLoop();
        }
    }

    /**
     * Arrêt de la boucle de rendu
     */
    private void stopRenderLoop() {
        if (renderLoop != null) {
            renderLoop.stop();
            renderLoop = null;
        }
    }

    /**
     * Nettoyage des ressources Legend
     */
    public void cleanup() {
        stopRenderLoop();
        music.arreterLegendMusic();
    }

    // ===== IMPLÉMENTATION DE PAUSEACTIONLISTENER =====

    @Override
    public void onResume() {
        resumeGame();
    }

    @Override
    public void onRestart() {
        cleanup();
        // Recréation d'une partie Legend
        board = new Legend1v1Board();
        music.demarrerLegendMusic();
        startRenderLoop();
        gameOver = false;
        victory = false;
        isPaused = false;
        gameContainer.requestFocus();
    }

    @Override
    public void onMainMenu() {
        cleanup();
        SceneManager.switchScene("MainMenu");
    }
}
