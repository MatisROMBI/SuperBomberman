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
 * Contrôleur de l'éditeur de niveau
 * Permet de créer, modifier et sauvegarder des maps personnalisées
 */
public class LevelEditorController {
    @FXML private VBox editorContainer;
    @FXML private Canvas editorCanvas;
    @FXML private TextField mapNameField;
    @FXML private ComboBox<String> existingMapsCombo;
    @FXML private Button saveButton;
    @FXML private Button loadButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button backButton;
    @FXML private RadioButton emptyRadio;
    @FXML private RadioButton wallRadio;
    @FXML private RadioButton destructibleRadio;
    @FXML private Label instructionsLabel;

    private GraphicsContext gc;
    private CellType[][] grid;
    private CellType selectedCellType = CellType.EMPTY;
    private ToggleGroup cellTypeGroup;
    private final MapManager mapManager = new MapManager();

    @FXML
    private void initialize() {
        setupCanvas();
        setupControls();
        initializeGrid();
        loadExistingMaps();
        render();
    }

    private void setupCanvas() {
        gc = editorCanvas.getGraphicsContext2D();
        editorCanvas.setWidth(Constants.BOARD_WIDTH * Constants.CELL_SIZE);
        editorCanvas.setHeight(Constants.BOARD_HEIGHT * Constants.CELL_SIZE);

        editorCanvas.setOnMouseClicked(this::handleCanvasClick);
        editorCanvas.setOnMouseDragged(this::handleCanvasDrag);
    }

