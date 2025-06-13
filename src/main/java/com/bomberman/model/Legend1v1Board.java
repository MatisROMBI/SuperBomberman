package com.bomberman.model;

import com.bomberman.controller.MapSelectionController;
import com.bomberman.model.enums.CellType;
import com.bomberman.utils.Constants;
import com.bomberman.utils.MapManager;
import java.util.*;

/**
 * Plateau du mode LEGEND 1v1
 * Joueur 1 (Blanc) : Position haut-gauche, contrôles ZQSD + R, 6 vies
 * Joueur 2 (Noir) : Position bas-droite, contrôles IJKL + P, 6 vies
 * Ennemis IA améliorés : 1 Bomber + 2 Yellow avec déplacements plus cohérents
 */
public class Legend1v1Board {
    private final Cell[][] grid;
    private final List<Bomb> bombs;
    private final List<Explosion> explosions;
    private Player player1; // Joueur humain 1 (haut-gauche)
    private Player player2; // Joueur humain 2 (bas-droite)
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

    /**
     * Initialise la map, les positions de départ des joueurs et des ennemis.
     */
    private void initializeBoard() {
        // Vérifier s'il y a une map personnalisée sélectionnée
        if (MapSelectionController.CustomMapHolder.hasCustomMap()) {
            loadCustomMap();
        } else {
            generateDefaultLegendMap();
        }

        setupPlayersAndEnemies();
    }

    /**
     * Charge une map personnalisée pour le mode Legend
     */
    private void loadCustomMap() {
        try {
            String selectedMap = MapSelectionController.CustomMapHolder.getSelectedMap();
            MapData mapData = mapManager.loadMap(selectedMap);

            // Copier la grille de la map personnalisée
            CellType[][] customGrid = mapData.getGrid();
            for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
                for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                    grid[x][y] = new Cell(customGrid[x][y]);
                }
            }

