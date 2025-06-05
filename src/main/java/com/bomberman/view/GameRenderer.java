package com.bomberman.view;

import com.bomberman.model.*;
import com.bomberman.model.enums.CellType;
import com.bomberman.model.enums.PowerUpType;
import com.bomberman.utils.Constants;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class GameRenderer {
    private Canvas canvas;
    private GraphicsContext gc;

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        canvas.setWidth(Constants.WINDOW_WIDTH);
        // Le HUD fait 56px de haut (bandeau orange + marges)
        canvas.setHeight(Constants.WINDOW_HEIGHT + 56 - Constants.HUD_HEIGHT);
    }

    public void render(Game game) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        renderHUD(game.getPlayer()); // Affiche le HUD EN HAUT !
        renderBoard(game.getBoard());
        renderExplosions(game.getBoard().getExplosions());
        renderBombs(game.getBoard().getBombs());
        renderPowerUps(game.getBoard());
        renderBots(game.getBoard().getBots());
        renderPlayer(game.getPlayer());
        // (Plus de HUD en bas)
    }

    private void renderHUD(Player player) {
        // Bandeau orange, 56px de haut, en haut de la fenêtre
        double hudHeight = 56;
        gc.setFill(Color.web("#FF8800"));
        gc.fillRect(0, 0, Constants.WINDOW_WIDTH, hudHeight);

        // Ombre pour l'effet "rétro"
        gc.setFill(Color.web("#CC6A00"));
        gc.fillRect(0, hudHeight - 6, Constants.WINDOW_WIDTH, 6);

        // Icône Bomberman stylisé (cercle blanc+noir+rouge) + vies
        double iconX = 16, iconY = 10, iconR = 18;
        for (int i = 0; i < player.getLives(); i++) {
            double offsetX = iconX + i * 26;
            // Bombe blanche
            gc.setFill(Color.WHITE);
            gc.fillOval(offsetX, iconY, iconR, iconR);
            // Contour noir
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(offsetX, iconY, iconR, iconR);
            // Petite mèche rouge
            gc.setFill(Color.RED);
            gc.fillOval(offsetX + 13, iconY - 5, 6, 6);
        }

        // Texte "SC" jaune style arcade
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        gc.setFill(Color.YELLOW);
        gc.fillText("SC", 120, 30);

        // Score en blanc sur fond noir (style original)
        gc.setFill(Color.BLACK);
        gc.fillRect(170, 12, 100, 28);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 26));
        String scoreStr = String.valueOf(player.getScore());
        gc.fillText(scoreStr, 260 - scoreStr.length() * 14, 34); // aligné à droite dans le rectangle

        // (Optionnel) Texte "PRESS START" à droite (en jaune)
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        gc.fillText("PRESS START", Constants.WINDOW_WIDTH - 180, 34);
    }

    private void renderBoard(Board board) {
        // Décale le plateau vers le bas sous le HUD
        double yOffset = 56;
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
        double yOffset = 56;
        for (Bomb bomb : bombs) {
            if (!bomb.hasExploded()) {
                double px = bomb.getX() * Constants.CELL_SIZE + 8;
                double py = bomb.getY() * Constants.CELL_SIZE + 8 + yOffset;
                gc.setFill(Color.BLACK);
                gc.fillOval(px, py, Constants.CELL_SIZE - 16, Constants.CELL_SIZE - 16);
                gc.setFill(Color.YELLOW);
                gc.fillOval(px + 8, py + 8, 6, 6);
            }
        }
    }

    private void renderExplosions(List<Explosion> explosions) {
        double yOffset = 56;
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
            double yOffset = 56;
            double px = player.getX() * Constants.CELL_SIZE + 6;
            double py = player.getY() * Constants.CELL_SIZE + 6 + yOffset;
            gc.setFill(Color.DODGERBLUE);
            gc.fillOval(px, py, Constants.CELL_SIZE - 12, Constants.CELL_SIZE - 12);
            gc.setFill(Color.WHITE);
            gc.fillOval(px + 7, py + 10, 8, 8);
        }
    }

    private void renderBots(List<PlayerBot> bots) {
        double yOffset = 56;
        Color[] botColors = {Color.CRIMSON, Color.GOLD, Color.MEDIUMVIOLETRED};
        int idx = 0;
        for (PlayerBot bot : bots) {
            if (bot.isAlive()) {
                double px = bot.getX() * Constants.CELL_SIZE + 8;
                double py = bot.getY() * Constants.CELL_SIZE + 8 + yOffset;
                gc.setFill(botColors[idx % botColors.length]);
                gc.fillOval(px, py, Constants.CELL_SIZE - 16, Constants.CELL_SIZE - 16);
                gc.setFill(Color.WHITE);
                gc.fillOval(px + 7, py + 8, 8, 8);
            }
            idx++;
        }
    }

    private void renderPowerUps(Board board) {
        double yOffset = 56;
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
