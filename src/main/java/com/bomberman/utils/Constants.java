package com.bomberman.utils;

public class Constants {
    public static final int BOARD_WIDTH = 15;
    public static final int BOARD_HEIGHT = 13;
    public static final int CELL_SIZE = 40;
    public static final int HUD_HEIGHT = 56; // Ajouté : bandeau score
    public static final int WINDOW_WIDTH = BOARD_WIDTH * CELL_SIZE;
    public static final int WINDOW_HEIGHT = BOARD_HEIGHT * CELL_SIZE + HUD_HEIGHT;

    public static final int BOMB_TIMER = 3000; // 3 secondes
    public static final int EXPLOSION_DURATION = 1000; // 1 seconde
    public static final int GAME_SPEED = 16; // 60 FPS
}