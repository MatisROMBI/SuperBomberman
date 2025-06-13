/**
 * Ennemi Bomber IA avancé pour le mode Legend 1v1
 * Comportement : explosion géante au contact des joueurs
 * IA améliorée avec pathfinding intelligent et anti-blocage
 */
package com.bomberman.model;

import java.util.Random;

public class LegendEnemyBomber {
    private int x, y;                    // Position sur la grille
    private boolean canExplode = true;   // Capacité d'explosion disponible
    private long lastExplosion = 0;      // Dernière explosion (pour cooldown)
    private static final int EXPLOSION_COOLDOWN = 2500; // Cooldown en millisecondes
    private boolean alive = true;        // État de vie
    private final Random rand = new Random();

    // NOUVELLES VARIABLES pour IA améliorée
    private long lastMoveTime = 0;
    private static final int MOVE_DELAY = 400; // Déplacement plus fluide
    private int stuckCounter = 0;        // Compteur anti-blocage
    private int lastX = -1, lastY = -1;  // Position précédente pour détection de blocage
    private com.bomberman.model.enums.Direction lastDirection = null; // Dernière direction

    /**
     * Constructeur avec position initiale
     */
    public LegendEnemyBomber(int x, int y) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
    }

    /**
     * IA principale - Tour de jeu AMÉLIORÉ
     * Stratégie : pathfinding intelligent vers le joueur le plus proche
     */
    public void playTurn(Legend1v1Board board, Player p1, Player p2) {
        if (!alive) return;

        long now = System.currentTimeMillis();

        // Contrôle de fréquence des mouvements
        if (now - lastMoveTime < MOVE_DELAY) return;

        // Sélection de la cible : joueur le plus proche
        Player target = (distance(p1) < distance(p2)) ? p1 : p2;

        // EXPLOSION GÉANTE si contact direct et cooldown terminé
        if (canExplode && isNextTo(target)) {
            explode(board);
            canExplode = false;
            lastExplosion = now;
            return; // Ne pas bouger après explosion
        }

        // Réactivation de l'explosion après cooldown
        if (!canExplode && now - lastExplosion > EXPLOSION_COOLDOWN) {
            canExplode = true;
        }

        // MOUVEMENT INTELLIGENT avec pathfinding
        performIntelligentMovement(board, target);

        // Système anti-blocage
        if (x == lastX && y == lastY) {
            stuckCounter++;
        } else {
            stuckCounter = 0;
            lastX = x;
            lastY = y;
        }

        lastMoveTime = now;

        // Mort si touché par une explosion
        if (board.isExplosionAt(x, y)) {
            alive = false;
        }
    }

    /**
     * NOUVEAU : Mouvement intelligent avec pathfinding et anti-blocage
     */
    private void performIntelligentMovement(Legend1v1Board board, Player target) {
        com.bomberman.model.enums.Direction bestDirection = null;

        // Déblocage forcé si bloqué trop longtemps
        if (stuckCounter >= 3) {
            bestDirection = getRandomValidDirection(board);
            stuckCounter = 0;
        } else {
            // Pathfinding intelligent utilisant les méthodes du plateau
            bestDirection = board.getBestDirection(x, y, target.getX(), target.getY());

            // Mouvement tactique si pas de chemin direct
            if (bestDirection == null) {
                bestDirection = getTacticalMovement(board, target);
            }

            // Mouvement aléatoire en dernier recours
            if (bestDirection == null) {
                bestDirection = getRandomValidDirection(board);
            }
        }

        // Exécution du mouvement
        if (bestDirection != null) {
            tryMove(board, bestDirection.getDx(), bestDirection.getDy());
            lastDirection = bestDirection;
        }
    }

    /**
     * NOUVEAU : Mouvement tactique quand le pathfinding direct échoue
     */
    private com.bomberman.model.enums.Direction getTacticalMovement(Legend1v1Board board, Player target) {
        // Évite de revenir immédiatement en arrière
        com.bomberman.model.enums.Direction oppositeDir = getOppositeDirection(lastDirection);

        // Teste toutes les directions sauf l'opposée
        for (com.bomberman.model.enums.Direction dir : com.bomberman.model.enums.Direction.values()) {
            if (dir == oppositeDir) continue;

            int nx = x + dir.getDx();
            int ny = y + dir.getDy();

            if (board.isValidPosition(nx, ny) &&
                    board.getCell(nx, ny).isWalkable() &&
                    !board.hasEnemyAt(nx, ny) &&
                    !board.isExplosionAt(nx, ny)) { // Évite les explosions actives
                return dir;
            }
        }
        return null;
    }

    /**
     * NOUVEAU : Direction aléatoire valide pour déblocage
     */
    private com.bomberman.model.enums.Direction getRandomValidDirection(Legend1v1Board board) {
        com.bomberman.model.enums.Direction[] directions = com.bomberman.model.enums.Direction.values();
        java.util.List<com.bomberman.model.enums.Direction> validDirections = new java.util.ArrayList<>();

        for (com.bomberman.model.enums.Direction dir : directions) {
            int nx = x + dir.getDx();
            int ny = y + dir.getDy();

            if (board.isValidPosition(nx, ny) &&
                    board.getCell(nx, ny).isWalkable() &&
                    !board.hasEnemyAt(nx, ny)) {
                validDirections.add(dir);
            }
        }

        if (validDirections.isEmpty()) return null;
        return validDirections.get(rand.nextInt(validDirections.size()));
    }

    /**
     * NOUVEAU : Calcule la direction opposée
     */
    private com.bomberman.model.enums.Direction getOppositeDirection(com.bomberman.model.enums.Direction dir) {
        if (dir == null) return null;
        switch (dir) {
            case UP: return com.bomberman.model.enums.Direction.DOWN;
            case DOWN: return com.bomberman.model.enums.Direction.UP;
            case LEFT: return com.bomberman.model.enums.Direction.RIGHT;
            case RIGHT: return com.bomberman.model.enums.Direction.LEFT;
            default: return null;
        }
    }

    /**
     * Explosion géante en forme de croix (portée 2)
     * Plus dangereuse et spectaculaire que les bombes normales
     */
    private void explode(Legend1v1Board board) {
        System.out.println("💥 Bomber explose à (" + x + "," + y + ") !");

        // Explosion en croix étendue (portée 2)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                // Forme de croix : dx=0 OU dy=0, distance max 2
                if ((dx == 0 || dy == 0) && Math.abs(dx) + Math.abs(dy) <= 2) {
                    int explosionX = x + dx;
                    int explosionY = y + dy;
                    if (board.isValidPosition(explosionX, explosionY)) {
                        board.createExplosion(explosionX, explosionY);
                    }
                }
            }
        }
    }

    /**
     * Tentative de mouvement avec vérifications de sécurité
     */
    private void tryMove(Legend1v1Board board, int dx, int dy) {
        int nx = x + dx;
        int ny = y + dy;

        if (board.isValidPosition(nx, ny) &&
                board.getCell(nx, ny).isWalkable() &&
                !board.hasEnemyAt(nx, ny) &&
                !board.isExplosionAt(nx, ny)) { // Évite les explosions
            x = nx;
            y = ny;
        }
    }

    /**
     * Vérifie si le joueur est adjacent (distance 1)
     */
    private boolean isNextTo(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y) == 1;
    }

    /**
     * Distance de Manhattan vers un joueur
     */
    private int distance(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y);
    }

    // --------- Accesseurs et méthodes utilitaires ----------
    public boolean isAlive() { return alive; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void kill() {
        alive = false;
        System.out.println("💀 Bomber éliminé à (" + x + "," + y + ")");
    }
    public boolean canExplode() { return canExplode; }
    public int getStuckCounter() { return stuckCounter; }
}
