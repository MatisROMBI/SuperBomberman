package com.bomberman.model;

import com.bomberman.utils.Constants;

public class Bomb {
    private int x, y;
    private int explosionRange;
    private long plantTime;
    private boolean hasExploded;

    public Bomb(int x, int y, int explosionRange) {
        this.x = x;
        this.y = y;
        this.explosionRange = explosionRange;
        this.plantTime = System.currentTimeMillis();
        this.hasExploded = false;
    }

    public boolean shouldExplode() {
        return !hasExploded && (System.currentTimeMillis() - plantTime) >= Constants.BOMB_TIMER;
    }

    public void explode() {
        hasExploded = true;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getExplosionRange() { return explosionRange; }
    public boolean hasExploded() { return hasExploded; }
}