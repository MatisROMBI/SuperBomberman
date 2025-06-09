package com.bomberman.view;

import com.bomberman.model.*;
import com.bomberman.model.enums.CellType;
import com.bomberman.model.enums.PowerUpType;
import com.bomberman.utils.Constants;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

public class GameRenderer {
    private final Canvas canvas;
    private final GraphicsContext gc;

    // Sprites pour tous modes
    private final Image[] playerSprites = new Image[4];
    private final Image legendWhiteIcon, legendBlackIcon, bomberEnemyIcon, yellowEnemyIcon;
    private final Image bombermanFaceIcon, bombermanBlackIcon, bombPixelIcon;
    private final Image bonusBombIcon, bonusRangeIcon, bonusLifeIcon, bonusSpeedIcon;

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        canvas.setWidth(Constants.WINDOW_WIDTH);
        canvas.setHeight(Constants.WINDOW_HEIGHT);

        // Chargement des sprites – robustes (ne crash pas si image manquante)
        bombermanFaceIcon = tryLoad("/images/bomberman_face.png");
        bombermanBlackIcon = tryLoad("/images/bomberman_black.png");
        bombPixelIcon = tryLoad("/images/bombe_pixel.png");
        legendWhiteIcon = tryLoad("/images/nija_white_bomberman.png");
        legendBlackIcon = tryLoad("/images/nija_black_bomberman.png");
        bomberEnemyIcon = tryLoad("/images/bomber_perso.png");
        yellowEnemyIcon = tryLoad("/images/yellow_perso.png");
        bonusBombIcon = tryLoad("/images/EXTRAT_BOMB.png");
        bonusRangeIcon = tryLoad("/images/RANGE_UP.png");
        bonusLifeIcon = tryLoad("/images/LIFE.png");
        bonusSpeedIcon = tryLoad("/images/SPEED.png");