    private void setupControls() {
        // Configuration du groupe de boutons radio
        cellTypeGroup = new ToggleGroup();
        emptyRadio.setToggleGroup(cellTypeGroup);
        wallRadio.setToggleGroup(cellTypeGroup);
        destructibleRadio.setToggleGroup(cellTypeGroup);
        emptyRadio.setSelected(true);

        // Listeners pour les boutons radio
        cellTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == emptyRadio) selectedCellType = CellType.EMPTY;
            else if (newToggle == wallRadio) selectedCellType = CellType.WALL;
            else if (newToggle == destructibleRadio) selectedCellType = CellType.DESTRUCTIBLE_WALL;
        });

        // Configuration des boutons
        saveButton.setOnAction(e -> saveMap());
        loadButton.setOnAction(e -> loadMap());
        deleteButton.setOnAction(e -> deleteMap());
        clearButton.setOnAction(e -> clearGrid());
        backButton.setOnAction(e -> SceneManager.switchScene("MainMenu"));

        // Instructions
        instructionsLabel.setText(
                "Clic gauche: Placer le type sélectionné | Clic droit: Vider la case | Glisser: Dessiner"
        );
    }

    private void initializeGrid() {
        grid = new CellType[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];

        // Initialisation avec un modèle de base
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                // Bordures = murs fixes
                if (x == 0 || y == 0 || x == Constants.BOARD_WIDTH - 1 ||
                        y == Constants.BOARD_HEIGHT - 1 || (x % 2 == 0 && y % 2 == 0)) {
                    grid[x][y] = CellType.WALL;
                } else {
                    grid[x][y] = CellType.EMPTY;
                }
            }
        }

        // Assurer les coins de spawn libres
        ensureSpawnAreasAreFree();
    }

    private void ensureSpawnAreasAreFree() {
        // Coin haut-gauche (spawn joueur 1)
        grid[1][1] = CellType.EMPTY;
        grid[1][2] = CellType.EMPTY;
        grid[2][1] = CellType.EMPTY;

        // Coin bas-droite (spawn joueur 2)
        int maxX = Constants.BOARD_WIDTH - 2;
        int maxY = Constants.BOARD_HEIGHT - 2;
        grid[maxX][maxY] = CellType.EMPTY;
        grid[maxX - 1][maxY] = CellType.EMPTY;
        grid[maxX][maxY - 1] = CellType.EMPTY;

        // Coin haut-droite (spawn bot 1)
        grid[maxX][1] = CellType.EMPTY;
        grid[maxX - 1][1] = CellType.EMPTY;
        grid[maxX][2] = CellType.EMPTY;

        // Coin bas-gauche (spawn bot 2)
        grid[1][maxY] = CellType.EMPTY;
        grid[2][maxY] = CellType.EMPTY;
        grid[1][maxY - 1] = CellType.EMPTY;
    }

    private void handleCanvasClick(MouseEvent event) {
        int x = (int) (event.getX() / Constants.CELL_SIZE);
        int y = (int) (event.getY() / Constants.CELL_SIZE);

        if (isValidPosition(x, y)) {
            if (event.getButton() == MouseButton.PRIMARY) {
                // Empêcher de modifier les zones de spawn critiques
                if (!isProtectedSpawnArea(x, y)) {
                    grid[x][y] = selectedCellType;
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                if (!isProtectedSpawnArea(x, y)) {
                    grid[x][y] = CellType.EMPTY;
                }
            }
            render();
        }
    }

    private void handleCanvasDrag(MouseEvent event) {
        handleCanvasClick(event); // Même logique que le clic
    }

    private boolean isProtectedSpawnArea(int x, int y) {
        // Protection minimale des positions de spawn exactes
        return (x == 1 && y == 1) || // P1
                (x == Constants.BOARD_WIDTH - 2 && y == Constants.BOARD_HEIGHT - 2) || // P2
                (x == Constants.BOARD_WIDTH - 2 && y == 1) || // Bot 1
                (x == 1 && y == Constants.BOARD_HEIGHT - 2); // Bot 2
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < Constants.BOARD_WIDTH && y >= 0 && y < Constants.BOARD_HEIGHT;
    }

    private void render() {
        gc.clearRect(0, 0, editorCanvas.getWidth(), editorCanvas.getHeight());

        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                double pixelX = x * Constants.CELL_SIZE;
                double pixelY = y * Constants.CELL_SIZE;

                // Couleur selon le type de cellule
                switch (grid[x][y]) {
                    case WALL:
                        gc.setFill(Color.DARKGRAY);
                        break;
                    case DESTRUCTIBLE_WALL:
                        gc.setFill(Color.SADDLEBROWN);
                        break;
                    case EMPTY:
                    default:
                        gc.setFill(Color.LIGHTGREEN);
                        break;
                }

                gc.fillRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);

                // Contour
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);

                // Marquer les zones de spawn protégées
                if (isProtectedSpawnArea(x, y)) {
                    gc.setFill(Color.BLUE);
                    gc.fillOval(pixelX + 5, pixelY + 5, Constants.CELL_SIZE - 10, Constants.CELL_SIZE - 10);
                }
            }
        }
    }

    private void saveMap() {
        String mapName = mapNameField.getText().trim();
        if (mapName.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un nom pour la map.");
            return;
        }

        try {
            MapData mapData = new MapData(mapName, grid);
            mapManager.saveMap(mapData);
            showAlert("Succès", "Map '" + mapName + "' sauvegardée avec succès !");
            loadExistingMaps(); // Rafraîchir la liste
            mapNameField.clear();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    private void loadMap() {
        String selectedMap = existingMapsCombo.getValue();
        if (selectedMap == null) {
            showAlert("Erreur", "Veuillez sélectionner une map à charger.");
            return;
        }

        try {
            MapData mapData = mapManager.loadMap(selectedMap);
            if (mapData != null) {
                grid = mapData.getGrid();
                render();
                mapNameField.setText(mapData.getName());
                showAlert("Succès", "Map '" + selectedMap + "' chargée avec succès !");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement : " + e.getMessage());
        }
    }

    private void deleteMap() {
        String selectedMap = existingMapsCombo.getValue();
        if (selectedMap == null) {
            showAlert("Erreur", "Veuillez sélectionner une map à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la map");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer '" + selectedMap + "' ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                mapManager.deleteMap(selectedMap);
                loadExistingMaps();
                showAlert("Succès", "Map '" + selectedMap + "' supprimée avec succès !");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    private void clearGrid() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Effacer la grille");
        confirmation.setContentText("Êtes-vous sûr de vouloir effacer toute la grille ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            initializeGrid();
            render();
        }
    }

    private void loadExistingMaps() {
        List<String> mapNames = mapManager.getAvailableMaps();
        existingMapsCombo.getItems().clear();
        existingMapsCombo.getItems().addAll(mapNames);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}