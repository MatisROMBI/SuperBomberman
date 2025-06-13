/**
 * Interface de callback pour signaler la fin de partie
 */
package com.bomberman.model;

public interface GameOverListener {
    /**
     * Méthode appelée lors de la fin de partie
     * @param score Score final du joueur
     */
    void onGameOver(int score);
}
