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
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @Mock
    private LevelEditorController.LevelEditorActionListener actionListener;
    @Mock
    private StackPane levelEditor;

    private LevelEditorController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new LevelEditorController();
        
        // Utilisation de la réflexion pour accéder aux champs privés
        Field saveButtonField = LevelEditorController.class.getDeclaredField("saveButton");
        saveButtonField.setAccessible(true);
        saveButtonField.set(controller, saveButton);

        Field loadButtonField = LevelEditorController.class.getDeclaredField("loadButton");
        loadButtonField.setAccessible(true);
        loadButtonField.set(controller, loadButton);

        Field clearButtonField = LevelEditorController.class.getDeclaredField("clearButton");
        clearButtonField.setAccessible(true);
        clearButtonField.set(controller, clearButton);

        Field backButtonField = LevelEditorController.class.getDeclaredField("backButton");
        backButtonField.setAccessible(true);
        backButtonField.set(controller, backButton);

        Field levelEditorField = LevelEditorController.class.getDeclaredField("levelEditor");
        levelEditorField.setAccessible(true);
        levelEditorField.set(controller, levelEditor);

        controller.setActionListener(actionListener);
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

    @Test
    void testShowLevelEditor() {
        // Act
        controller.showLevelEditor();
        
        // Assert
        verify(levelEditor).setVisible(true);
        verify(saveButton).requestFocus();
    }

    @Test
    void testHideLevelEditor() {
        // Act
        controller.hideLevelEditor();
        
        // Assert
        verify(levelEditor).setVisible(false);
    }

    @Test
    void testSaveButtonAction() throws Exception {
        // Arrange
        Field saveButtonField = LevelEditorController.class.getDeclaredField("saveButton");
        saveButtonField.setAccessible(true);
        Button saveBtn = (Button) saveButtonField.get(controller);
        
        // Act
        saveBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onSave();
    }

    @Test
    void testLoadButtonAction() throws Exception {
        // Arrange
        Field loadButtonField = LevelEditorController.class.getDeclaredField("loadButton");
        loadButtonField.setAccessible(true);
        Button loadBtn = (Button) loadButtonField.get(controller);
        
        // Act
        loadBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onLoad();
    }

    @Test
    void testClearButtonAction() throws Exception {
        // Arrange
        Field clearButtonField = LevelEditorController.class.getDeclaredField("clearButton");
        clearButtonField.setAccessible(true);
        Button clearBtn = (Button) clearButtonField.get(controller);
        
        // Act
        clearBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onClear();
    }

    @Test
    void testBackButtonAction() throws Exception {
        // Arrange
        Field backButtonField = LevelEditorController.class.getDeclaredField("backButton");
        backButtonField.setAccessible(true);
        Button backBtn = (Button) backButtonField.get(controller);
        
        // Act
        backBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onBack();
    }

    @Test
    void testIsLevelEditorVisible() {
        // Arrange
        when(levelEditor.isVisible()).thenReturn(true);
        
        // Act
        boolean isVisible = controller.isLevelEditorVisible();
        
        // Assert
        assertTrue(isVisible);
    }

    @Test
    void testSetActionListener() throws Exception {
        // Arrange
        LevelEditorController.LevelEditorActionListener newListener = mock(LevelEditorController.LevelEditorActionListener.class);
        
        // Act
        controller.setActionListener(newListener);
        
        // Assert
        // Vérifie que le listener a été correctement défini en testant une action
        Field saveButtonField = LevelEditorController.class.getDeclaredField("saveButton");
        saveButtonField.setAccessible(true);
        Button saveBtn = (Button) saveButtonField.get(controller);
        saveBtn.getOnAction().handle(null);
        verify(newListener).onSave();
    }
}
