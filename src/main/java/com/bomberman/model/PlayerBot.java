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
    private static final int BOT_MOVE_DELAY = 800;
    private int maxBombsBot = 2;
    private int bombsAvailableBot = 2;
    private static final int BOT_START_LIVES = 6;

    public PlayerBot(int startX, int startY) {
        super(startX, startY);
        setLives(BOT_START_LIVES);
    }

    // PAS d’@Override si la méthode n’est pas dans Player !
    public void onBombExploded() {
        bombsAvailableBot = Math.min(bombsAvailableBot + 1, maxBombsBot);
    }

    public int getBombsAvailable() {
        return bombsAvailableBot;
    }

    public int getMaxBombs() {
        return maxBombsBot;
    }

    public void setMaxBombsBot(int max) {
        maxBombsBot = max;
    }

    public void playTurn(Board board) {
        if (!isAlive()) return;
        long now = System.currentTimeMillis();
        if (now - lastMoveTime < BOT_MOVE_DELAY) return;

        Player human = board.getPlayer();
        int px = getX();
        int py = getY();

        if (isNextTo(px, py, human.getX(), human.getY()) && bombsAvailableBot > 0 && board.getCell(px, py).getBomb() == null) {
            Bomb bomb = new Bomb(px, py, getExplosionRange());
            board.getCell(px, py).setBomb(bomb);
            board.addBomb(bomb);
            bombsAvailableBot--;
            lastMoveTime = now;
            return;
        }

        Direction bestDir = getDirectionTowards(px, py, human.getX(), human.getY(), board);
        if (bestDir != null) {
            int nx = px + bestDir.getDx();
            int ny = py + bestDir.getDy();
            if (board.isValidPosition(nx, ny) && board.getCell(nx, ny).isWalkable() && !board.getCell(nx, ny).hasPlayer()) {
                move(bestDir, board, GameState.PLAYING);
            }
        } else {
            List<Direction> dirs = new ArrayList<>();
            Collections.addAll(dirs, Direction.values());
            Collections.shuffle(dirs, random);
            for (Direction dir : dirs) {
                int nx = px + dir.getDx();
                int ny = py + dir.getDy();
                if (board.isValidPosition(nx, ny) && board.getCell(nx, ny).isWalkable() && !board.getCell(nx, ny).hasPlayer()) {
                    move(dir, board, GameState.PLAYING);
                    break;
                }
            }
        }

        if (random.nextDouble() < 0.15 && bombsAvailableBot > 0 && board.getCell(px, py).getBomb() == null) {
            Bomb bomb = new Bomb(px, py, getExplosionRange());
            board.getCell(px, py).setBomb(bomb);
            board.addBomb(bomb);
            bombsAvailableBot--;
        }

        lastMoveTime = now;
    }

    private boolean isNextTo(int x1, int y1, int x2, int y2) {
        return (Math.abs(x1 - x2) == 1 && y1 == y2) ||
                (Math.abs(y1 - y2) == 1 && x1 == x2);
    }

    private Direction getDirectionTowards(int fromX, int fromY, int toX, int toY, Board board) {
        List<Direction> dirs = new ArrayList<>();
        if (toX > fromX) dirs.add(Direction.RIGHT);
        if (toX < fromX) dirs.add(Direction.LEFT);
        if (toY > fromY) dirs.add(Direction.DOWN);
        if (toY < fromY) dirs.add(Direction.UP);
        for (Direction dir : dirs) {
            int nx = fromX + dir.getDx();
            int ny = fromY + dir.getDy();
            if (board.isValidPosition(nx, ny) && board.getCell(nx, ny).isWalkable() && !board.getCell(nx, ny).hasPlayer()) {
                return dir;
            }
        }
        return null;
    }

    // Ici tu peux garder l’annotation si applyPowerUp est bien "protected" dans Player.
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

    // Surcharge propre, pas d’Override (signature différente de Player)
    public void takeDamage(Board board) {
        if (!isAlive()) return;

        setLives(getLives() - 1);
        if (getLives() <= 0) {
            setLives(0);
            board.getCell(getX(), getY()).setHasPlayer(false); // Libérer la cellule
            setIsAlive(false);
        }
        setDeathTime(System.currentTimeMillis());
    }



    public void respawnAtStart(Board board) {
        if (!isAlive()) return;
        board.getCell(getX(), getY()).setHasPlayer(false);
        setX(getStartX());
        setY(getStartY());
        board.getCell(getX(), getY()).setHasPlayer(true);
        setIsAlive(true);
    }
}
