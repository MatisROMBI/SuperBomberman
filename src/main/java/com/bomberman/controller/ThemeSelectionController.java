/**
 * Contrôleur pour la sélection et gestion des thèmes visuels
 * Permet de prévisualiser, appliquer, créer et supprimer des thèmes
 * Interface avec aperçus des sprites et informations détaillées
 */
package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import com.bomberman.utils.ThemeData;
import com.bomberman.utils.ThemeManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.Optional;

public class ThemeSelectionController {

    // ===== ÉLÉMENTS DE L'INTERFACE FXML =====
    @FXML private VBox themeContainer;           // Conteneur principal
    @FXML private ListView<String> themeListView; // Liste des thèmes disponibles
    @FXML private Label themeNameLabel;          // Nom du thème sélectionné
    @FXML private Label themeDescriptionLabel;   // Description du thème
    @FXML private GridPane spritePreviewGrid;    // Grille d'aperçu des sprites

    // Boutons d'action
    @FXML private Button applyThemeButton;       // Appliquer le thème
    @FXML private Button createThemeButton;      // Créer un nouveau thème
    @FXML private Button deleteThemeButton;      // Supprimer le thème
    @FXML private Button backButton;             // Retour menu principal

    // ImageView pour les aperçus de sprites
    @FXML private ImageView playerPreview1;      // Aperçu joueur 1
    @FXML private ImageView playerPreview2;      // Aperçu joueur 2
    @FXML private ImageView enemyPreview1;       // Aperçu ennemi Bomber
    @FXML private ImageView enemyPreview2;       // Aperçu ennemi Yellow
    @FXML private ImageView bombPreview;         // Aperçu bombe
    @FXML private ImageView wallPreview;         // Aperçu mur destructible
    @FXML private ImageView powerUpPreview;      // Aperçu power-up

    // ===== GESTIONNAIRE ET ÉTAT =====
    private final ThemeManager themeManager = ThemeManager.getInstance();
    private String selectedTheme;                // Thème actuellement sélectionné

    // New fields for the ThemeSelectionActionListener
    public interface ThemeSelectionActionListener {
        void onThemeSelected(String themeName);
        void onBack();
    }

    private ThemeSelectionActionListener actionListener;
    private Button classicThemeButton;
    private Button modernThemeButton;
    private Button retroThemeButton;
    private StackPane themeSelection;

    /**
     * Initialisation du contrôleur de sélection de thèmes
     */
    @FXML
    private void initialize() {
        setupControls();
        loadThemeList();
        selectCurrentTheme();
    }

    /**
     * Configuration des contrôles de l'interface
     */
    private void setupControls() {
        // Configuration des boutons d'action
        applyThemeButton.setOnAction(e -> applySelectedTheme());
        createThemeButton.setOnAction(e -> createNewTheme());
        deleteThemeButton.setOnAction(e -> deleteSelectedTheme());
        backButton.setOnAction(e -> SceneManager.switchScene("MainMenu"));

        // Désactivation par défaut des boutons contextuels
        applyThemeButton.setDisable(true);
        deleteThemeButton.setDisable(true);

        // Configuration de la sélection de thèmes
        setupThemeSelection();
    }

