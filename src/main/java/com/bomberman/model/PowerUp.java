package com.bomberman.model;

import com.bomberman.model.enums.PowerUpType;

public class PowerUp {
    private PowerUpType type;

    public PowerUp(PowerUpType type) {
        this.type = type;
    }

    public PowerUpType getType() {
        return type;
    }

    // Méthode utilitaire pour générer un power-up aléatoire
    public static PowerUp random() {
        double r = Math.random();
        if (r < 0.33) return new PowerUp(PowerUpType.EXTRA_BOMB);
        if (r < 0.66) return new PowerUp(PowerUpType.RANGE_UP);
        if (r < 0.83) return new PowerUp(PowerUpType.LIFE);
        return new PowerUp(PowerUpType.SPEED);
    }
}