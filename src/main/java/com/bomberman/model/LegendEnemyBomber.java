package com.bomberman.model;

import java.util.Random;

public class LegendEnemyBomber {
    private int x, y;
    private boolean canExplode = true;
    private long lastExplosion = 0;
    private static final int EXPLOSION_COOLDOWN = 2000; // ms

    public LegendEnemyBomber(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void playTurn(Legend1v1Board board, Player p1, Player p2) {
        long now = System.currentTimeMillis();
        // Suivre le joueur le plus proche
        Player target = (distance(p1) < distance(p2)) ? p1 : p2;
        if (canExplode && isNextTo(target)) {
            // Explosion géante
            explode(board);
            canExplode = false;
            lastExplosion = now;
        }
        if (!canExplode && now - lastExplosion > EXPLOSION_COOLDOWN) {
            canExplode = true;
        }
        // Déplacement random ou vers joueur
        if (now % 3 == 0) { // petite IA "décalée"
            int dx = Integer.compare(target.getX(), x);
            int dy = Integer.compare(target.getY(), y);
            tryMove(board, dx, dy);
        }
    }

    private void explode(Legend1v1Board board) {
        // Crée une explosion sur 2 cases dans chaque direction
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                if (Math.abs(dx) + Math.abs(dy) <= 2)
                    board.getExplosions().add(new Explosion(x + dx, y + dy));
        // (Optionnel: peut jouer un son ici)
    }

    private boolean isNextTo(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y) == 1;
    }

    private int distance(Player p) {
        return Math.abs(p.getX() - x) + Math.abs(p.getY() - y);
    }

    private void tryMove(Legend1v1Board board, int dx, int dy) {
        int nx = x + dx;
        int ny = y + dy;
        if (board.isValidPosition(nx, ny) && board.getCell(nx, ny).isWalkable()) {
            x = nx;
            y = ny;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
