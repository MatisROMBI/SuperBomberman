package com.bomberman.model;

import com.bomberman.controller.MapSelectionController;
import com.bomberman.model.enums.CellType;
import com.bomberman.model.enums.Direction;
import com.bomberman.utils.Constants;
import com.bomberman.utils.MapManager;
import java.util.*;

/**
 * Plateau du mode LEGEND 1v1 - CORRECTION FINALE
 */
public class Legend1v1Board {
    private final Cell[][] grid;
    private final List<Bomb> bombs;
    private final List<Explosion> explosions;
    private Player player1;
    private Player player2;
    private final List<LegendEnemyBomber> bomberEnemies;
    private final List<LegendEnemyYellow> yellowEnemies;
    private final Music music = new Music();
    private final MapManager mapManager = new MapManager();

    public Legend1v1Board() {
        grid = new Cell[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        bomberEnemies = new ArrayList<>();
        yellowEnemies = new ArrayList<>();
        initializeBoard();
    }

    private void initializeBoard() {
        if (MapSelectionController.CustomMapHolder.hasCustomMap()) {
            loadCustomMap();
        } else {
            generateDefaultLegendMap();
        }
        setupPlayersAndEnemies();
    }

    private void loadCustomMap() {
        try {
            String selectedMap = MapSelectionController.CustomMapHolder.getSelectedMap();
            MapData mapData = mapManager.loadMap(selectedMap);

            CellType[][] customGrid = mapData.getGrid();
            for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
                for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                    grid[x][y] = new Cell(customGrid[x][y]);
                }
            }
        } catch (Exception e) {
            generateDefaultLegendMap();
        }
    }

    private void generateDefaultLegendMap() {
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
        ensureLegendSpawnAreasAreFree();
    }

    private void ensureLegendSpawnAreasAreFree() {
        grid[1][1].setType(CellType.EMPTY);
        grid[1][2].setType(CellType.EMPTY);
        grid[2][1].setType(CellType.EMPTY);

        int xMax = Constants.BOARD_WIDTH - 2;
        int yMax = Constants.BOARD_HEIGHT - 2;
        grid[xMax][yMax].setType(CellType.EMPTY);
        grid[xMax - 1][yMax].setType(CellType.EMPTY);
        grid[xMax][yMax - 1].setType(CellType.EMPTY);
    }

    private void setupPlayersAndEnemies() {
        int xMax = Constants.BOARD_WIDTH - 2;
        int yMax = Constants.BOARD_HEIGHT - 2;

        player1 = new Player(1, 1);
        player2 = new Player(xMax, yMax);

        grid[1][1].setHasPlayer(true);
        grid[xMax][yMax].setHasPlayer(true);

        bomberEnemies.add(new LegendEnemyBomber(Constants.BOARD_WIDTH / 2, 2));
        yellowEnemies.add(new LegendEnemyYellow(xMax, 1));
        yellowEnemies.add(new LegendEnemyYellow(1, yMax));
    }

    public void update() {
        updateBombs();
        updateExplosions();
        updateEnemies();
        checkPlayerDamage();
        checkEnemyDamage();
        cleanDeadEnemies();
    }

    private void updateBombs() {
        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb bomb = it.next();
            if (bomb.shouldExplode()) {
                explodeBomb(bomb);
                it.remove();
                player1.onBombExploded();
                player2.onBombExploded();
            }
        }
    }

    private void updateExplosions() {
        long now = System.currentTimeMillis();
        explosions.removeIf(e -> e.isExpired(now, Constants.EXPLOSION_DURATION));
    }

    private void updateEnemies() {
        for (LegendEnemyBomber b : bomberEnemies) {
            if (b.isAlive()) {
                b.playTurn(this, player1, player2);
            }
        }
        for (LegendEnemyYellow y : yellowEnemies) {
            if (y.isAlive()) {
                y.playTurn(this, player1, player2);
            }
        }
    }

    private void checkPlayerDamage() {
        for (Explosion explosion : explosions) {
            if (player1.isAlive() && player1.getX() == explosion.getX() && player1.getY() == explosion.getY()) {
                player1.takeDamage();
                if (player1.isAlive()) {
                    player1.respawnAtStart(this);
                }
            }
            if (player2.isAlive() && player2.getX() == explosion.getX() && player2.getY() == explosion.getY()) {
                player2.takeDamage();
                if (player2.isAlive()) {
                    player2.respawnAtStart(this);
                }
            }
        }
    }

    private void checkEnemyDamage() {
        for (Explosion explosion : explosions) {
            for (LegendEnemyBomber b : bomberEnemies) {
                if (b.isAlive() && b.getX() == explosion.getX() && b.getY() == explosion.getY()) {
                    b.kill();
                    player1.addScore(200);
                    player2.addScore(200);
                }
            }
            for (LegendEnemyYellow y : yellowEnemies) {
                if (y.isAlive() && y.getX() == explosion.getX() && y.getY() == explosion.getY()) {
                    y.kill();
                    player1.addScore(150);
                    player2.addScore(150);
                }
            }
        }
    }

    private void cleanDeadEnemies() {
        bomberEnemies.removeIf(b -> !b.isAlive());
        yellowEnemies.removeIf(y -> !y.isAlive());
    }

    private void explodeBomb(Bomb bomb) {
        bomb.explode();
        int bombX = bomb.getX(), bombY = bomb.getY();
        grid[bombX][bombY].setBomb(null);
        createExplosion(bombX, bombY);
        music.jouerExplosion();

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
                    if (Math.random() < 0.25) {
                        cell.setPowerUp(PowerUp.random());
                    }
                    break;
                }
                createExplosion(x, y);

                if (cell.getBomb() != null && !cell.getBomb().hasExploded()) {
                    explodeBomb(cell.getBomb());
                }
            }
        }
    }

    public void createExplosion(int x, int y) {
        if (isValidPosition(x, y)) {
            explosions.add(new Explosion(x, y));
        }
    }

    public boolean isExplosionAt(int x, int y) {
        for (Explosion e : explosions) {
            if (e.getX() == x && e.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEnemyAt(int x, int y) {
        for (LegendEnemyBomber b : bomberEnemies) {
            if (b.getX() == x && b.getY() == y && b.isAlive()) {
                return true;
            }
        }
        for (LegendEnemyYellow yel : yellowEnemies) {
            if (yel.getX() == x && yel.getY() == y && yel.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < Constants.BOARD_WIDTH && y >= 0 && y < Constants.BOARD_HEIGHT;
    }

    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    public Cell getCell(int x, int y) {
        return grid[x][y];
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public List<LegendEnemyBomber> getBomberEnemies() {
        return bomberEnemies;
    }

    public List<LegendEnemyYellow> getYellowEnemies() {
        return yellowEnemies;
    }
}