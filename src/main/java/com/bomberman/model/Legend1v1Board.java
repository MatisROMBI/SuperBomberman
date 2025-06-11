package com.bomberman.model;

import com.bomberman.model.enums.CellType;
import com.bomberman.utils.Constants;
import java.util.*;

/**
 * Plateau du mode LEGEND 1v1 :
 * - Gère la map, les joueurs, les ennemis et les explosions.
 * - Optimisé pour IA intelligente (ennemis évitent de se marcher dessus, meurent dans une explosion, etc.)
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

    public Legend1v1Board() {
        grid = new Cell[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        bomberEnemies = new ArrayList<>();
        yellowEnemies = new ArrayList<>();
        initializeBoard();
    }

    /**
     * Initialise la map, les positions de départ des joueurs et des ennemis.
     */
    private void initializeBoard() {
        // Génère la map avec murs fixes, destructibles, et coins ouverts (style Bomberman)
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                if (x == 0 || y == 0 || x == Constants.BOARD_WIDTH - 1 ||
                        y == Constants.BOARD_HEIGHT - 1 || (x % 2 == 0 && y % 2 == 0)) {
                    grid[x][y] = new Cell(CellType.WALL); // Mur fixe
                } else {
                    grid[x][y] = new Cell(Math.random() < 0.7 ? CellType.DESTRUCTIBLE_WALL : CellType.EMPTY);
                }
            }
        }
        // Coins ouverts pour spawn safe
        grid[1][1].setType(CellType.EMPTY);
        grid[1][2].setType(CellType.EMPTY);
        grid[2][1].setType(CellType.EMPTY);
        int xMax = Constants.BOARD_WIDTH - 2, yMax = Constants.BOARD_HEIGHT - 2;
        grid[xMax][yMax].setType(CellType.EMPTY);
        grid[xMax - 1][yMax].setType(CellType.EMPTY);
        grid[xMax][yMax - 1].setType(CellType.EMPTY);

        // Placement des joueurs
        player1 = new Player(1, 1);
        player2 = new Player(xMax, yMax);
        grid[1][1].setHasPlayer(true);
        grid[xMax][yMax].setHasPlayer(true);

        // Ennemis Legend (ex : 1 bomber + 2 yellow)
        bomberEnemies.add(new LegendEnemyBomber(Constants.BOARD_WIDTH / 2, 1));
        grid[Constants.BOARD_WIDTH / 2][1].setHasEnemy(true);
        yellowEnemies.add(new LegendEnemyYellow(1, yMax));
        grid[1][yMax].setHasEnemy(true);
        yellowEnemies.add(new LegendEnemyYellow(xMax, 1));
        grid[xMax][1].setHasEnemy(true);
    }

    /**
     * Met à jour la logique du plateau à chaque tick : bombes, explosions, IA ennemie.
     */
    public void update() {
        updateBombs();
        updateExplosions();
        updateEnemies();
        cleanDeadEnemies();
    }

    /** Gère les explosions de bombes et retire les bombes qui ont explosé. */
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

    /** Retire les explosions terminées. */
    private void updateExplosions() {
        long now = System.currentTimeMillis();
        explosions.removeIf(e -> e.isExpired(now, Constants.EXPLOSION_DURATION));
    }

    /**
     * Fait jouer le tour de chaque ennemi vivant (IA optimisée).
     */
    private void updateEnemies() {
        for (LegendEnemyBomber b : bomberEnemies)
            if (b.isAlive()) b.playTurn(this, player1, player2);
        for (LegendEnemyYellow y : yellowEnemies)
            if (y.isAlive()) y.playTurn(this, player1, player2);
    }

    /**
     * Nettoie les ennemis morts (tombés dans une explosion).
     */
    private void cleanDeadEnemies() {
        bomberEnemies.removeIf(b -> !b.isAlive());
        yellowEnemies.removeIf(y -> !y.isAlive());
    }

    /**
     * Déclenche une explosion de bombe : propagation et gestion des collisions.
     */
    private void explodeBomb(Bomb bomb) {
        bomb.explode();
        int bombX = bomb.getX(), bombY = bomb.getY();
        grid[bombX][bombY].setBomb(null);
        createExplosion(bombX, bombY);
        music.jouerExplosion();

        for (var direction : com.bomberman.model.enums.Direction.values()) {
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

    /**
     * Crée une explosion à la case (x,y), gère les collisions avec joueurs et ennemis.
     */
    public void createExplosion(int x, int y) {
        if (!isValidPosition(x, y)) return;
        explosions.add(new Explosion(x, y));
        Cell cell = grid[x][y];

        // Dommages aux joueurs
        if (player1.isAlive() && player1.getX() == x && player1.getY() == y) {
            player1.takeDamage();
            if (player1.isAlive()) player1.respawnAtStart(this);
        }
        if (player2.isAlive() && player2.getX() == x && player2.getY() == y) {
            player2.takeDamage();
            if (player2.isAlive()) player2.respawnAtStart(this);
        }

        // Dommages aux ennemis
        for (LegendEnemyBomber b : bomberEnemies)
            if (b.getX() == x && b.getY() == y && b.isAlive()) b.kill();
        for (LegendEnemyYellow yel : yellowEnemies)
            if (yel.getX() == x && yel.getY() == y && yel.isAlive()) yel.kill();
    }

    /**
     * Utilitaire : true s'il y a une explosion à la case (x,y).
     */
    public boolean isExplosionAt(int x, int y) {
        for (Explosion e : explosions)
            if (e.getX() == x && e.getY() == y)
                return true;
        return false;
    }

    /**
     * Utilitaire : true s'il y a déjà un ennemi vivant sur la case (x,y).
     */
    /**
     * Retourne vrai s'il y a déjà un ennemi vivant (bomber ou yellow) à la case (x, y).
     */
    public boolean hasEnemyAt(int x, int y) {
        // Vérifie tous les bomber ennemis
        for (LegendEnemyBomber b : bomberEnemies)
            if (b.getX() == x && b.getY() == y && b.isAlive())
                return true;
        // Vérifie tous les yellow ennemis (utilise un autre nom que y)
        for (LegendEnemyYellow yel : yellowEnemies)
            if (yel.getX() == x && yel.getY() == y && yel.isAlive())
                return true;
        return false;
    }

    // ===========================
    // GETTERS ET METHODES UTILITAIRES
    // ===========================

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
