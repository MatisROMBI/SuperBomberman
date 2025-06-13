/**
 * Plateau principal pour le mode classique (1 joueur vs 3 bots)
 * Gère la génération de carte, les collisions et la logique de jeu
 */
package com.bomberman.model;

import com.bomberman.controller.MapSelectionController;
import com.bomberman.model.enums.CellType;
import com.bomberman.model.enums.Direction;
import com.bomberman.utils.Constants;
import com.bomberman.utils.MapManager;
import java.util.*;

public class Board {
    private Cell[][] grid;               // Grille de jeu 15x13
    private List<Bomb> bombs;            // Liste des bombes actives
    private List<Explosion> explosions;  // Liste des explosions actives
    private Player player;               // Joueur principal
    private List<PlayerBot> bots;        // 3 bots IA
    private Music music = new Music();   // Gestionnaire audio
    private MapManager mapManager = new MapManager(); // Gestionnaire de cartes

    /**
     * Constructeur - Initialise le plateau et les entités
     */
    public Board() {
        grid = new Cell[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        bots = new ArrayList<>();
        initializeBoard();
    }

    /**
     * Initialisation complète du plateau
     */
    private void initializeBoard() {
        // Vérification de carte personnalisée
        if (MapSelectionController.CustomMapHolder.hasCustomMap()) {
            loadCustomMap();
        } else {
            generateDefaultMap();
        }
        setupPlayers();
    }

    /**
     * Charge une carte personnalisée sélectionnée
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

            System.out.println("Carte personnalisée chargée : " + selectedMap);

        } catch (Exception e) {
            System.err.println("Erreur chargement carte personnalisée : " + e.getMessage());
            generateDefaultMap(); // Retour vers la carte par défaut
        }
    }

    /**
     * Génère la carte par défaut (style Bomberman classique)
     */
    private void generateDefaultMap() {
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                // Bordures et damier de murs fixes
                if (x == 0 || y == 0 || x == Constants.BOARD_WIDTH - 1 ||
                        y == Constants.BOARD_HEIGHT - 1 || (x % 2 == 0 && y % 2 == 0)) {
                    grid[x][y] = new Cell(CellType.WALL);
                } else {
                    // 70% murs destructibles, 30% cases vides
                    grid[x][y] = new Cell(Math.random() < 0.7 ? CellType.DESTRUCTIBLE_WALL : CellType.EMPTY);
                }
            }
        }

