package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import com.bomberman.utils.ThemeData;
import com.bomberman.utils.ThemeManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur pour la sélection et gestion des thèmes
 */
public class ThemeSelectionController {
    @FXML private VBox themeContainer;
    @FXML private ListView<String> themeListView;
    @FXML private Label themeNameLabel;
    @FXML private Label themeDescriptionLabel;
    @FXML private GridPane spritePreviewGrid;
    @FXML private Button applyThemeButton;
    @FXML private Button createThemeButton;
    @FXML private Button deleteThemeButton;
    @FXML private Button backButton;
    @FXML private ImageView playerPreview1;
    @FXML private ImageView playerPreview2;
    @FXML private ImageView enemyPreview1;
    @FXML private ImageView enemyPreview2;
    @FXML private ImageView bombPreview;
    @FXML private ImageView wallPreview;
    @FXML private ImageView powerUpPreview;

    private final ThemeManager themeManager = ThemeManager.getInstance();
    private String selectedTheme;

    @FXML
    private void initialize() {
        setupControls();
        loadThemeList();
        selectCurrentTheme();
    }

    private void setupControls() {
        // Configuration des boutons
        applyThemeButton.setOnAction(e -> applySelectedTheme());
        createThemeButton.setOnAction(e -> createNewTheme());
        deleteThemeButton.setOnAction(e -> deleteSelectedTheme());
        backButton.setOnAction(e -> SceneManager.switchScene("MainMenu"));

        // Désactiver les boutons par défaut
        applyThemeButton.setDisable(true);
        deleteThemeButton.setDisable(true);

        // Configuration de la liste
        setupThemeSelection();
    }

    private void setupThemeSelection() {
        themeListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTheme = newSelection;
                showThemePreview(newSelection);

                // Activer/désactiver les boutons selon le thème sélectionné
                applyThemeButton.setDisable(false);
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

    private void loadThemeList() {
        Map<String, ThemeData> themes = themeManager.getAvailableThemes();
        themeListView.getItems().clear();

        // Ajouter les thèmes dans un ordre spécifique
        if (themes.containsKey("classic")) {
            themeListView.getItems().add("classic");
        }
        if (themes.containsKey("legend")) {
            themeListView.getItems().add("legend");
        }

        // Ajouter les autres thèmes
        themes.keySet().stream()
                .filter(id -> !"classic".equals(id) && !"legend".equals(id))
                .sorted()
                .forEach(themeListView.getItems()::add);
    }

    private void selectCurrentTheme() {
        String currentTheme = themeManager.getCurrentTheme();
        themeListView.getSelectionModel().select(currentTheme);
    }

    private void showThemePreview(String themeId) {
        ThemeData theme = themeManager.getAvailableThemes().get(themeId);
        if (theme == null) return;

        // Afficher les informations du thème
        themeNameLabel.setText(theme.getName());
        themeDescriptionLabel.setText(theme.getDescription());

        // Afficher les aperçus des sprites
        updateSpritePreview(theme);
    }

    private void updateSpritePreview(ThemeData theme) {
        // Aperçus des joueurs
        if (theme.getPlayerSprites().size() > 0) {
            setImagePreview(playerPreview1, theme.getPlayerSprites().get(0));
        }
        if (theme.getPlayerSprites().size() > 1) {
            setImagePreview(playerPreview2, theme.getPlayerSprites().get(1));
        }

        // Aperçus des ennemis
        Map<String, String> enemies = theme.getEnemySprites();
        if (enemies.containsKey("bomber")) {
            setImagePreview(enemyPreview1, enemies.get("bomber"));
        }
        if (enemies.containsKey("yellow")) {
            setImagePreview(enemyPreview2, enemies.get("yellow"));
        }

        // Aperçu de la bombe
        setImagePreview(bombPreview, theme.getBombSprite());

        // Aperçu du mur destructible
        setImagePreview(wallPreview, theme.getDestructibleWallSprite());

        // Aperçu d'un power-up
        Map<String, String> powerUps = theme.getPowerUpSprites();
        if (powerUps.containsKey("EXTRA_BOMB")) {
            setImagePreview(powerUpPreview, powerUps.get("EXTRA_BOMB"));
        }
    }

    private void setImagePreview(ImageView imageView, String imagePath) {
        if (imageView != null && imagePath != null) {
            try {
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                imageView.setImage(image);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
            } catch (Exception e) {
                // Si l'image n'existe pas, afficher une image par défaut ou vider
                imageView.setImage(null);
                System.err.println("Impossible de charger l'aperçu : " + imagePath);
            }
        }
    }

    private void clearThemePreview() {
        themeNameLabel.setText("Sélectionnez un thème");
        themeDescriptionLabel.setText("");

        // Effacer tous les aperçus
        if (playerPreview1 != null) playerPreview1.setImage(null);
        if (playerPreview2 != null) playerPreview2.setImage(null);
        if (enemyPreview1 != null) enemyPreview1.setImage(null);
        if (enemyPreview2 != null) enemyPreview2.setImage(null);
        if (bombPreview != null) bombPreview.setImage(null);
        if (wallPreview != null) wallPreview.setImage(null);
        if (powerUpPreview != null) powerUpPreview.setImage(null);
    }

    private void applySelectedTheme() {
        if (selectedTheme != null) {
            themeManager.setCurrentTheme(selectedTheme);

            // Afficher une confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thème appliqué");
            alert.setHeaderText(null);
            alert.setContentText("Le thème '" + themeManager.getAvailableThemes().get(selectedTheme).getName() +
                    "' a été appliqué avec succès !\nIl sera utilisé dans toutes les parties.");
            alert.showAndWait();
        }
    }

    private void createNewTheme() {
        // Ouvrir un dialogue pour créer un nouveau thème
        Optional<ThemeData> result = showThemeCreationDialog();

        if (result.isPresent()) {
            ThemeData newTheme = result.get();
            themeManager.addCustomTheme(newTheme);
            loadThemeList();
            themeListView.getSelectionModel().select(newTheme.getId());

            showAlert("Succès", "Nouveau thème '" + newTheme.getName() + "' créé avec succès !");
        }
    }

    /**
     * Affiche le dialogue de création de thème
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

        grid.add(new Label("ID:"), 0, 0);
        grid.add(themeId, 1, 0);
        grid.add(new Label("Nom:"), 0, 1);
        grid.add(themeName, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(themeDescription, 1, 2);
        grid.add(new Label("Basé sur:"), 0, 3);
        grid.add(baseTheme, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Validation
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        // Listeners pour validation
        Runnable validation = () -> {
            boolean valid = !themeId.getText().trim().isEmpty() && !themeName.getText().trim().isEmpty();
            createButton.setDisable(!valid);
        };

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

                    // Copier depuis le thème de base
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

    private void deleteSelectedTheme() {
        if (selectedTheme == null) return;

        // Empêcher la suppression des thèmes par défaut
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}