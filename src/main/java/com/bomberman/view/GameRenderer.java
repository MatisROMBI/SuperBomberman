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
    private Canvas canvas;
    private GraphicsContext gc;

    private Image bombermanFaceIcon;
    private Image bombermanBlackIcon;
    private Image bombPixelIcon;
    private Image[] playerSprites = new Image[4]; // p1 = humain, p2/p3/p4 = bots

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        canvas.setWidth(Constants.WINDOW_WIDTH);
        canvas.setHeight(Constants.WINDOW_HEIGHT);

        try {
            bombermanFaceIcon = new Image(getClass().getResourceAsStream("/images/bomberman_face.png"));
        } catch (Exception e) { bombermanFaceIcon = null; }
        try {
            bombermanBlackIcon = new Image(getClass().getResourceAsStream("/images/bomberman_black.png"));
        } catch (Exception e) { bombermanBlackIcon = null; }
        try {
            bombPixelIcon = new Image(getClass().getResourceAsStream("/images/bombe_pixel.png"));
        } catch (Exception e) { bombPixelIcon = null; }

        // --- CHARGE LES SPRITES JOUEUR & BOTS ---
        for (int i = 0; i < 4; i++) {
            try {
                playerSprites[i] = new Image(getClass().getResourceAsStream("/images/bomberman_p" + (i + 1) + ".png"));
            } catch (Exception e) {
                playerSprites[i] = null;
            }
        }
    }

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

        // Bomberman blanc (HUD)
        double iconX = 26, iconY = 8, iconSize = 38;
        if (bombermanFaceIcon != null) {
            gc.drawImage(bombermanFaceIcon, iconX, iconY, iconSize, iconSize);
        }

        // Vies = player.getLives() - 1 (convention Bomberman)
        int livesDisplay = Math.max(0, player.getLives() - 1);
        double lifeBoxX = iconX + iconSize + 12;
        double lifeBoxY = iconY + 10;
        double lifeBoxW = 30;
        double lifeBoxH = 28;
        gc.setFill(Color.WHITE);
        gc.fillRect(lifeBoxX, lifeBoxY, lifeBoxW, lifeBoxH);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeRect(lifeBoxX, lifeBoxY, lifeBoxW, lifeBoxH);
        gc.setFont(Font.font("Arial Black", FontWeight.BOLD, 24));
        gc.setFill(Color.BLACK);
        gc.fillText(String.valueOf(livesDisplay), lifeBoxX + 6, lifeBoxY + 22);

        // "SC"
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        gc.setFill(Color.WHITE);
        double scX = lifeBoxX + lifeBoxW + 20;
        gc.fillText("SC", scX, 38);

        // Score sur fond noir, barre plus courte
        gc.setFill(Color.BLACK);
        double scoreRectX = scX + 60;
        double scoreRectW = 140;
        double scoreRectH = 40;
        gc.fillRect(scoreRectX, 8, scoreRectW, scoreRectH);

        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 34));
        gc.setFill(Color.WHITE);
        String scoreStr = String.valueOf(player.getScore());
        Text scoreText = new Text(scoreStr);
        scoreText.setFont(gc.getFont());
        double scoreStrWidth = scoreText.getLayoutBounds().getWidth();
        double scoreTextX = scoreRectX + (scoreRectW - scoreStrWidth) / 2;
        gc.fillText(scoreStr, scoreTextX, 40);

        // Bomberman noir (à droite du score)
        double blackIconX = scoreRectX + scoreRectW + 30;
        if (bombermanBlackIcon != null) {
            gc.drawImage(bombermanBlackIcon, blackIconX, 12, 32, 32);
        }

        // "PRESS START" plus petit, bien placé pour être visible en entier
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 26)); // Plus petit
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
                    case WALL:
                        gc.setFill(Color.DARKGRAY);
                        gc.fillRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        break;
                    case DESTRUCTIBLE_WALL:
                        gc.setFill(Color.SADDLEBROWN);
                        gc.fillRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        break;
                    default:
                        gc.setFill(Color.LIGHTGREEN);
                        gc.fillRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        break;
                }
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
                if (bombPixelIcon != null) {
                    gc.drawImage(bombPixelIcon, px, py, Constants.CELL_SIZE - 8, Constants.CELL_SIZE - 8);
                } else {
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

    // ----------- HUMAIN en sprite -----------
    private void renderPlayer(Player player) {
        if (player.isAlive()) {
            double yOffset = Constants.HUD_HEIGHT;
            double px = player.getX() * Constants.CELL_SIZE;
            double py = player.getY() * Constants.CELL_SIZE + yOffset;
            if (playerSprites[0] != null) {
                gc.drawImage(playerSprites[0], px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            } else {
                gc.setFill(Color.DODGERBLUE);
                gc.fillOval(px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    // ----------- BOTS en sprites -----------
    private void renderBots(List<PlayerBot> bots) {
        double yOffset = Constants.HUD_HEIGHT;
        for (int i = 0; i < bots.size(); i++) {
            PlayerBot bot = bots.get(i);
            if (bot.isAlive()) {
                double px = bot.getX() * Constants.CELL_SIZE;
                double py = bot.getY() * Constants.CELL_SIZE + yOffset;
                int spriteIdx = Math.min(i + 1, playerSprites.length - 1); // bots: p2,p3,p4
                if (playerSprites[spriteIdx] != null) {
                    gc.drawImage(playerSprites[spriteIdx], px, py, Constants.CELL_SIZE, Constants.CELL_SIZE);
                } else {
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
                    double px = x * Constants.CELL_SIZE + 14;
                    double py = y * Constants.CELL_SIZE + 14 + yOffset;
                    switch (type) {
                        case EXTRA_BOMB:
                            gc.setFill(Color.BLUE);
                            gc.fillOval(px, py, 12, 12);
                            break;
                        case RANGE_UP:
                            gc.setFill(Color.ORANGERED);
                            gc.fillRect(px, py, 12, 12);
                            break;
                        case LIFE:
                            gc.setFill(Color.MEDIUMVIOLETRED);
                            gc.fillPolygon(new double[]{px + 6, px, px + 12}, new double[]{py, py + 12, py + 12}, 3);
                            break;
                        case SPEED:
                            gc.setFill(Color.GREEN);
                            gc.fillOval(px, py, 12, 8);
                            break;
                    }
                }
            }
        }
    }
}
