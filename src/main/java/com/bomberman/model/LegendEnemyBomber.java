package com.bomberman.model;

import java.util.Random;

/**
 * Ennemi Bomber IA :
 * - Déplacements plus intelligents et cohérents
 * - Pathfinding amélioré vers le joueur le plus proche
 * - Pose une explosion géante au contact (cooldown optimisé)
 * - Évite de rester bloqué avec des stratégies de déblocage
 * - Meurt si touché par une explosion
 */
public class LegendEnemyBomber {
    private int x, y;
    private boolean canExplode = true;
    private long lastExplosion = 0;
    private static final int EXPLOSION_COOLDOWN = 2500; // ms (légèrement plus long)
    private boolean alive = true;
    private final Random rand = new Random();


    private long lastMoveTime = 0;
    private static final int MOVE_DELAY = 400; // Déplacement plus fluide
    private int stuckCounter = 0; // Compteur anti-blocage
    private int lastX = -1, lastY = -1; // Position précédente
    private com.bomberman.model.enums.Direction lastDirection = null; // Dernière direction

    public LegendEnemyBomber(int x, int y) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
    }

    /**
     *  IA plus cohérente et intelligente
     */
    public void playTurn(Legend1v1Board board, Player p1, Player p2) {
        if (!alive) return;

        long now = System.currentTimeMillis();

        // Système de cooldown pour les mouvements (plus fluide)
        if (now - lastMoveTime < MOVE_DELAY) return;

        // Cible : le joueur humain le plus proche
        Player target = (distance(p1) < distance(p2)) ? p1 : p2;

        // EXPLOSION GÉANTE si contact et cooldown fini
        if (canExplode && isNextTo(target)) {
            explode(board);
            canExplode = false;
            lastExplosion = now;
            return; // Ne pas bouger après explosion
        }

        // Réactiver l'explosion après cooldown
        if (!canExplode && now - lastExplosion > EXPLOSION_COOLDOWN) {
            canExplode = true;
        }

        // MOUVEMENT INTELLIGENT avec pathfinding amélioré
        performIntelligentMovement(board, target);

        // Détection anti-blocage
        if (x == lastX && y == lastY) {
            stuckCounter++;
        } else {
            stuckCounter = 0;
            lastX = x;
            lastY = y;
        }

        lastMoveTime = now;

        // Meurt si sur explosion
        if (board.isExplosionAt(x, y)) alive = false;
    }

    /**
     * Mouvement intelligent avec pathfinding et anti-blocage
     */
    private void performIntelligentMovement(Legend1v1Board board, Player target) {
        com.bomberman.model.enums.Direction bestDirection = null;

        // Si bloqué depuis trop longtemps, mouvement aléatoire pour débloquer
        if (stuckCounter >= 3) {
            bestDirection = getRandomValidDirection(board);
            stuckCounter = 0; // Reset
        } else {
            // Pathfinding intelligent : utiliser la méthode du board
            bestDirection = board.getBestDirection(x, y, target.getX(), target.getY());

            // Si pas de chemin optimal, essayer un mouvement tactique
            if (bestDirection == null) {
                bestDirection = getTacticalMovement(board, target);
            }

            // En dernier recours, mouvement aléatoire
            if (bestDirection == null) {
                bestDirection = getRandomValidDirection(board);
            }
        }

        // Exécuter le mouvement choisi
        if (bestDirection != null) {
            tryMove(board, bestDirection.getDx(), bestDirection.getDy());
            lastDirection = bestDirection;
        }
    }

    /**
     * Mouvement tactique quand le pathfinding direct échoue
     */
    private com.bomberman.model.enums.Direction getTacticalMovement(Legend1v1Board board, Player target) {
        // Éviter de revenir en arrière immédiatement
        com.bomberman.model.enums.Direction oppositeDir = getOppositeDirection(lastDirection);

        // Essayer les directions qui ne sont pas l'opposé de la dernière
        for (com.bomberman.model.enums.Direction dir : com.bomberman.model.enums.Direction.values()) {
            if (dir == oppositeDir) continue; // Éviter de revenir en arrière

            int nx = x + dir.getDx();
            int ny = y + dir.getDy();

            if (board.isValidPosition(nx, ny) &&
                    board.getCell(nx, ny).isWalkable() &&
                    !board.hasEnemyAt(nx, ny) &&
                    !board.isExplosionAt(nx, ny)) { // Éviter les explosions

                return dir;
            }
        }

        return null;
    }

    /**
     * Obtenir une direction aléatoire valide pour déblocage
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
     * Obtenir la direction opposée pour éviter les aller-retours
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
     * Explosion géante sur 2 cases dans chaque direction (croix)
     */
    private void explode(Legend1v1Board board) {
        // Explosion en croix plus large et plus dangereuse
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                // Forme de croix plus étendue
                if ((dx == 0 || dy == 0) && Math.abs(dx) + Math.abs(dy) <= 2) {
                    int explosionX = x + dx;
                    int explosionY = y + dy;
                    if (board.isValidPosition(explosionX, explosionY)) {
                        board.createExplosion(explosionX, explosionY);
                    }
                }
            }
        }

        // Sons et effets peuvent être ajoutés ici
        System.out.println("💥 Bomber explode à (" + x + "," + y + ") !");
    }

    /**
     * Déplacement avec vérifications renforcées
     */
    private void tryMove(Legend1v1Board board, int dx, int dy) {
        int nx = x + dx;
        int ny = y + dy;

        if (board.isValidPosition(nx, ny) &&
                board.getCell(nx, ny).isWalkable() &&
                !board.hasEnemyAt(nx, ny) &&
                !board.isExplosionAt(nx, ny)) { // Éviter les explosions actives
            x = nx;
            y = ny;
        }
    }

    /**
     * True si le joueur est sur une case adjacente à Bomber
     */
    private boolean isNextTo(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y) == 1;
    }

    /**
     * Distance de Manhattan entre le Bomber et un joueur
     */
    private int distance(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y);
    }



    /** Renvoie true si le Bomber est encore en vie */
    public boolean isAlive() { return alive; }

    /** Position X du Bomber */
    public int getX() { return x; }

    /** Position Y du Bomber */
    public int getY() { return y; }

    /** Tue le Bomber (appelé si sur explosion) */
    public void kill() {
        alive = false;
        System.out.println("💀 Bomber eliminated at (" + x + "," + y + ")");
    }

    /**  True si le Bomber peut exploser actuellement */
    public boolean canExplode() { return canExplode; }

    /**  Compteur de blocage pour debug */
    public int getStuckCounter() { return stuckCounter; }
}