            System.out.println("Map personnalisée chargée pour le mode Legend : " + selectedMap);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la map personnalisée : " + e.getMessage());
            generateDefaultLegendMap(); // Fallback vers la map par défaut
        }
    }

    /**
     * Génère la map par défaut du mode Legend
     */
    private void generateDefaultLegendMap() {
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

        // Assurer les coins ouverts pour spawn safe
        ensureLegendSpawnAreasAreFree();
    }

    /**
     * S'assure que les zones de spawn du mode Legend sont libres
     */
    private void ensureLegendSpawnAreasAreFree() {
        // Coin haut-gauche (Joueur 1)
        grid[1][1].setType(CellType.EMPTY);
        grid[1][2].setType(CellType.EMPTY);
        grid[2][1].setType(CellType.EMPTY);

        // Coin bas-droite (Joueur 2)
        int xMax = Constants.BOARD_WIDTH - 2;
        int yMax = Constants.BOARD_HEIGHT - 2;
        grid[xMax][yMax].setType(CellType.EMPTY);
        grid[xMax - 1][yMax].setType(CellType.EMPTY);
        grid[xMax][yMax - 1].setType(CellType.EMPTY);
    }

    /**
     * Place les joueurs et ennemis sur le plateau
     * Joueurs avec 6 vies garanties + ennemis IA optimisés
     */
    private void setupPlayersAndEnemies() {
        int xMax = Constants.BOARD_WIDTH - 2;
        int yMax = Constants.BOARD_HEIGHT - 2;

        // JOUEURS HUMAINS avec 6 vies garanties
        player1 = new Player(1, 1);          // Joueur 1 : haut-gauche
        player2 = new Player(xMax, yMax);    // Joueur 2 : bas-droite

        // S'assurer que les joueurs ont bien 6 vies
        player1.setLives(6);
        player2.setLives(6);

        grid[1][1].setHasPlayer(true);
        grid[xMax][yMax].setHasPlayer(true);

        // Ennemis IA Legend positionnés stratégiquement
        // 1 Bomber au centre pour contrôler le milieu
        int centerX = Constants.BOARD_WIDTH / 2;
        int centerY = Constants.BOARD_HEIGHT / 2;
        bomberEnemies.add(new LegendEnemyBomber(centerX, centerY));
        grid[centerX][centerY].setHasEnemy(true);

        // 2 Yellow ennemis dans les coins non-occupés pour créer de la pression
        yellowEnemies.add(new LegendEnemyYellow(xMax, 1));     // haut-droite
        grid[xMax][1].setHasEnemy(true);

        yellowEnemies.add(new LegendEnemyYellow(1, yMax));     // bas-gauche
        grid[1][yMax].setHasEnemy(true);

        System.out.println("Mode Legend 1v1 initialisé :");
        System.out.println("- Joueur 1 (Blanc) : 6 vies à (" + player1.getX() + "," + player1.getY() + ")");
        System.out.println("- Joueur 2 (Noir) : 6 vies à (" + player2.getX() + "," + player2.getY() + ")");
        System.out.println("- " + bomberEnemies.size() + " Bomber enemies");
        System.out.println("- " + yellowEnemies.size() + " Yellow enemies");
    }

    /**
     * Met à jour la logique du plateau à chaque tick : bombes, explosions, IA ennemie.
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
     * Fait jouer le tour de chaque ennemi vivant (IA améliorée).
     */
    private void updateEnemies() {
        for (LegendEnemyBomber b : bomberEnemies)
            if (b.isAlive()) b.playTurn(this, player1, player2);
        for (LegendEnemyYellow y : yellowEnemies)
            if (y.isAlive()) y.playTurn(this, player1, player2);
    }

    /**
     * Nettoie les ennemis morts et ajoute des points aux joueurs
     */
    private void cleanDeadEnemies() {
        // Compter les ennemis tués pour bonus de score
        int killedBombers = (int) bomberEnemies.stream().filter(b -> !b.isAlive()).count();
        int killedYellows = (int) yellowEnemies.stream().filter(y -> !y.isAlive()).count();

        if (killedBombers > 0 || killedYellows > 0) {
            // Répartir les points entre les joueurs vivants
            int totalBonus = killedBombers * 500 + killedYellows * 300;
            if (player1.isAlive() && player2.isAlive()) {
                player1.addScore(totalBonus / 2);
                player2.addScore(totalBonus / 2);
            } else if (player1.isAlive()) {
                player1.addScore(totalBonus);
            } else if (player2.isAlive()) {
                player2.addScore(totalBonus);
            }
        }

        bomberEnemies.removeIf(b -> !b.isAlive());
        yellowEnemies.removeIf(y -> !y.isAlive());
    }

    /**
     * Déclenche une explosion de bombe : propagation et gestion des collisions.
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

        // Dommages aux joueurs HUMAINS
        if (player1.isAlive() && player1.getX() == x && player1.getY() == y) {
            player1.takeDamage();
            if (player1.isAlive()) player1.respawnAtStart(this);
        }
        if (player2.isAlive() && player2.getX() == x && player2.getY() == y) {
            player2.takeDamage();
            if (player2.isAlive()) player2.respawnAtStart(this);
        }

        // Dommages aux ennemis IA
        for (LegendEnemyBomber b : bomberEnemies)
            if (b.getX() == x && b.getY() == y && b.isAlive()) b.kill();
        for (LegendEnemyYellow yel : yellowEnemies)
            if (yel.getX() == x && yel.getY() == y && yel.isAlive()) yel.kill();
    }

    /**
     * true s'il y a une explosion à la case (x,y).
     */
    public boolean isExplosionAt(int x, int y) {
        for (Explosion e : explosions)
            if (e.getX() == x && e.getY() == y)
                return true;
        return false;
    }

    /**
     * Retourne vrai s'il y a déjà un ennemi vivant (bomber ou yellow) à la case (x, y).
     */
    public boolean hasEnemyAt(int x, int y) {
        // Vérifie tous les bomber ennemis
        for (LegendEnemyBomber b : bomberEnemies)
            if (b.getX() == x && b.getY() == y && b.isAlive())
                return true;
        // Vérifie tous les yellow ennemis
        for (LegendEnemyYellow yel : yellowEnemies)
            if (yel.getX() == x && yel.getY() == y && yel.isAlive())
                return true;
        return false;
    }

    /**
     * Pathfinding simple pour les ennemis
     * Retourne la direction optimale pour aller de (fromX, fromY) vers (toX, toY)
     */
    public com.bomberman.model.enums.Direction getBestDirection(int fromX, int fromY, int toX, int toY) {
        List<com.bomberman.model.enums.Direction> possibleMoves = new ArrayList<>();

        // Calculer les distances pour chaque direction possible
        for (com.bomberman.model.enums.Direction dir : com.bomberman.model.enums.Direction.values()) {
            int newX = fromX + dir.getDx();
            int newY = fromY + dir.getDy();

            if (isValidPosition(newX, newY) &&
                    getCell(newX, newY).isWalkable() &&
                    !hasEnemyAt(newX, newY)) {
                possibleMoves.add(dir);
            }
        }

        if (possibleMoves.isEmpty()) return null;

        // Trouver la direction qui rapproche le plus de la cible
        com.bomberman.model.enums.Direction bestDir = possibleMoves.get(0);
        int bestDistance = Integer.MAX_VALUE;

        for (com.bomberman.model.enums.Direction dir : possibleMoves) {
            int newX = fromX + dir.getDx();
            int newY = fromY + dir.getDy();
            int distance = Math.abs(newX - toX) + Math.abs(newY - toY);

            if (distance < bestDistance) {
                bestDistance = distance;
                bestDir = dir;
            }
        }

        return bestDir;
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