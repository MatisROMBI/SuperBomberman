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
 * Rendu principal pour Bomberman avec support complet des thèmes
 * - Mode classique/Survivor : décor thématique
 * - Mode LEGEND 1v1 : damier bleu avec sprites ninja
 */
public class GameRenderer {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final ThemeManager themeManager;

    // Images par défaut pour fallback
    private final Image defaultPlayerSprite;
    private final Image defaultBombSprite;

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.themeManager = ThemeManager.getInstance();

        canvas.setWidth(Constants.WINDOW_WIDTH);
        canvas.setHeight(Constants.WINDOW_HEIGHT);

        // Chargement des sprites par défaut pour fallback
        this.defaultPlayerSprite = tryLoadDefault("/images/bomberman_p1.png");
        this.defaultBombSprite = tryLoadDefault("/images/bombe_pixel.png");

        System.out.println("GameRenderer initialisé avec ThemeManager");
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
        double hudHeight = Constants.HUD_HEIGHT;

        // Fond du HUD
        gc.setFill(colors.getHudBackgroundColor());
        gc.fillRect(0, 0, Constants.WINDOW_WIDTH, hudHeight);
        gc.setFill(colors.getHudBorderColor());
        gc.fillRect(0, hudHeight - 6, Constants.WINDOW_WIDTH, 6);

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

