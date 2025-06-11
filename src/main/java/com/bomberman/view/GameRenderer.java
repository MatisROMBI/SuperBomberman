package com.bomberman.view;

import com.bomberman.model.*;
import com.bomberman.model.enums.CellType;
import com.bomberman.model.enums.PowerUpType;
import com.bomberman.utils.Constants;
import com.bomberman.utils.ThemeColors;
import com.bomberman.utils.ThemeManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Rendu principal pour Bomberman avec support complet des thèmes - ENTIÈREMENT CORRIGÉ
 * - Mode classique/Survivor : décor thématique
 * - Mode LEGEND 1v1 : damier bleu avec sprites ninja
 */
public class GameRenderer {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final ThemeManager themeManager;

    // OPTIMISATION: Cache pour éviter les recalculs
    private final double cachedHudHeight = Constants.HUD_HEIGHT;
    private final double cachedCellSize = Constants.CELL_SIZE;

    // OPTIMISATION: Pré-calculer les positions fréquemment utilisées
    private final double[][] cellPositionsX = new double[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
    private final double[][] cellPositionsY = new double[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];

    // Images par défaut pour fallback
    private final Image defaultPlayerSprite;
    private final Image defaultBombSprite;

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.themeManager = ThemeManager.getInstance();

        canvas.setWidth(Constants.WINDOW_WIDTH);
        canvas.setHeight(Constants.WINDOW_HEIGHT);

        // OPTIMISATION: Pré-calculer toutes les positions des cellules
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                cellPositionsX[x][y] = x * cachedCellSize;
                cellPositionsY[x][y] = y * cachedCellSize + cachedHudHeight;
            }
        }

        // Chargement des sprites par défaut pour fallback
        this.defaultPlayerSprite = tryLoadDefault("/images/bomberman_p1.png");
        this.defaultBombSprite = tryLoadDefault("/images/bombe_pixel.png");

        System.out.println("GameRenderer initialisé avec ThemeManager et optimisations");
    }

    /** Charge une image par défaut de manière sécurisée */
    private Image tryLoadDefault(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.err.println("Impossible de charger l'image par défaut : " + path);
            return null;
        }
    }

    // ================================================================
    //                    MODE CLASSIQUE / SURVIVOR
    // ================================================================
    public void render(Game game) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        renderHUD(game.getPlayer());
        renderBoard(game.getBoard());
        renderExplosions(game.getBoard().getExplosions());
        renderBombs(game.getBoard().getBombs());
        renderPowerUps(game.getBoard());
        renderBots(game.getBoard().getBots());
        renderPlayer(game.getPlayer());
    }

    /** HUD avec couleurs thématiques */
    private void renderHUD(Player player) {
        ThemeColors colors = themeManager.getThemeColors();

        // Fond du HUD
        gc.setFill(colors.getHudBackgroundColor());
        gc.fillRect(0, 0, Constants.WINDOW_WIDTH, cachedHudHeight);
        gc.setFill(colors.getHudBorderColor());
        gc.fillRect(0, cachedHudHeight - 6, Constants.WINDOW_WIDTH, 6);

        double iconX = 26, iconY = 8, iconSize = 38;

        // Icône du joueur depuis le thème
        Image playerIcon = themeManager.getPlayerSprite(0);
        if (playerIcon != null) {
            gc.drawImage(playerIcon, iconX, iconY, iconSize, iconSize);
        } else if (defaultPlayerSprite != null) {
            gc.drawImage(defaultPlayerSprite, iconX, iconY, iconSize, iconSize);
        }

        // Affichage des vies
        int livesDisplay = Math.max(0, player.getLives() - 1);
        double lifeBoxX = iconX + iconSize + 12, lifeBoxY = iconY + 10, lifeBoxW = 30, lifeBoxH = 28;
        gc.setFill(Color.WHITE);
        gc.fillRect(lifeBoxX, lifeBoxY, lifeBoxW, lifeBoxH);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeRect(lifeBoxX, lifeBoxY, lifeBoxW, lifeBoxH);
        gc.setFont(Font.font("Arial Black", FontWeight.BOLD, 24));
        gc.setFill(Color.BLACK);
        gc.fillText(String.valueOf(livesDisplay), lifeBoxX + 6, lifeBoxY + 22);

        // Texte "SC"
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        gc.setFill(colors.getHudTextColor());
        double scX = lifeBoxX + lifeBoxW + 20;
        gc.fillText("SC", scX, 38);

        // Score
        gc.setFill(colors.getScoreBackgroundColor());
        double scoreRectX = scX + 60, scoreRectW = 140, scoreRectH = 40;
        gc.fillRect(scoreRectX, 8, scoreRectW, scoreRectH);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 34));
        gc.setFill(colors.getHudTextColor());
        String scoreStr = String.valueOf(player.getScore());
        Text scoreText = new Text(scoreStr);
        scoreText.setFont(gc.getFont());
        double scoreStrWidth = scoreText.getLayoutBounds().getWidth();
        double scoreTextX = scoreRectX + (scoreRectW - scoreStrWidth) / 2;
        gc.fillText(scoreStr, scoreTextX, 40);

        // Icône joueur supplémentaire
        double blackIconX = scoreRectX + scoreRectW + 30;
        Image secondIcon = themeManager.getPlayerSprite(1);
        if (secondIcon != null) {
            gc.drawImage(secondIcon, blackIconX, 12, 32, 32);
        }

        // Texte "PRESS START"
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        double pressStartX = blackIconX + 40;
        gc.fillText("PRESS START", pressStartX, 38);
    }

    /** OPTIMISATION: Plateau avec positions pré-calculées */
    private void renderBoard(Board board) {
        ThemeColors colors = themeManager.getThemeColors();

        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                double pixelX = cellPositionsX[x][y];
                double pixelY = cellPositionsY[x][y];

                switch (cell.getType()) {
                    case WALL:
                        gc.setFill(colors.getWallColor());
                        gc.fillRect(pixelX, pixelY, cachedCellSize, cachedCellSize);
                        break;
                    case DESTRUCTIBLE_WALL:
                        // Utiliser le sprite thématique s'il existe
                        Image wallSprite = themeManager.getDestructibleWallSprite();
                        if (wallSprite != null) {
                            gc.drawImage(wallSprite, pixelX, pixelY, cachedCellSize, cachedCellSize);
                        } else {
                            gc.setFill(Color.SADDLEBROWN);
                            gc.fillRect(pixelX, pixelY, cachedCellSize, cachedCellSize);
                        }
                        break;
                    default:
                        // Alternance de couleurs pour les cases vides
                        Color cellColor = ((x + y) % 2 == 0) ? colors.getEmptyCell1() : colors.getEmptyCell2();
                        gc.setFill(cellColor);
                        gc.fillRect(pixelX, pixelY, cachedCellSize, cachedCellSize);
                        break;
                }

                // Contour
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(pixelX, pixelY, cachedCellSize, cachedCellSize);
            }
        }
    }

    /** OPTIMISATION: Bombes avec positions pré-calculées */
    private void renderBombs(List<Bomb> bombs) {
        for (Bomb bomb : bombs) {
            if (!bomb.hasExploded()) {
                double px = cellPositionsX[bomb.getX()][0] + 4;
                double py = cellPositionsY[0][bomb.getY()] + 4;

                Image bombSprite = themeManager.getBombSprite();
                if (bombSprite != null) {
                    gc.drawImage(bombSprite, px, py, cachedCellSize - 8, cachedCellSize - 8);
                } else if (defaultBombSprite != null) {
                    gc.drawImage(defaultBombSprite, px, py, cachedCellSize - 8, cachedCellSize - 8);
                } else {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(px, py, cachedCellSize - 8, cachedCellSize - 8);
                }
            }
        }
    }

    /** Explosions avec couleurs thématiques */
    private void renderExplosions(List<Explosion> explosions) {
        ThemeColors colors = themeManager.getThemeColors();

        for (Explosion explosion : explosions) {
            double px = cellPositionsX[explosion.getX()][0];
            double py = cellPositionsY[0][explosion.getY()];
            gc.setGlobalAlpha(0.7);
            gc.setFill(colors.getExplosionColor());
            gc.fillRect(px + 2, py + 2, cachedCellSize - 4, cachedCellSize - 4);
            gc.setGlobalAlpha(1.0);
        }
    }

    /** Joueur principal avec sprite thématique */
    private void renderPlayer(Player player) {
        if (player.isAlive()) {
            double px = cellPositionsX[player.getX()][0];
            double py = cellPositionsY[0][player.getY()];

            Image playerSprite = themeManager.getPlayerSprite(0);
            if (playerSprite != null) {
                gc.drawImage(playerSprite, px, py, cachedCellSize, cachedCellSize);
            } else if (defaultPlayerSprite != null) {
                gc.drawImage(defaultPlayerSprite, px, py, cachedCellSize, cachedCellSize);
            } else {
                gc.setFill(Color.DODGERBLUE);
                gc.fillOval(px, py, cachedCellSize, cachedCellSize);
            }
        }
    }

    /** Bots avec sprites thématiques */
    private void renderBots(List<PlayerBot> bots) {
        for (int i = 0; i < bots.size(); i++) {
            PlayerBot bot = bots.get(i);
            if (bot.isAlive()) {
                double px = cellPositionsX[bot.getX()][0];
                double py = cellPositionsY[0][bot.getY()];

                // Utiliser différents sprites selon l'index du bot
                Image botSprite = themeManager.getPlayerSprite(Math.min(i + 1, 3));
                if (botSprite != null) {
                    gc.drawImage(botSprite, px, py, cachedCellSize, cachedCellSize);
                } else {
                    // Couleurs par défaut pour les bots
                    Color[] botColors = {Color.RED, Color.PURPLE, Color.ORANGE};
                    gc.setFill(botColors[i % botColors.length]);
                    gc.fillOval(px, py, cachedCellSize, cachedCellSize);
                }
            }
        }
    }

    /** Power-ups avec sprites thématiques */
    private void renderPowerUps(Board board) {
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                if (cell.hasPowerUp()) {
                    double px = cellPositionsX[x][y] + 4;
                    double py = cellPositionsY[x][y] + 4;
                    PowerUpType type = cell.getPowerUp().getType();

                    Image powerUpSprite = themeManager.getPowerUpSprite(type.name());
                    if (powerUpSprite != null) {
                        gc.drawImage(powerUpSprite, px, py, cachedCellSize - 8, cachedCellSize - 8);
                    } else {
                        // Fallback avec couleurs
                        switch (type) {
                            case EXTRA_BOMB:
                                gc.setFill(Color.YELLOW);
                                break;
                            case RANGE_UP:
                                gc.setFill(Color.RED);
                                break;
                            case LIFE:
                                gc.setFill(Color.GREEN);
                                break;
                            case SPEED:
                                gc.setFill(Color.CYAN);
                                break;
                            default:
                                gc.setFill(Color.MAGENTA);
                                break;
                        }
                        gc.fillOval(px, py, cachedCellSize - 8, cachedCellSize - 8);
                    }
                }
            }
        }
    }

    // ================================================================
    //                    MODE LEGEND 1v1 - SIMPLIFIÉ
    // ================================================================
    public void renderLegend1v1(Legend1v1Board board) {
        ThemeColors colors = themeManager.getThemeColors();

        // Fond global du mode Legend
        gc.setFill(colors.getLegendBackground());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        renderLegendHUD(board);
        renderLegendBoard(board);
        renderLegendPlayers(board);
        renderLegendEnemies(board);
        renderLegendBombs(board);
        renderLegendExplosions(board);
        renderLegendPowerUps(board);
    }

    /** HUD Legend simplifié */
    private void renderLegendHUD(Legend1v1Board board) {
        // Fond HUD Legend
        gc.setFill(Color.rgb(20, 20, 40));
        gc.fillRect(0, 0, Constants.WINDOW_WIDTH, cachedHudHeight);

        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        // Joueur 1 (gauche)
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("J1: ♥" + p1.getLives() + " Score:" + p1.getScore(), 20, 25);

        // Joueur 2 (droite)
        gc.fillText("J2: ♥" + p2.getLives() + " Score:" + p2.getScore(), Constants.WINDOW_WIDTH - 200, 25);

        // Titre central
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("LEGEND 1v1", Constants.WINDOW_WIDTH / 2 - 50, 30);
    }

    /** Plateau damier bleu pour le mode Legend */
    private void renderLegendBoard(Legend1v1Board board) {
        ThemeColors colors = themeManager.getThemeColors();
        double yOffset = cachedHudHeight;

        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                double px = x * cachedCellSize;
                double py = y * cachedCellSize + yOffset;

                switch (cell.getType()) {
                    case WALL:
                        gc.setFill(colors.getLegendWall());
                        gc.fillRect(px, py, cachedCellSize, cachedCellSize);
                        break;
                    case DESTRUCTIBLE_WALL:
                        Image wallSprite = themeManager.getDestructibleWallSprite();
                        if (wallSprite != null) {
                            gc.drawImage(wallSprite, px, py, cachedCellSize, cachedCellSize);
                        } else {
                            gc.setFill(Color.web("#85baf8"));
                            gc.fillRect(px, py, cachedCellSize, cachedCellSize);
                        }
                        break;
                    default:
                        // Damier bleu/bleu clair
                        Color cellColor = ((x + y) % 2 == 0) ? colors.getLegendEmpty1() : colors.getLegendEmpty2();
                        gc.setFill(cellColor);
                        gc.fillRect(px, py, cachedCellSize, cachedCellSize);
                        break;
                }

                // Contour
                gc.setStroke(Color.DARKBLUE);
                gc.setLineWidth(1);
                gc.strokeRect(px, py, cachedCellSize, cachedCellSize);
            }
        }
    }

    /** Explosions en mode Legend */
    private void renderLegendExplosions(Legend1v1Board board) {
        double yOffset = cachedHudHeight;
        for (Explosion explosion : board.getExplosions()) {
            double px = explosion.getX() * cachedCellSize;
            double py = explosion.getY() * cachedCellSize + yOffset;
            gc.setGlobalAlpha(0.8);
            gc.setFill(Color.ORANGE);
            gc.fillRect(px + 2, py + 2, cachedCellSize - 4, cachedCellSize - 4);
            gc.setGlobalAlpha(1.0);
        }
    }

    /** Bombes en mode Legend */
    private void renderLegendBombs(Legend1v1Board board) {
        double yOffset = cachedHudHeight;
        for (Bomb bomb : board.getBombs()) {
            if (!bomb.hasExploded()) {
                double px = bomb.getX() * cachedCellSize + 4;
                double py = bomb.getY() * cachedCellSize + 4 + yOffset;

                Image bombSprite = themeManager.getBombSprite();
                if (bombSprite != null) {
                    gc.drawImage(bombSprite, px, py, cachedCellSize - 8, cachedCellSize - 8);
                } else {
                    gc.setFill(Color.DARKRED);
                    gc.fillOval(px, py, cachedCellSize - 8, cachedCellSize - 8);
                }
            }
        }
    }

    /** Power-ups Legend */
    private void renderLegendPowerUps(Legend1v1Board board) {
        double yOffset = cachedHudHeight;
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                if (cell.hasPowerUp()) {
                    PowerUpType type = cell.getPowerUp().getType();
                    double px = x * cachedCellSize + 7;
                    double py = y * cachedCellSize + 7 + yOffset;

                    // Essayer d'abord les versions "neige" pour le mode Legend
                    String snowVersion = type.name() + "_SNOW";
                    Image powerUpSprite = themeManager.getPowerUpSprite(snowVersion);

                    // Si pas de version neige, utiliser la version normale
                    if (powerUpSprite == null) {
                        powerUpSprite = themeManager.getPowerUpSprite(type.name());
                    }

                    if (powerUpSprite != null) {
                        gc.drawImage(powerUpSprite, px, py, 28, 28);
                    } else {
                        // Fallback avec couleurs
                        switch (type) {
                            case EXTRA_BOMB: gc.setFill(Color.DARKRED); break;
                            case RANGE_UP: gc.setFill(Color.ORANGE); break;
                            case LIFE: gc.setFill(Color.GREEN); break;
                            case SPEED: gc.setFill(Color.BLUE); break;
                        }
                        gc.fillOval(px, py, 28, 28);
                    }
                }
            }
        }
    }

    /** Joueurs Legend avec sprites thématiques */
    private void renderLegendPlayers(Legend1v1Board board) {
        Player p1 = board.getPlayer1(), p2 = board.getPlayer2();
        double yOffset = cachedHudHeight;

        if (p1.isAlive()) {
            double px = p1.getX() * cachedCellSize;
            double py = p1.getY() * cachedCellSize + yOffset;

            // Utiliser le sprite thématique pour le joueur 1 (Legend blanc)
            Image p1Sprite = themeManager.getThemedImage("/images/nija_white_bomberman.png");
            if (p1Sprite == null) p1Sprite = themeManager.getPlayerSprite(0);

            if (p1Sprite != null) {
                gc.drawImage(p1Sprite, px, py, cachedCellSize, cachedCellSize);
            } else {
                gc.setFill(Color.WHITE);
                gc.fillOval(px, py, cachedCellSize, cachedCellSize);
            }
        }

        if (p2.isAlive()) {
            double px = p2.getX() * cachedCellSize;
            double py = p2.getY() * cachedCellSize + yOffset;

            // Utiliser le sprite thématique pour le joueur 2 (Legend noir)
            Image p2Sprite = themeManager.getThemedImage("/images/nija_black_bomberman.png");
            if (p2Sprite == null) p2Sprite = themeManager.getPlayerSprite(1);

            if (p2Sprite != null) {
                gc.drawImage(p2Sprite, px, py, cachedCellSize, cachedCellSize);
            } else {
                gc.setFill(Color.BLACK);
                gc.fillOval(px, py, cachedCellSize, cachedCellSize);
            }
        }
    }

    /** Ennemis Legend avec sprites thématiques */
    private void renderLegendEnemies(Legend1v1Board board) {
        double yOffset = cachedHudHeight;

        // Bomber ennemis
        for (LegendEnemyBomber b : board.getBomberEnemies()) {
            if (b.isAlive()) {
                double px = b.getX() * cachedCellSize;
                double py = b.getY() * cachedCellSize + yOffset;

                Image bomberSprite = themeManager.getEnemySprite("bomber");
                if (bomberSprite != null) {
                    gc.drawImage(bomberSprite, px, py, cachedCellSize, cachedCellSize);
                } else {
                    gc.setFill(Color.DARKRED);
                    gc.fillOval(px, py, cachedCellSize, cachedCellSize);
                }
            }
        }

        // Yellow ennemis
        for (LegendEnemyYellow y : board.getYellowEnemies()) {
            if (y.isAlive()) {
                double px = y.getX() * cachedCellSize;
                double py = y.getY() * cachedCellSize + yOffset;

                Image yellowSprite = themeManager.getEnemySprite("yellow");
                if (yellowSprite != null) {
                    gc.drawImage(yellowSprite, px, py, cachedCellSize, cachedCellSize);
                } else {
                    gc.setFill(Color.GOLD);
                    gc.fillOval(px, py, cachedCellSize, cachedCellSize);
                }
            }
        }
    }
}