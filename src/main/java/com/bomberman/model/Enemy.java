package com.bomberman.model;

import com.bomberman.model.enums.Direction;
import java.util.Random;

public class Enemy {
    private int x, y;
    private boolean isAlive;
    private Direction currentDirection;
    private Random random;
    private long lastMoveTime;
    private static final int MOVE_DELAY = 800; // Plus rapide que joueur

    public Enemy(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.isAlive = true;
        this.random = new Random();
        this.currentDirection = Direction.values()[random.nextInt(4)];
        this.lastMoveTime = System.currentTimeMillis();
    }

    public void update(Board board) {
        if (!isAlive) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime >= MOVE_DELAY) {
            move(board);
            lastMoveTime = currentTime;
        }
    }

    private void move(Board board) {
        // IA simple (aléatoire)
        Direction[] directions = Direction.values();
        Direction newDirection = directions[random.nextInt(4)];
        int newX = x + newDirection.getDx();
        int newY = y + newDirection.getDy();
        if (board.isValidPosition(newX, newY) && board.getCell(newX, newY).isWalkable()) {
            board.getCell(x, y).setHasEnemy(false);
            this.x = newX;
            this.y = newY;
            board.getCell(x, y).setHasEnemy(true);
            this.currentDirection = newDirection;
        }
    }

    public void die() {
        isAlive = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isAlive() { return isAlive; }
}