package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class GameControllerTest {

    @Mock
    private Game game;
    @Mock
    private PauseOverlayController pauseController;
    @Mock
    private KeyEvent keyEvent;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameController = new GameController();
        // Initialisation des mocks nécessaires
    }

    @Test
    void testHandleKeyPressed_Up() {
        // Arrange
        when(keyEvent.getCode()).thenReturn(KeyCode.UP);
        
        // Act
        gameController.handleKeyPressed(keyEvent);
        
        // Assert
        verify(game.getPlayer()).move(eq(Direction.UP), any(), any());
    }

    @Test
    void testHandleKeyPressed_Down() {
        // Arrange
        when(keyEvent.getCode()).thenReturn(KeyCode.DOWN);
        
        // Act
        gameController.handleKeyPressed(keyEvent);
        
        // Assert
        verify(game.getPlayer()).move(eq(Direction.DOWN), any(), any());
    }

    @Test
    void testHandleKeyPressed_Left() {
        // Arrange
        when(keyEvent.getCode()).thenReturn(KeyCode.LEFT);
        
        // Act
        gameController.handleKeyPressed(keyEvent);
        
        // Assert
        verify(game.getPlayer()).move(eq(Direction.LEFT), any(), any());
    }

    @Test
    void testHandleKeyPressed_Right() {
        // Arrange
        when(keyEvent.getCode()).thenReturn(KeyCode.RIGHT);
        
        // Act
        gameController.handleKeyPressed(keyEvent);
        
        // Assert
        verify(game.getPlayer()).move(eq(Direction.RIGHT), any(), any());
    }

    @Test
    void testHandleKeyPressed_Space() {
        // Arrange
        when(keyEvent.getCode()).thenReturn(KeyCode.SPACE);
        
        // Act
        gameController.handleKeyPressed(keyEvent);
        
        // Assert
        verify(game.getPlayer()).placeBomb(any());
    }

    @Test
    void testHandleKeyPressed_Escape() {
        // Arrange
        when(keyEvent.getCode()).thenReturn(KeyCode.ESCAPE);
        
        // Act
        gameController.handleKeyPressed(keyEvent);
        
        // Assert
        verify(pauseController).showPause();
    }

    @Test
    void testPauseGame() {
        // Act
        gameController.pauseGame();
        
        // Assert
        verify(game).pause();
        verify(pauseController).showPause();
    }

    @Test
    void testResumeGame() {
        // Act
        gameController.resumeGame();
        
        // Assert
        verify(game).pause(); // Le jeu bascule son état de pause
        verify(pauseController).hidePause();
    }

    @Test
    void testOnGameOver() {
        // Arrange
        int score = 1000;
        
        // Act
        gameController.onGameOver(score);
        
        // Assert
        verify(game).setGameState(GameState.GAME_OVER);
    }

    @Test
    void testOnRestart() {
        // Act
        gameController.onRestart();
        
        // Assert
        verify(game).reset();
    }
}
