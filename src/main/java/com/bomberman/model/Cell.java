package com.bomberman.model;

import com.bomberman.model.enums.CellType;

public class Cell {
    private CellType type;
    private boolean hasPlayer;
    private boolean hasEnemy;
    private Bomb bomb;
    private PowerUp powerUp; // AJOUT

    public Cell(CellType type) {
        this.type = type;
        this.hasPlayer = false;
        this.hasEnemy = false;
    }

    public CellType getType() { return type; }
    public void setType(CellType type) { this.type = type; }

    public boolean hasPlayer() { return hasPlayer; }
    public void setHasPlayer(boolean hasPlayer) { this.hasPlayer = hasPlayer; }

    public boolean hasEnemy() { return hasEnemy; }
    public void setHasEnemy(boolean hasEnemy) { this.hasEnemy = hasEnemy; }

    public Bomb getBomb() { return bomb; }
    public void setBomb(Bomb bomb) { this.bomb = bomb; }

    public PowerUp getPowerUp() { return powerUp; }
    public void setPowerUp(PowerUp powerUp) { this.powerUp = powerUp; }
    public boolean hasPowerUp() { return powerUp != null; }

    public boolean isWalkable() {
        return type == CellType.EMPTY && bomb == null;
    }
}