    /** Plateau avec couleurs et sprites thématiques */
    private void renderBoard(Board board) {
        ThemeColors colors = themeManager.getThemeColors();
        double yOffset = Constants.HUD_HEIGHT;

        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                double pixelX = x * Constants.CELL_SIZE;
                double pixelY = y * Constants.CELL_SIZE + yOffset;

                switch (cell.getType()) {
                    case WALL:
                        gc.setFill(colors.getWallColor());
                        gc.fillRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        break;
                    case DESTRUCTIBLE_WALL:
                        // Utiliser le sprite thématique s'il existe
                        Image wallSprite = themeManager.getDestructibleWallSprite();
                        if (wallSprite != null) {
                            gc.drawImage(wallSprite, pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        } else {
                            gc.setFill(Color.SADDLEBROWN);
                            gc.fillRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        }
                        break;
                    default:
                        // Alternance de couleurs pour les cases vides
                        Color cellColor = ((x + y) % 2 == 0) ? colors.getEmptyCell1() : colors.getEmptyCell2();
                        gc.setFill(cellColor);
                        gc.fillRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        break;
                }

                // Contour
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    /** Bombes avec sprites thématiques */
    private void renderBombs(List<Bomb> bombs) {
        double yOffset = Constants.HUD_HEIGHT;
        for (Bomb bomb : bombs) {
            if (!bomb.hasExploded()) {
                double px = bomb.getX() * Constants.CELL_SIZE + 4;
                double py = bomb.getY() * Constants.CELL_SIZE + 4 + yOffset;

                Image bombSprite = themeManager.getBombSprite();
                if (bombSprite != null) {
                    gc.drawImage(bombSprite, px, py, Constants.CELL_SIZE - 8, Constants.CELL_SIZE - 8);
                } else if (defaultBombSprite != null) {
                    gc.drawImage(defaultBombSprite, px, py, Constants.CELL_SIZE - 8, Constants.CELL_SIZE - 8);
                } else {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(px, py, Constants.CELL_SIZE - 8, Constants.CELL_SIZE - 8);
                }
            }
        }
    }

    /** Explosions avec couleurs thématiques */
    private void renderExplosions(List<Explosion> explosions) {
        ThemeColors colors = themeManager.getThemeColors();
        double yOffset = Constants.HUD_HEIGHT;

        for (Explosion explosion : explosions) {
            double px = explosion.getX() * Constants.CELL_SIZE;
            double py = explosion.getY() * Constants.CELL_SIZE + yOffset;
            gc.setGlobalAlpha(0.7);
            gc.setFill(colors.getExplosionColor());
            gc.fillRect(px + 2, py + 2, Constants.CELL_SIZE - 4, Constants.CELL_SIZE - 4);
            gc.setGlobalAlpha(1.0);
        }
    }

    /** Joueur principal avec sprite thématique */
    private void renderPlayer(Player player) {
        if (player.isAlive()) {
            double yOffset = Constants.HUD_HEIGHT;
            double px = player.getX() * Constants.CELL_SIZE;
            double py = player.getY() * Constants.CELL_SIZE + yOffset;

            Image playerSprite = themeManager.getPlayerSprite(0);
            if (playerSprite != null) {
                gc.drawImage(playerSprite, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            } else if (defaultPlayerSprite != null) {
                gc.drawImage(defaultPlayerSprite, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            } else {
                gc.setFill(Color.DODGERBLUE);
                gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    /** Bots avec sprites thématiques */
    private void renderBots(List<PlayerBot> bots) {
        double yOffset = Constants.HUD_HEIGHT;
        for (int i = 0; i < bots.size(); i++) {
            PlayerBot bot = bots.get(i);
            if (bot.isAlive()) {
                double px = bot.getX() * Constants.CELL_SIZE;
                double py = bot.getY() * Constants.CELL_SIZE + yOffset;

                // Utiliser différents sprites de joueur pour les bots
                int spriteIndex = Math.min(i + 1, 3);
                Image botSprite = themeManager.getPlayerSprite(spriteIndex);

                if (botSprite != null) {
                    gc.drawImage(botSprite, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                } else {
                    gc.setFill(Color.RED);
                    gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                }
            }
        }
    }

    /** Power-ups avec sprites thématiques */
    private void renderPowerUps(Board board) {
        double yOffset = Constants.HUD_HEIGHT;
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                if (cell.hasPowerUp()) {
                    PowerUpType type = cell.getPowerUp().getType();
                    double px = x * Constants.CELL_SIZE + 7;
                    double py = y * Constants.CELL_SIZE + 7 + yOffset;

                    Image powerUpSprite = themeManager.getPowerUpSprite(type.name());
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

    // ================================================================
    //                      MODE LEGEND 1V1
    // ================================================================

    /**
     * MÉTHODE PRINCIPALE pour le rendu du mode Legend 1v1
     */
    public void renderLegend1v1(Legend1v1Board board) {
        ThemeColors colors = themeManager.getThemeColors();

        // Fond global du mode Legend
        gc.setFill(colors.getLegendBackground());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        renderLegendHUD(board);
        renderLegendBoard(board);
        renderLegendPlayers(board);
        renderLegendEnemies(board);
        renderBombs(board.getBombs());
        renderExplosions(board.getExplosions());
        renderLegendPowerUps(board);
    }

    /** HUD Legend avec couleurs thématiques */
    private void renderLegendHUD(Legend1v1Board board) {
        ThemeColors colors = themeManager.getThemeColors();
        double hudHeight = Constants.HUD_HEIGHT;
        double hudPadding = 16;
        double avatarSize = 42;
        double boxW = 34, boxH = 34;
        double scOffset = 12;
        double scoreW = 140, scoreH = 38;

        // Fond HUD Legend
        gc.setFill(colors.getLegendHud());
        gc.fillRect(0, 0, Constants.WINDOW_WIDTH, hudHeight);

        // ----- JOUEUR 1 (gauche) -----
        Player p1 = board.getPlayer1();
        Image avatar1 = themeManager.getThemedImage("/images/head_ninja_white.png");
        double x1 = hudPadding;
        double y = (hudHeight - avatarSize) / 2;
        if (avatar1 != null) gc.drawImage(avatar1, x1, y, avatarSize, avatarSize);

        // Affichage vies J1
        int vies1 = Math.max(0, Math.min(p1.getLives(), 5));
        double lifeBoxX1 = x1 + avatarSize + 6;
        double lifeBoxY = (hudHeight - boxH) / 2;
        gc.setFill(Color.WHITE);
        gc.fillRect(lifeBoxX1, lifeBoxY, boxW, boxH);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeRect(lifeBoxX1, lifeBoxY, boxW, boxH);
        gc.setFont(Font.font("Arial Black", FontWeight.BOLD, 24));
        gc.setFill(Color.BLACK);
        gc.fillText(String.valueOf(vies1), lifeBoxX1 + 10, lifeBoxY + 26);

        // SC J1
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 24));
        gc.setFill(Color.WHITE);
        double scX1 = lifeBoxX1 + boxW + scOffset;
        gc.fillText("SC", scX1, hudHeight - 15);

        // Score J1
        double scoreRectX1 = scX1 + 44;
        gc.setFill(Color.BLACK);
        gc.fillRect(scoreRectX1, (hudHeight - scoreH) / 2, scoreW, scoreH);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 30));
        gc.setFill(Color.WHITE);
        String score1 = String.valueOf(p1.getScore());
        Text t1 = new Text(score1);
        t1.setFont(gc.getFont());
        double strW1 = t1.getLayoutBounds().getWidth();
        double scoreY = (hudHeight + scoreH) / 2 - 8;
        gc.fillText(score1, scoreRectX1 + (scoreW - strW1) / 2, scoreY);

        // ----- JOUEUR 2 (droite) -----
        Player p2 = board.getPlayer2();
        Image avatar2 = themeManager.getThemedImage("/images/head_ninja_black.png");
        double x2 = Constants.WINDOW_WIDTH - hudPadding - avatarSize;
        double lifeBoxX2 = x2 - boxW - 6;
        double scX2 = lifeBoxX2 - 44;
        double scoreRectX2 = scX2 - scoreW - scOffset;

        // Score J2
        gc.setFill(Color.BLACK);
        gc.fillRect(scoreRectX2, (hudHeight - scoreH) / 2, scoreW, scoreH);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 30));
        gc.setFill(Color.WHITE);
        String score2 = String.valueOf(p2.getScore());
        Text t2 = new Text(score2);
        t2.setFont(gc.getFont());
        double strW2 = t2.getLayoutBounds().getWidth();
        gc.fillText(score2, scoreRectX2 + (scoreW - strW2) / 2, scoreY);

        // SC J2
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 24));
        gc.setFill(Color.WHITE);
        gc.fillText("SC", scX2, hudHeight - 15);

        // Affichage vies J2
        int vies2 = Math.max(0, Math.min(p2.getLives(), 5));
        gc.setFill(Color.WHITE);
        gc.fillRect(lifeBoxX2, lifeBoxY, boxW, boxH);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeRect(lifeBoxX2, lifeBoxY, boxW, boxH);
        gc.setFont(Font.font("Arial Black", FontWeight.BOLD, 24));
        gc.setFill(Color.BLACK);
        gc.fillText(String.valueOf(vies2), lifeBoxX2 + 10, lifeBoxY + 26);

        // Avatar J2
        if (avatar2 != null) gc.drawImage(avatar2, x2, y, avatarSize, avatarSize);
    }

    /** Damier Legend avec couleurs thématiques */
    private void renderLegendBoard(Legend1v1Board board) {
        ThemeColors colors = themeManager.getThemeColors();
        double yOffset = Constants.HUD_HEIGHT;

        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                double px = x * Constants.CELL_SIZE;
                double py = y * Constants.CELL_SIZE + yOffset;

                if (cell.getType() == CellType.EMPTY) {
                    Color cellColor = ((x + y) % 2 == 0) ? colors.getLegendEmpty1() : colors.getLegendEmpty2();
                    gc.setFill(cellColor);
                    gc.fillRect(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                }
                else if (cell.getType() == CellType.WALL) {
                    gc.setFill(colors.getLegendWall());
                    gc.fillRect(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                }
                else if (cell.getType() == CellType.DESTRUCTIBLE_WALL) {
                    // Utiliser le sprite thématique pour les murs destructibles Legend
                    Image wallSprite = themeManager.getDestructibleWallSprite();
                    if (wallSprite != null) {
                        gc.drawImage(wallSprite, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                    } else {
                        gc.setFill(Color.web("#85baf8"));
                        gc.fillRect(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                    }
                }

                // Contour
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    /** Power-ups Legend avec sprites thématiques "neige" */
    private void renderLegendPowerUps(Legend1v1Board board) {
        double yOffset = Constants.HUD_HEIGHT;
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                if (cell.hasPowerUp()) {
                    PowerUpType type = cell.getPowerUp().getType();
                    double px = x * Constants.CELL_SIZE + 7;
                    double py = y * Constants.CELL_SIZE + 7 + yOffset;

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
        double yOffset = Constants.HUD_HEIGHT;

        if (p1.isAlive()) {
            double px = p1.getX() * Constants.CELL_SIZE;
            double py = p1.getY() * Constants.CELL_SIZE + yOffset;

            // Utiliser le sprite thématique pour le joueur 1 (Legend blanc)
            Image p1Sprite = themeManager.getThemedImage("/images/nija_white_bomberman.png");
            if (p1Sprite == null) p1Sprite = themeManager.getPlayerSprite(0);

            if (p1Sprite != null) {
                gc.drawImage(p1Sprite, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            } else {
                gc.setFill(Color.WHITE);
                gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }

        if (p2.isAlive()) {
            double px = p2.getX() * Constants.CELL_SIZE;
            double py = p2.getY() * Constants.CELL_SIZE + yOffset;

            // Utiliser le sprite thématique pour le joueur 2 (Legend noir)
            Image p2Sprite = themeManager.getThemedImage("/images/nija_black_bomberman.png");
            if (p2Sprite == null) p2Sprite = themeManager.getPlayerSprite(1);

            if (p2Sprite != null) {
                gc.drawImage(p2Sprite, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            } else {
                gc.setFill(Color.BLACK);
                gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    /** Ennemis Legend avec sprites thématiques */
    private void renderLegendEnemies(Legend1v1Board board) {
        double yOffset = Constants.HUD_HEIGHT;

        // Bomber ennemis
        for (LegendEnemyBomber b : board.getBomberEnemies()) {
            if (b.isAlive()) {
                double px = b.getX() * Constants.CELL_SIZE;
                double py = b.getY() * Constants.CELL_SIZE + yOffset;

                Image bomberSprite = themeManager.getEnemySprite("bomber");
                if (bomberSprite != null) {
                    gc.drawImage(bomberSprite, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                } else {
                    gc.setFill(Color.DARKRED);
                    gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                }
            }
        }

        // Yellow ennemis
        for (LegendEnemyYellow y : board.getYellowEnemies()) {
            if (y.isAlive()) {
                double px = y.getX() * Constants.CELL_SIZE;
                double py = y.getY() * Constants.CELL_SIZE + yOffset;

                Image yellowSprite = themeManager.getEnemySprite("yellow");
                if (yellowSprite != null) {
                    gc.drawImage(yellowSprite, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                } else {
                    gc.setFill(Color.GOLD);
                    gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                }
            }
        }
    }
}