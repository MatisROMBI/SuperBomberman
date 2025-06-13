package com.bomberman.controller;

import com.bomberman.model.Legend1v1Board;
import com.bomberman.model.Music;
import com.bomberman.model.Player;
import com.bomberman.utils.SceneManager;
import com.bomberman.view.GameRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegendGameControllerTest extends ApplicationTest {

    @Mock
    private VBox gameContainer;
    @Mock
    private Canvas gameCanvas;
    @Mock
    private Legend1v1Board board;
    @Mock
    private GameRenderer renderer;
    @Mock
    private Music music;
    @Mock
    private StackPane pauseOverlay;
    @Mock
    private PauseOverlayController pauseController;
    @Mock
    private Player player1;
    @Mock
    private Player player2;
    @Mock
    private LegendGameController.LegendGameActionListener actionListener;
    @Mock
    private Button startButton;
    @Mock
    private Button backButton;
    @Mock
    private StackPane legendGame;

    private LegendGameController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new LegendGameController();
        
        // Utilisation de la réflexion pour accéder aux champs privés
        Field startButtonField = LegendGameController.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        startButtonField.set(controller, startButton);

        Field backButtonField = LegendGameController.class.getDeclaredField("backButton");
        backButtonField.setAccessible(true);
        backButtonField.set(controller, backButton);

        Field legendGameField = LegendGameController.class.getDeclaredField("legendGame");
        legendGameField.setAccessible(true);
        legendGameField.set(controller, legendGame);

        controller.setActionListener(actionListener);

        // Initialisation des mocks
        controller.gameContainer = gameContainer;
        controller.gameCanvas = gameCanvas;
        controller.board = board;
        controller.renderer = renderer;
        controller.music = music;
        controller.pauseOverlay = pauseOverlay;
        controller.pauseController = pauseController;
        when(board.getPlayer1()).thenReturn(player1);
        when(board.getPlayer2()).thenReturn(player2);
    }

    @Test
    void testInitialize() {
        // Act
        controller.initialize();
        
        // Assert
        verify(music).demarrerLegendMusic();
    }

    @Test
    void testHandleKeyPressed_Player1_Up() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.Z);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player1).moveUp(board);
    }

    @Test
    void testHandleKeyPressed_Player1_Down() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.S);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player1).moveDown(board);
    }

    @Test
    void testHandleKeyPressed_Player1_Left() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.Q);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player1).moveLeft(board);
    }

    @Test
    void testHandleKeyPressed_Player1_Right() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.D);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player1).moveRight(board);
    }

    @Test
    void testHandleKeyPressed_Player1_Bomb() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.R);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player1).placeBomb(board);
    }

    @Test
    void testHandleKeyPressed_Player2_Up() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.I);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player2).moveUp(board);
    }

    @Test
    void testHandleKeyPressed_Player2_Down() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.K);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player2).moveDown(board);
    }

    @Test
    void testHandleKeyPressed_Player2_Left() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.J);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player2).moveLeft(board);
    }

    @Test
    void testHandleKeyPressed_Player2_Right() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.L);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player2).moveRight(board);
    }

    @Test
    void testHandleKeyPressed_Player2_Bomb() {
        // Arrange
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.P);
        
        // Act
        controller.handleKeyPressed(event);
        
        // Assert
        verify(player2).placeBomb(board);
    }

    @Test
    void testPauseGame() {
        // Act
        controller.pauseGame();
        
        // Assert
        verify(music).arreterLegendMusic();
        verify(pauseController).showPause();
    }

    @Test
    void testResumeGame() {
        // Act
        controller.resumeGame();
        
        // Assert
        verify(music).demarrerLegendMusic();
        verify(pauseController).hidePause();
        verify(gameContainer).requestFocus();
    }

    @Test
    void testOnResume() {
        // Act
        controller.onResume();
        
        // Assert
        verify(music).demarrerLegendMusic();
        verify(pauseController).hidePause();
    }

    @Test
    void testOnRestart() {
        // Act
        controller.onRestart();
        
        // Assert
        verify(SceneManager.class).switchScene("LegendGame");
    }

    @Test
    void testOnMainMenu() {
        // Act
        controller.onMainMenu();
        
        // Assert
        verify(SceneManager.class).switchScene("MainMenu");
    }

    @Test
    void testShowLegendGame() {
        // Act
        controller.showLegendGame();
        
        // Assert
        verify(legendGame).setVisible(true);
        verify(startButton).requestFocus();
    }

    @Test
    void testHideLegendGame() {
        // Act
        controller.hideLegendGame();
        
        // Assert
        verify(legendGame).setVisible(false);
    }

    @Test
    void testStartButtonAction() throws Exception {
        // Arrange
        Field startButtonField = LegendGameController.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        Button startBtn = (Button) startButtonField.get(controller);
        
        // Act
        startBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onStart();
    }

    @Test
    void testBackButtonAction() throws Exception {
        // Arrange
        Field backButtonField = LegendGameController.class.getDeclaredField("backButton");
        backButtonField.setAccessible(true);
        Button backBtn = (Button) backButtonField.get(controller);
        
        // Act
        backBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onBack();
    }

    @Test
    void testIsLegendGameVisible() {
        // Arrange
        when(legendGame.isVisible()).thenReturn(true);
        
        // Act
        boolean isVisible = controller.isLegendGameVisible();
        
        // Assert
        assertTrue(isVisible);
    }

    @Test
    void testSetActionListener() throws Exception {
        // Arrange
        LegendGameController.LegendGameActionListener newListener = mock(LegendGameController.LegendGameActionListener.class);
        
        // Act
        controller.setActionListener(newListener);
        
        // Assert
        // Vérifie que le listener a été correctement défini en testant une action
        Field startButtonField = LegendGameController.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        Button startBtn = (Button) startButtonField.get(controller);
        startBtn.getOnAction().handle(null);
        verify(newListener).onStart();
    }
}
