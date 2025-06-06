package com.bomberman.model;

import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayerBot extends Player {
    private Random random = new Random();
    private long lastMoveTime = 0;
    private static final int BOT_MOVE_DELAY = 1800; // 1.8 secondes entre actions
    private int maxBombsBot = 0;
    private int bombsAvailableBot = 0;

    public PlayerBot(int startX, int startY) {
        super(startX, startY);
    }

    // Gestion bombes pour ce bot
    @Override
    public void onBombExploded() {
        bombsAvailableBot = Math.min(bombsAvailableBot + 1, maxBombsBot);
    }

    @Override
    public int getBombsAvailable() {
        return bombsAvailableBot;
    }

    @Override
    public int getMaxBombs() {
        return maxBombsBot;
    }

    public void setMaxBombsBot(int max) {
        maxBombsBot = max;
    }

    // Amélioration du déplacement : teste toutes les directions, pas juste une au hasard
    public void playTurn(Board board) {
        if (!isAlive()) return; // S'il est mort, ne joue plus
        long now = System.currentTimeMillis();
        if (now - lastMoveTime < BOT_MOVE_DELAY) return; // lent

        boolean moved = false;

        // 70% bouge, 30% pose bombe
        if (random.nextDouble() < 0.7) {
            // Essayons toutes les directions dans un ordre aléatoire pour éviter de rester bloqué
            List<Direction> dirs = new ArrayList<>();
            Collections.addAll(dirs, Direction.values());
            Collections.shuffle(dirs, random);
            for (Direction dir : dirs) {
                int nx = getX() + dir.getDx();
                int ny = getY() + dir.getDy();
                if (board.isValidPosition(nx, ny)) {
                    if (board.getCell(nx, ny).isWalkable() && !board.getCell(nx, ny).hasPlayer()) {
                        move(dir, board, GameState.PLAYING);
                        moved = true;
                        break;
                    }
                }
            }
        }
        // Si le bot n'a pas bougé ou si c'est son tour de poser une bombe
        if (!moved || random.nextDouble() >= 0.7) {
            if (bombsAvailableBot > 0 && board.getCell(getX(), getY()).getBomb() == null) {
                Bomb bomb = new Bomb(getX(), getY(), getExplosionRange());
                board.getCell(getX(), getY()).setBomb(bomb);
                board.addBomb(bomb);
                bombsAvailableBot--;
            }
        }

        lastMoveTime = now;
    }

    // Power-ups adaptés pour le bot
    protected void applyPowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case EXTRA_BOMB:
                maxBombsBot++;
                bombsAvailableBot++;
                break;
            case RANGE_UP:
                setExplosionRange(getExplosionRange() + 1);
                break;
            case LIFE:
                setLives(getLives() + 1);
                break;
            case SPEED:
                setSpeedBoost(true);
                break;
        }
    }

    // GESTION VIE LIMITÉE DU BOT : après 6 dégâts, il meurt vraiment (isAlive = false)
    @Override
    public void takeDamage() {
        if (!isAlive()) return;
        setLives(getLives() - 1);
        if (getLives() <= 0) {
            setLives(0);
            setIsAlive(false); // Le bot meurt définitivement
        }
        setDeathTime(System.currentTimeMillis());
    }

    @Override
    public void respawnAtStart(Board board) {
        if (!isAlive()) return; // Ne respawn plus s'il est mort définitivement
        board.getCell(getX(), getY()).setHasPlayer(false);
        setX(getStartX());
        setY(getStartY());
        board.getCell(getX(), getY()).setHasPlayer(true);
        setIsAlive(true);
    }
}