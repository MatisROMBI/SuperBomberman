package com.bomberman.model;

import java.util.Random;

public class LegendEnemyYellow {
    private int x, y;
    private Random rand = new Random();

    public LegendEnemyYellow(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void playTurn(Legend1v1Board board, Player p1, Player p2) {
        Player target = (distance(p1) < distance(p2)) ? p1 : p2;
        // Si contact avec un joueur
        if (isNextTo(target)) {
            target.takeDamage();
            target.respawnAtStart(board);
        }
        // Déplacement IA simple vers la cible
        int dx = Integer.compare(target.getX(), x);
        int dy = Integer.compare(target.getY(), y);
        tryMove(board, dx, dy);
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
