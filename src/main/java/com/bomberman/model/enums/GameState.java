/**
 * États possibles du jeu
 */
package com.bomberman.model.enums;

public enum GameState {
    PLAYING,    // Jeu en cours
    GAME_OVER,  // Fin de partie (défaite)
    VICTORY,    // Victoire
    PAUSED      // Jeu en pause
}
