package com.bomberman.controller;

import com.bomberman.BaseControllerTest;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControllerTest extends BaseControllerTest {

    @Mock
    private GameController.GameActionListener actionListener;
    @Mock
    private Button pauseButton;
    @Mock
    private Button resumeButton;
    @Mock
    private Button restartButton;
    @Mock
    private Button mainMenuButton;
    @Mock
    private StackPane game;

    private GameController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new GameController();
        
        // Utilisation de la réflexion pour accéder aux champs privés
        Field pauseButtonField = GameController.class.getDeclaredField("pauseButton");
        pauseButtonField.setAccessible(true);
        pauseButtonField.set(controller, pauseButton);

        Field resumeButtonField = GameController.class.getDeclaredField("resumeButton");
        resumeButtonField.setAccessible(true);
        resumeButtonField.set(controller, resumeButton);

        Field restartButtonField = GameController.class.getDeclaredField("restartButton");
        restartButtonField.setAccessible(true);
        restartButtonField.set(controller, restartButton);

        Field mainMenuButtonField = GameController.class.getDeclaredField("mainMenuButton");
        mainMenuButtonField.setAccessible(true);
        mainMenuButtonField.set(controller, mainMenuButton);

        Field gameField = GameController.class.getDeclaredField("game");
        gameField.setAccessible(true);
        gameField.set(controller, game);

        controller.setActionListener(actionListener);
    }

    @Test
    void testShowGame() {
        // Act
        controller.showGame();
        
        // Assert
        verify(game).setVisible(true);
        verify(pauseButton).requestFocus();
    }

    @Test
    void testHideGame() {
        // Act
        controller.hideGame();
        
        // Assert
        verify(game).setVisible(false);
    }

    @Test
    void testPauseButtonAction() throws Exception {
        // Arrange
        Field pauseButtonField = GameController.class.getDeclaredField("pauseButton");
        pauseButtonField.setAccessible(true);
        Button pauseBtn = (Button) pauseButtonField.get(controller);
        
        // Act
        pauseBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onPause();
    }

    @Test
    void testResumeButtonAction() throws Exception {
        // Arrange
        Field resumeButtonField = GameController.class.getDeclaredField("resumeButton");
        resumeButtonField.setAccessible(true);
        Button resumeBtn = (Button) resumeButtonField.get(controller);
        
        // Act
        resumeBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onResume();
    }

    @Test
    void testRestartButtonAction() throws Exception {
        // Arrange
        Field restartButtonField = GameController.class.getDeclaredField("restartButton");
        restartButtonField.setAccessible(true);
        Button restartBtn = (Button) restartButtonField.get(controller);
        
        // Act
        restartBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onRestart();
    }

    @Test
    void testMainMenuButtonAction() throws Exception {
        // Arrange
        Field mainMenuButtonField = GameController.class.getDeclaredField("mainMenuButton");
        mainMenuButtonField.setAccessible(true);
        Button mainMenuBtn = (Button) mainMenuButtonField.get(controller);
        
        // Act
        mainMenuBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onMainMenu();
    }

    @Test
    void testIsGameVisible() {
        // Arrange
        when(game.isVisible()).thenReturn(true);
        
        // Act
        boolean isVisible = controller.isGameVisible();
        
        // Assert
        assertTrue(isVisible);
    }

    @Test
    void testSetActionListener() throws Exception {
        // Arrange
        GameController.GameActionListener newListener = mock(GameController.GameActionListener.class);
        
        // Act
        controller.setActionListener(newListener);
        
        // Assert
        // Vérifie que le listener a été correctement défini en testant une action
        Field pauseButtonField = GameController.class.getDeclaredField("pauseButton");
        pauseButtonField.setAccessible(true);
        Button pauseBtn = (Button) pauseButtonField.get(controller);
        pauseBtn.getOnAction().handle(null);
        verify(newListener).onPause();
    }
}
