package com.bomberman.controller;

import com.bomberman.utils.SceneManager;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.Mockito.*;

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

    private MainMenuController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new MainMenuController();
        // Initialisation des mocks
        controller.backgroundImage = backgroundImage;
        controller.balloon1 = balloon1;
        controller.balloon2 = balloon2;
        controller.balloon3 = balloon3;
        controller.robotSurvivorButton = robotSurvivorButton;
        controller.legend1v1Button = legend1v1Button;
        controller.levelEditorButton = levelEditorButton;
        controller.themesButton = themesButton;
        controller.quitButton = quitButton;
    }

    @Test
    void testRobotSurvivorButtonAction() {
        // Act
        controller.robotSurvivorButton.getOnAction().handle(null);
        
        // Assert
        verify(SceneManager.class).switchScene("MapSelection");
    }

    @Test
    void testLegend1v1ButtonAction() {
        // Act
        controller.legend1v1Button.getOnAction().handle(null);
        
        // Assert
        verify(SceneManager.class).switchScene("LegendGame");
    }

    @Test
    void testLevelEditorButtonAction() {
        // Act
        controller.levelEditorButton.getOnAction().handle(null);
        
        // Assert
        verify(SceneManager.class).switchScene("LevelEditor");
    }

    @Test
    void testThemesButtonAction() {
        // Act
        controller.themesButton.getOnAction().handle(null);
        
        // Assert
        verify(SceneManager.class).switchScene("ThemeSelection");
    }

    @Test
    void testQuitButtonAction() {
        // Act
        controller.quitButton.getOnAction().handle(null);
        
        // Assert
        // Vérifie que System.exit(0) est appelé
        // Note: Ce test peut être difficile à exécuter car il ferme l'application
        // Une alternative serait de mocker System.exit
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
}
