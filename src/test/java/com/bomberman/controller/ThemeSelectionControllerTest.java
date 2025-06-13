package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import com.bomberman.utils.ThemeData;
import com.bomberman.utils.ThemeManager;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class ThemeSelectionControllerTest extends ApplicationTest {

    @Mock
    private VBox themeContainer;
    @Mock
    private ListView<String> themeListView;
    @Mock
    private Label themeNameLabel;
    @Mock
    private Label themeDescriptionLabel;
    @Mock
    private GridPane spritePreviewGrid;
    @Mock
    private Button applyThemeButton;
    @Mock
    private Button createThemeButton;
    @Mock
    private Button deleteThemeButton;
    @Mock
    private Button backButton;
    @Mock
    private ImageView playerPreview1;
    @Mock
    private ImageView playerPreview2;
    @Mock
    private ImageView enemyPreview1;
    @Mock
    private ImageView enemyPreview2;
    @Mock
    private ImageView bombPreview;
    @Mock
    private ImageView wallPreview;
    @Mock
    private ImageView powerUpPreview;
    @Mock
    private ThemeManager themeManager;

    private ThemeSelectionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ThemeSelectionController();
        // Initialisation des mocks
        controller.themeContainer = themeContainer;
        controller.themeListView = themeListView;
        controller.themeNameLabel = themeNameLabel;
        controller.themeDescriptionLabel = themeDescriptionLabel;
        controller.spritePreviewGrid = spritePreviewGrid;
        controller.applyThemeButton = applyThemeButton;
        controller.createThemeButton = createThemeButton;
        controller.deleteThemeButton = deleteThemeButton;
        controller.backButton = backButton;
        controller.playerPreview1 = playerPreview1;
        controller.playerPreview2 = playerPreview2;
        controller.enemyPreview1 = enemyPreview1;
        controller.enemyPreview2 = enemyPreview2;
        controller.bombPreview = bombPreview;
        controller.wallPreview = wallPreview;
        controller.powerUpPreview = powerUpPreview;
    }

    @Test
    void testInitialize() {
        // Act
        controller.initialize();
        
        // Assert
        verify(applyThemeButton).setDisable(true);
        verify(deleteThemeButton).setDisable(true);
    }

    @Test
    void testLoadThemeList() {
        // Arrange
        Map<String, ThemeData> themes = new HashMap<>();
        themes.put("classic", new ThemeData());
        themes.put("legend", new ThemeData());
        themes.put("custom", new ThemeData());
        when(themeManager.getAvailableThemes()).thenReturn(themes);
        
        // Act
        controller.loadThemeList();
        
        // Assert
        verify(themeListView.getItems()).clear();
        verify(themeListView.getItems()).add("classic");
        verify(themeListView.getItems()).add("legend");
        verify(themeListView.getItems()).add("custom");
    }

    @Test
    void testShowThemePreview() {
        // Arrange
        String themeId = "classic";
        ThemeData theme = new ThemeData();
        theme.setName("Classic Theme");
        theme.setDescription("Classic Bomberman theme");
        when(themeManager.getAvailableThemes().get(themeId)).thenReturn(theme);
        
        // Act
        controller.showThemePreview(themeId);
        
        // Assert
        verify(themeNameLabel).setText("Classic Theme");
        verify(themeDescriptionLabel).setText("Classic Bomberman theme");
    }

    @Test
    void testApplySelectedTheme() {
        // Arrange
        String themeId = "classic";
        controller.selectedTheme = themeId;
        
        // Act
        controller.applySelectedTheme();
        
        // Assert
        verify(themeManager).setCurrentTheme(themeId);
    }

    @Test
    void testDeleteSelectedTheme() {
        // Arrange
        String themeId = "custom";
        controller.selectedTheme = themeId;
        
        // Act
        controller.deleteSelectedTheme();
        
        // Assert
        verify(themeManager).deleteTheme(themeId);
    }

    @Test
    void testBackButtonAction() {
        // Act
        controller.backButton.getOnAction().handle(null);
        
        // Assert
        verify(SceneManager.class).switchScene("MainMenu");
    }

    @Test
    void testThemeSelection_DefaultTheme() {
        // Arrange
        String themeId = "classic";
        
        // Act
        controller.themeListView.getSelectionModel().selectedItemProperty().getValue();
        
        // Assert
        verify(applyThemeButton).setDisable(false);
        verify(deleteThemeButton).setDisable(true);
    }

    @Test
    void testThemeSelection_CustomTheme() {
        // Arrange
        String themeId = "custom";
        
        // Act
        controller.themeListView.getSelectionModel().selectedItemProperty().getValue();
        
        // Assert
        verify(applyThemeButton).setDisable(false);
        verify(deleteThemeButton).setDisable(false);
    }

    @Test
    void testClearThemePreview() {
        // Act
        controller.clearThemePreview();
        
        // Assert
        verify(themeNameLabel).setText("");
        verify(themeDescriptionLabel).setText("");
        verify(playerPreview1).setImage(null);
        verify(playerPreview2).setImage(null);
        verify(enemyPreview1).setImage(null);
        verify(enemyPreview2).setImage(null);
        verify(bombPreview).setImage(null);
        verify(wallPreview).setImage(null);
        verify(powerUpPreview).setImage(null);
    }
}
