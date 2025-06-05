package com.bomberman.view;

import com.bomberman.model.*;
import com.bomberman.model.enums.CellType;
import com.bomberman.model.enums.PowerUpType;
import com.bomberman.utils.Constants;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class GameRenderer {
    private Canvas canvas;
    private GraphicsContext gc;

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        canvas.setWidth(Constants.WINDOW_WIDTH);
        canvas.setHeight(Constants.WINDOW_HEIGHT); // Plus de "-50" ni rien d'autre
    }

    public void render(Game game) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        renderBoard(game.getBoard());
        renderExplosions(game.getBoard().getExplosions());
        renderBombs(game.getBoard().getBombs());
        renderPowerUps(game.getBoard());
        renderBots(game.getBoard().getBots());
        renderPlayer(game.getPlayer());
        renderHUD(game.getPlayer());
    }

    private void renderBoard(Board board) {
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                double pixelX = x * Constants.CELL_SIZE;
                double pixelY = y * Constants.CELL_SIZE;

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

                // Bordure
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(pixelX, pixelY, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    private void renderBombs(List<Bomb> bombs) {
        for (Bomb bomb : bombs) {
            if (!bomb.hasExploded()) {
                double px = bomb.getX() * Constants.CELL_SIZE + 8;
                double py = bomb.getY() * Constants.CELL_SIZE + 8;
                gc.setFill(Color.BLACK);
                gc.fillOval(px, py, Constants.CELL_SIZE - 16, Constants.CELL_SIZE - 16);
                gc.setFill(Color.YELLOW);
                gc.fillOval(px + 8, py + 8, 6, 6); // petite mèche
            }
        }
    }

    private void renderExplosions(List<Explosion> explosions) {
        for (Explosion explosion : explosions) {
            double px = explosion.getX() * Constants.CELL_SIZE;
            double py = explosion.getY() * Constants.CELL_SIZE;
            gc.setGlobalAlpha(0.7);
            gc.setFill(Color.ORANGE);
            gc.fillRect(px + 2, py + 2, Constants.CELL_SIZE - 4, Constants.CELL_SIZE - 4);
            gc.setGlobalAlpha(1.0);
        }
    }

    private void renderPlayer(Player player) {
        if (player.isAlive()) {
            double px = player.getX() * Constants.CELL_SIZE + 6;
            double py = player.getY() * Constants.CELL_SIZE + 6;
            gc.setFill(Color.DODGERBLUE);
            gc.fillOval(px, py, Constants.CELL_SIZE - 12, Constants.CELL_SIZE - 12);
            gc.setFill(Color.WHITE);
            gc.fillOval(px + 7, py + 10, 8, 8); // visage
        }
    }

    private void renderBots(List<PlayerBot> bots) {
        Color[] botColors = {Color.CRIMSON, Color.GOLD, Color.MEDIUMVIOLETRED};
        int idx = 0;
        for (PlayerBot bot : bots) {
            if (bot.isAlive()) {
                double px = bot.getX() * Constants.CELL_SIZE + 8;
                double py = bot.getY() * Constants.CELL_SIZE + 8;
                gc.setFill(botColors[idx % botColors.length]);
                gc.fillOval(px, py, Constants.CELL_SIZE - 16, Constants.CELL_SIZE - 16);
                gc.setFill(Color.WHITE);
                gc.fillOval(px + 7, py + 8, 8, 8);
            }
            idx++;
        }
    }

    private void renderPowerUps(Board board) {
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                Cell cell = board.getCell(x, y);
                if (cell.hasPowerUp()) {
                    PowerUpType type = cell.getPowerUp().getType();
                    double px = x * Constants.CELL_SIZE + 14;
                    double py = y * Constants.CELL_SIZE + 14;
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

    private void renderHUD(Player player) {
        double hudY = Constants.BOARD_HEIGHT * Constants.CELL_SIZE; // Position Y juste SOUS le plateau
        double textY = hudY + 22; // Décalage vertical pour centrer le texte dans le bandeau

        gc.setFill(Color.BLACK);
        gc.fillRect(0, hudY, Constants.WINDOW_WIDTH, Constants.HUD_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 18));
        gc.fillText("SCORE : " + player.getScore(), Constants.WINDOW_WIDTH / 2.0 - 60, textY);

        gc.setFont(Font.font("Arial", 12));
        gc.fillText("Vies : " + player.getLives(), 10, textY);
        gc.fillText("Bombes max : " + player.getMaxBombs(), 120, textY);
        gc.fillText("Bombes restantes : " + player.getBombsAvailable(), 300, textY);
        gc.fillText("Portée : " + player.getExplosionRange(), 250, textY);
        gc.fillText("Bomberman 90 JavaFX", Constants.WINDOW_WIDTH - 170, textY);
    }
}