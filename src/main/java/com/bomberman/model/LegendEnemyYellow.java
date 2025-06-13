/**
 * Ennemi Yellow IA avancé pour le mode Legend 1v1
 * Comportement : poursuite rapide et agressive des joueurs
 * Attaque au contact direct (fait perdre une vie)
 */
package com.bomberman.model;

import java.util.Random;

public class LegendEnemyYellow {
    private int x, y;                    // Position sur la grille
    private boolean alive = true;        // État de vie
    private final Random rand = new Random();

    // NOUVELLES VARIABLES pour IA améliorée
    private long lastMoveTime = 0;
    private static final int MOVE_DELAY = 300; // Plus rapide que Bomber
    private int stuckCounter = 0;        // Compteur anti-blocage
    private int lastX = -1, lastY = -1;  // Position précédente
    private com.bomberman.model.enums.Direction lastDirection = null;
    private Player currentTarget = null; // Cible persistante
    private long targetSwitchTime = 0;   // Derniert changement de cible
    private static final int TARGET_SWITCH_DELAY = 2000; // Délai avant changement de cible

    /**
     * Constructeur avec position initiale
     */
    public LegendEnemyYellow(int x, int y) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
    }

    /**
     * IA principale - Tour de jeu AMÉLIORÉ
     * Stratégie : poursuite intelligente avec cible persistante
     */
    public void playTurn(Legend1v1Board board, Player p1, Player p2) {
        if (!alive) return;

        long now = System.currentTimeMillis();

        // Contrôle de fréquence (plus rapide que Bomber)
        if (now - lastMoveTime < MOVE_DELAY) return;

        // SÉLECTION INTELLIGENTE de la cible
        Player target = selectOptimalTarget(p1, p2, now);

        // ATTAQUE si adjacent au joueur
        if (isNextTo(target)) {
            performAttack(target, board);
            lastMoveTime = now;
            return; // Ne pas bouger après attaque
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

        // Mort si touché par explosion
        if (board.isExplosionAt(x, y)) {
            alive = false;
            System.out.println("💀 Yellow éliminé par explosion à (" + x + "," + y + ")");
        }
    }

    /**
     * NOUVEAU : Sélection intelligente de cible avec persistance
     * Évite les changements de cible trop fréquents (zigzag)
     */
    private Player selectOptimalTarget(Player p1, Player p2, long now) {
        // Maintient la cible actuelle si encore valide
        if (currentTarget != null && currentTarget.isAlive() &&
                now - targetSwitchTime < TARGET_SWITCH_DELAY) {
            return currentTarget;
        }

        // Sélection d'une nouvelle cible optimale
        Player newTarget;
        int dist1 = distance(p1);
        int dist2 = distance(p2);

        if (!p1.isAlive()) {
            newTarget = p2;
        } else if (!p2.isAlive()) {
            newTarget = p1;
        } else if (dist1 < dist2) {
            newTarget = p1;
        } else if (dist2 < dist1) {
            newTarget = p2;
        } else {
            // À distance égale, garde la cible actuelle ou choisit aléatoirement
            newTarget = (currentTarget != null && currentTarget.isAlive()) ?
                    currentTarget : (rand.nextBoolean() ? p1 : p2);
        }

        // Mise à jour de la cible si changement
        if (newTarget != currentTarget) {
            currentTarget = newTarget;
            targetSwitchTime = now;
            System.out.println("🎯 Yellow change de cible vers Joueur " +
                    (newTarget == p1 ? "1" : "2") + " à distance " + distance(newTarget));
        }

        return newTarget;
    }

    /**
     * NOUVEAU : Attaque optimisée avec feedback
     */
    private void performAttack(Player target, Legend1v1Board board) {
        System.out.println("⚔️ Yellow attaque le Joueur " +
                (target == board.getPlayer1() ? "1" : "2") + " à (" + x + "," + y + ")");

        target.takeDamage(); // Fait perdre une vie
        if (target.isAlive()) {
            target.respawnAtStart(board); // Respawn si encore vivant
        }
    }

    /**
     * NOUVEAU : Mouvement intelligent avec pathfinding avancé
     */
    private void performIntelligentMovement(Legend1v1Board board, Player target) {
        com.bomberman.model.enums.Direction bestDirection = null;

        // Déblocage forcé si bloqué trop longtemps
        if (stuckCounter >= 4) {
            bestDirection = getRandomValidDirection(board);
            stuckCounter = 0;
            System.out.println("🔄 Yellow utilisne mouvement anti-blocage");
        } else {
            // Pathfinding intelligent
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
     * NOUVEAU : Mouvement tactique avec évitement des dangers
     */
    private com.bomberman.model.enums.Direction getTacticalMovement(Legend1v1Board board, Player target) {
        // Évite de revenir en arrière immédiatement
        com.bomberman.model.enums.Direction oppositeDir = getOppositeDirection(lastDirection);

        // Calcule les priorités de mouvement
        java.util.List<DirectionPriority> priorities = new java.util.ArrayList<>();

        for (com.bomberman.model.enums.Direction dir : com.bomberman.model.enums.Direction.values()) {
            if (dir == oppositeDir) continue; // Évite le retour en arrière

            int nx = x + dir.getDx();
            int ny = y + dir.getDy();

            if (board.isValidPosition(nx, ny) &&
                    board.getCell(nx, ny).isWalkable() &&
                    !board.hasEnemyAt(nx, ny)) {

                int priority = calculateMovePriority(board, nx, ny, target);
                if (priority > 0) {
                    priorities.add(new DirectionPriority(dir, priority));
                }
            }
        }

        // Trie par priorité et prend la meilleure
        if (!priorities.isEmpty()) {
            priorities.sort((a, b) -> Integer.compare(b.priority, a.priority));
            return priorities.get(0).direction;
        }

        return null;
    }

    /**
     * NOUVEAU : Calcule la priorité d'un mouvement
     */
    private int calculateMovePriority(Legend1v1Board board, int nx, int ny, Player target) {
        int priority = 100; // Priorité de base

        // Évite les explosions (priorité critique)
        if (board.isExplosionAt(nx, ny)) {
            return 0; // Mouvement interdit
        }

        // Évite les bombes proches (danger élevé)
        for (Bomb bomb : board.getBombs()) {
            int bombDist = Math.abs(nx - bomb.getX()) + Math.abs(ny - bomb.getY());
            if (bombDist <= bomb.getExplosionRange()) {
                priority -= 50; // Très dangereux
            }
        }

        // Bonus pour se rapprocher de la cible
        int currentDistToTarget = Math.abs(x - target.getX()) + Math.abs(y - target.getY());
        int newDistToTarget = Math.abs(nx - target.getX()) + Math.abs(ny - target.getY());
        if (newDistToTarget < currentDistToTarget) {
            priority += 30; // Bonus pour rapprochement
        } else if (newDistToTarget > currentDistToTarget) {
            priority -= 20; // Malus pour éloignement
        }

        // Évite les coins (malus léger)
        if ((nx == 1 || nx == 13) && (ny == 1 || ny == 11)) {
            priority -= 10;
        }

        return Math.max(priority, 1); // Minimum 1 pour rester valide
    }

    /**
     * Classe utilitaire pour les priorités de direction
     */
    private static class DirectionPriority {
        com.bomberman.model.enums.Direction direction;
        int priority;

        DirectionPriority(com.bomberman.model.enums.Direction direction, int priority) {
            this.direction = direction;
            this.priority = priority;
        }
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
                    !board.hasEnemyAt(nx, ny) &&
                    !board.isExplosionAt(nx, ny)) { // Évite les explosions
                validDirections.add(dir);
            }
        }

        if (validDirections.isEmpty()) return null;
        return validDirections.get(rand.nextInt(validDirections.size()));
    }

    /**
     * NOUVEAU : Direction opposée pour éviter les aller-retours
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
     * Tentative de mouvement avec vérifications
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
     * Vérifie si le joueur est adjacent
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
        System.out.println("💀 Yellow éliminé à (" + x + "," + y + ")");
    }
    public Player getCurrentTarget() { return currentTarget; }
    public int getStuckCounter() { return stuckCounter; }
}