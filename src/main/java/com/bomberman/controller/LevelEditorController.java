package com.bomberman.controller;

import com.bomberman.model.MapData;
import com.bomberman.model.enums.CellType;
import com.bomberman.utils.Constants;
import com.bomberman.utils.MapManager;
import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Contrôleur de l'éditeur de niveau pour Bomberman
 * Permet de créer, modifier et sauvegarder des maps personnalisées
 * Interface graphique avec canvas pour dessiner et outils de gestion
 */
public class LevelEditorController {

    // ==================== ÉLÉMENTS DE L'INTERFACE ====================

    @FXML private VBox editorContainer;        // Container principal de l'éditeur
    @FXML private Canvas editorCanvas;         // Canvas pour dessiner la map
    @FXML private TextField mapNameField;      // Champ de saisie du nom de la map
    @FXML private ComboBox<String> existingMapsCombo; // Liste des maps existantes

    // Boutons de gestion des maps
    @FXML private Button saveButton;           // Sauvegarder la map actuelle
    @FXML private Button loadButton;           // Charger une map existante
    @FXML private Button deleteButton;         // Supprimer une map
    @FXML private Button clearButton;          // Effacer toute la grille
    @FXML private Button backButton;           // Retour au menu principal

    // Boutons radio pour sélectionner le type de cellule à dessiner
    @FXML private RadioButton emptyRadio;      // Case vide
    @FXML private RadioButton wallRadio;       // Mur fixe (indestructible)
    @FXML private RadioButton destructibleRadio; // Mur destructible

    @FXML private Label instructionsLabel;     // Label d'instructions pour l'utilisateur

    // ==================== VARIABLES DE CLASSE ====================

    private GraphicsContext gc;                // Contexte graphique pour dessiner sur le canvas
    private CellType[][] grid;                 // Grille représentant la map (données)
    private CellType selectedCellType = CellType.EMPTY; // Type de cellule actuellement sélectionné
    private ToggleGroup cellTypeGroup;         // Groupe pour les boutons radio (un seul sélectionné)
    private final MapManager mapManager = new MapManager(); // Gestionnaire pour sauvegarder/charger les maps

    /**
     * Méthode d'initialisation appelée automatiquement après le chargement du FXML
     * Configure tous les composants de l'éditeur
     */
    @FXML
    private void initialize() {
        setupCanvas();           // Configurer le canvas de dessin
        setupControls();         // Configurer les boutons et contrôles
        initializeGrid();        // Créer une grille de base
        loadExistingMaps();      // Charger la liste des maps existantes
        render();                // Afficher la grille initiale
    }

    /**
     * Configure le canvas de dessin et ses événements de souris
     */
    private void setupCanvas() {
        // Obtenir le contexte graphique pour dessiner
        gc = editorCanvas.getGraphicsContext2D();

        // Définir la taille du canvas en fonction des constantes du jeu
        editorCanvas.setWidth(Constants.BOARD_WIDTH * Constants.CELL_SIZE);
        editorCanvas.setHeight(Constants.BOARD_HEIGHT * Constants.CELL_SIZE);

        // Configurer les événements de souris pour dessiner
        editorCanvas.setOnMouseClicked(this::handleCanvasClick);   // Clic simple
        editorCanvas.setOnMouseDragged(this::handleCanvasDrag);    // Glisser pour dessiner
    }

