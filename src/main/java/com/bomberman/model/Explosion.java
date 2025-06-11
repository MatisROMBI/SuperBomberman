package com.bomberman.model;

public class Explosion {
    private final int x, y;
    private final long startTime;
    private final long duration;
    private final long endTime; // OPTIMISATION: Pré-calculer la fin
    private boolean isFinished;

    public Explosion(int x, int y) {
        this(x, y, 1000); // Durée par défaut de 1 seconde
    }

    public Explosion(int x, int y, long duration) {
        this.x = x;
        this.y = y;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
        this.endTime = startTime + duration; // OPTIMISATION: Pré-calculer
        this.isFinished = false;
    }

    // OPTIMISATION: Méthode d'update pour éviter les calculs constants
    public void update() {
        if (!isFinished && System.currentTimeMillis() >= endTime) {
            isFinished = true;
        }
    }

    public boolean isExpired(long now, long maxDuration) {
        return now >= endTime;
    }

    public boolean isFinished() {
        return isFinished || System.currentTimeMillis() >= endTime;
    }

    // Getters optimisés
    public int getX() { return x; }
    public int getY() { return y; }
    public long getStartTime() { return startTime; }
    public long getDuration() { return duration; }

    // OPTIMISATION: Méthode pour obtenir le temps restant
    public long getTimeRemaining() {
        if (isFinished) return 0;
        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    // OPTIMISATION: Méthode pour obtenir l'intensité de l'explosion (pour les effets visuels)
    public float getIntensity() {
        if (isFinished) return 0.0f;
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = (float) elapsed / duration;

        // Courbe d'intensité : forte au début, puis diminue
        return Math.max(0.0f, 1.0f - progress);
    }

    // OPTIMISATION: Méthode pour savoir si l'explosion est active à une position
    public boolean affects(int posX, int posY) {
        return !isFinished() && this.x == posX && this.y == posY;
    }
}