    /**
     * Configuration du système de sélection de thèmes
     */
    private void setupThemeSelection() {
        themeListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTheme = newSelection;
                showThemePreview(newSelection);  // Affiche l'aperçu

                // Active le bouton d'application
                applyThemeButton.setDisable(false);

                // Désactive la suppression pour les thèmes par défaut
                boolean isDefaultTheme = "classic".equals(newSelection) || "legend".equals(newSelection);
                deleteThemeButton.setDisable(isDefaultTheme);
            } else {
                selectedTheme = null;
                clearThemePreview();
                applyThemeButton.setDisable(true);
                deleteThemeButton.setDisable(true);
            }
        });
    }

    /**
     * Charge la liste de tous les thèmes disponibles
     */
    private void loadThemeList() {
        Map<String, ThemeData> themes = themeManager.getAvailableThemes();
        themeListView.getItems().clear();

        // Ajoute les thèmes par défaut en premier
        if (themes.containsKey("classic")) {
            themeListView.getItems().add("classic");
        }
        if (themes.containsKey("legend")) {
            themeListView.getItems().add("legend");
        }

        // Ajoute les autres thèmes triés alphabétiquement
        themes.keySet().stream()
                .filter(id -> !"classic".equals(id) && !"legend".equals(id))
                .sorted()
                .forEach(themeListView.getItems()::add);
    }

    /**
     * Sélectionne automatiquement le thème actuellement actif
     */
    private void selectCurrentTheme() {
        String currentTheme = themeManager.getCurrentTheme();
        themeListView.getSelectionModel().select(currentTheme);
    }

    /**
     * Affiche l'aperçu complet d'un thème sélectionné
     * @param themeId Identifiant du thème à prévisualiser
     */
    private void showThemePreview(String themeId) {
        ThemeData theme = themeManager.getAvailableThemes().get(themeId);
        if (theme == null) return;

        // Affichage des informations du thème
        themeNameLabel.setText(theme.getName());
        themeDescriptionLabel.setText(theme.getDescription());

        // Mise à jour des aperçus de sprites
        updateSpritePreview(theme);
    }

    /**
     * Met à jour tous les aperçus de sprites pour un thème
     * @param theme Données du thème à prévisualiser
     */
    private void updateSpritePreview(ThemeData theme) {
        // ===== APERÇUS DES JOUEURS =====
        if (theme.getPlayerSprites().size() > 0) {
            setImagePreview(playerPreview1, theme.getPlayerSprites().get(0));
        }
        if (theme.getPlayerSprites().size() > 1) {
            setImagePreview(playerPreview2, theme.getPlayerSprites().get(1));
        }

        // ===== APERÇUS DES ENNEMIS =====
        Map<String, String> enemies = theme.getEnemySprites();
        if (enemies.containsKey("bomber")) {
            setImagePreview(enemyPreview1, enemies.get("bomber"));
        }
        if (enemies.containsKey("yellow")) {
            setImagePreview(enemyPreview2, enemies.get("yellow"));
        }

        // ===== APERÇU DE LA BOMBE =====
        setImagePreview(bombPreview, theme.getBombSprite());

        // ===== APERÇU DU MUR DESTRUCTIBLE =====
        setImagePreview(wallPreview, theme.getDestructibleWallSprite());

        // ===== APERÇU D'UN POWER-UP =====
        Map<String, String> powerUps = theme.getPowerUpSprites();
        if (powerUps.containsKey("EXTRA_BOMB")) {
            setImagePreview(powerUpPreview, powerUps.get("EXTRA_BOMB"));
        }
    }

    /**
     * Configure l'aperçu d'une image dans un ImageView
     * @param imageView L'ImageView à configurer
     * @param imagePath Chemin vers l'image à afficher
     */
    private void setImagePreview(ImageView imageView, String imagePath) {
        if (imageView != null && imagePath != null) {
            try {
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                imageView.setImage(image);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
            } catch (Exception e) {
                // Si l'image n'existe pas, affiche une image par défaut ou vide
                imageView.setImage(null);
                System.err.println("Impossible de charger l'aperçu : " + imagePath);
            }
        }
    }

    /**
     * Efface tous les aperçus de thème
     */
    private void clearThemePreview() {
        themeNameLabel.setText("Sélectionnez un thème");
        themeDescriptionLabel.setText("");

        // Efface tous les aperçus d'images
        if (playerPreview1 != null) playerPreview1.setImage(null);
        if (playerPreview2 != null) playerPreview2.setImage(null);
        if (enemyPreview1 != null) enemyPreview1.setImage(null);
        if (enemyPreview2 != null) enemyPreview2.setImage(null);
        if (bombPreview != null) bombPreview.setImage(null);
        if (wallPreview != null) wallPreview.setImage(null);
        if (powerUpPreview != null) powerUpPreview.setImage(null);
    }

    /**
     * Applique le thème sélectionné comme thème actif
     */
    private void applySelectedTheme() {
        if (selectedTheme != null) {
            themeManager.setCurrentTheme(selectedTheme);

            // Affichage d'une confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thème appliqué");
            alert.setHeaderText(null);
            alert.setContentText("Le thème '" + themeManager.getAvailableThemes().get(selectedTheme).getName() +
                    "' a été appliqué avec succès !\nIl sera utilisé dans toutes les parties.");
            alert.showAndWait();
        }
    }

    /**
     * Ouvre le dialogue de création d'un nouveau thème
     */
    private void createNewTheme() {
        // Ouvre le dialogue de création
        Optional<ThemeData> result = showThemeCreationDialog();

        if (result.isPresent()) {
            ThemeData newTheme = result.get();
            themeManager.addCustomTheme(newTheme);
            loadThemeList(); // Rafraîchit la liste
            themeListView.getSelectionModel().select(newTheme.getId());

            showAlert("Succès", "Nouveau thème '" + newTheme.getName() + "' créé avec succès !");
        }
    }

    /**
     * Affiche le dialogue de création de thème personnalisé
     * @return Optional contenant le nouveau thème ou vide si annulé
     */
    private Optional<ThemeData> showThemeCreationDialog() {
        Dialog<ThemeData> dialog = new Dialog<>();
        dialog.setTitle("Créer un nouveau thème");
        dialog.setHeaderText("Créer un thème personnalisé");

        // Configuration des boutons
        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Création du formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Champs du formulaire
        TextField themeId = new TextField();
        themeId.setPromptText("ID du thème (ex: montheme)");
        TextField themeName = new TextField();
        themeName.setPromptText("Nom du thème");
        TextArea themeDescription = new TextArea();
        themeDescription.setPromptText("Description du thème");
        themeDescription.setPrefRowCount(2);

        ComboBox<String> baseTheme = new ComboBox<>();
        baseTheme.getItems().addAll("classic", "legend", "retro", "futuristic");
        baseTheme.setValue("classic");
        baseTheme.setPromptText("Thème de base");

        // Ajout des champs à la grille
        grid.add(new Label("ID:"), 0, 0);
        grid.add(themeId, 1, 0);
        grid.add(new Label("Nom:"), 0, 1);
        grid.add(themeName, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(themeDescription, 1, 2);
        grid.add(new Label("Basé sur:"), 0, 3);
        grid.add(baseTheme, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Validation en temps réel
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        // Fonction de validation
        Runnable validation = () -> {
            boolean valid = !themeId.getText().trim().isEmpty() && !themeName.getText().trim().isEmpty();
            createButton.setDisable(!valid);
        };

        // Listeners pour validation automatique
        themeId.textProperty().addListener((obs, oldText, newText) -> validation.run());
        themeName.textProperty().addListener((obs, oldText, newText) -> validation.run());

        // Conversion du résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String id = themeId.getText().trim().toLowerCase();
                String name = themeName.getText().trim();
                String description = themeDescription.getText().trim();
                String base = baseTheme.getValue();

                if (!id.isEmpty() && !name.isEmpty()) {
                    ThemeData newTheme = new ThemeData(id, name, description.isEmpty() ? "Thème personnalisé" : description);

                    // Copie depuis le thème de base
                    ThemeData baseThemeData = themeManager.getAvailableThemes().get(base);
                    if (baseThemeData != null) {
                        newTheme.copyFrom(baseThemeData);
                    }

                    return newTheme;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Supprime le thème sélectionné après confirmation
     */
    private void deleteSelectedTheme() {
        if (selectedTheme == null) return;

        // Empêche la suppression des thèmes par défaut
        if ("classic".equals(selectedTheme) || "legend".equals(selectedTheme)) {
            showAlert("Erreur", "Impossible de supprimer les thèmes par défaut.");
            return;
        }

        // Confirmation de suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer le thème");
        confirmation.setHeaderText("Suppression du thème");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le thème '" +
                themeManager.getAvailableThemes().get(selectedTheme).getName() + "' ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (themeManager.deleteTheme(selectedTheme)) {
                loadThemeList();
                selectCurrentTheme();
                showAlert("Succès", "Thème supprimé avec succès !");
            } else {
                showAlert("Erreur", "Erreur lors de la suppression du thème.");
            }
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

    public void setActionListener(ThemeSelectionActionListener listener) {
        this.actionListener = listener;
    }

    public void showThemeSelection() {
        themeSelection.setVisible(true);
        classicThemeButton.requestFocus();
    }

    public void hideThemeSelection() {
        themeSelection.setVisible(false);
    }

    public boolean isThemeSelectionVisible() {
        return themeSelection.isVisible();
    }
}