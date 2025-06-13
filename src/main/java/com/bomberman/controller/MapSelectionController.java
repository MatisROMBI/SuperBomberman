/**
 * Contrôleur pour la sélection de cartes avant de jouer
 * Permet de choisir entre carte par défaut et cartes personnalisées
 * Sélection du mode de jeu (Robot Survivor vs Legend 1v1)
 */
package com.bomberman.controller;

import com.bomberman.model.MapData;
import com.bomberman.utils.MapManager;
import com.bomberman.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

import java.util.List;

public class MapSelectionController {

    // ===== ÉLÉMENTS DE L'INTERFACE FXML =====
    @FXML private VBox mapSelectionContainer;    // Conteneur principal
    @FXML private ListView<String> mapListView;  // Liste des cartes disponibles
    @FXML private TextArea mapPreviewArea;       // Zone d'aperçu textuel de la carte
    @FXML private Label mapInfoLabel;            // Informations sur la carte sélectionnée
    @FXML private Button playButton;             // Bouton de lancement du jeu
    @FXML private Button editButton;             // Bouton vers l'éditeur
    @FXML private Button backButton;             // Bouton retour menu principal
    @FXML private RadioButton classicModeRadio; // Mode Robot Survivor
    @FXML private RadioButton legendModeRadio;   // Mode Legend 1v1
    @FXML private StackPane mapSelection;

    // ===== GESTIONNAIRES ET ÉTAT =====
    private final MapManager mapManager = new MapManager(); // Gestionnaire de cartes
    private ToggleGroup gameModeGroup;                      // Groupe des boutons radio
    private String selectedGameMode = "classic";           // Mode sélectionné par défaut

    private MapSelectionActionListener actionListener;
    private Button map1Button;
    private Button map2Button;
    private Button map3Button;

    /**
     * Initialisation du contrôleur de sélection de cartes
     */
    @FXML
    private void initialize() {
        setupControls();
        loadAvailableMaps();
        setupMapSelection();
    }

