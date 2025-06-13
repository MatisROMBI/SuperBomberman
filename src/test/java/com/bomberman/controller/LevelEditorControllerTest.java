package com.bomberman.controller;

import com.bomberman.model.MapData;
import com.bomberman.model.enums.CellType;
import com.bomberman.utils.MapManager;
import com.bomberman.utils.SceneManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class LevelEditorControllerTest extends ApplicationTest {

    @Mock
    private VBox editorContainer;
    @Mock
    private Canvas editorCanvas;
    @Mock
    private TextField mapNameField;
    @Mock
    private ComboBox<String> existingMapsCombo;
    @Mock
    private Button saveButton;
    @Mock
    private Button loadButton;
    @Mock
    private Button deleteButton;
    @Mock
    private Button clearButton;
    @Mock
    private Button backButton;
    @Mock
    private RadioButton emptyRadio;
    @Mock
    private RadioButton wallRadio;
    @Mock
    private RadioButton destructibleRadio;
    @Mock
    private Label instructionsLabel;
    @Mock
    private GraphicsContext gc;
    @Mock
    private MapManager mapManager;
    @Mock
    private ToggleGroup cellTypeGroup;

    private LevelEditorController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new LevelEditorController();
        // Initialisation des mocks
        controller.editorContainer = editorContainer;
        controller.editorCanvas = editorCanvas;
        controller.mapNameField = mapNameField;
        controller.existingMapsCombo = existingMapsCombo;
        controller.saveButton = saveButton;
        controller.loadButton = loadButton;
        controller.deleteButton = deleteButton;
        controller.clearButton = clearButton;
        controller.backButton = backButton;
        controller.emptyRadio = emptyRadio;
        controller.wallRadio = wallRadio;
        controller.destructibleRadio = destructibleRadio;
        controller.instructionsLabel = instructionsLabel;
        when(editorCanvas.getGraphicsContext2D()).thenReturn(gc);
    }

    @Test
    void testInitialize() {
        // Act
        controller.initialize();
        
        // Assert
        verify(editorCanvas).setWidth(anyDouble());
        verify(editorCanvas).setHeight(anyDouble());
        verify(emptyRadio).setSelected(true);
    }

    @Test
    void testHandleCanvasClick_LeftClick() {
        // Arrange
        MouseEvent event = mock(MouseEvent.class);
        when(event.getButton()).thenReturn(MouseButton.PRIMARY);
        when(event.getX()).thenReturn(50.0);
        when(event.getY()).thenReturn(50.0);
        
        // Act
        controller.handleCanvasClick(event);
        
        // Assert
        verify(gc).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testHandleCanvasClick_RightClick() {
        // Arrange
        MouseEvent event = mock(MouseEvent.class);
        when(event.getButton()).thenReturn(MouseButton.SECONDARY);
        when(event.getX()).thenReturn(50.0);
        when(event.getY()).thenReturn(50.0);
        
        // Act
        controller.handleCanvasClick(event);
        
        // Assert
        verify(gc).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testSaveMap() {
        // Arrange
        String mapName = "TestMap";
        when(mapNameField.getText()).thenReturn(mapName);
        
        // Act
        controller.saveMap();
        
        // Assert
        verify(mapManager).saveMap(any(MapData.class));
    }

    @Test
    void testLoadMap() {
        // Arrange
        String mapName = "TestMap";
        when(existingMapsCombo.getValue()).thenReturn(mapName);
        MapData mapData = mock(MapData.class);
        when(mapManager.loadMap(mapName)).thenReturn(mapData);
        
        // Act
        controller.loadMap();
        
        // Assert
        verify(mapManager).loadMap(mapName);
    }

    @Test
    void testDeleteMap() {
        // Arrange
        String mapName = "TestMap";
        when(existingMapsCombo.getValue()).thenReturn(mapName);
        
        // Act
        controller.deleteMap();
        
        // Assert
        verify(mapManager).deleteMap(mapName);
    }

    @Test
    void testClearGrid() {
        // Act
        controller.clearGrid();
        
        // Assert
        verify(gc).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testLoadExistingMaps() {
        // Arrange
        List<String> maps = Arrays.asList("Map1", "Map2");
        when(mapManager.getAvailableMaps()).thenReturn(maps);
        
        // Act
        controller.loadExistingMaps();
        
        // Assert
        verify(existingMapsCombo.getItems()).addAll(maps);
    }

    @Test
    void testBackButtonAction() {
        // Act
        controller.backButton.getOnAction().handle(null);
        
        // Assert
        verify(SceneManager.class).switchScene("MainMenu");
    }

    @Test
    void testCellTypeSelection() {
        // Arrange
        when(emptyRadio.isSelected()).thenReturn(true);
        when(wallRadio.isSelected()).thenReturn(false);
        when(destructibleRadio.isSelected()).thenReturn(false);
        
        // Act
        controller.cellTypeGroup.selectedToggleProperty().getValue();
        
        // Assert
        assertEquals(CellType.EMPTY, controller.selectedCellType);
    }
}
