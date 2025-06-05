package com.bomberman.model;

import com.bomberman.model.enums.Direction;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy {
    private int x, y;
    private boolean isAlive;
    private Direction currentDirection;
    private Random random;
    private long lastMoveTime;
    private static final int MOVE_DELAY = 800; // ms

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
        Direction[] directions = Direction.values();
        List<Direction> possibleMoves = new ArrayList<>();

        for (Direction dir : directions) {
            int nx = x + dir.getDx();
            int ny = y + dir.getDy();
            if (board.isValidPosition(nx, ny)) {
                Cell targetCell = board.getCell(nx, ny);
                if (targetCell.isWalkable() && !targetCell.hasEnemy()) {
                    possibleMoves.add(dir);
                }
            }
        }

        if (!possibleMoves.isEmpty()) {
            Direction chosen = possibleMoves.get(random.nextInt(possibleMoves.size()));
            int newX = x + chosen.getDx();
            int newY = y + chosen.getDy();

            board.getCell(x, y).setHasEnemy(false);
            x = newX;
            y = newY;
            board.getCell(x, y).setHasEnemy(true);
            currentDirection = chosen;
        }
        // Sinon il ne bouge pas
    }

    public void die() {
        isAlive = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isAlive() { return isAlive; }
}