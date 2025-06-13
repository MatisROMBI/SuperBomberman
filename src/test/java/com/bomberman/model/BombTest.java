package com.bomberman.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BombTest {
    private Bomb bomb;
    private static final int TEST_X = 1;
    private static final int TEST_Y = 1;
    private static final int TEST_RANGE = 2;
    private static final int TEST_OWNER = 1;

    @BeforeEach
    void setUp() {
        bomb = new Bomb(TEST_X, TEST_Y, TEST_RANGE, TEST_OWNER);
    }

    @Test
    void testBombInitialization() {
        assertNotNull(bomb);
        assertEquals(TEST_X, bomb.getX());
        assertEquals(TEST_Y, bomb.getY());
        assertEquals(TEST_RANGE, bomb.getExplosionRange());
        assertEquals(TEST_OWNER, bomb.getOwner());
        assertFalse(bomb.hasExploded());
    }

    @Test
    void testBombExplosion() {
        assertFalse(bomb.hasExploded());
        bomb.explode();
        assertTrue(bomb.hasExploded());
    }

    @Test
    void testBombProgress() {
        float initialProgress = bomb.getProgress();
        assertTrue(initialProgress >= 0.0f && initialProgress <= 1.0f);
        
        bomb.explode();
        assertEquals(1.0f, bomb.getProgress());
    }

    @Test
    void testBombTimeRemaining() {
        long initialTime = bomb.getTimeRemaining();
        assertTrue(initialTime > 0);
        
        bomb.explode();
        assertEquals(0, bomb.getTimeRemaining());
    }
} 