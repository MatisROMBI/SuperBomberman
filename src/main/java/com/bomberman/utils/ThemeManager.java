package com.bomberman.utils;

import javafx.scene.image.Image;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Gestionnaire de thèmes simplifié pour Bomberman
 */
public class ThemeManager {
    private static final String THEMES_DIRECTORY = "themes";
    private static final String THEME_CONFIG_FILE = "theme_config.properties";
    private static final String DEFAULT_THEME = "classic";

    private static ThemeManager instance;
    private String currentTheme;
    private Properties themeConfig;
    private Map<String, ThemeData> availableThemes;
    private Map<String, Image> imageCache;

    private ThemeManager() {
        this.currentTheme = DEFAULT_THEME;
        this.themeConfig = new Properties();
        this.availableThemes = new HashMap<>();
        this.imageCache = new HashMap<>();
        initializeThemeSystem();
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    private void initializeThemeSystem() {
        createThemesDirectoryIfNotExists();
        loadThemeConfig();
        createDefaultThemes();
        loadCurrentTheme();
    }

    private void createThemesDirectoryIfNotExists() {
        try {
            Path themesPath = Paths.get(THEMES_DIRECTORY);
            if (!Files.exists(themesPath)) {
                Files.createDirectories(themesPath);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du dossier themes : " + e.getMessage());
        }
    }

    private void createDefaultThemes() {
        // Thème classique
        ThemeData classicTheme = new ThemeData("classic", "Classique", "Thème Bomberman original");
        classicTheme.setPlayerSprites(Arrays.asList(
                "/images/bomberman_p1.png", "/images/bomberman_p2.png",
                "/images/bomberman_p3.png", "/images/bomberman_p4.png"
        ));
        classicTheme.setEnemySprites(Map.of(
                "bomber", "/images/bomber_perso.png",
                "yellow", "/images/yellow_perso.png"
        ));
        classicTheme.setBombSprite("/images/bombe_pixel.png");
        classicTheme.setDestructibleWallSprite("/images/block_rock.png");
        classicTheme.setPowerUpSprites(Map.of(
                "EXTRA_BOMB", "/images/EXTRAT_BOMB.png",
                "RANGE_UP", "/images/RANGE_UP.png",
                "LIFE", "/images/LIFE.png",
                "SPEED", "/images/SPEED.png"
        ));

        // Thème Legend
        ThemeData legendTheme = new ThemeData("legend", "Legend", "Thème glacial pour le mode Legend");
        legendTheme.setPlayerSprites(Arrays.asList(
                "/images/nija_white_bomberman.png", "/images/nija_black_bomberman.png",
                "/images/nija_white_bomberman.png", "/images/nija_black_bomberman.png"
        ));
        legendTheme.setEnemySprites(Map.of(
                "bomber", "/images/bomber_perso.png",
                "yellow", "/images/yellow_perso.png"
        ));
        legendTheme.setBombSprite("/images/bombe_pixel.png");
        legendTheme.setDestructibleWallSprite("/images/ice_cube.png");
        legendTheme.setPowerUpSprites(Map.of(
                "EXTRA_BOMB", "/images/EXTRAT_BOMB_SNOW.png",
                "RANGE_UP", "/images/RANGE_UP_SNOW.png",
                "LIFE", "/images/LIFE_SNOW.png",
                "SPEED", "/images/SPEED_SNOW.png"
        ));

        // Thème Retro
        ThemeData retroTheme = new ThemeData("retro", "Rétro", "Thème pixel art old-school");
        retroTheme.copyFrom(classicTheme);

        // Thème Futuriste
        ThemeData futuristicTheme = new ThemeData("futuristic", "Futuriste", "Thème sci-fi moderne");
        futuristicTheme.copyFrom(classicTheme);

        availableThemes.put("classic", classicTheme);
        availableThemes.put("legend", legendTheme);
        availableThemes.put("retro", retroTheme);
        availableThemes.put("futuristic", futuristicTheme);
    }

    private void loadThemeConfig() {
        Path configPath = Paths.get(THEME_CONFIG_FILE);
        if (Files.exists(configPath)) {
            try (InputStream input = new FileInputStream(configPath.toFile())) {
                themeConfig.load(input);
                currentTheme = themeConfig.getProperty("current.theme", DEFAULT_THEME);
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la config des thèmes : " + e.getMessage());
                currentTheme = DEFAULT_THEME;
            }
        }
    }

    private void saveThemeConfig() {
        themeConfig.setProperty("current.theme", currentTheme);
        try (OutputStream output = new FileOutputStream(THEME_CONFIG_FILE)) {
            themeConfig.store(output, "Bomberman Theme Configuration");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la config des thèmes : " + e.getMessage());
        }
    }

    private void loadCurrentTheme() {
        if (!availableThemes.containsKey(currentTheme)) {
            currentTheme = DEFAULT_THEME;
            saveThemeConfig();
        }
        clearImageCache();
    }

    public void setCurrentTheme(String themeId) {
        if (availableThemes.containsKey(themeId)) {
            this.currentTheme = themeId;
            saveThemeConfig();
            clearImageCache();
            System.out.println("Thème changé vers : " + themeId);
        } else {
            System.err.println("Thème non trouvé : " + themeId);
        }
    }

    private void clearImageCache() {
        imageCache.clear();
    }

    public Image getThemedImage(String imagePath) {
        String cacheKey = currentTheme + ":" + imagePath;

        if (imageCache.containsKey(cacheKey)) {
            return imageCache.get(cacheKey);
        }

        Image image = loadImageSafely(imagePath);
        if (image != null) {
            imageCache.put(cacheKey, image);
        }

        return image;
    }

    private Image loadImageSafely(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                InputStream stream = getClass().getResourceAsStream(imagePath);
                if (stream != null) {
                    return new Image(stream);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + imagePath);
        }
        return null;
    }

    public Image getPlayerSprite(int playerIndex) {
        ThemeData theme = getCurrentThemeData();
        if (theme != null && playerIndex < theme.getPlayerSprites().size()) {
            return getThemedImage(theme.getPlayerSprites().get(playerIndex));
        }
        return null;
    }

    public Image getEnemySprite(String enemyType) {
        ThemeData theme = getCurrentThemeData();
        if (theme != null && theme.getEnemySprites().containsKey(enemyType)) {
            return getThemedImage(theme.getEnemySprites().get(enemyType));
        }
        return null;
    }

    public Image getBombSprite() {
        ThemeData theme = getCurrentThemeData();
        if (theme != null) {
            return getThemedImage(theme.getBombSprite());
        }
        return null;
    }

    public Image getDestructibleWallSprite() {
        ThemeData theme = getCurrentThemeData();
        if (theme != null) {
            return getThemedImage(theme.getDestructibleWallSprite());
        }
        return null;
    }

    public Image getPowerUpSprite(String powerUpType) {
        ThemeData theme = getCurrentThemeData();
        if (theme != null && theme.getPowerUpSprites().containsKey(powerUpType)) {
            return getThemedImage(theme.getPowerUpSprites().get(powerUpType));
        }
        return null;
    }

    public ThemeColors getThemeColors() {
        ThemeData theme = getCurrentThemeData();
        return theme != null ? theme.getColors() : new ThemeColors();
    }

    // Getters
    public String getCurrentTheme() {
        return currentTheme;
    }

    public ThemeData getCurrentThemeData() {
        return availableThemes.get(currentTheme);
    }

    public Map<String, ThemeData> getAvailableThemes() {
        return new HashMap<>(availableThemes);
    }

    public List<String> getThemeNames() {
        return new ArrayList<>(availableThemes.keySet());
    }

    public void addCustomTheme(ThemeData theme) {
        availableThemes.put(theme.getId(), theme);
        try {
            saveThemeData(theme);
            System.out.println("Thème personnalisé ajouté : " + theme.getName());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du thème personnalisé : " + e.getMessage());
        }
    }

    private void saveThemeData(ThemeData theme) throws IOException {
        Path themeFile = Paths.get(THEMES_DIRECTORY, theme.getId() + ".theme");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(themeFile.toFile()))) {
            oos.writeObject(theme);
        }
    }

    public boolean deleteTheme(String themeId) {
        if ("classic".equals(themeId) || "legend".equals(themeId)) {
            return false; // Ne pas supprimer les thèmes par défaut
        }

        if (availableThemes.containsKey(themeId)) {
            availableThemes.remove(themeId);
            try {
                Path themeFile = Paths.get(THEMES_DIRECTORY, themeId + ".theme");
                Files.deleteIfExists(themeFile);

                if (currentTheme.equals(themeId)) {
                    setCurrentTheme(DEFAULT_THEME);
                }
                return true;
            } catch (IOException e) {
                System.err.println("Erreur lors de la suppression du thème : " + e.getMessage());
            }
        }
        return false;
    }
}