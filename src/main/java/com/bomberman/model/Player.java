/**
 * Classe représentant le joueur principal
 * Gère les statistiques, mouvements, bombes et bonus
 */
package com.bomberman.model;

import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;

public class Player {
    // Position et spawn
    private int x, y;                    // Position actuelle
    private int startX, startY;          // Position de départ (pour respawn)

    // Statistiques de base
    private int lives = 6;               // Vies (6 par défaut)
    private int maxBombs = 2;            // Nombre maximum de bombes
    private int bombsAvailable = 2;      // Bombes actuellement disponibles
    private int explosionRange = 1;      // Portée des explosions
    private int score = 0;               // Score du joueur
    private boolean isAlive = true;      // État de vie

    // Bonus temporaire SPEED
    private boolean speedBoost = false;  // Boost de vitesse actif
    private long speedEndTime = 0;       // Fin du boost de vitesse

    // OPTIMISATION: Contrôle de la fluidité des mouvements
    private long lastMoveTime = 0;
    private static final int NORMAL_MOVE_DELAY = 100; // Délai normal (réduit pour fluidité)
    private static final int SPEED_BOOST_DELAY = 60;  // Délai avec boost de vitesse

    // Gestion du respawn
    private int respawnDelay = 1000;     // Délai de respawn (1 seconde)
    protected long deathTime = -1;       // Moment de la mort

    // Callback pour fin de partie
    private GameOverListener gameOverListener;

    /**
     * Constructeur par défaut
     */
    public Player() {
        this(0, 0);
    }

    /**
     * Constructeur avec position de départ
     */
    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;
    }

    /**
     * Définit le listener pour les fins de partie
     */
    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    /**
     * Respawn du joueur à sa position de départ
     * Supporte les deux types de plateau (Board et Legend1v1Board)
     */
    public void respawnAtStart(Object board) {
        if (board instanceof Legend1v1Board) {
            Legend1v1Board b = (Legend1v1Board) board;
            b.getCell(x, y).setHasPlayer(false);  // Libère l'ancienne position
            x = startX;
            y = startY;
            b.getCell(x, y).setHasPlayer(true);   // Occupe la nouvelle position
        } else if (board instanceof Board) {
            Board b = (Board) board;
            b.getCell(x, y).setHasPlayer(false);
            x = startX;
            y = startY;
            b.getCell(x, y).setHasPlayer(true);
        }
        isAlive = true;
    }

    /**
     * OPTIMISATION: Mouvement avec contrôle de fluidité
     * Gère les déplacements dans une direction donnée
     */
    public void move(Direction direction, Object board, GameState gameState) {
        // Vérifications préliminaires
        if (gameState != null && gameState != GameState.PLAYING) return;
        if (!isAlive) return;

        // Contrôle de la fluidité des mouvements
        long now = System.currentTimeMillis();
        int delay = speedBoost ? SPEED_BOOST_DELAY : NORMAL_MOVE_DELAY;
        if (now - lastMoveTime < delay) return;
        lastMoveTime = now;

        // Vérification de la fin du bonus speed
        if (speedBoost && speedEndTime > 0 && now > speedEndTime) {
            speedBoost = false;
            speedEndTime = 0;
        }

        // Calcul de la nouvelle position
        int newX = x + direction.getDx();
        int newY = y + direction.getDy();

        // Vérification de validité selon le type de plateau
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

        // Vérification que la cellule est accessible
        if (cell.isWalkable() && !cell.hasPlayer()) {
            // Déplacement effectif
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

            // Collecte des bonus
            if (cell.hasPowerUp()) {
                addScore(300); // +300 points pour un bonus
                applyPowerUp(cell.getPowerUp());
                cell.setPowerUp(null);
            }
        }
    }

    /**
     * Pose une bombe à la position actuelle du joueur
     */
    public void placeBomb(Object board, GameState gameState) {
        if (!isAlive) return;
        if (bombsAvailable <= 0) return;

        // Pose de bombe selon le type de plateau
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

    /**
     * Callback appelé quand une bombe explose
     * Remet une bombe à disposition
     */
    public void onBombExploded() {
        bombsAvailable = Math.min(bombsAvailable + 1, maxBombs);
    }

    /**
     * Gère les dégâts subis par le joueur
     */
    public void takeDamage() {
        if (!isAlive) return;

        lives--;
        if (lives <= 0) {
            lives = 0;
            isAlive = false;
            // Déclenche le callback de fin de partie
            if (gameOverListener != null) {
                gameOverListener.onGameOver(score);
            }
        }
        deathTime = System.currentTimeMillis();
    }

    /**
     * Tente de faire respawn le joueur après un délai
     */
    public void tryRespawn() {
        if (!isAlive && deathTime > 0 &&
                System.currentTimeMillis() - deathTime > respawnDelay) {
            x = startX;
            y = startY;
            isAlive = (lives > 0);
            deathTime = -1;
        }
    }

    /**
     * Ajoute des points au score
     */
    public void addScore(int pts) {
        score += pts;
    }

    /**
     * Applique les effets d'un bonus collecté
     */
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
                speedEndTime = System.currentTimeMillis() + 5000; // 5 secondes
                break;
        }
    }

    // --- Méthodes de convenance pour le mode 1v1 Legend ---
    public void moveUp(Object board)    { move(Direction.UP,    board, null); }
    public void moveDown(Object board)  { move(Direction.DOWN,  board, null); }
    public void moveLeft(Object board)  { move(Direction.LEFT,  board, null); }
    public void moveRight(Object board) { move(Direction.RIGHT, board, null); }
    public void placeBomb(Object board) { placeBomb(board, null); }

    // ----------- Accesseurs complets -----------
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
