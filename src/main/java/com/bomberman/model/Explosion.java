package com.bomberman.model;

import com.bomberman.model.Music;

public class Explosion {
    private int x, y;
    private long startTime;
    Music explosion = new Music();

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.startTime = System.currentTimeMillis();
        explosion.jouerExplosion();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public long getStartTime() { return startTime; }
    public boolean isExpired(long now, long duration) {
        return now - startTime > duration;
    }
}