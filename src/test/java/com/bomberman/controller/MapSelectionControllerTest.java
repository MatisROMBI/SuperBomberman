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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @Mock
    private MapSelectionController.MapSelectionActionListener actionListener;
    @Mock
    private Button map1Button;
    @Mock
    private Button map2Button;
    @Mock
    private Button map3Button;
    @Mock
    private StackPane mapSelection;

    private MapSelectionController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new MapSelectionController();
        
        // Utilisation de la réflexion pour accéder aux champs privés
        Field map1ButtonField = MapSelectionController.class.getDeclaredField("map1Button");
        map1ButtonField.setAccessible(true);
        map1ButtonField.set(controller, map1Button);

        Field map2ButtonField = MapSelectionController.class.getDeclaredField("map2Button");
        map2ButtonField.setAccessible(true);
        map2ButtonField.set(controller, map2Button);

        Field map3ButtonField = MapSelectionController.class.getDeclaredField("map3Button");
        map3ButtonField.setAccessible(true);
        map3ButtonField.set(controller, map3Button);

        Field backButtonField = MapSelectionController.class.getDeclaredField("backButton");
        backButtonField.setAccessible(true);
        backButtonField.set(controller, backButton);

        Field mapSelectionField = MapSelectionController.class.getDeclaredField("mapSelection");
        mapSelectionField.setAccessible(true);
        mapSelectionField.set(controller, mapSelection);

        controller.setActionListener(actionListener);
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

    @Test
    void testShowMapSelection() {
        // Act
        controller.showMapSelection();
        
        // Assert
        verify(mapSelection).setVisible(true);
        verify(map1Button).requestFocus();
    }

    @Test
    void testHideMapSelection() {
        // Act
        controller.hideMapSelection();
        
        // Assert
        verify(mapSelection).setVisible(false);
    }

    @Test
    void testMap1ButtonAction() throws Exception {
        // Arrange
        Field map1ButtonField = MapSelectionController.class.getDeclaredField("map1Button");
        map1ButtonField.setAccessible(true);
        Button map1Btn = (Button) map1ButtonField.get(controller);
        
        // Act
        map1Btn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onMapSelected(1);
    }

    @Test
    void testMap2ButtonAction() throws Exception {
        // Arrange
        Field map2ButtonField = MapSelectionController.class.getDeclaredField("map2Button");
        map2ButtonField.setAccessible(true);
        Button map2Btn = (Button) map2ButtonField.get(controller);
        
        // Act
        map2Btn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onMapSelected(2);
    }

    @Test
    void testMap3ButtonAction() throws Exception {
        // Arrange
        Field map3ButtonField = MapSelectionController.class.getDeclaredField("map3Button");
        map3ButtonField.setAccessible(true);
        Button map3Btn = (Button) map3ButtonField.get(controller);
        
        // Act
        map3Btn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onMapSelected(3);
    }

    @Test
    void testBackButtonAction() throws Exception {
        // Arrange
        Field backButtonField = MapSelectionController.class.getDeclaredField("backButton");
        backButtonField.setAccessible(true);
        Button backBtn = (Button) backButtonField.get(controller);
        
        // Act
        backBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onBack();
    }

    @Test
    void testIsMapSelectionVisible() {
        // Arrange
        when(mapSelection.isVisible()).thenReturn(true);
        
        // Act
        boolean isVisible = controller.isMapSelectionVisible();
        
        // Assert
        assertTrue(isVisible);
    }

    @Test
    void testSetActionListener() throws Exception {
        // Arrange
        MapSelectionController.MapSelectionActionListener newListener = mock(MapSelectionController.MapSelectionActionListener.class);
        
        // Act
        controller.setActionListener(newListener);
        
        // Assert
        // Vérifie que le listener a été correctement défini en testant une action
        Field map1ButtonField = MapSelectionController.class.getDeclaredField("map1Button");
        map1ButtonField.setAccessible(true);
        Button map1Btn = (Button) map1ButtonField.get(controller);
        map1Btn.getOnAction().handle(null);
        verify(newListener).onMapSelected(1);
    }
}
