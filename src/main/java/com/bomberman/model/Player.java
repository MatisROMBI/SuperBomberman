package com.bomberman.model;

import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;

public class Player {
    private int x, y;
    private int startX, startY;
    private int lives = 6; // <-- CORRIGÉ : 6 vies de départ
    private int maxBombs = 2;
    private int bombsAvailable = 2;
    private int explosionRange = 1;
    private int score = 0;
    private boolean isAlive = true;
    private boolean speedBoost = false;
    private int respawnDelay = 1000;
    protected long deathTime = -1;

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

    public void move(Direction direction, Board board, GameState gameState) {
        if (gameState != GameState.PLAYING) return;
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
                addScore(300); // +300 points pour un bonus
                applyPowerUp(cell.getPowerUp());
                cell.setPowerUp(null);
            }
        }
    }

    public void placeBomb(Board board, GameState gameState) {
        if (bombsAvailable > 0 && board.getCell(x, y).getBomb() == null && gameState == GameState.PLAYING) {
            Bomb bomb = new Bomb(x, y, explosionRange, 1);
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

    protected void applyPowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case EXTRA_BOMB:
                maxBombs++;
                bombsAvailable++;
                break;
            case RANGE_UP:
                explosionRange++;
                break;
            case LIFE:
                lives++;
                break;
            case SPEED:
                speedBoost = true;
                break;
        }
    }

    // ----------- Getters & Setters complets -----------

    public int getX() { return x; }
    public void setX(int v) { this.x = v; }

    public int getY() { return y; }
    public void setY(int v) { this.y = v; }

    public int getStartX() { return startX; }
    public int getStartY() { return startY; }

    public int getLives() { return lives; }
    public void setLives(int l) { this.lives = l; }

    public int getBombsAvailable() { return bombsAvailable; }
    public void setBombsAvailable(int b) { this.bombsAvailable = b; }

    public int getMaxBombs() { return maxBombs; }
    public void setMaxBombs(int m) { this.maxBombs = m; }

    public int getExplosionRange() { return explosionRange; }
    public void setExplosionRange(int r) { this.explosionRange = r; }

    public boolean isAlive() { return isAlive; }
    public void setIsAlive(boolean b) { this.isAlive = b; }

    public int getScore() { return score; }
    public void setScore(int s) { this.score = s; }

    public boolean hasSpeedBoost() { return speedBoost; }
    public void setSpeedBoost(boolean s) { speedBoost = s; }

    public long getDeathTime() { return deathTime; }
    public void setDeathTime(long t) { deathTime = t; }
}
