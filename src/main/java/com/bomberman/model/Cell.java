/**
 * Représente une cellule individuelle du plateau de jeu
 * Contient toutes les informations d'état d'une case
 */
package com.bomberman.model;

import com.bomberman.model.enums.CellType;

public class Cell {
    private CellType type;          // Type de la cellule
    private boolean hasPlayer;      // Présence d'un joueur
    private boolean hasEnemy;       // Présence d'un ennemi
    private Bomb bomb;              // Bombe présente (null si aucune)
    private PowerUp powerUp;        // Bonus présent (null si aucun)

    /**
     * Constructeur avec type de cellule
     */
    public Cell(CellType type) {
        this.type = type;
        this.hasPlayer = false;
        this.hasEnemy = false;
    }

    // Accesseurs pour le type de cellule
    public CellType getType() { return type; }
    public void setType(CellType type) { this.type = type; }

    // Gestion de la présence de joueur
    public boolean hasPlayer() { return hasPlayer; }
    public void setHasPlayer(boolean hasPlayer) { this.hasPlayer = hasPlayer; }

    // Gestion de la présence d'ennemi
    public boolean hasEnemy() { return hasEnemy; }
    public void setHasEnemy(boolean hasEnemy) { this.hasEnemy = hasEnemy; }

    // Gestion des bombes
    public Bomb getBomb() { return bomb; }
    public void setBomb(Bomb bomb) { this.bomb = bomb; }

    // Gestion des bonus
    public PowerUp getPowerUp() { return powerUp; }
    public void setPowerUp(PowerUp powerUp) { this.powerUp = powerUp; }
    public boolean hasPowerUp() { return powerUp != null; }

    /**
     * Détermine si la cellule est marchable
     * Une cellule est marchable si elle est vide et sans bombe
     */
    public boolean isWalkable() {
        return type == CellType.EMPTY && bomb == null;
    }
}