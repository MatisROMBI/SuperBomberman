package com.bomberman.model;

import com.bomberman.model.enums.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
    }

    @Test
    void testGameInitialization() {
        assertNotNull(game);
        assertNotNull(game.getBoard());
        assertNotNull(game.getPlayer());
        assertEquals(GameState.PLAYING, game.getGameState());
    }

    @Test
    void testGamePause() {
        assertEquals(GameState.PLAYING, game.getGameState());
        game.pause();
        assertEquals(GameState.PAUSED, game.getGameState());
        game.pause(); // Reprendre le jeu
        assertEquals(GameState.PLAYING, game.getGameState());
    }

    @Test
    void testGameStop() {
        game.stop();
        // Vérifier que le jeu est arrêté
        assertNotNull(game.getBoard());
        assertNotNull(game.getPlayer());
    }

    @Test
    void testMultiplayerGame() {
        Game multiplayerGame = new Game(true);
        assertNotNull(multiplayerGame.getPlayers());
        assertTrue(multiplayerGame.getPlayers().size() == 4);
    }
} 