/**
 * Constantes utilisées dans tout le projet
 * Centralisées pour faciliter les modifications
 */
package com.bomberman.utils;

public class Constants {
    // Dimensions du plateau de jeu
    public static final int BOARD_WIDTH = 15;   // Largeur en cellules
    public static final int BOARD_HEIGHT = 13;  // Hauteur en cellules
    public static final int CELL_SIZE = 40;     // Taille d'une cellule en pixels

    // Interface utilisateur
    public static final int HUD_HEIGHT = 56;    // Hauteur de l'interface en pixels
    public static final int WINDOW_WIDTH = BOARD_WIDTH * CELL_SIZE;   // 600px
    public static final int WINDOW_HEIGHT = BOARD_HEIGHT * CELL_SIZE + HUD_HEIGHT; // 576px

    // Timers de jeu
    public static final int BOMB_TIMER = 3000;         // Temps avant explosion (3 secondes)
    public static final int EXPLOSION_DURATION = 1000; // Durée d'une explosion (1 seconde)

    // Optimisations de performance
    public static final int GAME_SPEED = 8;            // Vitesse de mise à jour (plus fluide)
    public static final int PLAYER_MOVE_DELAY = 100;   // Délai entre mouvements joueur (ms)
    public static final int BOT_MOVE_DELAY = 400;      // Délai entre mouvements bot (ms)
    public static final int ENEMY_MOVE_DELAY = 300;    // Délai entre mouvements ennemi (ms)
}
