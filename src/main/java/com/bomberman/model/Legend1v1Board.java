/**
 * Plateau spécialisé pour le mode Legend 1v1
 * Support de 2 joueurs humains + ennemis IA avancés
 * Mécaniques de jeu améliorées et pathfinding intelligent
 */
package com.bomberman.model;

import com.bomberman.controller.MapSelectionController;
import com.bomberman.model.enums.CellType;
import com.bomberman.utils.Constants;
import com.bomberman.utils.MapManager;
import java.util.*;

public class Legend1v1Board {
    private final Cell[][] grid;                    // Grille de jeu
    private final List<Bomb> bombs;                 // Bombes actives
    private final List<Explosion> explosions;       // Explosions actives
    private Player player1;                         // Joueur 1 (Blanc, haut-gauche)
    private Player player2;                         // Joueur 2 (Noir, bas-droite)
    private final List<LegendEnemyBomber> bomberEnemies;  // Ennemis Bomber
    private final List<LegendEnemyYellow> yellowEnemies;  // Ennemis Yellow
    private final Music music = new Music();
    private final MapManager mapManager = new MapManager();

    /**
     * Constructeur - Initialise le plateau Legend
     */
    public Legend1v1Board() {
        grid = new Cell[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        bomberEnemies = new ArrayList<>();
        yellowEnemies = new ArrayList<>();
        initializeBoard();
    }

    /**
     * Initialisation complète du plateau Legend
     */
    private void initializeBoard() {
        // Chargement de carte (personnalisée ou par défaut)
        if (MapSelectionController.CustomMapHolder.hasCustomMap()) {
            loadCustomMap();
        } else {
            generateDefaultLegendMap();
        }
        setupPlayersAndEnemies();
    }

    /**
     * Charge une carte personnalisée pour le mode Legend
     */
    private void loadCustomMap() {
        try {
            String selectedMap = MapSelectionController.CustomMapHolder.getSelectedMap();
            MapData mapData = mapManager.loadMap(selectedMap);

            // Copie de la grille personnalisée
            CellType[][] customGrid = mapData.getGrid();
            for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
                for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                    grid[x][y] = new Cell(customGrid[x][y]);
                }
            }

            System.out.println("Carte personnalisée chargée pour le mode Legend : " + selectedMap);

        } catch (Exception e) {
            System.err.println("Erreur chargement carte Legend : " + e.getMessage());
            generateDefaultLegendMap();
        }
    }

    /**
     * Génère la carte par défaut du mode Legend
     */
    private void generateDefaultLegendMap() {
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                // Structure classique Bomberman
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

    /**
     * Garantit que les zones de spawn Legend sont libres
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
     * Place les joueurs et ennemis sur le plateau Legend
     * Configuration : 2 joueurs humains (6 vies) + 1 Bomber + 2 Yellow
     */
    private void setupPlayersAndEnemies() {
        int xMax = Constants.BOARD_WIDTH - 2;
        int yMax = Constants.BOARD_HEIGHT - 2;

        // JOUEURS HUMAINS avec 6 vies garanties
        player1 = new Player(1, 1);          // Joueur 1 : haut-gauche
        player2 = new Player(xMax, yMax);    // Joueur 2 : bas-droite

        // Configuration des vies
        player1.setLives(6);
        player2.setLives(6);

        // Marquage des cellules
        grid[1][1].setHasPlayer(true);
        grid[xMax][yMax].setHasPlayer(true);

        // ENNEMIS IA Legend - Positionnement stratégique
        // 1 Bomber au centre pour contrôler le milieu
        int centerX = Constants.BOARD_WIDTH / 2;
        int centerY = Constants.BOARD_HEIGHT / 2;
        bomberEnemies.add(new LegendEnemyBomber(centerX, centerY));
        grid[centerX][centerY].setHasEnemy(true);

        // 2 Yellow ennemis dans les coins libres
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
     * Mise à jour principale du plateau Legend
     */
    public void update() {
        updateBombs();
        updateExplosions();
        updateEnemies();
        cleanDeadEnemies();
    }

    /**
     * Gestion des explosions de bombes
     */
    private void updateBombs() {
        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb bomb = it.next();
            if (bomb.shouldExplode()) {
                explodeBomb(bomb);
                it.remove();
                // Notification aux joueurs
                player1.onBombExploded();
                player2.onBombExploded();
            }
        }
    }

    /**
     * Nettoyage des explosions expirées
     */
    private void updateExplosions() {
        long now = System.currentTimeMillis();
        explosions.removeIf(e -> e.isExpired(now, Constants.EXPLOSION_DURATION));
    }

    /**
     * Mise à jour de l'IA ennemie améliorée
     */
    private void updateEnemies() {
        // IA Bomber avancée
        for (LegendEnemyBomber b : bomberEnemies) {
            if (b.isAlive()) {
                b.playTurn(this, player1, player2);
            }
        }

        // IA Yellow avancée
        for (LegendEnemyYellow y : yellowEnemies) {
            if (y.isAlive()) {
                y.playTurn(this, player1, player2);
            }
        }
    }

    /**
     * Nettoyage des ennemis morts et attribution des points
     */
    private void cleanDeadEnemies() {
        // Compte des ennemis tués pour attribution de points
        int killedBombers = (int) bomberEnemies.stream().filter(b -> !b.isAlive()).count();
        int killedYellows = (int) yellowEnemies.stream().filter(y -> !y.isAlive()).count();

        if (killedBombers > 0 || killedYellows > 0) {
            // Calcul des points bonus
            int totalBonus = killedBombers * 500 + killedYellows * 300;

            // Répartition entre les joueurs vivants
            if (player1.isAlive() && player2.isAlive()) {
                player1.addScore(totalBonus / 2);
                player2.addScore(totalBonus / 2);
            } else if (player1.isAlive()) {
                player1.addScore(totalBonus);
            } else if (player2.isAlive()) {
                player2.addScore(totalBonus);
            }
        }

        // Suppression des ennemis morts
        bomberEnemies.removeIf(b -> !b.isAlive());
        yellowEnemies.removeIf(y -> !y.isAlive());
    }

    /**
     * Gestion complète de l'explosion d'une bombe (version Legend)
     */
    private void explodeBomb(Bomb bomb) {
        bomb.explode();
        int bombX = bomb.getX(), bombY = bomb.getY();
        grid[bombX][bombY].setBomb(null);
        createExplosion(bombX, bombY);
        music.jouerExplosion();

        // Propagation en croix
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
                    // Apparition de bonus
                    if (Math.random() < 0.25) cell.setPowerUp(PowerUp.random());
                    break;
                }

                createExplosion(x, y);

                // Réaction en chaîne
                if (cell.getBomb() != null && !cell.getBomb().hasExploded()) {
                    explodeBomb(cell.getBomb());
                }
            }
        }
    }

    /**
     * Crée une explosion avec gestion des dégâts Legend
     */
    public void createExplosion(int x, int y) {
        if (!isValidPosition(x, y)) return;
        explosions.add(new Explosion(x, y));
        Cell cell = grid[x][y];

        // Dégâts aux joueurs HUMAINS
        if (player1.isAlive() && player1.getX() == x && player1.getY() == y) {
            player1.takeDamage();
            if (player1.isAlive()) player1.respawnAtStart(this);
        }
        if (player2.isAlive() && player2.getX() == x && player2.getY() == y) {
            player2.takeDamage();
            if (player2.isAlive()) player2.respawnAtStart(this);
        }

        // Dégâts aux ennemis IA
        for (LegendEnemyBomber b : bomberEnemies) {
            if (b.getX() == x && b.getY() == y && b.isAlive()) b.kill();
        }
        for (LegendEnemyYellow yel : yellowEnemies) {
            if (yel.getX() == x && yel.getY() == y && yel.isAlive()) yel.kill();
        }
    }

    /**
     * Vérifie s'il y a une explosion à une position donnée
     */
    public boolean isExplosionAt(int x, int y) {
        for (Explosion e : explosions) {
            if (e.getX() == x && e.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie s'il y a un ennemi vivant à une position donnée
     */
    public boolean hasEnemyAt(int x, int y) {
        // Vérification des Bomber
        for (LegendEnemyBomber b : bomberEnemies) {
            if (b.getX() == x && b.getY() == y && b.isAlive()) {
                return true;
            }
        }
        // Vérification des Yellow
        for (LegendEnemyYellow yel : yellowEnemies) {
            if (yel.getX() == x && yel.getY() == y && yel.isAlive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * NOUVELLE MÉTHODE : Pathfinding simple pour les ennemis
     * Retourne la direction optimale pour aller de (fromX, fromY) vers (toX, toY)
     */
    public com.bomberman.model.enums.Direction getBestDirection(int fromX, int fromY, int toX, int toY) {
        List<com.bomberman.model.enums.Direction> possibleMoves = new ArrayList<>();

        // Trouve toutes les directions praticables
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

        // Trouve la direction qui rapproche le plus de la cible
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

    // ===========================
    // ACCESSEURS ET MÉTHODES UTILITAIRES
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