    /**
     * Configure tous les boutons, contrôles et leurs actions
     */
    private void setupControls() {
        // ===== CONFIGURATION DES BOUTONS RADIO =====
        // Créer un groupe pour que seul un bouton radio soit sélectionné à la fois
        cellTypeGroup = new ToggleGroup();
        emptyRadio.setToggleGroup(cellTypeGroup);
        wallRadio.setToggleGroup(cellTypeGroup);
        destructibleRadio.setToggleGroup(cellTypeGroup);
        emptyRadio.setSelected(true); // Sélectionner "vide" par défaut

        // Écouter les changements de sélection des boutons radio
        cellTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            // Mettre à jour le type de cellule sélectionné selon le bouton radio choisi
            if (newToggle == emptyRadio) selectedCellType = CellType.EMPTY;
            else if (newToggle == wallRadio) selectedCellType = CellType.WALL;
            else if (newToggle == destructibleRadio) selectedCellType = CellType.DESTRUCTIBLE_WALL;
        });

        // ===== CONFIGURATION DES BOUTONS D'ACTION =====
        saveButton.setOnAction(e -> saveMap());        // Sauvegarder la map
        loadButton.setOnAction(e -> loadMap());        // Charger une map
        deleteButton.setOnAction(e -> deleteMap());    // Supprimer une map
        clearButton.setOnAction(e -> clearGrid());     // Effacer la grille
        backButton.setOnAction(e -> SceneManager.switchScene("MainMenu")); // Retour menu

        // ===== TEXTE D'INSTRUCTIONS =====
        instructionsLabel.setText(
                "Clic gauche: Placer le type sélectionné | Clic droit: Vider la case | Glisser: Dessiner"
        );
    }

    /**
     * Initialise la grille avec un modèle de base (style Bomberman classique)
     */
    private void initializeGrid() {
        // Créer une nouvelle grille vide
        grid = new CellType[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];

        // Remplir la grille avec le pattern classique de Bomberman
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                // Bordures et intersections paires = murs fixes
                if (x == 0 || y == 0 || x == Constants.BOARD_WIDTH - 1 ||
                        y == Constants.BOARD_HEIGHT - 1 || (x % 2 == 0 && y % 2 == 0)) {
                    grid[x][y] = CellType.WALL; // Mur fixe
                } else {
                    grid[x][y] = CellType.EMPTY; // Case vide
                }
            }
        }

        // S'assurer que les zones de spawn des joueurs sont libres
        ensureSpawnAreasAreFree();
    }

    /**
     * Garantit que les 4 coins (zones de spawn des joueurs) sont libres de tout obstacle
     */
    private void ensureSpawnAreasAreFree() {
        // === COIN HAUT-GAUCHE (spawn joueur 1) ===
        grid[1][1] = CellType.EMPTY; // Position exacte du joueur
        grid[1][2] = CellType.EMPTY; // Case en dessous
        grid[2][1] = CellType.EMPTY; // Case à droite

        // === COIN BAS-DROITE (spawn joueur 2) ===
        int maxX = Constants.BOARD_WIDTH - 2;
        int maxY = Constants.BOARD_HEIGHT - 2;
        grid[maxX][maxY] = CellType.EMPTY;     // Position exacte du joueur
        grid[maxX - 1][maxY] = CellType.EMPTY; // Case à gauche
        grid[maxX][maxY - 1] = CellType.EMPTY; // Case au dessus

        // === COIN HAUT-DROITE (spawn bot 1) ===
        grid[maxX][1] = CellType.EMPTY;        // Position exacte
        grid[maxX - 1][1] = CellType.EMPTY;    // Case à gauche
        grid[maxX][2] = CellType.EMPTY;        // Case en dessous

        // === COIN BAS-GAUCHE (spawn bot 2) ===
        grid[1][maxY] = CellType.EMPTY;        // Position exacte
        grid[2][maxY] = CellType.EMPTY;        // Case à droite
        grid[1][maxY - 1] = CellType.EMPTY;    // Case au dessus
    }

    /**
     * Gère les clics de souris sur le canvas pour modifier la grille
     * @param event Événement de clic de souris
     */
    private void handleCanvasClick(MouseEvent event) {
        // Convertir les coordonnées pixel en coordonnées de grille
        int x = (int) (event.getX() / Constants.CELL_SIZE);
        int y = (int) (event.getY() / Constants.CELL_SIZE);

        // Vérifier que les coordonnées sont valides
        if (isValidPosition(x, y)) {
            if (event.getButton() == MouseButton.PRIMARY) { // Clic gauche
                // Empêcher de modifier les zones de spawn critiques
                if (!isProtectedSpawnArea(x, y)) {
                    grid[x][y] = selectedCellType; // Placer le type sélectionné
                }
            } else if (event.getButton() == MouseButton.SECONDARY) { // Clic droit
                // Vider la case (sauf zones protégées)
                if (!isProtectedSpawnArea(x, y)) {
                    grid[x][y] = CellType.EMPTY;
                }
            }
            render(); // Redessiner la grille
        }
    }

    /**
     * Gère le glissement de souris pour dessiner en continu
     * @param event Événement de glissement de souris
     */
    private void handleCanvasDrag(MouseEvent event) {
        handleCanvasClick(event); // Utiliser la même logique que le clic
    }

    /**
     * Vérifie si une position est dans une zone de spawn protégée
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return true si la position est protégée
     */
    private boolean isProtectedSpawnArea(int x, int y) {
        // Protection minimale des positions de spawn exactes
        return (x == 1 && y == 1) || // Joueur 1
                (x == Constants.BOARD_WIDTH - 2 && y == Constants.BOARD_HEIGHT - 2) || // Joueur 2
                (x == Constants.BOARD_WIDTH - 2 && y == 1) || // Bot 1
                (x == 1 && y == Constants.BOARD_HEIGHT - 2); // Bot 2
    }

    /**
     * Vérifie si les coordonnées sont dans les limites de la grille
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return true si les coordonnées sont valides
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < Constants.BOARD_WIDTH && y >= 0 && y < Constants.BOARD_HEIGHT;
    }

    /**
     * Dessine la grille sur le canvas avec des couleurs différentes selon le type de cellule
     */
    private void render() {
        // Effacer le canvas
        gc.clearRect(0, 0, editorCanvas.getWidth(), editorCanvas.getHeight());

        // Parcourir toute la grille et dessiner chaque cellule
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                // Calculer les coordonnées pixel de la cellule
                double pixelX = x * Constants.CELL_SIZE;
                double pixelY = y * Constants.CELL_SIZE;

                // Choisir la couleur selon le type de cellule
                switch (grid[x][y]) {
                    case WALL:
                        gc.setFill(Color.DARKGRAY); // Gris foncé pour les murs fixes
                        break;
                    case DESTRUCTIBLE_WALL:
                        gc.setFill(Color.SADDLEBROWN); // Marron pour les murs destructibles
                        break;
                    case EMPTY:
                    default:
                        gc.setFill(Color.LIGHTGREEN); // Vert clair pour les cases vides
                        break;
                }

                // Dessiner le rectangle coloré
                gc.fillRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);

                // Dessiner le contour noir
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);

                // Marquer les zones de spawn protégées avec un cercle bleu
                if (isProtectedSpawnArea(x, y)) {
                    gc.setFill(Color.BLUE);
                    gc.fillOval(pixelX + 5, pixelY + 5, Constants.CELL_SIZE - 10, Constants.CELL_SIZE - 10);
                }
            }
        }
    }

    /**
     * Sauvegarde la map actuelle dans un fichier
     */
    private void saveMap() {
        // Récupérer le nom de la map saisi par l'utilisateur
        String mapName = mapNameField.getText().trim();
        if (mapName.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un nom pour la map.");
            return;
        }

        try {
            // Créer un objet MapData avec le nom et la grille
            MapData mapData = new MapData(mapName, grid);

            // Sauvegarder via le MapManager
            mapManager.saveMap(mapData);

            // Confirmer le succès à l'utilisateur
            showAlert("Succès", "Map '" + mapName + "' sauvegardée avec succès !");

            // Rafraîchir la liste des maps et vider le champ de nom
            loadExistingMaps();
            mapNameField.clear();
        } catch (Exception e) {
            // Afficher l'erreur en cas de problème
            showAlert("Erreur", "Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Charge une map existante sélectionnée dans la liste
     */
    private void loadMap() {
        // Récupérer la map sélectionnée dans la liste déroulante
        String selectedMap = existingMapsCombo.getValue();
        if (selectedMap == null) {
            showAlert("Erreur", "Veuillez sélectionner une map à charger.");
            return;
        }

        try {
            // Charger la map via le MapManager
            MapData mapData = mapManager.loadMap(selectedMap);
            if (mapData != null) {
                // Remplacer la grille actuelle par celle de la map chargée
                grid = mapData.getGrid();

                // Redessiner la grille et remplir le champ de nom
                render();
                mapNameField.setText(mapData.getName());

                // Confirmer le succès
                showAlert("Succès", "Map '" + selectedMap + "' chargée avec succès !");
            }
        } catch (Exception e) {
            // Afficher l'erreur en cas de problème
            showAlert("Erreur", "Erreur lors du chargement : " + e.getMessage());
        }
    }

    /**
     * Supprime une map existante après confirmation
     */
    private void deleteMap() {
        // Récupérer la map sélectionnée
        String selectedMap = existingMapsCombo.getValue();
        if (selectedMap == null) {
            showAlert("Erreur", "Veuillez sélectionner une map à supprimer.");
            return;
        }

        // Demander confirmation à l'utilisateur
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la map");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer '" + selectedMap + "' ?");

        // Si l'utilisateur confirme
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Supprimer la map via le MapManager
                mapManager.deleteMap(selectedMap);

                // Rafraîchir la liste des maps
                loadExistingMaps();

                // Confirmer le succès
                showAlert("Succès", "Map '" + selectedMap + "' supprimée avec succès !");
            } catch (Exception e) {
                // Afficher l'erreur en cas de problème
                showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    /**
     * Efface toute la grille après confirmation de l'utilisateur
     */
    private void clearGrid() {
        // Demander confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Effacer la grille");
        confirmation.setContentText("Êtes-vous sûr de vouloir effacer toute la grille ?");

        // Si l'utilisateur confirme
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Réinitialiser la grille avec le modèle de base
            initializeGrid();

            // Redessiner la grille
            render();
        }
    }

    /**
     * Charge la liste des maps existantes dans la liste déroulante
     */
    private void loadExistingMaps() {
        // Récupérer la liste des noms de maps depuis le MapManager
        List<String> mapNames = mapManager.getAvailableMaps();

        // Vider et remplir la liste déroulante
        existingMapsCombo.getItems().clear();
        existingMapsCombo.getItems().addAll(mapNames);
    }

    /**
     * Affiche une boîte de dialogue avec un message à l'utilisateur
     * @param title Titre de la boîte de dialogue
     * @param message Message à afficher
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Pas de header
        alert.setContentText(message);
        alert.showAndWait(); // Attendre que l'utilisateur ferme la boîte de dialogue
    }
}