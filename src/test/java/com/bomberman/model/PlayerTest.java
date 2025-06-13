package com.bomberman.model;

import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;
import com.bomberman.model.enums.PowerUpType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests unitaires pour la classe Player")
class PlayerTest {

    private Player player;
    
    @Mock
    private Board mockBoard;
    
    @Mock
    private Cell mockCell;
    
    @Mock
    private GameOverListener mockGameOverListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        player = new Player(5, 5); // Position de départ (5,5)
        player.setGameOverListener(mockGameOverListener);
    }

    @Test
    @DisplayName("Test de l'initialisation du joueur")
    void testPlayerInitialization() {
        assertEquals(5, player.getX());
        assertEquals(5, player.getY());
        assertEquals(5, player.getStartX());
        assertEquals(5, player.getStartY());
        assertEquals(6, player.getLives());
        assertEquals(2, player.getBombsAvailable());
        assertEquals(2, player.getMaxBombs());
        assertEquals(1, player.getExplosionRange());
        assertEquals(0, player.getScore());
        assertTrue(player.isAlive());
        assertFalse(player.hasSpeedBoost());
    }

    @Test
    @DisplayName("Test du mouvement du joueur")
    void testPlayerMovement() {
        // Mock du plateau et des cellules
        when(mockBoard.isValidPosition(6, 5)).thenReturn(true);
        when(mockBoard.getCell(5, 5)).thenReturn(mockCell);
        when(mockBoard.getCell(6, 5)).thenReturn(mockCell);
        when(mockCell.isWalkable()).thenReturn(true);
        when(mockCell.hasPlayer()).thenReturn(false);
        when(mockCell.hasPowerUp()).thenReturn(false);

        // Test du mouvement vers la droite
        player.move(Direction.RIGHT, mockBoard, GameState.PLAYING);
        
        assertEquals(6, player.getX());
        assertEquals(5, player.getY());
        
        // Vérifier que les méthodes du board ont été appelées
        verify(mockBoard).isValidPosition(6, 5);
        verify(mockBoard).getCell(5, 5);
        verify(mockBoard).getCell(6, 5);
        verify(mockCell).setHasPlayer(false);
        verify(mockCell).setHasPlayer(true);
    }

    @Test
    @DisplayName("Test du mouvement bloqué par un mur")
    void testPlayerMovementBlocked() {
        // Mock d'une cellule non-walkable
        when(mockBoard.isValidPosition(6, 5)).thenReturn(true);
        when(mockBoard.getCell(6, 5)).thenReturn(mockCell);
        when(mockCell.isWalkable()).thenReturn(false);

        // Tenter de se déplacer vers une cellule bloquée
        player.move(Direction.RIGHT, mockBoard, GameState.PLAYING);
        
        // Le joueur ne doit pas bouger
        assertEquals(5, player.getX());
        assertEquals(5, player.getY());
    }

    @Test
    @DisplayName("Test de la collecte de power-up")
    void testPowerUpCollection() {
        PowerUp powerUp = new PowerUp(PowerUpType.EXTRA_BOMB);
        
        when(mockBoard.isValidPosition(6, 5)).thenReturn(true);
        when(mockBoard.getCell(5, 5)).thenReturn(mockCell);
        when(mockBoard.getCell(6, 5)).thenReturn(mockCell);
        when(mockCell.isWalkable()).thenReturn(true);
        when(mockCell.hasPlayer()).thenReturn(false);
        when(mockCell.hasPowerUp()).thenReturn(true);
        when(mockCell.getPowerUp()).thenReturn(powerUp);

        int initialBombs = player.getMaxBombs();
        int initialScore = player.getScore();

        player.move(Direction.RIGHT, mockBoard, GameState.PLAYING);

        // Vérifier que les stats ont été améliorées
        assertEquals(initialBombs + 1, player.getMaxBombs());
        assertEquals(initialScore + 300, player.getScore());
        verify(mockCell).setPowerUp(null);
    }

    @Test
    @DisplayName("Test de la pose de bombe")
    void testBombPlacement() {
        when(mockBoard.getCell(5, 5)).thenReturn(mockCell);
        when(mockCell.getBomb()).thenReturn(null);

        int initialBombs = player.getBombsAvailable();
        
        player.placeBomb(mockBoard, GameState.PLAYING);
        
        assertEquals(initialBombs - 1, player.getBombsAvailable());
        verify(mockBoard).addBomb(any(Bomb.class));
        verify(mockCell).setBomb(any(Bomb.class));
    }

    @Test
    @DisplayName("Test de la pose de bombe impossible (plus de bombes)")
    void testBombPlacementNoBombsLeft() {
        player.setBombsAvailable(0);
        
        player.placeBomb(mockBoard, GameState.PLAYING);
        
        // Aucune bombe ne doit être posée
        verify(mockBoard, never()).addBomb(any(Bomb.class));
        verify(mockCell, never()).setBomb(any(Bomb.class));
    }

    @Test
    @DisplayName("Test des dégâts subis par le joueur")
    void testPlayerTakeDamage() {
        int initialLives = player.getLives();
        
        player.takeDamage();
        
        assertEquals(initialLives - 1, player.getLives());
        assertTrue(player.isAlive());
        assertTrue(player.getDeathTime() > 0);
    }

    @Test
    @DisplayName("Test de la mort du joueur")
    void testPlayerDeath() {
        // Réduire les vies à 1
        player.setLives(1);
        
        player.takeDamage();
        
        assertEquals(0, player.getLives());
        assertFalse(player.isAlive());
        verify(mockGameOverListener).onGameOver(player.getScore());
    }

    @Test
    @DisplayName("Test du respawn du joueur")
    void testPlayerRespawn() {
        // Déplacer le joueur
        player.setX(10);
        player.setY(10);
        
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn(mockCell);
        
        player.respawnAtStart(mockBoard);
        
        assertEquals(5, player.getX()); // Position de départ
        assertEquals(5, player.getY());
        assertTrue(player.isAlive());
        verify(mockCell, times(2)).setHasPlayer(anyBoolean());
    }

    @Test
    @DisplayName("Test de l'ajout de score")
    void testScoreAddition() {
        int initialScore = player.getScore();
        
        player.addScore(100);
        
        assertEquals(initialScore + 100, player.getScore());
    }

    @Test
    @DisplayName("Test du power-up LIFE")
    void testLifePowerUp() {
        PowerUp lifePowerUp = new PowerUp(PowerUpType.LIFE);
        int initialLives = player.getLives();
        
        player.applyPowerUp(lifePowerUp);
        
        assertEquals(initialLives + 1, player.getLives());
    }

    @Test
    @DisplayName("Test du power-up RANGE_UP")
    void testRangeUpPowerUp() {
        PowerUp rangeUpPowerUp = new PowerUp(PowerUpType.RANGE_UP);
        int initialRange = player.getExplosionRange();
        
        player.applyPowerUp(rangeUpPowerUp);
        
        assertEquals(initialRange + 1, player.getExplosionRange());
    }

    @Test
    @DisplayName("Test du power-up SPEED")
    void testSpeedPowerUp() {
        PowerUp speedPowerUp = new PowerUp(PowerUpType.SPEED);
        
        player.applyPowerUp(speedPowerUp);
        
        assertTrue(player.hasSpeedBoost());
    }

    @Test
    @DisplayName("Test de l'explosion de bombe (récupération)")
    void testBombExploded() {
        player.setBombsAvailable(0);
        
        player.onBombExploded();
        
        assertEquals(1, player.getBombsAvailable());
    }

    @Test
    @DisplayName("Test du mouvement en mode pause")
    void testMovementWhilePaused() {
        when(mockBoard.isValidPosition(6, 5)).thenReturn(true);
        
        // Tenter de bouger en mode pause
        player.move(Direction.RIGHT, mockBoard, GameState.PAUSED);
        
        // Le joueur ne doit pas bouger
        assertEquals(5, player.getX());
        assertEquals(5, player.getY());
        verify(mockBoard, never()).getCell(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Test du mouvement d'un joueur mort")
    void testMovementWhileDead() {
        player.setIsAlive(false);
        
        when(mockBoard.isValidPosition(6, 5)).thenReturn(true);
        
        player.move(Direction.RIGHT, mockBoard, GameState.PLAYING);
        
        // Le joueur mort ne doit pas bouger
        assertEquals(5, player.getX());
        assertEquals(5, player.getY());
        verify(mockBoard, never()).getCell(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Test des méthodes de mouvement rapide")
    void testQuickMovementMethods() {
        when(mockBoard.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn(mockCell);
        when(mockCell.isWalkable()).thenReturn(true);
        when(mockCell.hasPlayer()).thenReturn(false);
        when(mockCell.hasPowerUp()).thenReturn(false);

        // Test moveUp
        player.moveUp(mockBoard);
        assertEquals(4, player.getY());

        // Remettre en position
        player.setY(5);
        
        // Test moveDown
        player.moveDown(mockBoard);
        assertEquals(6, player.getY());

        // Remettre en position
        player.setY(5);
        
        // Test moveLeft
        player.moveLeft(mockBoard);
        assertEquals(4, player.getX());

        // Remettre en position
        player.setX(5);
        
        // Test moveRight
        player.moveRight(mockBoard);
        assertEquals(6, player.getX());
    }
}