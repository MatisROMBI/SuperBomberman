package com.bomberman.model;

import java.util.Random;

/**
 * Ennemi Yellow IA AMÉLIORÉ :
 * - Poursuit le joueur humain le plus proche avec intelligence tactique
 * - Déplacements plus fluides et cohérents
 * - Évite les pièges et les explosions
 * - S'il touche un joueur, il lui enlève une vie et le fait respawn
 * - Système anti-blocage avancé
 * - Meurt s'il est touché par une explosion
 */
public class LegendEnemyYellow {
    private int x, y;               // Position du Yellow sur la grille
    private boolean alive = true;   // État de vie de l'ennemi
    private final Random rand = new Random();

    // NOUVELLES VARIABLES pour IA améliorée
    private long lastMoveTime = 0;
    private static final int MOVE_DELAY = 300; // Plus rapide que Bomber
    private int stuckCounter = 0; // Compteur anti-blocage
    private int lastX = -1, lastY = -1; // Position précédente
    private com.bomberman.model.enums.Direction lastDirection = null; // Dernière direction
    private Player currentTarget = null; // Cible actuelle pour persistance
    private long targetSwitchTime = 0; // Dernière fois qu'on a changé de cible
    private static final int TARGET_SWITCH_DELAY = 2000; // ms avant de pouvoir changer de cible

