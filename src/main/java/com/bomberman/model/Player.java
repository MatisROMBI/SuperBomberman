package com.bomberman.model;

import com.bomberman.model.enums.Direction;

public class Player {
    private int x, y;
    private int startX, startY;
    private int lives = 6;
    private int maxBombs = 2;      // Peut être augmenté par power-up
    private int bombsAvailable = 2;
    private int explosionRange = 1;
    private int score = 0;
    private boolean isAlive = true;
    private boolean speedBoost = false; // Pour le power-up rapidité
    private int respawnDelay = 1000;    // ms
    private long deathTime = -1;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;
    }

    public void respawnAtStart(Board board) {
        board.getCell(x, y).setHasPlayer(false); // On enlève le joueur de l'ancienne case
        x = startX;
        y = startY;
        board.getCell(x, y).setHasPlayer(true); // On place le joueur sur sa case de départ
        isAlive = true; // Au cas où tu veux gérer une invincibilité temporaire, tu peux l’ajouter ici
    }

    // Déplacement (ne peut PAS traverser bombe ou mur)
    public void move(Direction direction, Board board) {
        int newX = x + direction.getDx();
        int newY = y + direction.getDy();

        if (!isAlive || !board.isValidPosition(newX, newY)) return;
        Cell cell = board.getCell(newX, newY);

        if (cell.isWalkable()) {
            board.getCell(x, y).setHasPlayer(false);
            x = newX; y = newY;
            board.getCell(x, y).setHasPlayer(true);

            // Ramassage power-up
            if (cell.hasPowerUp()) {
                applyPowerUp(cell.getPowerUp());
                cell.setPowerUp(null);
            }
        }
        // Si la case contient une bombe OU un mur => bloqué (ne fait rien)
    }

    // Place une bombe si possible (bloque la case)
    public void placeBomb(Board board) {
        if (bombsAvailable > 0 && board.getCell(x, y).getBomb() == null) {
            Bomb bomb = new Bomb(x, y, explosionRange);
            board.getCell(x, y).setBomb(bomb);
            board.addBomb(bomb);
            bombsAvailable--;
        }
    }

    // Appelée par Board quand la bombe explose
    public void onBombExploded() {
        bombsAvailable = Math.min(bombsAvailable + 1, maxBombs);
    }

    // Prend un dégât
    public void takeDamage() {
        if (!isAlive) return;
        lives--;
        if (lives <= 0) {
            lives = 0;
            isAlive = false;
            System.out.println("GAME OVER !");
            System.out.println("Votre score : " + score);
        }
        deathTime = System.currentTimeMillis();
        System.out.println("Vies restantes : " + lives);
    }

    public void tryRespawn() {
        if (!isAlive && deathTime > 0 && System.currentTimeMillis() - deathTime > respawnDelay) {
            x = startX;
            y = startY;
            isAlive = (lives > 0); // Correction ici !
            deathTime = -1;
        }
    }

    // Score +100 par ennemi tué (plus si combos, à gérer ailleurs)
    public void addScore(int pts) { score += pts; }

    // Power-up
    private void applyPowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case EXTRA_BOMB: maxBombs++; bombsAvailable++; break;
            case RANGE_UP: explosionRange++; break;
            case LIFE: lives++; break;
            case SPEED: speedBoost = true; break;
        }
    }

    private GameController gameController; // à setter lors de l'initialisation

    public void setGameController(GameController gc) { this.gameController = gc; }

    // Getters & Setters compressés
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
