package com.bomberman.model;

import java.util.Random;

/**
 * Ennemi Bomber IA : Se déplace vers l'humain le plus proche.
 * - Pose une explosion géante au contact.
 * - Meurt si touché par une explosion.
 */
public class LegendEnemyBomber {
    private int x, y;
    private boolean canExplode = true;
    private long lastExplosion = 0;
    private static final int EXPLOSION_COOLDOWN = 2000; // ms
    private boolean alive = true;
    private final Random rand = new Random();

    public LegendEnemyBomber(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Tour de jeu : suit le joueur le plus proche (avec un peu de random), explose s'il touche, meurt sur explosion.
     */
    public void playTurn(Legend1v1Board board, Player p1, Player p2) {
        if (!alive) return;
        long now = System.currentTimeMillis();

        // Cible : le joueur humain le plus proche
        Player target = (distance(p1) < distance(p2)) ? p1 : p2;

        // Explosion géante si contact
        if (canExplode && isNextTo(target)) {
            explode(board);
            canExplode = false;
            lastExplosion = now;
        }
        if (!canExplode && now - lastExplosion > EXPLOSION_COOLDOWN) {
            canExplode = true;
        }

        // Mouvement semi-aléatoire vers le joueur
        int dx = Integer.compare(target.getX(), x);
        int dy = Integer.compare(target.getY(), y);

        // 1/3 de chance de faire un pas aléatoire, sinon traque
        if (rand.nextInt(3) == 0) {
            int[] dirs = {-1, 0, 1};
            dx = dirs[rand.nextInt(3)];
            dy = dirs[rand.nextInt(3)];
        }

        tryMove(board, dx, dy);

        // Meurt si sur explosion
        if (board.isExplosionAt(x, y)) alive = false;
    }

    /**
     * Explosion géante sur 2 cases dans chaque direction (croix).
     */
    private void explode(Legend1v1Board board) {
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                if (Math.abs(dx) + Math.abs(dy) <= 2 && board.isValidPosition(x + dx, y + dy))
                    board.createExplosion(x + dx, y + dy);
        // (On peut ajouter un son ici)
    }

    /**
     * Déplacement possible uniquement si la case est marchable et pas déjà occupée par un ennemi.
     */
    private void tryMove(Legend1v1Board board, int dx, int dy) {
        int nx = x + dx;
        int ny = y + dy;
        if (board.isValidPosition(nx, ny) && board.getCell(nx, ny).isWalkable() && !board.hasEnemyAt(nx, ny)) {
            x = nx;
            y = ny;
        }
    }

    private boolean isNextTo(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y) == 1;
    }

    private int distance(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y);
    }

    public boolean isAlive() { return alive; }
    public int getX() { return x; }
    public int getY() { return y; }

    // Permet de tuer l'ennemi depuis l'extérieur si besoin (explosion du plateau)
    public void kill() { alive = false; }
}
