package com.bomberman.utils;

public class Constants {
    public static final int BOARD_WIDTH = 15;
    public static final int BOARD_HEIGHT = 13;
    public static final int CELL_SIZE = 40;
    public static final int HUD_HEIGHT = 56;
    public static final int WINDOW_WIDTH = BOARD_WIDTH * CELL_SIZE;
    public static final int WINDOW_HEIGHT = BOARD_HEIGHT * CELL_SIZE + HUD_HEIGHT;
    public static final int BOMB_TIMER = 3000; // 3 secondes
    public static final int EXPLOSION_DURATION = 1000; // 1 seconde

    // OPTIMISATION: Réduction de la fréquence de mise à jour du jeu
    public static final int GAME_SPEED = 8; // Changé de 16 à 8 (plus fluide)

    // NOUVELLES CONSTANTES pour optimiser les mouvements
    public static final int PLAYER_MOVE_DELAY = 100; // ms entre chaque mouvement
    public static final int BOT_MOVE_DELAY = 400; // Réduit de 800 à 400ms
    public static final int ENEMY_MOVE_DELAY = 300; // Pour les ennemis Legend
}
