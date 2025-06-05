package com.bomberman.model;

import com.bomberman.model.enums.Direction;

public class Player {
    private int x, y;
    private int startX, startY;
    private int lives = 6;
    private int maxBombs = 2;
    private int bombsAvailable = 2;
    private int explosionRange = 1;
    private int score = 0;
    private boolean isAlive = true;
    private boolean speedBoost = false;
    private int respawnDelay = 1000;
    private long deathTime = -1;

    // Ajout du listener
    private GameOverListener gameOverListener;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    public void respawnAtStart(Board board) {
        board.getCell(x, y).setHasPlayer(false);
        x = startX;
        y = startY;
        board.getCell(x, y).setHasPlayer(true);
        isAlive = true;
    }

    public void move(Direction direction, Board board) {
        int newX = x + direction.getDx();
        int newY = y + direction.getDy();

        if (!isAlive || !board.isValidPosition(newX, newY)) return;
        Cell cell = board.getCell(newX, newY);

        if (cell.isWalkable()) {
            board.getCell(x, y).setHasPlayer(false);
            x = newX;
            y = newY;
            board.getCell(x, y).setHasPlayer(true);

            if (cell.hasPowerUp()) {
                applyPowerUp(cell.getPowerUp());
                cell.setPowerUp(null);
            }
        }
    }

    public void placeBomb(Board board) {
        if (bombsAvailable > 0 && board.getCell(x, y).getBomb() == null) {
            Bomb bomb = new Bomb(x, y, explosionRange);
            board.getCell(x, y).setBomb(bomb);
            board.addBomb(bomb);
            bombsAvailable--;
        }
    }

    public void onBombExploded() {
        bombsAvailable = Math.min(bombsAvailable + 1, maxBombs);
    }

    public void takeDamage() {
        if (!isAlive) return;
        lives--;
        if (lives <= 0) {
            lives = 0;
            isAlive = false;
            if (gameOverListener != null) {
                gameOverListener.onGameOver(score);
            }
        }
        deathTime = System.currentTimeMillis();
    }

    public void tryRespawn() {
        if (!isAlive && deathTime > 0 && System.currentTimeMillis() - deathTime > respawnDelay) {
            x = startX;
            y = startY;
            isAlive = (lives > 0);
            deathTime = -1;
        }
    }

    public void addScore(int pts) { score += pts; }

    private void applyPowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case EXTRA_BOMB: maxBombs++; bombsAvailable++; break;
            case RANGE_UP: explosionRange++; break;
            case LIFE: lives++; break;
            case SPEED: speedBoost = true; break;
        }
    }

    // Getters & Setters compressÃ©s
    public int getX() { return x; }
    public int getY() { return y; }
    public int getLives() { return lives; }
    public int getBombsAvailable() { return bombsAvailable; }
    public int getMaxBombs() { return maxBombs; }
    public int getExplosionRange() { return explosionRange; }
    public boolean isAlive() { return isAlive; }
    public int getScore() { return score; }
    public boolean hasSpeedBoost() { return speedBoost; }
    public void setSpeedBoost(boolean s) { speedBoost = s; }
}