        for (int i = 0; i < 4; i++) {
            playerSprites[i] = tryLoad("/images/bomberman_p" + (i + 1) + ".png");
        }
    }

    private Image tryLoad(String path) {
        try { return new Image(getClass().getResourceAsStream(path)); }
        catch (Exception e) { return null; }
    }

    // =================== MODE CLASSIQUE/SURVIVOR ===================
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

    private void renderHUD(Player player) {
        double hudHeight = Constants.HUD_HEIGHT;
        gc.setFill(Color.web("#FF8800"));
        gc.fillRect(0, 0, Constants.WINDOW_WIDTH, hudHeight);
        gc.setFill(Color.web("#CC6A00"));
        gc.fillRect(0, hudHeight - 6, Constants.WINDOW_WIDTH, 6);

        double iconX = 26, iconY = 8, iconSize = 38;
        if (bombermanFaceIcon != null)
            gc.drawImage(bombermanFaceIcon, iconX, iconY, iconSize, iconSize);

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

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        gc.setFill(Color.WHITE);
        double scX = lifeBoxX + lifeBoxW + 20;
        gc.fillText("SC", scX, 38);

        gc.setFill(Color.BLACK);
        double scoreRectX = scX + 60, scoreRectW = 140, scoreRectH = 40;
        gc.fillRect(scoreRectX, 8, scoreRectW, scoreRectH);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 34));
        gc.setFill(Color.WHITE);
        String scoreStr = String.valueOf(player.getScore());
        Text scoreText = new Text(scoreStr);
        scoreText.setFont(gc.getFont());
        double scoreStrWidth = scoreText.getLayoutBounds().getWidth();
        double scoreTextX = scoreRectX + (scoreRectW - scoreStrWidth) / 2;
        gc.fillText(scoreStr, scoreTextX, 40);

        double blackIconX = scoreRectX + scoreRectW + 30;
        if (bombermanBlackIcon != null)
            gc.drawImage(bombermanBlackIcon, blackIconX, 12, 32, 32);

        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        double pressStartX = blackIconX + 40;
        gc.fillText("PRESS START", pressStartX, 38);
    }

    private void renderBoard(Board board) {
        double yOffset = Constants.HUD_HEIGHT;
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                double pixelX = x * Constants.CELL_SIZE;
                double pixelY = y * Constants.CELL_SIZE + yOffset;
                switch (cell.getType()) {
                    case WALL: gc.setFill(Color.DARKGRAY); break;
                    case DESTRUCTIBLE_WALL: gc.setFill(Color.SADDLEBROWN); break;
                    default: gc.setFill(Color.LIGHTGREEN); break;
                }
                gc.fillRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    private void renderBombs(List<Bomb> bombs) {
        double yOffset = Constants.HUD_HEIGHT;
        for (Bomb bomb : bombs) {
            if (!bomb.hasExploded()) {
                double px = bomb.getX() * Constants.CELL_SIZE + 4;
                double py = bomb.getY() * Constants.CELL_SIZE + 4 + yOffset;
                if (bombPixelIcon != null)
                    gc.drawImage(bombPixelIcon, px, py, Constants.CELL_SIZE - 8, Constants.CELL_SIZE - 8);
                else {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(px, py, Constants.CELL_SIZE - 8, Constants.CELL_SIZE - 8);
                }
            }
        }
    }

    private void renderExplosions(List<Explosion> explosions) {
        double yOffset = Constants.HUD_HEIGHT;
        for (Explosion explosion : explosions) {
            double px = explosion.getX() * Constants.CELL_SIZE;
            double py = explosion.getY() * Constants.CELL_SIZE + yOffset;
            gc.setGlobalAlpha(0.7);
            gc.setFill(Color.ORANGE);
            gc.fillRect(px + 2, py + 2, Constants.CELL_SIZE - 4, Constants.CELL_SIZE - 4);
            gc.setGlobalAlpha(1.0);
        }
    }

    private void renderPlayer(Player player) {
        if (player.isAlive()) {
            double yOffset = Constants.HUD_HEIGHT;
            double px = player.getX() * Constants.CELL_SIZE;
            double py = player.getY() * Constants.CELL_SIZE + yOffset;
            if (playerSprites[0] != null)
                gc.drawImage(playerSprites[0], px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            else {
                gc.setFill(Color.DODGERBLUE);
                gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    private void renderBots(List<PlayerBot> bots) {
        double yOffset = Constants.HUD_HEIGHT;
        for (int i = 0; i < bots.size(); i++) {
            PlayerBot bot = bots.get(i);
            if (bot.isAlive()) {
                double px = bot.getX() * Constants.CELL_SIZE;
                double py = bot.getY() * Constants.CELL_SIZE + yOffset;
                int spriteIdx = Math.min(i + 1, playerSprites.length - 1);
                if (playerSprites[spriteIdx] != null)
                    gc.drawImage(playerSprites[spriteIdx], px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                else {
                    gc.setFill(Color.RED);
                    gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                }
            }
        }
    }

    private void renderPowerUps(Board board) {
        double yOffset = Constants.HUD_HEIGHT;
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                if (cell.hasPowerUp()) {
                    PowerUpType type = cell.getPowerUp().getType();
                    double px = x * Constants.CELL_SIZE + 7;
                    double py = y * Constants.CELL_SIZE + 7 + yOffset;
                    switch (type) {
                        case EXTRA_BOMB: if (bonusBombIcon != null) gc.drawImage(bonusBombIcon, px, py, 28, 28); break;
                        case RANGE_UP:   if (bonusRangeIcon != null) gc.drawImage(bonusRangeIcon, px, py, 28, 28); break;
                        case LIFE:       if (bonusLifeIcon != null) gc.drawImage(bonusLifeIcon, px, py, 28, 28); break;
                        case SPEED:      if (bonusSpeedIcon != null) gc.drawImage(bonusSpeedIcon, px, py, 28, 28); break;
                    }
                }
            }
        }
    }

    // ============ MODE LEGEND 1v1 AVEC DAMIER BLEU ================
    public void renderLegend1v1(Legend1v1Board board) {
        // Fond bleu global (pour éviter contours bizarres)
        gc.setFill(Color.web("#1882f7"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        renderLegendHUD(board);
        renderLegendBoard(board);
        renderLegendPlayers(board);
        renderLegendEnemies(board);
        renderBombs(board.getBombs());
        renderExplosions(board.getExplosions());
        // Pour afficher les bonus dans ce mode, dé-commente ici :
        // renderPowerUps(board);
    }

    private void renderLegendHUD(Legend1v1Board board) {
        double hudHeight = Constants.HUD_HEIGHT;
        gc.setFill(Color.web("#2257ad"));
        gc.fillRect(0, 0, Constants.WINDOW_WIDTH, hudHeight);

        Player p1 = board.getPlayer1();
        Player p2 = board.getPlayer2();

        gc.setFont(Font.font("Arial Black", FontWeight.BOLD, 24));
        gc.setFill(Color.WHITE);
        gc.fillText("J1 - Score: " + p1.getScore() + " | Vies: " + p1.getLives(), 32, hudHeight - 16);

        gc.setFill(Color.LIGHTYELLOW);
        gc.fillText("J2 - Score: " + p2.getScore() + " | Vies: " + p2.getLives(), Constants.WINDOW_WIDTH - 285, hudHeight - 16);
    }

    // ==== Damier bleu, murs fixes et destructibles ====
    private void renderLegendBoard(Legend1v1Board board) {
        double yOffset = Constants.HUD_HEIGHT;
        Color blue1 = Color.web("#7ed5fa"); // Bleu clair
        Color blue2 = Color.web("#0e51b8"); // Bleu foncé

        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                double px = x * Constants.CELL_SIZE;
                double py = y * Constants.CELL_SIZE + yOffset;

                // Dessin du damier de fond uniquement sur cases vides
                if (cell.getType() == CellType.EMPTY) {
                    gc.setFill(((x + y) % 2 == 0) ? blue1 : blue2);
                } else if (cell.getType() == CellType.WALL) {
                    gc.setFill(Color.web("#3657a6")); // Mur FIXE bleu/gris
                } else if (cell.getType() == CellType.DESTRUCTIBLE_WALL) {
                    gc.setFill(Color.web("#85baf8")); // Mur cassable bleu très clair
                } else {
                    gc.setFill(blue1); // fallback
                }
                gc.fillRect(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);

                // Contour noir
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    private void renderLegendPlayers(Legend1v1Board board) {
        Player p1 = board.getPlayer1(), p2 = board.getPlayer2();
        double yOffset = Constants.HUD_HEIGHT;

        if (p1.isAlive()) {
            double px = p1.getX() * Constants.CELL_SIZE;
            double py = p1.getY() * Constants.CELL_SIZE + yOffset;
            if (legendWhiteIcon != null)
                gc.drawImage(legendWhiteIcon, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            else if (playerSprites[0] != null)
                gc.drawImage(playerSprites[0], px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            else {
                gc.setFill(Color.WHITE);
                gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
        if (p2.isAlive()) {
            double px = p2.getX() * Constants.CELL_SIZE;
            double py = p2.getY() * Constants.CELL_SIZE + yOffset;
            if (legendBlackIcon != null)
                gc.drawImage(legendBlackIcon, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            else if (playerSprites[1] != null)
                gc.drawImage(playerSprites[1], px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            else {
                gc.setFill(Color.BLACK);
                gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    private void renderLegendEnemies(Legend1v1Board board) {
        double yOffset = Constants.HUD_HEIGHT;
        for (LegendEnemyBomber b : board.getBomberEnemies()) {
            double px = b.getX() * Constants.CELL_SIZE;
            double py = b.getY() * Constants.CELL_SIZE + yOffset;
            if (bomberEnemyIcon != null)
                gc.drawImage(bomberEnemyIcon, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            else {
                gc.setFill(Color.DARKRED);
                gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
        for (LegendEnemyYellow y : board.getYellowEnemies()) {
            double px = y.getX() * Constants.CELL_SIZE;
            double py = y.getY() * Constants.CELL_SIZE + yOffset;
            if (yellowEnemyIcon != null)
                gc.drawImage(yellowEnemyIcon, px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            else {
                gc.setFill(Color.GOLD);
                gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }
}
