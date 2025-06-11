package com.bomberman.utils;

import java.io.Serializable;
import java.util.*;

/**
 * Classe contenant toutes les données d'un thème
 */
public class ThemeData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private List<String> playerSprites;
    private Map<String, String> enemySprites;
    private String bombSprite;
    private String destructibleWallSprite;
    private Map<String, String> powerUpSprites;
    private ThemeColors colors;
    private long creationTime;

    public ThemeData(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.playerSprites = new ArrayList<>();
        this.enemySprites = new HashMap<>();
        this.powerUpSprites = new HashMap<>();
        this.colors = new ThemeColors();
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * Copie les données d'un autre thème
     */
    public void copyFrom(ThemeData other) {
        if (other != null) {
            this.playerSprites = new ArrayList<>(other.playerSprites);
            this.enemySprites = new HashMap<>(other.enemySprites);
            this.bombSprite = other.bombSprite;
            this.destructibleWallSprite = other.destructibleWallSprite;
            this.powerUpSprites = new HashMap<>(other.powerUpSprites);
            this.colors = new ThemeColors(other.colors);
        }
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getPlayerSprites() { return playerSprites; }
    public void setPlayerSprites(List<String> playerSprites) {
        this.playerSprites = playerSprites != null ? playerSprites : new ArrayList<>();
    }

    public Map<String, String> getEnemySprites() { return enemySprites; }
    public void setEnemySprites(Map<String, String> enemySprites) {
        this.enemySprites = enemySprites != null ? enemySprites : new HashMap<>();
    }

    public String getBombSprite() { return bombSprite; }
    public void setBombSprite(String bombSprite) { this.bombSprite = bombSprite; }

    public String getDestructibleWallSprite() { return destructibleWallSprite; }
    public void setDestructibleWallSprite(String destructibleWallSprite) {
        this.destructibleWallSprite = destructibleWallSprite;
    }

    public Map<String, String> getPowerUpSprites() { return powerUpSprites; }
    public void setPowerUpSprites(Map<String, String> powerUpSprites) {
        this.powerUpSprites = powerUpSprites != null ? powerUpSprites : new HashMap<>();
    }

    public ThemeColors getColors() {
        if (colors == null) {
            colors = new ThemeColors();
        }
        return colors;
    }
    public void setColors(ThemeColors colors) {
        this.colors = colors != null ? colors : new ThemeColors();
    }

    public long getCreationTime() { return creationTime; }
    public void setCreationTime(long creationTime) { this.creationTime = creationTime; }

    @Override
    public String toString() {
        return name != null ? name : id;
    }
}