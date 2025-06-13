/**
 * Palette de couleurs pour un thème
 * Gère les couleurs du plateau, HUD et mode Legend
 */
package com.bomberman.utils;

import javafx.scene.paint.Color;
import java.io.Serializable;

public class ThemeColors implements Serializable {
    private static final long serialVersionUID = 1L;

    // Couleurs du plateau classique
    private String emptyCell1 = "#90EE90";     // Vert clair
    private String emptyCell2 = "#7CFC00";     // Vert vif
    private String wallColor = "#696969";      // Gris foncé
    private String explosionColor = "#FFA500"; // Orange

    // Couleurs du HUD
    private String hudBackgroundColor = "#FF8800";  // Orange
    private String hudBorderColor = "#CC6A00";      // Orange foncé
    private String hudTextColor = "#FFFFFF";        // Blanc
    private String scoreBackgroundColor = "#000000"; // Noir

    // Couleurs spéciales mode Legend
    private String legendBackground = "#1882f7";    // Bleu
    private String legendHud = "#38b6ff";           // Bleu clair
    private String legendEmpty1 = "#7ed5fa";        // Bleu très clair
    private String legendEmpty2 = "#0e51b8";        // Bleu foncé
    private String legendWall = "#3657a6";          // Bleu marine

    /**
     * Constructeur par défaut
     */
    public ThemeColors() {}

    /**
     * Constructeur de copie
     */
    public ThemeColors(ThemeColors other) {
        if (other != null) {
            this.emptyCell1 = other.emptyCell1;
            this.emptyCell2 = other.emptyCell2;
            this.wallColor = other.wallColor;
            this.explosionColor = other.explosionColor;
            this.hudBackgroundColor = other.hudBackgroundColor;
            this.hudBorderColor = other.hudBorderColor;
            this.hudTextColor = other.hudTextColor;
            this.scoreBackgroundColor = other.scoreBackgroundColor;
            this.legendBackground = other.legendBackground;
            this.legendHud = other.legendHud;
            this.legendEmpty1 = other.legendEmpty1;
            this.legendEmpty2 = other.legendEmpty2;
            this.legendWall = other.legendWall;
        }
    }

    // Accesseurs avec gestion d'erreur et fallback
    public Color getEmptyCell1() {
        try { return Color.web(emptyCell1); }
        catch (Exception e) { return Color.LIGHTGREEN; }
    }
    public void setEmptyCell1(String color) { this.emptyCell1 = color; }

    public Color getEmptyCell2() {
        try { return Color.web(emptyCell2); }
        catch (Exception e) { return Color.GREEN; }
    }
    public void setEmptyCell2(String color) { this.emptyCell2 = color; }

    public Color getWallColor() {
        try { return Color.web(wallColor); }
        catch (Exception e) { return Color.DARKGRAY; }
    }
    public void setWallColor(String color) { this.wallColor = color; }

    public Color getExplosionColor() {
        try { return Color.web(explosionColor); }
        catch (Exception e) { return Color.ORANGE; }
    }
    public void setExplosionColor(String color) { this.explosionColor = color; }

    public Color getHudBackgroundColor() {
        try { return Color.web(hudBackgroundColor); }
        catch (Exception e) { return Color.ORANGE; }
    }
    public void setHudBackgroundColor(String color) { this.hudBackgroundColor = color; }

    public Color getHudBorderColor() {
        try { return Color.web(hudBorderColor); }
        catch (Exception e) { return Color.DARKORANGE; }
    }
    public void setHudBorderColor(String color) { this.hudBorderColor = color; }

    public Color getHudTextColor() {
        try { return Color.web(hudTextColor); }
        catch (Exception e) { return Color.WHITE; }
    }
    public void setHudTextColor(String color) { this.hudTextColor = color; }

    public Color getScoreBackgroundColor() {
        try { return Color.web(scoreBackgroundColor); }
        catch (Exception e) { return Color.BLACK; }
    }
    public void setScoreBackgroundColor(String color) { this.scoreBackgroundColor = color; }

    // Couleurs mode Legend
    public Color getLegendBackground() {
        try { return Color.web(legendBackground); }
        catch (Exception e) { return Color.BLUE; }
    }
    public void setLegendBackground(String color) { this.legendBackground = color; }

    public Color getLegendHud() {
        try { return Color.web(legendHud); }
        catch (Exception e) { return Color.LIGHTBLUE; }
    }
    public void setLegendHud(String color) { this.legendHud = color; }

    public Color getLegendEmpty1() {
        try { return Color.web(legendEmpty1); }
        catch (Exception e) { return Color.LIGHTBLUE; }
    }
    public void setLegendEmpty1(String color) { this.legendEmpty1 = color; }

    public Color getLegendEmpty2() {
        try { return Color.web(legendEmpty2); }
        catch (Exception e) { return Color.DARKBLUE; }
    }
    public void setLegendEmpty2(String color) { this.legendEmpty2 = color; }

    public Color getLegendWall() {
        try { return Color.web(legendWall); }
        catch (Exception e) { return Color.NAVY; }
    }
    public void setLegendWall(String color) { this.legendWall = color; }
}
