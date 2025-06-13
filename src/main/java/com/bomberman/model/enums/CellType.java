/**
 * Types de cellules possibles sur le plateau de jeu
 */
package com.bomberman.model.enums;

public enum CellType {
    EMPTY,              // Cellule vide (marchable)
    WALL,               // Mur fixe (non destructible)
    DESTRUCTIBLE_WALL,  // Mur destructible (peut être cassé par les bombes)
    BOMB,               // Cellule contenant une bombe
    EXPLOSION,          // Cellule en cours d'explosion
    PLAYER,             // Cellule occupée par un joueur
    ENEMY               // Cellule occupée par un ennemi
}
