/**
 * Représente une bombe posée sur le plateau
 * Gère le timer et l'explosion
 */
package com.bomberman.model;

import com.bomberman.utils.Constants;

public class Bomb {
    private final int x, y;              // Position de la bombe
    private final int explosionRange;    // Portée de l'explosion
    private final long plantTime;        // Moment où la bombe a été posée
    private boolean hasExploded;         // État d'explosion
    private final int owner;             // Propriétaire de la bombe (pour les points)

    // OPTIMISATION: Pré-calcul du moment d'explosion
    private final long explosionTime;

    /**
     * Constructeur complet avec propriétaire
     */
    public Bomb(int x, int y, int explosionRange, int owner) {
        this.x = x;
        this.y = y;
        this.explosionRange = explosionRange;
        this.plantTime = System.currentTimeMillis();
        this.explosionTime = plantTime + Constants.BOMB_TIMER; // Pré-calculé
        this.hasExploded = false;
        this.owner = owner;
    }

    /**
     * Constructeur simplifié sans propriétaire
     */
    public Bomb(int x, int y, int explosionRange) {
        this(x, y, explosionRange, 0);
    }

    /**
     * Mise à jour de l'état de la bombe
     * OPTIMISATION: Évite les calculs répétés
     */
    public void update() {
        if (!hasExploded && System.currentTimeMillis() >= explosionTime) {
            hasExploded = true;
        }
    }

    /**
     * Vérifie si la bombe doit exploser
     */
    public boolean shouldExplode() {
        return !hasExploded && System.currentTimeMillis() >= explosionTime;
    }

    /**
     * Déclenche l'explosion
     */
    public void explode() {
        hasExploded = true;
    }

    // Accesseurs optimisés
    public int getX() { return x; }
    public int getY() { return y; }
    public int getExplosionRange() { return explosionRange; }
    public boolean hasExploded() { return hasExploded; }
    public int getOwner() { return owner; }
    public long getPlantTime() { return plantTime; }

    /**
     * OPTIMISATION: Calcule le temps restant avant explosion
     */
    public long getTimeRemaining() {
        if (hasExploded) return 0;
        long remaining = explosionTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * OPTIMISATION: Calcule le pourcentage d'avancement vers l'explosion
     */
    public float getProgress() {
        if (hasExploded) return 1.0f;
        long elapsed = System.currentTimeMillis() - plantTime;
        return Math.min(1.0f, (float) elapsed / Constants.BOMB_TIMER);
    }
}