    /**
     * Configuration des contrôles de l'interface
     */
    private void setupControls() {
        // ===== CONFIGURATION DES MODES DE JEU =====
        gameModeGroup = new ToggleGroup();
        classicModeRadio.setToggleGroup(gameModeGroup);
        legendModeRadio.setToggleGroup(gameModeGroup);
        classicModeRadio.setSelected(true); // Mode classique par défaut

        // Listener pour changement de mode
        gameModeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == classicModeRadio) {
                selectedGameMode = "classic";
            } else if (newToggle == legendModeRadio) {
                selectedGameMode = "legend";
            }
        });

        // ===== CONFIGURATION DES BOUTONS =====
        playButton.setOnAction(e -> startGame());
        editButton.setOnAction(e -> SceneManager.switchScene("LevelEditor"));
        backButton.setOnAction(e -> SceneManager.switchScene("MainMenu"));

        // Désactivation du bouton jouer par défaut
        playButton.setDisable(true);
    }

    /**
     * Chargement de toutes les cartes disponibles
     */
    private void loadAvailableMaps() {
        List<String> maps = mapManager.getAvailableMaps();

        // Ajout de la carte par défaut en premier
        mapListView.getItems().add("Carte par défaut");

        // Ajout des cartes personnalisées
        mapListView.getItems().addAll(maps);

        // Message si aucune carte personnalisée
        if (mapListView.getItems().isEmpty()) {
            mapInfoLabel.setText("Aucune carte disponible. Créez-en une dans l'éditeur !");
        } else {
            mapInfoLabel.setText("Sélectionnez une carte pour jouer");
        }
    }

    /**
     * Configuration de la sélection interactive des cartes
     */
    private void setupMapSelection() {
        mapListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                playButton.setDisable(false); // Active le bouton jouer
                showMapPreview(newSelection);  // Affiche l'aperçu
            } else {
                playButton.setDisable(true);   // Désactive le bouton jouer
                clearMapPreview();             // Efface l'aperçu
            }
        });
    }

    /**
     * Affiche l'aperçu d'une carte sélectionnée
     * @param mapName Nom de la carte à prévisualiser
     */
    private void showMapPreview(String mapName) {
        if ("Carte par défaut".equals(mapName)) {
            // Aperçu de la carte générée automatiquement
            mapPreviewArea.setText(generateDefaultMapPreview());
            mapInfoLabel.setText("Carte générée automatiquement avec layout classique Bomberman");
        } else {
            try {
                // Aperçu d'une carte personnalisée
                String mapInfo = mapManager.getMapInfo(mapName);
                mapInfoLabel.setText(mapInfo);

                // Génération de l'aperçu textuel
                MapData mapData = mapManager.loadMap(mapName);
                mapPreviewArea.setText(generateMapPreview(mapData));

            } catch (Exception e) {
                mapInfoLabel.setText("Erreur lors du chargement de la carte : " + e.getMessage());
                mapPreviewArea.setText("Aperçu non disponible");
            }
        }
    }

    /**
     * Efface l'aperçu de carte
     */
    private void clearMapPreview() {
        mapPreviewArea.clear();
        mapInfoLabel.setText("Sélectionnez une carte pour voir l'aperçu");
    }

    /**
     * Génère un aperçu textuel de la carte par défaut
     * @return Représentation ASCII de la carte par défaut
     */
    private String generateDefaultMapPreview() {
        StringBuilder preview = new StringBuilder();
        preview.append("Carte par défaut - Layout classique Bomberman\n\n");

        // Simulation de la génération de carte par défaut
        for (int y = 0; y < 13; y++) {
            for (int x = 0; x < 15; x++) {
                if (x == 0 || y == 0 || x == 14 || y == 12 || (x % 2 == 0 && y % 2 == 0)) {
                    preview.append("#"); // Mur fixe
                } else if ((x == 1 && y == 1) || (x == 13 && y == 11) ||
                        (x == 13 && y == 1) || (x == 1 && y == 11)) {
                    preview.append("S"); // Zone de spawn
                } else {
                    // 70% destructible, 30% vide (simulé)
                    preview.append(Math.random() < 0.7 ? "X" : ".");
                }
            }
            preview.append("\n");
        }

        preview.append("\nLégende: # = Mur fixe, X = Destructible, . = Vide, S = Spawn");
        return preview.toString();
    }

    /**
     * Génère un aperçu textuel d'une carte personnalisée
     * @param mapData Données de la carte à prévisualiser
     * @return Représentation ASCII de la carte
     */
    private String generateMapPreview(MapData mapData) {
        StringBuilder preview = new StringBuilder();
        preview.append("Aperçu de ").append(mapData.getName()).append("\n\n");

        for (int y = 0; y < com.bomberman.utils.Constants.BOARD_HEIGHT; y++) {
            for (int x = 0; x < com.bomberman.utils.Constants.BOARD_WIDTH; x++) {
                // Marquage spécial des zones de spawn
                if ((x == 1 && y == 1) || (x == 13 && y == 11) ||
                        (x == 13 && y == 1) || (x == 1 && y == 11)) {
                    preview.append("S");
                } else {
                    // Représentation selon le type de cellule
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

    /**
     * Lance le jeu avec la carte et le mode sélectionnés
     */
    private void startGame() {
        String selectedMap = mapListView.getSelectionModel().getSelectedItem();
        if (selectedMap == null) {
            showAlert("Erreur", "Veuillez sélectionner une carte.");
            return;
        }

        // Sauvegarde de la carte sélectionnée pour les contrôleurs de jeu
        if (!"Carte par défaut".equals(selectedMap)) {
            // Stockage pour utilisation par les contrôleurs Game/LegendGame
            CustomMapHolder.setSelectedMap(selectedMap);
        } else {
            CustomMapHolder.setSelectedMap(null); // Utilise la génération par défaut
        }

        // **LANCEMENT DU BON MODE DE JEU**
        if ("legend".equals(selectedGameMode)) {
            System.out.println("Lancement du mode Legend 1v1 avec carte: " + selectedMap);
            SceneManager.switchScene("LegendGame"); // Mode Legend 1v1 (2 joueurs humains)
        } else {
            System.out.println("Lancement du mode Robot Survivor avec carte: " + selectedMap);
            SceneManager.switchScene("Game"); // Mode classique (1 joueur vs bots)
        }
    }

    /**
     * Affiche une alerte d'information
     * @param title Titre de l'alerte
     * @param message Message à afficher
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Classe utilitaire statique pour partager la carte sélectionnée
     * entre les contrôleurs MapSelection et Game/LegendGame
     */
    public static class CustomMapHolder {
        private static String selectedCustomMap = null;

        /**
         * Définit la carte personnalisée à utiliser
         * @param mapName Nom de la carte ou null pour la carte par défaut
         */
        public static void setSelectedMap(String mapName) {
            selectedCustomMap = mapName;
        }

        /**
         * @return Le nom de la carte sélectionnée ou null
         */
        public static String getSelectedMap() {
            return selectedCustomMap;
        }

        /**
         * @return true s'il y a une carte personnalisée sélectionnée
         */
        public static boolean hasCustomMap() {
            return selectedCustomMap != null;
        }
    }

    public void setActionListener(MapSelectionActionListener listener) {
        this.actionListener = listener;
    }

    public void showMapSelection() {
        mapSelection.setVisible(true);
        map1Button.requestFocus();
    }

    public void hideMapSelection() {
        mapSelection.setVisible(false);
    }

    public boolean isMapSelectionVisible() {
        return mapSelection.isVisible();
    }
}