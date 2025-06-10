package com.bomberman.model;

import java.util.Random;

/**
 * Ennemi Yellow IA :
 * - Poursuit le joueur humain le plus proche avec un mouvement légèrement aléatoire.
 * - S'il touche un joueur, il lui enlève une vie et le fait respawn.
 * - Meurt s'il est touché par une explosion.
 */
public class LegendEnemyYellow {
    private int x, y;               // Position du Yellow sur la grille
    private boolean alive = true;   // Etat de vie de l'ennemi
    private final Random rand = new Random();

    /**
     * Constructeur pour positionner le Yellow au départ.
     */
    public LegendEnemyYellow(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Action réalisée à chaque tour de jeu :
     * - Suit le joueur humain le plus proche
     * - 1/3 du temps, fait un mouvement totalement aléatoire pour casser la monotonie
     * - Attaque le joueur s'il est à côté
     * - Meurt s'il est sur une explosion
     */
    public void playTurn(Legend1v1Board board, Player p1, Player p2) {
        if (!alive) return;

        // Cible : le joueur le plus proche (en cases)
        Player target = (distance(p1) < distance(p2)) ? p1 : p2;

        // Si adjacent au joueur cible, attaque (inflige des dégâts et fait respawn)
        if (isNextTo(target)) {
            target.takeDamage();
            target.respawnAtStart(board);
        }

        // Mouvement semi-aléatoire : 1/3 random, sinon vers le joueur
        int dx, dy;
        if (rand.nextInt(3) == 0) { // 1 chance sur 3 de mouvement aléatoire
            int[] dirs = {-1, 0, 1};
            dx = dirs[rand.nextInt(3)];
            dy = dirs[rand.nextInt(3)];
        } else {
            dx = Integer.compare(target.getX(), x);
            dy = Integer.compare(target.getY(), y);
        }

        tryMove(board, dx, dy);

        // Meurt s'il se trouve sur une explosion
        if (board.isExplosionAt(x, y)) alive = false;
    }

    /**
     * Essaie de déplacer le Yellow.
     * Ne se déplace que si la case est walkable ET sans autre ennemi vivant dessus.
     */
    private void tryMove(Legend1v1Board board, int dx, int dy) {
        int nx = x + dx;
        int ny = y + dy;
        if (board.isValidPosition(nx, ny)
                && board.getCell(nx, ny).isWalkable()
                && !board.hasEnemyAt(nx, ny)) {
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
    public void kill() { alive = false; }
}
