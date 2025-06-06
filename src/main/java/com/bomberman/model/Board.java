package com.bomberman.model;

import com.bomberman.model.enums.CellType;
import com.bomberman.model.enums.Direction;
import com.bomberman.utils.Constants;
import java.util.*;

public class Board {
    private Cell[][] grid;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private Player player;
    private List<PlayerBot> bots; // 3 bots

    public Board() {
        grid = new Cell[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        bots = new ArrayList<>();
        initializeBoard();
    }

    private void initializeBoard() {
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
        // Débloquer les 4 coins pour les joueurs
        grid[1][1] = new Cell(CellType.EMPTY);
        grid[1][2] = new Cell(CellType.EMPTY);
        grid[2][1] = new Cell(CellType.EMPTY);

        int xMax = Constants.BOARD_WIDTH - 2, yMax = Constants.BOARD_HEIGHT - 2;
        grid[xMax][yMax] = new Cell(CellType.EMPTY);
        grid[xMax - 1][yMax] = new Cell(CellType.EMPTY);
        grid[xMax][yMax - 1] = new Cell(CellType.EMPTY);

        grid[xMax][1] = new Cell(CellType.EMPTY);
        grid[xMax - 1][1] = new Cell(CellType.EMPTY);
        grid[xMax][2] = new Cell(CellType.EMPTY);

        grid[1][yMax] = new Cell(CellType.EMPTY);
        grid[2][yMax] = new Cell(CellType.EMPTY);
        grid[1][yMax - 1] = new Cell(CellType.EMPTY);

        // Placement des joueurs
        player = new Player(1, 1);
        grid[1][1].setHasPlayer(true);

        // 3 bots dans les autres coins
        bots.add(new PlayerBot(xMax, yMax));       // bas droite
        grid[xMax][yMax].setHasPlayer(true);
        bots.add(new PlayerBot(xMax, 1));          // haut droite
        grid[xMax][1].setHasPlayer(true);
        bots.add(new PlayerBot(1, yMax));          // bas gauche
        grid[1][yMax].setHasPlayer(true);
    }

    public void update(Player player) {
        this.player = player;
        updateBombs();
        updateBots();
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
                for (PlayerBot bot : bots) {
                    bot.onBombExploded();
                }
            }
        }
    }

    private void updateBots() {
        for (PlayerBot bot : bots) {
            if (bot.isAlive()) {
                bot.playTurn(this);
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

    private void explodeBomb(Bomb bomb) {
        bomb.explode();
        int bombX = bomb.getX();
        int bombY = bomb.getY();
        grid[bombX][bombY].setBomb(null);
        createExplosion(bombX, bombY, bomb.getOwner());

        for (Direction direction : Direction.values()) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                int x = bombX + direction.getDx() * i;
                int y = bombY + direction.getDy() * i;
                if (!isValidPosition(x, y)) break;

                Cell cell = grid[x][y];
                if (cell.getType() == CellType.WALL) break;
                if (cell.getType() == CellType.DESTRUCTIBLE_WALL) {
                    // Ajout du score pour bloc cassé
                    if (bomb.getOwner() != 0) {
                        player.addScore(50); // +50 points pour bloc cassé
                    }
                    createExplosion(x, y, bomb.getOwner());
                    cell.setType(CellType.EMPTY);
                    if (Math.random() < 0.25) {
                        cell.setPowerUp(PowerUp.random());
                    }
                    break;
                }
                createExplosion(x, y, bomb.getOwner());

                if (cell.getBomb() != null && !cell.getBomb().hasExploded()) {
                    explodeBomb(cell.getBomb());
                }
            }
        }
    }

    private void createExplosion(int x, int y, int owner) {
        explosions.add(new Explosion(x, y));
        Cell cell = grid[x][y];

        // Humain
        if (cell.hasPlayer() && player != null && player.getX() == x && player.getY() == y) {
            player.takeDamage();
            player.respawnAtStart(this);
        }
        // Bots
        for (PlayerBot bot : bots) {
            if (bot.isAlive() && bot.getX() == x && bot.getY() == y) {
                boolean botWasAlive = bot.isAlive();
                bot.takeDamage();
                bot.respawnAtStart(this);
                // S'il vient d'être éliminé
                if (botWasAlive && !bot.isAlive() && owner != 0) {
                    player.addScore(600); // +600 points pour chaque bot éliminé
                }
            }
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

    public List<PlayerBot> getBots() {
        return bots;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public Player getPlayer() {
        return player;
    }
}