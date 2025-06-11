package com.bomberman.controller;

import com.bomberman.model.MapData;
import com.bomberman.utils.MapManager;
import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Contrôleur pour la sélection de map avant de jouer
 */
public class MapSelectionController {
    @FXML private VBox mapSelectionContainer;
    @FXML private ListView<String> mapListView;
    @FXML private TextArea mapPreviewArea;
    @FXML private Label mapInfoLabel;
    @FXML private Button playButton;
    @FXML private Button editButton;
    @FXML private Button backButton;
    @FXML private RadioButton classicModeRadio;
    @FXML private RadioButton legendModeRadio;

    private final MapManager mapManager = new MapManager();
    private ToggleGroup gameModeGroup;
    private String selectedGameMode = "classic";

    @FXML
    private void initialize() {
        setupControls();
        loadAvailableMaps();
        setupMapSelection();
    }

    private void setupControls() {
        // Configuration des modes de jeu
        gameModeGroup = new ToggleGroup();
        classicModeRadio.setToggleGroup(gameModeGroup);
        legendModeRadio.setToggleGroup(gameModeGroup);
        classicModeRadio.setSelected(true);

        gameModeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == classicModeRadio) {
                selectedGameMode = "classic";
            } else if (newToggle == legendModeRadio) {
                selectedGameMode = "legend";
            }
        });

        // Configuration des boutons
        playButton.setOnAction(e -> startGame());
        editButton.setOnAction(e -> SceneManager.switchScene("LevelEditor"));
        backButton.setOnAction(e -> SceneManager.switchScene("MainMenu"));

        // Désactiver le bouton play par défaut
        playButton.setDisable(true);
    }

    private void loadAvailableMaps() {
        List<String> maps = mapManager.getAvailableMaps();

        // Ajouter les maps par défaut
        mapListView.getItems().add("Map par défaut");

        // Ajouter les maps personnalisées
        mapListView.getItems().addAll(maps);

        if (mapListView.getItems().isEmpty()) {
            mapInfoLabel.setText("Aucune map disponible. Créez-en une dans l'éditeur !");
        } else {
            mapInfoLabel.setText("Sélectionnez une map pour jouer");
        }
    }

    private void setupMapSelection() {
        mapListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                playButton.setDisable(false);
                showMapPreview(newSelection);
            } else {
                playButton.setDisable(true);
                clearMapPreview();
            }
        });
    }

    private void showMapPreview(String mapName) {
        if ("Map par défaut".equals(mapName)) {
            mapPreviewArea.setText(generateDefaultMapPreview());
            mapInfoLabel.setText("Map générée automatiquement avec layout classique Bomberman");
        } else {
            try {
                String mapInfo = mapManager.getMapInfo(mapName);
                mapInfoLabel.setText(mapInfo);

                // Générer un aperçu textuel de la map
                MapData mapData = mapManager.loadMap(mapName);
                mapPreviewArea.setText(generateMapPreview(mapData));

            } catch (Exception e) {
                mapInfoLabel.setText("Erreur lors du chargement de la map : " + e.getMessage());
                mapPreviewArea.setText("Aperçu non disponible");
            }
        }
    }

    private void clearMapPreview() {
        mapPreviewArea.clear();
        mapInfoLabel.setText("Sélectionnez une map pour voir l'aperçu");
    }

    private String generateDefaultMapPreview() {
        StringBuilder preview = new StringBuilder();
        preview.append("Map par défaut - Layout classique Bomberman\n\n");

        // Simuler l'aperçu de la map par défaut
        for (int y = 0; y < 13; y++) {
            for (int x = 0; x < 15; x++) {
                if (x == 0 || y == 0 || x == 14 || y == 12 || (x % 2 == 0 && y % 2 == 0)) {
                    preview.append("#"); // Mur fixe
                } else if ((x == 1 && y == 1) || (x == 13 && y == 11) ||
                        (x == 13 && y == 1) || (x == 1 && y == 11)) {
                    preview.append("S"); // Spawn
                } else {
                    preview.append(Math.random() < 0.7 ? "X" : "."); // Destructible ou vide
                }
            }
            preview.append("\n");
        }

        preview.append("\nLégende: # = Mur fixe, X = Destructible, . = Vide, S = Spawn");
        return preview.toString();
    }

    private String generateMapPreview(MapData mapData) {
        StringBuilder preview = new StringBuilder();
        preview.append("Aperçu de ").append(mapData.getName()).append("\n\n");

        for (int y = 0; y < com.bomberman.utils.Constants.BOARD_HEIGHT; y++) {
            for (int x = 0; x < com.bomberman.utils.Constants.BOARD_WIDTH; x++) {
                // Marquer les zones de spawn
                if ((x == 1 && y == 1) || (x == 13 && y == 11) ||
                        (x == 13 && y == 1) || (x == 1 && y == 11)) {
                    preview.append("S");
                } else {
                    switch (mapData.getGrid()[x][y]) {
                        case WALL:
                            preview.append("#");
                            break;
                        case DESTRUCTIBLE_WALL:
                            preview.append("X");
                            break;
                        case EMPTY:
                            preview.append(".");
                            break;
                        default:
                            preview.append("?");
                            break;
                    }
                }
            }
            preview.append("\n");
        }

        preview.append("\nLégende: # = Mur fixe, X = Destructible, . = Vide, S = Spawn");
        return preview.toString();
    }

    private void startGame() {
        String selectedMap = mapListView.getSelectionModel().getSelectedItem();
        if (selectedMap == null) {
            showAlert("Erreur", "Veuillez sélectionner une map.");
            return;
        }

        // Sauvegarder la map sélectionnée pour les contrôleurs de jeu
        if (!"Map par défaut".equals(selectedMap)) {
            // Stocker la map personnalisée pour les contrôleurs
            CustomMapHolder.setSelectedMap(selectedMap);
        } else {
            CustomMapHolder.setSelectedMap(null); // Utiliser la génération par défaut
        }

        // **CORRECTION** : Lancer le bon mode de jeu selon la sélection
        if ("legend".equals(selectedGameMode)) {
            System.out.println("Lancement du mode Legend 1v1 avec map: " + selectedMap);
            SceneManager.switchScene("LegendGame"); // **=> Mode Legend 1v1 (2 joueurs humains)**
        } else {
            System.out.println("Lancement du mode Robot Survivor avec map: " + selectedMap);
            SceneManager.switchScene("Game"); // **=> Mode classique (1 joueur vs bots)**
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Classe utilitaire pour partager la map sélectionnée entre contrôleurs
     */
    public static class CustomMapHolder {
        private static String selectedCustomMap = null;

        public static void setSelectedMap(String mapName) {
            selectedCustomMap = mapName;
        }

        public static String getSelectedMap() {
            return selectedCustomMap;
        }

        public static boolean hasCustomMap() {
            return selectedCustomMap != null;
        }
    }
}