package com.bomberman.model;

import com.bomberman.model.enums.CellType;
import com.bomberman.model.enums.Direction;
import com.bomberman.utils.Constants;
import java.util.*;

public class Board {
    private Cell[][] grid;
    private List<Bomb> bombs;
    private List<Explosion> explosions; // AJOUT
    private List<Enemy> enemies;
    private Player player; // Pour infliger les dégâts

    public Board() {
        grid = new Cell[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        enemies = new ArrayList<>();
        initializeBoard();
    }

    private void initializeBoard() {
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                if (x == 0 || y == 0 || x == Constants.BOARD_WIDTH - 1 ||
                        y == Constants.BOARD_HEIGHT - 1 || (x % 2 == 0 && y % 2 == 0)) {
                    grid[x][y] = new Cell(CellType.WALL);
                } else {
                    if (Math.random() < 0.7 && !(x <= 2 && y <= 2)) {
                        grid[x][y] = new Cell(CellType.DESTRUCTIBLE_WALL);
                    } else {
                        grid[x][y] = new Cell(CellType.EMPTY);
                    }
                }
            }
        }
        grid[1][1] = new Cell(CellType.EMPTY);
        grid[2][1] = new Cell(CellType.EMPTY);
        grid[1][2] = new Cell(CellType.EMPTY);

        addEnemies();
    }

    private void addEnemies() {
        enemies.add(new Enemy(Constants.BOARD_WIDTH - 2, Constants.BOARD_HEIGHT - 2));
        enemies.add(new Enemy(Constants.BOARD_WIDTH - 3, Constants.BOARD_HEIGHT - 2));
        for (Enemy enemy : enemies) {
            grid[enemy.getX()][enemy.getY()].setHasEnemy(true);
        }
    }

    public void update(Player player) {
        this.player = player;
        updateBombs();
        updateEnemies();
        updateExplosions();
    }

    private void updateBombs() {
        Iterator<Bomb> bombIterator = bombs.iterator();
        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();
            if (bomb.shouldExplode()) {
                explodeBomb(bomb);
                bombIterator.remove();
                player.onBombExploded();
            }
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.update(this);
        }
    }

    private void updateExplosions() {
        long now = System.currentTimeMillis();
        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion e = it.next();
            if (e.isExpired(now, Constants.EXPLOSION_DURATION)) {
                it.remove();
            }
        }
    }

    private void explodeBomb(Bomb bomb) {
        bomb.explode();
        int bombX = bomb.getX();
        int bombY = bomb.getY();
        grid[bombX][bombY].setBomb(null);
        createExplosion(bombX, bombY);

        for (Direction direction : Direction.values()) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                int x = bombX + direction.getDx() * i;
                int y = bombY + direction.getDy() * i;
                if (!isValidPosition(x, y)) break;

                Cell cell = grid[x][y];
                if (cell.getType() == CellType.WALL) break;
                if (cell.getType() == CellType.DESTRUCTIBLE_WALL) {
                    cell.setType(CellType.EMPTY);
                    if (Math.random() < 0.25) {
                        cell.setPowerUp(PowerUp.random());
                    }
                    createExplosion(x, y);
                    break;
                }
                createExplosion(x, y);

                // Réaction en chaîne :
                if (cell.getBomb() != null && !cell.getBomb().hasExploded()) {
                    explodeBomb(cell.getBomb());
                }
            }
        }
    }

    private void createExplosion(int x, int y) {
        explosions.add(new Explosion(x, y));
        Cell cell = grid[x][y];
        if (cell.hasEnemy()) {
            for (Enemy enemy : enemies) {
                if (enemy.getX() == x && enemy.getY() == y && enemy.isAlive()) {
                    enemy.die();
                    cell.setHasEnemy(false);
                }
            }
        }
        if (cell.hasPlayer() && player != null) {
            player.takeDamage();
            player.respawnAtStart(this); // Ajoute cette ligne !
        }
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < Constants.BOARD_WIDTH &&
                y >= 0 && y < Constants.BOARD_HEIGHT;
    }

    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    public Cell getCell(int x, int y) {
        return grid[x][y];
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }
}