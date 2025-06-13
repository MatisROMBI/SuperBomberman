package com.bomberman.controller;

import com.bomberman.model.MapData;
import com.bomberman.utils.MapManager;
import com.bomberman.utils.SceneManager;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class MapSelectionControllerTest extends ApplicationTest {

    @Mock
    private VBox mapSelectionContainer;
    @Mock
    private ListView<String> mapListView;
    @Mock
    private TextArea mapPreviewArea;
    @Mock
    private Label mapInfoLabel;
    @Mock
    private Button playButton;
    @Mock
    private Button editButton;
    @Mock
    private Button backButton;
    @Mock
    private RadioButton classicModeRadio;
    @Mock
    private RadioButton legendModeRadio;
    @Mock
    private MapManager mapManager;
    @Mock
    private ToggleGroup gameModeGroup;

    private MapSelectionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new MapSelectionController();
        // Initialisation des mocks
        controller.mapSelectionContainer = mapSelectionContainer;
        controller.mapListView = mapListView;
        controller.mapPreviewArea = mapPreviewArea;
        controller.mapInfoLabel = mapInfoLabel;
        controller.playButton = playButton;
        controller.editButton = editButton;
        controller.backButton = backButton;
        controller.classicModeRadio = classicModeRadio;
        controller.legendModeRadio = legendModeRadio;
    }

    @Test
    void testInitialize() {
        // Act
        controller.initialize();
        
        // Assert
        verify(mapListView).getItems();
        verify(playButton).setDisable(true);
    }

    @Test
    void testLoadAvailableMaps() {
        // Arrange
        List<String> maps = Arrays.asList("Map1", "Map2");
        when(mapManager.getAvailableMaps()).thenReturn(maps);
        
        // Act
        controller.loadAvailableMaps();
        
        // Assert
        verify(mapListView.getItems()).add("Carte par défaut");
        verify(mapListView.getItems()).addAll(maps);
    }

    @Test
    void testShowMapPreview_DefaultMap() {
        // Act
        controller.showMapPreview("Carte par défaut");
        
        // Assert
        verify(mapPreviewArea).setText(anyString());
        verify(mapInfoLabel).setText("Carte générée automatiquement avec layout classique Bomberman");
    }

    @Test
    void testShowMapPreview_CustomMap() {
        // Arrange
        String mapName = "CustomMap";
        MapData mapData = mock(MapData.class);
        when(mapManager.loadMap(mapName)).thenReturn(mapData);
        when(mapManager.getMapInfo(mapName)).thenReturn("Map Info");
        
        // Act
        controller.showMapPreview(mapName);
        
        // Assert
        verify(mapManager).loadMap(mapName);
        verify(mapPreviewArea).setText(anyString());
        verify(mapInfoLabel).setText("Map Info");
    }

    @Test
    void testClearMapPreview() {
        // Act
        controller.clearMapPreview();
        
        // Assert
        verify(mapPreviewArea).clear();
        verify(mapInfoLabel).setText("Sélectionnez une carte pour voir l'aperçu");
    }

    @Test
    void testPlayButtonAction() {
        // Act
        controller.playButton.getOnAction().handle(null);
        
        // Assert
        // Vérifie que la scène est changée en fonction du mode sélectionné
        verify(SceneManager.class).switchScene(anyString());
    }

    @Test
    void testEditButtonAction() {
        // Act
        controller.editButton.getOnAction().handle(null);
        
        // Assert
        verify(SceneManager.class).switchScene("LevelEditor");
    }

    @Test
    void testBackButtonAction() {
        // Act
        controller.backButton.getOnAction().handle(null);
        
        // Assert
        verify(SceneManager.class).switchScene("MainMenu");
    }

    @Test
    void testGameModeSelection() {
        // Arrange
        when(classicModeRadio.isSelected()).thenReturn(true);
        when(legendModeRadio.isSelected()).thenReturn(false);
        
        // Act
        controller.gameModeGroup.selectedToggleProperty().getValue();
        
        // Assert
        assertEquals("classic", controller.selectedGameMode);
    }

    @Test
    void testMapSelection() {
        // Arrange
        String selectedMap = "TestMap";
        when(mapListView.getSelectionModel().getSelectedItem()).thenReturn(selectedMap);
        
        // Act
        controller.mapListView.getSelectionModel().selectedItemProperty().getValue();
        
        // Assert
        verify(playButton).setDisable(false);
    }
}