        // Assure les zones de spawn libres
        ensureSpawnAreasAreFree();
    }

    /**
     * Garantit que les 4 coins (zones de spawn) sont accessibles
     */
    private void ensureSpawnAreasAreFree() {
        // Coin haut-gauche (joueur principal)
        grid[1][1] = new Cell(CellType.EMPTY);
        grid[1][2] = new Cell(CellType.EMPTY);
        grid[2][1] = new Cell(CellType.EMPTY);

        // Coin bas-droite (bot 1)
        int xMax = Constants.BOARD_WIDTH - 2, yMax = Constants.BOARD_HEIGHT - 2;
        grid[xMax][yMax] = new Cell(CellType.EMPTY);
        grid[xMax - 1][yMax] = new Cell(CellType.EMPTY);
        grid[xMax][yMax - 1] = new Cell(CellType.EMPTY);

        // Coin haut-droite (bot 2)
        grid[xMax][1] = new Cell(CellType.EMPTY);
        grid[xMax - 1][1] = new Cell(CellType.EMPTY);
        grid[xMax][2] = new Cell(CellType.EMPTY);

        // Coin bas-gauche (bot 3)
        grid[1][yMax] = new Cell(CellType.EMPTY);
        grid[2][yMax] = new Cell(CellType.EMPTY);
        grid[1][yMax - 1] = new Cell(CellType.EMPTY);
    }

    /**
     * Placement des joueurs sur le plateau
     */
    private void setupPlayers() {
        // Joueur principal en haut-gauche
        player = new Player(1, 1);
        grid[1][1].setHasPlayer(true);

        // 3 bots dans les autres coins
        int xMax = Constants.BOARD_WIDTH - 2, yMax = Constants.BOARD_HEIGHT - 2;

        bots.add(new PlayerBot(xMax, yMax));       // bas-droite
        grid[xMax][yMax].setHasPlayer(true);

        bots.add(new PlayerBot(xMax, 1));          // haut-droite
        grid[xMax][1].setHasPlayer(true);

        bots.add(new PlayerBot(1, yMax));          // bas-gauche
        grid[1][yMax].setHasPlayer(true);
    }

    /**
     * Mise à jour principale du plateau (appelée à chaque frame)
     */
    public void update(Player player) {
        this.player = player;
        updateBombs();
        updateBots();
        updateExplosions();
    }

    /**
     * Gestion des bombes : explosions et nettoyage
     */
    private void updateBombs() {
        Iterator<Bomb> bombIterator = bombs.iterator();
        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();
            if (bomb.shouldExplode()) {
                explodeBomb(bomb);
                bombIterator.remove();
                // Notification aux joueurs qu'une bombe a explosé
                player.onBombExploded();
                for (PlayerBot bot : bots) {
                    bot.onBombExploded();
                }
            }
        }
    }

    /**
     * Mise à jour de l'IA des bots
     */
    private void updateBots() {
        for (PlayerBot bot : bots) {
            if (bot.isAlive()) {
                bot.playTurn(this);
            }
        }
    }

    /**
     * Nettoyage des explosions expirées
     */
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

    /**
     * Gestion complète de l'explosion d'une bombe
     * Propagation en croix avec gestion des collisions
     */
    private void explodeBomb(Bomb bomb) {
        bomb.explode();
        int bombX = bomb.getX();
        int bombY = bomb.getY();

        // Libère la cellule de la bombe
        grid[bombX][bombY].setBomb(null);
        createExplosion(bombX, bombY, bomb.getOwner());

        // Son d'explosion
        music.jouerExplosion();

        // Propagation dans les 4 directions
        for (Direction direction : Direction.values()) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                int x = bombX + direction.getDx() * i;
                int y = bombY + direction.getDy() * i;

                if (!isValidPosition(x, y)) break;

                Cell cell = grid[x][y];

                // Mur fixe : arrêt de la propagation
                if (cell.getType() == CellType.WALL) break;

                // Mur destructible : destruction et arrêt
                if (cell.getType() == CellType.DESTRUCTIBLE_WALL) {
                    // Points pour destruction (uniquement joueur humain)
                    if (bomb.getOwner() != 0) {
                        player.addScore(50);
                    }
                    createExplosion(x, y, bomb.getOwner());
                    cell.setType(CellType.EMPTY);
                    // 25% de chance d'apparition de bonus
                    if (Math.random() < 0.25) {
                        cell.setPowerUp(PowerUp.random());
                    }
                    break;
                }

                // Case vide : explosion continue
                createExplosion(x, y, bomb.getOwner());

                // Réaction en chaîne : autres bombes
                if (cell.getBomb() != null && !cell.getBomb().hasExploded()) {
                    explodeBomb(cell.getBomb());
                }
            }
        }
    }

    /**
     * Crée une explosion à une position donnée
     * Gère les dégâts aux joueurs et bots
     */
    private void createExplosion(int x, int y, int owner) {
        explosions.add(new Explosion(x, y));
        Cell cell = grid[x][y];

        // Dégâts au joueur humain
        if (cell.hasPlayer() && player != null && player.getX() == x && player.getY() == y) {
            player.takeDamage();
            player.respawnAtStart(this);
        }

        // Dégâts aux bots
        for (PlayerBot bot : bots) {
            if (bot.isAlive() && bot.getX() == x && bot.getY() == y) {
                boolean botWasAlive = bot.isAlive();
                bot.takeDamage(this);
                if (bot.isAlive()) {
                    bot.respawnAtStart(this);
                } else if (botWasAlive && owner != 0) {
                    // Points pour élimination de bot (joueur humain seulement)
                    player.addScore(300);
                }
            }
        }
    }

    /**
     * Vérifie si une position est valide sur le plateau
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < Constants.BOARD_WIDTH &&
                y >= 0 && y < Constants.BOARD_HEIGHT;
    }

    // --------- Accesseurs publics ----------
    public void addBomb(Bomb bomb) { bombs.add(bomb); }
    public Cell getCell(int x, int y) { return grid[x][y]; }
    public List<PlayerBot> getBots() { return bots; }
    public List<Bomb> getBombs() { return bombs; }
    public List<Explosion> getExplosions() { return explosions; }
    public Player getPlayer() { return player; }
    public int getWidth() { return Constants.BOARD_WIDTH; }
    public int getHeight() { return Constants.BOARD_HEIGHT; }
}