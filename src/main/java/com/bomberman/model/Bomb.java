package com.bomberman.model;

import com.bomberman.utils.Constants;

public class Bomb {
    private final int x, y;
    private final int explosionRange;
    private final long plantTime;
    private boolean hasExploded;
    private final int owner;

    // Cache pour éviter les calculs répétés
    private final long explosionTime;

    public Bomb(int x, int y, int explosionRange, int owner) {
        this.x = x;
        this.y = y;
        this.explosionRange = explosionRange;
        this.plantTime = System.currentTimeMillis();
        this.explosionTime = plantTime + Constants.BOMB_TIMER; // Pré-calculer
        this.hasExploded = false;
        this.owner = owner;
    }

    public Bomb(int x, int y, int explosionRange) {
        this(x, y, explosionRange, 0);
    }

    // Méthode d'update pour éviter les calculs constants
    public void update() {
        if (!hasExploded && System.currentTimeMillis() >= explosionTime) {
            hasExploded = true;
        }
    }

    public boolean shouldExplode() {
        return !hasExploded && System.currentTimeMillis() >= explosionTime;
    }

    public void explode() {
        hasExploded = true;
    }

    // Getters optimisés
    public int getX() { return x; }
    public int getY() { return y; }
    public int getExplosionRange() { return explosionRange; }
    public boolean hasExploded() { return hasExploded; }
    public int getOwner() { return owner; }
    public long getPlantTime() { return plantTime; }

    // Méthode pour obtenir le temps restant
    public long getTimeRemaining() {
        if (hasExploded) return 0;
        long remaining = explosionTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    // Méthode pour obtenir le pourcentage d'avancement
    public float getProgress() {
        if (hasExploded) return 1.0f;
        long elapsed = System.currentTimeMillis() - plantTime;
        return Math.min(1.0f, (float) elapsed / Constants.BOMB_TIMER);
    }
}