    /**
     * Constructeur pour positionner le Yellow au départ.
     */
    public LegendEnemyYellow(int x, int y) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
    }

    /**
     * Action AMÉLIORÉE réalisée à chaque tour de jeu :
     * - IA plus intelligente et cohérente
     * - Cible persistante pour éviter les zigzags
     * - Attaque intelligente des joueurs
     * - Anti-blocage avancé
     */
    public void playTurn(Legend1v1Board board, Player p1, Player p2) {
        if (!alive) return;

        long now = System.currentTimeMillis();

        // Système de cooldown pour les mouvements (plus rapide que Bomber)
        if (now - lastMoveTime < MOVE_DELAY) return;

        // SÉLECTION INTELLIGENTE DE LA CIBLE
        Player target = selectOptimalTarget(p1, p2, now);

        // ATTAQUE si adjacent au joueur cible
        if (isNextTo(target)) {
            performAttack(target, board);
            lastMoveTime = now;
            return; // Ne pas bouger après attaque
        }

        // MOUVEMENT INTELLIGENT avec pathfinding et tactiques
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

        // Meurt s'il se trouve sur une explosion
        if (board.isExplosionAt(x, y)) {
            alive = false;
            System.out.println("💀 Yellow eliminated by explosion at (" + x + "," + y + ")");
        }
    }

    /**
     * NOUVEAU : Sélection intelligente de la cible avec persistance
     */
    private Player selectOptimalTarget(Player p1, Player p2, long now) {
        // Si on a déjà une cible et qu'elle est encore valide, continuer avec elle
        if (currentTarget != null && currentTarget.isAlive() &&
                now - targetSwitchTime < TARGET_SWITCH_DELAY) {
            return currentTarget;
        }

        // Choisir la nouvelle cible optimale
        Player newTarget;

        // Prioriser le joueur le plus proche
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
            // À distance égale, garder la cible actuelle ou choisir aléatoirement
            newTarget = (currentTarget != null && currentTarget.isAlive()) ?
                    currentTarget : (rand.nextBoolean() ? p1 : p2);
        }

        // Mettre à jour la cible si nécessaire
        if (newTarget != currentTarget) {
            currentTarget = newTarget;
            targetSwitchTime = now;
            System.out.println("🎯 Yellow changed target to Player " +
                    (newTarget == p1 ? "1" : "2") + " at distance " + distance(newTarget));
        }

        return newTarget;
    }

    /**
     * NOUVEAU : Attaque optimisée avec feedback
     */
    private void performAttack(Player target, Legend1v1Board board) {
        System.out.println("⚔️ Yellow attacks Player " +
                (target == board.getPlayer1() ? "1" : "2") + " at (" + x + "," + y + ")");

        target.takeDamage();
        if (target.isAlive()) {
            target.respawnAtStart(board);
        }
    }

    /**
     * NOUVEAU : Mouvement intelligent avec pathfinding et tactiques avancées
     */
    private void performIntelligentMovement(Legend1v1Board board, Player target) {
        com.bomberman.model.enums.Direction bestDirection = null;

        // Si bloqué depuis trop longtemps, mouvement aléatoire pour débloquer
        if (stuckCounter >= 4) {
            bestDirection = getRandomValidDirection(board);
            stuckCounter = 0; // Reset
            System.out.println("🔄 Yellow using anti-stuck movement");
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
     * NOUVEAU : Mouvement tactique avec évitement des dangers
     */
    private com.bomberman.model.enums.Direction getTacticalMovement(Legend1v1Board board, Player target) {
        // Éviter de revenir en arrière immédiatement
        com.bomberman.model.enums.Direction oppositeDir = getOppositeDirection(lastDirection);

        // Calculer les priorités de mouvement
        java.util.List<DirectionPriority> priorities = new java.util.ArrayList<>();

        for (com.bomberman.model.enums.Direction dir : com.bomberman.model.enums.Direction.values()) {
            if (dir == oppositeDir) continue; // Éviter de revenir en arrière

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

        // Trier par priorité et prendre la meilleure
        if (!priorities.isEmpty()) {
            priorities.sort((a, b) -> Integer.compare(b.priority, a.priority));
            return priorities.get(0).direction;
        }

        return null;
    }

    /**
     * NOUVEAU : Calcule la priorité d'un mouvement selon les dangers et la cible
     */
    private int calculateMovePriority(Legend1v1Board board, int nx, int ny, Player target) {
        int priority = 100; // Base priority

        // Éviter les explosions (priorité critique)
        if (board.isExplosionAt(nx, ny)) {
            return 0; // Mouvement interdit
        }

        // Vérifier les bombes à proximité (danger élevé)
        for (Bomb bomb : board.getBombs()) {
            int bombDist = Math.abs(nx - bomb.getX()) + Math.abs(ny - bomb.getY());
            if (bombDist <= bomb.getExplosionRange()) {
                priority -= 50; // Très dangereux
            }
        }

        // Se rapprocher de la cible (bonus)
        int currentDistToTarget = Math.abs(x - target.getX()) + Math.abs(y - target.getY());
        int newDistToTarget = Math.abs(nx - target.getX()) + Math.abs(ny - target.getY());
        if (newDistToTarget < currentDistToTarget) {
            priority += 30; // Bonus pour se rapprocher
        } else if (newDistToTarget > currentDistToTarget) {
            priority -= 20; // Malus pour s'éloigner
        }

        // Éviter les coins (malus léger) - utilise des constantes fixes pour éviter les imports
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
     * NOUVEAU : Obtenir une direction aléatoire valide pour déblocage
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
                    !board.isExplosionAt(nx, ny)) { // Éviter les explosions
                validDirections.add(dir);
            }
        }

        if (validDirections.isEmpty()) return null;
        return validDirections.get(rand.nextInt(validDirections.size()));
    }

    /**
     * NOUVEAU : Obtenir la direction opposée pour éviter les aller-retours
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
     * Essaie de déplacer le Yellow avec vérifications renforcées.
     */
    private void tryMove(Legend1v1Board board, int dx, int dy) {
        int nx = x + dx;
        int ny = y + dy;
        if (board.isValidPosition(nx, ny)
                && board.getCell(nx, ny).isWalkable()
                && !board.hasEnemyAt(nx, ny)
                && !board.isExplosionAt(nx, ny)) { // Éviter les explosions
            x = nx;
            y = ny;
        }
    }

    /**
     * True si le joueur est sur une case adjacente à Yellow.
     */
    private boolean isNextTo(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y) == 1;
    }

    /**
     * Distance de Manhattan entre le Yellow et un joueur.
     */
    private int distance(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y);
    }

    // --------- Getters / Setters / Kill ----------

    /** Renvoie true si le Yellow est encore en vie */
    public boolean isAlive() { return alive; }

    /** Position X du Yellow */
    public int getX() { return x; }

    /** Position Y du Yellow */
    public int getY() { return y; }

    /** Tue le Yellow (appelé si sur explosion) */
    public void kill() {
        alive = false;
        System.out.println("💀 Yellow eliminated at (" + x + "," + y + ")");
    }

    /** NOUVEAU : Cible actuelle pour debug */
    public Player getCurrentTarget() { return currentTarget; }

    /** NOUVEAU : Compteur de blocage pour debug */
    public int getStuckCounter() { return stuckCounter; }
}