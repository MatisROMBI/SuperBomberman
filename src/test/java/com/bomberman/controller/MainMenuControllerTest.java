package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainMenuControllerTest extends ApplicationTest {

    @Mock
    private ImageView backgroundImage;
    @Mock
    private ImageView balloon1;
    @Mock
    private ImageView balloon2;
    @Mock
    private ImageView balloon3;
    @Mock
    private Button robotSurvivorButton;
    @Mock
    private Button legend1v1Button;
    @Mock
    private Button levelEditorButton;
    @Mock
    private Button themesButton;
    @Mock
    private Button quitButton;
    @Mock
    private MediaPlayer menuMusicPlayer;
    @Mock
    private SceneManager sceneManager;
    @Mock
    private MainMenuController.MainMenuActionListener actionListener;
    @Mock
    private StackPane mainMenu;

    private MainMenuController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new MainMenuController();
        
        // Utilisation de la réflexion pour accéder aux champs privés
        Field robotSurvivorButtonField = MainMenuController.class.getDeclaredField("robotSurvivorButton");
        robotSurvivorButtonField.setAccessible(true);
        robotSurvivorButtonField.set(controller, robotSurvivorButton);

        Field legend1v1ButtonField = MainMenuController.class.getDeclaredField("legend1v1Button");
        legend1v1ButtonField.setAccessible(true);
        legend1v1ButtonField.set(controller, legend1v1Button);

        Field levelEditorButtonField = MainMenuController.class.getDeclaredField("levelEditorButton");
        levelEditorButtonField.setAccessible(true);
        levelEditorButtonField.set(controller, levelEditorButton);

        Field themesButtonField = MainMenuController.class.getDeclaredField("themesButton");
        themesButtonField.setAccessible(true);
        themesButtonField.set(controller, themesButton);

        Field quitButtonField = MainMenuController.class.getDeclaredField("quitButton");
        quitButtonField.setAccessible(true);
        quitButtonField.set(controller, quitButton);

        Field backgroundImageField = MainMenuController.class.getDeclaredField("backgroundImage");
        backgroundImageField.setAccessible(true);
        backgroundImageField.set(controller, backgroundImage);

        Field balloon1Field = MainMenuController.class.getDeclaredField("balloon1");
        balloon1Field.setAccessible(true);
        balloon1Field.set(controller, balloon1);

        Field balloon2Field = MainMenuController.class.getDeclaredField("balloon2");
        balloon2Field.setAccessible(true);
        balloon2Field.set(controller, balloon2);

        Field balloon3Field = MainMenuController.class.getDeclaredField("balloon3");
        balloon3Field.setAccessible(true);
        balloon3Field.set(controller, balloon3);

        Field mainMenuField = MainMenuController.class.getDeclaredField("mainMenu");
        mainMenuField.setAccessible(true);
        mainMenuField.set(controller, mainMenu);

        controller.setActionListener(actionListener);
    }

    @Test
    void testRobotSurvivorButtonAction() throws Exception {
        // Arrange
        Field robotSurvivorButtonField = MainMenuController.class.getDeclaredField("robotSurvivorButton");
        robotSurvivorButtonField.setAccessible(true);
        Button robotSurvivorBtn = (Button) robotSurvivorButtonField.get(controller);
        
        // Act
        robotSurvivorBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onPlay();
    }

    @Test
    void testLegend1v1ButtonAction() throws Exception {
        // Arrange
        Field legend1v1ButtonField = MainMenuController.class.getDeclaredField("legend1v1Button");
        legend1v1ButtonField.setAccessible(true);
        Button legend1v1Btn = (Button) legend1v1ButtonField.get(controller);
        
        // Act
        legend1v1Btn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onLegend();
    }

    @Test
    void testLevelEditorButtonAction() throws Exception {
        // Arrange
        Field levelEditorButtonField = MainMenuController.class.getDeclaredField("levelEditorButton");
        levelEditorButtonField.setAccessible(true);
        Button levelEditorBtn = (Button) levelEditorButtonField.get(controller);
        
        // Act
        levelEditorBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onEditor();
    }

    @Test
    void testThemesButtonAction() throws Exception {
        // Arrange
        Field themesButtonField = MainMenuController.class.getDeclaredField("themesButton");
        themesButtonField.setAccessible(true);
        Button themesBtn = (Button) themesButtonField.get(controller);
        
        // Act
        themesBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onThemes();
    }

    @Test
    void testQuitButtonAction() throws Exception {
        // Arrange
        Field quitButtonField = MainMenuController.class.getDeclaredField("quitButton");
        quitButtonField.setAccessible(true);
        Button quitBtn = (Button) quitButtonField.get(controller);
        
        // Act
        quitBtn.getOnAction().handle(null);
        
        // Assert
        verify(actionListener).onQuit();
    }

    @Test
    void testButtonEffects() {
        // Test des effets de survol
        controller.robotSurvivorButton.getOnMouseEntered().handle(null);
        verify(robotSurvivorButton).requestFocus();

        // Test des effets de clic
        controller.robotSurvivorButton.getOnMousePressed().handle(null);
        verify(robotSurvivorButton).setScaleX(0.95);
        verify(robotSurvivorButton).setScaleY(0.95);

        // Test du retour à la taille normale
        controller.robotSurvivorButton.getOnMouseReleased().handle(null);
        verify(robotSurvivorButton).setScaleX(1.0);
        verify(robotSurvivorButton).setScaleY(1.0);
    }

    @Test
    void testBalloonAnimations() {
        // Vérifie que les ballons sont positionnés correctement
        verify(balloon1).setTranslateX(anyDouble());
        verify(balloon1).setTranslateY(100.0);
        
        verify(balloon2).setTranslateX(anyDouble());
        verify(balloon2).setTranslateY(anyDouble());
        
        verify(balloon3).setTranslateX(anyDouble());
        verify(balloon3).setTranslateY(170.0);
    }

    @Test
    void testMenuMusic() {
        // Vérifie que la musique est démarrée
        controller.playMenuMusic();
        // Note: Ce test peut être difficile à exécuter car il dépend de ressources externes
        // Une alternative serait de mocker MediaPlayer et Media
    }

    @Test
    void testShowMainMenu() {
        // Act
        controller.showMainMenu();
        
        // Assert
        verify(mainMenu).setVisible(true);
        verify(robotSurvivorButton).requestFocus();
    }

    @Test
    void testHideMainMenu() {
        // Act
        controller.hideMainMenu();
        
        // Assert
        verify(mainMenu).setVisible(false);
    }

    @Test
    void testIsMainMenuVisible() {
        // Arrange
        when(mainMenu.isVisible()).thenReturn(true);
        
        // Act
        boolean isVisible = controller.isMainMenuVisible();
        
        // Assert
        assertTrue(isVisible);
    }

    @Test
    void testSetActionListener() throws Exception {
        // Arrange
        MainMenuController.MainMenuActionListener newListener = mock(MainMenuController.MainMenuActionListener.class);
        
        // Act
        controller.setActionListener(newListener);
        
        // Assert
        // Vérifie que le listener a été correctement défini en testant une action
        Field robotSurvivorButtonField = MainMenuController.class.getDeclaredField("robotSurvivorButton");
        robotSurvivorButtonField.setAccessible(true);
        Button robotSurvivorBtn = (Button) robotSurvivorButtonField.get(controller);
        robotSurvivorBtn.getOnAction().handle(null);
        verify(newListener).onPlay();
    }
}
