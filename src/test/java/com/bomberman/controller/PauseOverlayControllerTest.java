package com.bomberman.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.Mockito.*;

class PauseOverlayControllerTest extends ApplicationTest {

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PauseOverlayController();
        // Initialisation des mocks
        controller.resumeButton = resumeButton;
        controller.restartButton = restartButton;
        controller.mainMenuButton = mainMenuButton;
        controller.pauseOverlay = pauseOverlay;
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
    void testResumeButtonAction() {
        // Act
        controller.resumeButton.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onResume();
    }

    @Test
    void testRestartButtonAction() {
        // Act
        controller.restartButton.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onRestart();
    }

    @Test
    void testMainMenuButtonAction() {
        // Act
        controller.mainMenuButton.getOnAction().handle(null);
        
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
    void testSetActionListener() {
        // Arrange
        PauseOverlayController.PauseActionListener newListener = mock(PauseOverlayController.PauseActionListener.class);
        
        // Act
        controller.setActionListener(newListener);
        
        // Assert
        // Vérifie que le listener a été correctement défini en testant une action
        controller.resumeButton.getOnAction().handle(null);
        verify(newListener).onResume();
    }
}
