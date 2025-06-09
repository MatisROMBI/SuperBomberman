package com.bomberman.model;

import com.bomberman.model.enums.CellType;
import com.bomberman.model.enums.Direction;
import com.bomberman.utils.Constants;

import java.util.*;

public class Legend1v1Board {
    private Cell[][] grid;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private Player player1;
    private Player player2;
    private List<LegendEnemyBomber> bomberEnemies;
    private List<LegendEnemyYellow> yellowEnemies;
    private Music sounds = new Music();

    public Legend1v1Board() {
        grid = new Cell[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        bomberEnemies = new ArrayList<>();
        yellowEnemies = new ArrayList<>();
        initializeBoard();
    }

    private void initializeBoard() {
        // Génère la map comme Bomberman
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                if (x == 0 || y == 0 || x == Constants.BOARD_WIDTH - 1 ||
                        y == Constants.BOARD_HEIGHT - 1 || (x % 2 == 0 && y % 2 == 0)) {
                    grid[x][y] = new Cell(CellType.WALL);
                } else {
                    grid[x][y] = new Cell(Math.random() < 0.7 ? CellType.DESTRUCTIBLE_WALL : CellType.EMPTY);
                }
            }
        }
        // Spawn zones
        grid[1][1] = new Cell(CellType.EMPTY);
        grid[1][2] = new Cell(CellType.EMPTY);
        grid[2][1] = new Cell(CellType.EMPTY);
        int xMax = Constants.BOARD_WIDTH - 2, yMax = Constants.BOARD_HEIGHT - 2;
        grid[xMax][yMax] = new Cell(CellType.EMPTY);
        grid[xMax-1][yMax] = new Cell(CellType.EMPTY);
        grid[xMax][yMax-1] = new Cell(CellType.EMPTY);

        // Players
        player1 = new Player(1, 1);
        player2 = new Player(xMax, yMax);
        grid[1][1].setHasPlayer(true);
        grid[xMax][yMax].setHasPlayer(true);

        // Ennemis spéciaux
        bomberEnemies.add(new LegendEnemyBomber(xMax/2, 1));
        grid[xMax/2][1].setHasEnemy(true);
        yellowEnemies.add(new LegendEnemyYellow(1, yMax));
        grid[1][yMax].setHasEnemy(true);
        yellowEnemies.add(new LegendEnemyYellow(xMax, 1));
        grid[xMax][1].setHasEnemy(true);
    }

    public void update() {
        updateBombs();
        updateExplosions();
        updateEnemies();
    }

    private void updateBombs() {
        Iterator<Bomb> bombIterator = bombs.iterator();
        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();
            if (bomb.shouldExplode()) {
                explodeBomb(bomb);
                bombIterator.remove();
                player1.onBombExploded();
                player2.onBombExploded();
            }
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

    private void updateEnemies() {
        for (LegendEnemyBomber b : bomberEnemies) b.playTurn(this, player1, player2);
        for (LegendEnemyYellow y : yellowEnemies) y.playTurn(this, player1, player2);
    }

    private void explodeBomb(Bomb bomb) {
        bomb.explode();
        int bombX = bomb.getX();
        int bombY = bomb.getY();
        grid[bombX][bombY].setBomb(null);
        createExplosion(bombX, bombY);

        sounds.jouerExplosion();

        for (Direction direction : Direction.values()) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                int x = bombX + direction.getDx() * i;
                int y = bombY + direction.getDy() * i;
                if (!isValidPosition(x, y)) break;

                Cell cell = grid[x][y];
                if (cell.getType() == CellType.WALL) break;
                if (cell.getType() == CellType.DESTRUCTIBLE_WALL) {
                    createExplosion(x, y);
                    cell.setType(CellType.EMPTY);
                    if (Math.random() < 0.25) cell.setPowerUp(PowerUp.random());
                    break;
                }
                createExplosion(x, y);

                if (cell.getBomb() != null && !cell.getBomb().hasExploded()) {
                    explodeBomb(cell.getBomb());
                }
            }
        }
    }

    private void createExplosion(int x, int y) {
        explosions.add(new Explosion(x, y));
        Cell cell = grid[x][y];
        // Touche joueur 1 ?
        if (cell.hasPlayer() && player1.getX() == x && player1.getY() == y) {
            player1.takeDamage();
            player1.respawnAtStart(this);
        }
        // Touche joueur 2 ?
        if (cell.hasPlayer() && player2.getX() == x && player2.getY() == y) {
            player2.takeDamage();
            player2.respawnAtStart(this);
        }
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < Constants.BOARD_WIDTH && y >= 0 && y < Constants.BOARD_HEIGHT;
    }

    public void addBomb(Bomb bomb) { bombs.add(bomb); }
    public Cell getCell(int x, int y) { return grid[x][y]; }
    public List<Bomb> getBombs() { return bombs; }
    public List<Explosion> getExplosions() { return explosions; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public List<LegendEnemyBomber> getBomberEnemies() { return bomberEnemies; }
    public List<LegendEnemyYellow> getYellowEnemies() { return yellowEnemies; }
}
