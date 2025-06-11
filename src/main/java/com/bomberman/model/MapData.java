package com.bomberman.model;

import com.bomberman.model.enums.CellType;
import com.bomberman.utils.Constants;

import java.io.Serializable;

/**
 * Classe représentant les données d'une map personnalisée
 */
public class MapData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private CellType[][] grid;
    private long creationTime;
    private String description;

    public MapData(String name, CellType[][] grid) {
        this.name = name;
        this.grid = copyGrid(grid);
        this.creationTime = System.currentTimeMillis();
        this.description = "Map personnalisée";
    }

    public MapData(String name, CellType[][] grid, String description) {
        this.name = name;
        this.grid = copyGrid(grid);
        this.creationTime = System.currentTimeMillis();
        this.description = description;
    }

    /**
     * Crée une copie profonde de la grille pour éviter les références partagées
     */
    private CellType[][] copyGrid(CellType[][] original) {
        CellType[][] copy = new CellType[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                copy[x][y] = original[x][y];
            }
        }
        return copy;
    }

    /**
     * Valide que la map est jouable (zones de spawn libres, etc.)
     */
    public boolean isValid() {
        // Vérifier que les positions de spawn sont libres
        if (grid[1][1] != CellType.EMPTY) return false;

        int maxX = Constants.BOARD_WIDTH - 2;
        int maxY = Constants.BOARD_HEIGHT - 2;
        if (grid[maxX][maxY] != CellType.EMPTY) return false;
        if (grid[maxX][1] != CellType.EMPTY) return false;
        if (grid[1][maxY] != CellType.EMPTY) return false;

        // Vérifier qu'il y a au moins quelques cases vides accessibles
        int emptyCells = 0;
        for (int x = 1; x < Constants.BOARD_WIDTH - 1; x++) {
            for (int y = 1; y < Constants.BOARD_HEIGHT - 1; y++) {
                if (grid[x][y] == CellType.EMPTY || grid[x][y] == CellType.DESTRUCTIBLE_WALL) {
                    emptyCells++;
                }
            }
        }

        return emptyCells >= 10; // Au minimum 10 cases jouables
    }

    /**
     * Génère des statistiques sur la map
     */
    public String getStats() {
        int walls = 0, destructible = 0, empty = 0;

        for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                switch (grid[x][y]) {
                    case WALL: walls++; break;
                    case DESTRUCTIBLE_WALL: destructible++; break;
                    case EMPTY: empty++; break;
                }
            }
        }

        return String.format("Murs: %d | Destructibles: %d | Vides: %d", walls, destructible, empty);
    }

    // Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CellType[][] getGrid() { return grid; }
    public void setGrid(CellType[][] grid) { this.grid = copyGrid(grid); }

    public long getCreationTime() { return creationTime; }
    public void setCreationTime(long creationTime) { this.creationTime = creationTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name;
    }
}