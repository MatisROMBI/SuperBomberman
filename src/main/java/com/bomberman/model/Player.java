package com.bomberman.model;

import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;

public class Player {
    private int x, y;
    private int startX, startY;
    private int lives = 6;
    private int maxBombs = 2;
    private int bombsAvailable = 2;
    private int explosionRange = 1;
    private int score = 0;
    private boolean isAlive = true;

    // Gestion du bonus SPEED
    private boolean speedBoost = false;
    private long speedEndTime = 0;
    private long lastMoveTime = 0;

    private int respawnDelay = 1000;
    protected long deathTime = -1;

    // Pour callback fin de partie
    private GameOverListener gameOverListener;

    public Player() {
        this(0, 0); // Position par défaut
    }

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    // --- Supporte Board ET Legend1v1Board
    public void respawnAtStart(Object board) {
        if (board instanceof Legend1v1Board) {
            Legend1v1Board b = (Legend1v1Board) board;
            b.getCell(x, y).setHasPlayer(false);
            x = startX;
            y = startY;
            b.getCell(x, y).setHasPlayer(true);
        } else if (board instanceof Board) {
            Board b = (Board) board;
            b.getCell(x, y).setHasPlayer(false);
            x = startX;
            y = startY;
            b.getCell(x, y).setHasPlayer(true);
        }
        isAlive = true;
    }

    // --- Move (Board ou Legend1v1Board)
    public void move(Direction direction, Object board, GameState gameState) {
        if (gameState != null && gameState != GameState.PLAYING) return;
        if (!isAlive) return;

        long now = System.currentTimeMillis();
        int delay = speedBoost ? 80 : 160;
        if (now - lastMoveTime < delay) return;
        lastMoveTime = now;

        // Fin du bonus speed ?
        if (speedBoost && speedEndTime > 0 && now > speedEndTime) {
            speedBoost = false;
            speedEndTime = 0;
        }

        int newX = x + direction.getDx();
        int newY = y + direction.getDy();

        Cell cell = null;
        boolean valid = false;

        if (board instanceof Legend1v1Board) {
            Legend1v1Board b = (Legend1v1Board) board;
            valid = b.isValidPosition(newX, newY);
            if (valid) cell = b.getCell(newX, newY);
        } else if (board instanceof Board) {
            Board b = (Board) board;
            valid = b.isValidPosition(newX, newY);
            if (valid) cell = b.getCell(newX, newY);
        }
        if (!valid || cell == null) return;

        if (cell.isWalkable() && !cell.hasPlayer()) {
            if (board instanceof Legend1v1Board) {
                Legend1v1Board b = (Legend1v1Board) board;
                b.getCell(x, y).setHasPlayer(false);
                x = newX;
                y = newY;
                b.getCell(x, y).setHasPlayer(true);
            } else if (board instanceof Board) {
                Board b = (Board) board;
                b.getCell(x, y).setHasPlayer(false);
                x = newX;
                y = newY;
                b.getCell(x, y).setHasPlayer(true);
            }
            if (cell.hasPowerUp()) {
                addScore(300);
                applyPowerUp(cell.getPowerUp());
                cell.setPowerUp(null);
            }
        }
    }

    public void placeBomb(Object board, GameState gameState) {
        if (!isAlive) return;
        if (bombsAvailable <= 0) return;

        if (board instanceof Legend1v1Board) {
            Legend1v1Board b = (Legend1v1Board) board;
            if (b.getCell(x, y).getBomb() == null &&
                    (gameState == null || gameState == GameState.PLAYING)) {
                Bomb bomb = new Bomb(x, y, explosionRange, 1);
                b.getCell(x, y).setBomb(bomb);
                b.addBomb(bomb);
                bombsAvailable--;
            }
        } else if (board instanceof Board) {
            Board b = (Board) board;
            if (b.getCell(x, y).getBomb() == null &&
                    (gameState == null || gameState == GameState.PLAYING)) {
                Bomb bomb = new Bomb(x, y, explosionRange, 1);
                b.getCell(x, y).setBomb(bomb);
                b.addBomb(bomb);
                bombsAvailable--;
            }
        }
    }

    public void onBombExploded() {bombsAvailable = Math.min(bombsAvailable + 1, maxBombs);}

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
            case EXTRA_BOMB: maxBombs++; bombsAvailable++; break;
            case RANGE_UP: explosionRange++; break;
            case LIFE: lives++; break;
            case SPEED:
                speedBoost = true;
                speedEndTime = System.currentTimeMillis() + 5000;
                break;
        }
    }

    // --- Méthodes utilitaires pour le mode 1v1 Legend ---
    public void moveUp(Object board)    { move(Direction.UP,    board, null); }
    public void moveDown(Object board)  { move(Direction.DOWN,  board, null); }
    public void moveLeft(Object board)  { move(Direction.LEFT,  board, null); }
    public void moveRight(Object board) { move(Direction.RIGHT, board, null); }
    public void placeBomb(Object board) { placeBomb(board, null); }

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
