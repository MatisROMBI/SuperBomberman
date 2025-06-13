/**
 * Classe représentant un ennemi générique avec comportement basique
 * Utilisé dans les premiers prototypes avant les ennemis spécialisés Legend
 * Déplacements aléatoires avec évitement d'obstacles
 */
package com.bomberman.model;

import com.bomberman.model.enums.Direction;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy {
    private int x, y;                    // Position actuelle de l'ennemi sur la grille
    private boolean isAlive;             // État de vie de l'ennemi
    private Direction currentDirection;  // Direction actuelle de déplacement
    private Random random;               // Générateur de nombres aléatoires
    private long lastMoveTime;           // Timestamp du dernier mouvement
    private static final int MOVE_DELAY = 800; // Délai entre mouvements en millisecondes

    /**
     * Constructeur avec position de départ
     * @param startX Position X initiale
     * @param startY Position Y initiale
     */
    public Enemy(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.isAlive = true;
        this.random = new Random();
        // Direction initiale aléatoire
        this.currentDirection = Direction.values()[random.nextInt(4)];
        this.lastMoveTime = System.currentTimeMillis();
    }

    /**
     * Mise à jour de l'ennemi à chaque frame
     * Gère le timing des mouvements selon le délai défini
     * @param board Plateau de jeu pour vérifier les collisions
     */
    public void update(Board board) {
        if (!isAlive) return;

        long currentTime = System.currentTimeMillis();
        // Vérifie si assez de temps s'est écoulé depuis le dernier mouvement
        if (currentTime - lastMoveTime >= MOVE_DELAY) {
            move(board);
            lastMoveTime = currentTime;
        }
    }

    /**
     * Logique de déplacement de l'ennemi
     * Stratégie : mouvement aléatoire parmi les directions possibles
     * @param board Plateau pour vérifier la validité des mouvements
     */
    private void move(Board board) {
        Direction[] directions = Direction.values();
        List<Direction> possibleMoves = new ArrayList<>();

        // Analyse de toutes les directions pour trouver les mouvements valides
        for (Direction dir : directions) {
            int nx = x + dir.getDx();
            int ny = y + dir.getDy();

            // Vérifie si la position est valide sur le plateau
            if (board.isValidPosition(nx, ny)) {
                Cell targetCell = board.getCell(nx, ny);
                // Vérifie si la cellule est accessible (pas de mur, pas d'autre ennemi)
                if (targetCell.isWalkable() && !targetCell.hasEnemy()) {
                    possibleMoves.add(dir);
                }
            }
        }

        // Effectue un mouvement aléatoire parmi les possibilités
        if (!possibleMoves.isEmpty()) {
            Direction chosen = possibleMoves.get(random.nextInt(possibleMoves.size()));
            int newX = x + chosen.getDx();
            int newY = y + chosen.getDy();

            // Met à jour les cellules du plateau
            board.getCell(x, y).setHasEnemy(false);  // Libère l'ancienne position
            x = newX;
            y = newY;
            board.getCell(x, y).setHasEnemy(true);   // Occupe la nouvelle position
            currentDirection = chosen;
        }
        // Si aucun mouvement n'est possible, l'ennemi reste sur place
    }

    /**
     * Tue l'ennemi (appelé lors d'une explosion)
     */
    public void die() {
        isAlive = false;
    }

    // ===== ACCESSEURS =====

    /**
     * @return Position X actuelle
     */
    public int getX() { return x; }

    /**
     * @return Position Y actuelle
     */
    public int getY() { return y; }

    /**
     * @return true si l'ennemi est encore vivant
     */
    public boolean isAlive() { return isAlive; }
}