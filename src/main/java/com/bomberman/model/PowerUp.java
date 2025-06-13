/**
 * Représente un bonus collectable
 * Génération aléatoire et application des effets
 */
package com.bomberman.model;

import com.bomberman.model.enums.PowerUpType;

public class PowerUp {
    private PowerUpType type;

    /**
     * Constructeur avec type spécifique
     */
    public PowerUp(PowerUpType type) {
        this.type = type;
    }

    public PowerUpType getType() {
        return type;
    }

    /**
     * Génère un power-up aléatoire avec probabilités équilibrées
     * 33% EXTRA_BOMB, 33% RANGE_UP, 17% LIFE, 17% SPEED
     */
    public static PowerUp random() {
        double r = Math.random();
        if (r < 0.33) return new PowerUp(PowerUpType.EXTRA_BOMB);
        if (r < 0.66) return new PowerUp(PowerUpType.RANGE_UP);
        if (r < 0.83) return new PowerUp(PowerUpType.LIFE);
        return new PowerUp(PowerUpType.SPEED);
    }
}
