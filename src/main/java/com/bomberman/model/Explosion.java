/**
 * Représente une explosion temporaire
 * Gère la durée et les effets visuels
 */
package com.bomberman.model;

public class Explosion {
    private final int x, y;              // Position de l'explosion
    private final long startTime;        // Moment de début
    private final long duration;         // Durée de l'explosion
    private final long endTime;          // OPTIMISATION: Moment de fin pré-calculé
    private boolean isFinished;          // État de fin

    /**
     * Constructeur avec durée par défaut
     */
    public Explosion(int x, int y) {
        this(x, y, 1000); // 1 seconde par défaut
    }

    /**
     * Constructeur avec durée personnalisée
     */
    public Explosion(int x, int y, long duration) {
        this.x = x;
        this.y = y;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
        this.endTime = startTime + duration; // OPTIMISATION: Pré-calculé
        this.isFinished = false;
    }

    /**
     * OPTIMISATION: Mise à jour pour éviter les calculs constants
     */
    public void update() {
        if (!isFinished && System.currentTimeMillis() >= endTime) {
            isFinished = true;
        }
    }

    /**
     * Vérifie si l'explosion est expirée
     */
    public boolean isExpired(long now, long maxDuration) {
        return now >= endTime;
    }

    /**
     * Vérifie si l'explosion est terminée
     */
    public boolean isFinished() {
        return isFinished || System.currentTimeMillis() >= endTime;
    }

    // Accesseurs optimisés
    public int getX() { return x; }
    public int getY() { return y; }
    public long getStartTime() { return startTime; }
    public long getDuration() { return duration; }

    /**
     * OPTIMISATION: Calcule le temps restant
     */
    public long getTimeRemaining() {
        if (isFinished) return 0;
        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * OPTIMISATION: Calcule l'intensité pour les effets visuels
     * Retourne une valeur entre 0 et 1 (forte au début, diminue)
     */
    public float getIntensity() {
        if (isFinished) return 0.0f;
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = (float) elapsed / duration;
        return Math.max(0.0f, 1.0f - progress);
    }

    /**
     * OPTIMISATION: Vérifie si l'explosion affecte une position donnée
     */
    public boolean affects(int posX, int posY) {
        return !isFinished() && this.x == posX && this.y == posY;
    }
}
