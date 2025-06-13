/**
 * Bot joueur avec intelligence artificielle basique
 * Hérite du Player principal avec des comportements automatisés
 */
package com.bomberman.model;

import com.bomberman.model.enums.Direction;
import com.bomberman.model.enums.GameState;
import com.bomberman.utils.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayerBot extends Player {
    private Random random = new Random();
    private long lastMoveTime = 0;

    // OPTIMISATION: Utilise la constante globale optimisée
    private static final int BOT_MOVE_DELAY = Constants.BOT_MOVE_DELAY;

    // Statistiques spécifiques aux bots
    private int maxBombsBot = 2;
    private int bombsAvailableBot = 2;
    private static final int BOT_START_LIVES = 6;

    /**
     * Constructeur avec position de départ
     */
    public PlayerBot(int startX, int startY) {
        super(startX, startY);
        setLives(BOT_START_LIVES);
    }

    /**
     * Callback spécifique aux bots pour la gestion des bombes
     */
    public void onBombExploded() {
        bombsAvailableBot = Math.min(bombsAvailableBot + 1, maxBombsBot);
    }

    /**
     * Accesseurs spécifiques aux bots
     */
    public int getBombsAvailable() {
        return bombsAvailableBot;
    }

    public int getMaxBombs() {
        return maxBombsBot;
    }

    public void setMaxBombsBot(int max) {
        maxBombsBot = max;
    }

    /**
     * IA principale du bot - appelée à chaque tour
     * Stratégie : poursuite du joueur humain avec pose de bombes tactiques
     */
    public void playTurn(Board board) {
        if (!isAlive()) return;

        // Contrôle de la fréquence des actions
        long now = System.currentTimeMillis();
        if (now - lastMoveTime < BOT_MOVE_DELAY) return;

        Player human = board.getPlayer();
        int px = getX();
        int py = getY();

        // Stratégie prioritaire : poser une bombe si adjacent au joueur
        if (isNextTo(px, py, human.getX(), human.getY()) &&
                bombsAvailableBot > 0 &&
                board.getCell(px, py).getBomb() == null) {

            Bomb bomb = new Bomb(px, py, getExplosionRange());
            board.getCell(px, py).setBomb(bomb);
            board.addBomb(bomb);
            bombsAvailableBot--;
            lastMoveTime = now;
            return;
        }

        // Mouvement intelligent vers le joueur
        Direction bestDir = getDirectionTowards(px, py, human.getX(), human.getY(), board);
        if (bestDir != null) {
            int nx = px + bestDir.getDx();
            int ny = py + bestDir.getDy();
            if (board.isValidPosition(nx, ny) &&
                    board.getCell(nx, ny).isWalkable() &&
                    !board.getCell(nx, ny).hasPlayer()) {
                move(bestDir, board, GameState.PLAYING);
            }
        } else {
            // Mouvement aléatoire si pas de chemin direct
            List<Direction> dirs = new ArrayList<>();
            Collections.addAll(dirs, Direction.values());
            Collections.shuffle(dirs, random);

            for (Direction dir : dirs) {
                int nx = px + dir.getDx();
                int ny = py + dir.getDy();
                if (board.isValidPosition(nx, ny) &&
                        board.getCell(nx, ny).isWalkable() &&
                        !board.getCell(nx, ny).hasPlayer()) {
                    move(dir, board, GameState.PLAYING);
                    break;
                }
            }
        }

        // Pose de bombe aléatoire (15% de chance)
        if (random.nextDouble() < 0.15 &&
                bombsAvailableBot > 0 &&
                board.getCell(px, py).getBomb() == null) {

            Bomb bomb = new Bomb(px, py, getExplosionRange());
            board.getCell(px, py).setBomb(bomb);
            board.addBomb(bomb);
            bombsAvailableBot--;
        }

        lastMoveTime = now;
    }

    /**
     * Vérifie si deux positions sont adjacentes (distance de 1)
     */
    private boolean isNextTo(int x1, int y1, int x2, int y2) {
        return (Math.abs(x1 - x2) == 1 && y1 == y2) ||
                (Math.abs(y1 - y2) == 1 && x1 == x2);
    }

    /**
     * Calcule la meilleure direction pour se rapprocher d'une cible
     * Pathfinding basique avec évitement d'obstacles
     */
    private Direction getDirectionTowards(int fromX, int fromY, int toX, int toY, Board board) {
        List<Direction> dirs = new ArrayList<>();

        // Priorise les directions qui rapprochent de la cible
        if (toX > fromX) dirs.add(Direction.RIGHT);
        if (toX < fromX) dirs.add(Direction.LEFT);
        if (toY > fromY) dirs.add(Direction.DOWN);
        if (toY < fromY) dirs.add(Direction.UP);

        // Teste chaque direction prioritaire
        for (Direction dir : dirs) {
            int nx = fromX + dir.getDx();
            int ny = fromY + dir.getDy();
            if (board.isValidPosition(nx, ny) &&
                    board.getCell(nx, ny).isWalkable() &&
                    !board.getCell(nx, ny).hasPlayer()) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Application des bonus spécifique aux bots
     */
    @Override
    protected void applyPowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case EXTRA_BOMB:
                maxBombsBot++;
                bombsAvailableBot++;
                break;
            case RANGE_UP:
                setExplosionRange(getExplosionRange() + 1);
                break;
            case LIFE:
                setLives(getLives() + 1);
                break;
            case SPEED:
                setSpeedBoost(true);
                break;
        }
    }

    /**
     * Gestion des dégâts spécifique aux bots
     * Signature différente de Player (pas d'override)
     */
    public void takeDamage(Board board) {
        if (!isAlive()) return;

        setLives(getLives() - 1);
        if (getLives() <= 0) {
            setLives(0);
            board.getCell(getX(), getY()).setHasPlayer(false); // Libère la cellule
            setIsAlive(false);
        }
        setDeathTime(System.currentTimeMillis());
    }

    /**
     * Respawn spécifique aux bots
     */
    public void respawnAtStart(Board board) {
        if (!isAlive()) return;
        board.getCell(getX(), getY()).setHasPlayer(false);
        setX(getStartX());
        setY(getStartY());
        board.getCell(getX(), getY()).setHasPlayer(true);
        setIsAlive(true);
    }
}