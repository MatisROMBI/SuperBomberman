package com.bomberman.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    private Player player;

    @BeforeEach
    void setUp() {
        board = new Board();
        player = board.getPlayer();
    }

    @Test
    void testBoardInitialization() {
        assertNotNull(board);
        assertNotNull(board.getPlayer());
        assertNotNull(board.getBots());
        assertNotNull(board.getBombs());
        assertNotNull(board.getExplosions());
    }

    @Test
    void testBoardDimensions() {
        assertEquals(15, board.getWidth());
        assertEquals(13, board.getHeight());
    }

    @Test
    void testCellAccess() {
        assertNotNull(board.getCell(1, 1));
        assertNotNull(board.getCell(board.getWidth() - 2, board.getHeight() - 2));
    }

    @Test
    void testValidPosition() {
        assertTrue(board.isValidPosition(1, 1));
        assertFalse(board.isValidPosition(-1, 1));
        assertFalse(board.isValidPosition(1, -1));
        assertFalse(board.isValidPosition(board.getWidth(), 1));
        assertFalse(board.isValidPosition(1, board.getHeight()));
    }

    @Test
    void testPlaceBomb() {
        int x = 1;
        int y = 1;
        board.placeBomb(x, y);
        assertTrue(board.getCells()[y][x].hasBomb());
    }

    @Test
    void testExplosion() {
        int x = 1;
        int y = 1;
        board.placeBomb(x, y);
        board.update();
        assertFalse(board.getCells()[y][x].hasBomb());
    }
} 