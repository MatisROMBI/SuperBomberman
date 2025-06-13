package com.bomberman.controller;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PauseOverlayControllerTest {

    @Mock
    private PauseOverlayController.PauseActionListener actionListener;
    @Mock
    private Button resumeButton;
    @Mock
    private Button restartButton;
    @Mock
    private Button mainMenuButton;
    @Mock
    private StackPane pauseOverlay;

    private PauseOverlayController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new PauseOverlayController();
        
        // Utilisation de la réflexion pour accéder aux champs privés
        Field resumeButtonField = PauseOverlayController.class.getDeclaredField("resumeButton");
        resumeButtonField.setAccessible(true);
        resumeButtonField.set(controller, resumeButton);

        Field restartButtonField = PauseOverlayController.class.getDeclaredField("restartButton");
        restartButtonField.setAccessible(true);
        restartButtonField.set(controller, restartButton);

        Field mainMenuButtonField = PauseOverlayController.class.getDeclaredField("mainMenuButton");
        mainMenuButtonField.setAccessible(true);
        mainMenuButtonField.set(controller, mainMenuButton);

        Field pauseOverlayField = PauseOverlayController.class.getDeclaredField("pauseOverlay");
        pauseOverlayField.setAccessible(true);
        pauseOverlayField.set(controller, pauseOverlay);

        controller.setActionListener(actionListener);
    }

    @Test
    void testShowPause() {
        // Act
        controller.showPause();
        
        // Assert
        verify(pauseOverlay).setVisible(true);
        verify(resumeButton).requestFocus();
    }

    @Test
    void testHidePause() {
        // Act
        controller.hidePause();
        
        // Assert
        verify(pauseOverlay).setVisible(false);
    }

    @Test
    void testResumeButtonAction() throws Exception {
        // Arrange
        Field resumeButtonField = PauseOverlayController.class.getDeclaredField("resumeButton");
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
        Field restartButtonField = PauseOverlayController.class.getDeclaredField("restartButton");
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
        Field mainMenuButtonField = PauseOverlayController.class.getDeclaredField("mainMenuButton");
        mainMenuButtonField.setAccessible(true);
        Button mainMenuBtn = (Button) mainMenuButtonField.get(controller);
        
        // Act
        mainMenuBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onMainMenu();
    }

    @Test
    void testIsPauseVisible() {
        // Arrange
        when(pauseOverlay.isVisible()).thenReturn(true);
        
        // Act
        boolean isVisible = controller.isPauseVisible();
        
        // Assert
        assertTrue(isVisible);
    }

    @Test
    void testSetActionListener() throws Exception {
        // Arrange
        PauseOverlayController.PauseActionListener newListener = mock(PauseOverlayController.PauseActionListener.class);
        
        // Act
        controller.setActionListener(newListener);
        
        // Assert
        // Vérifie que le listener a été correctement défini en testant une action
        Field resumeButtonField = PauseOverlayController.class.getDeclaredField("resumeButton");
        resumeButtonField.setAccessible(true);
        Button resumeBtn = (Button) resumeButtonField.get(controller);
        resumeBtn.getOnAction().handle(null);
        verify(newListener).onResume();
    }
}
