/**
 * Directions de mouvement avec leurs coordonnées
 */
package com.bomberman.model.enums;

public enum Direction {
    UP(0, -1),      // Haut : décalage Y négatif
    DOWN(0, 1),     // Bas : décalage Y positif
    LEFT(-1, 0),    // Gauche : décalage X négatif
    RIGHT(1, 0);    // Droite : décalage X positif

    private final int dx, dy;

    /**
     * Constructeur avec les décalages de coordonnées
     */
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    // Accesseurs pour les décalages
    public int getDx() { return dx; }
    public int getDy() { return dy; }
}
