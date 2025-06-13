/**
 * Gestionnaire centralisé pour tous les thèmes visuels du jeu
 * Pattern Singleton pour assurer une instance unique
 * Gère les thèmes par défaut, personnalisés, et le cache d'images
 */
package com.bomberman.utils;

import javafx.scene.image.Image;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ThemeManager {
    // Constantes pour la gestion des fichiers
    private static final String THEMES_DIRECTORY = "themes";
    private static final String THEME_CONFIG_FILE = "theme_config.properties";
    private static final String DEFAULT_THEME = "classic";

    // Instance singleton
    private static ThemeManager instance;

    // État du gestionnaire
    private String currentTheme;                        // Thème actuellement actif
    private Properties themeConfig;                     // Configuration sauvegardée
    private Map<String, ThemeData> availableThemes;     // Tous les thèmes disponibles
    private Map<String, Image> imageCache;              // Cache des images chargées

    /**
     * Constructeur privé pour le pattern Singleton
     * Initialise tout le système de thèmes
     */
    private ThemeManager() {
        this.currentTheme = DEFAULT_THEME;
        this.themeConfig = new Properties();
        this.availableThemes = new HashMap<>();
        this.imageCache = new HashMap<>();
        initializeThemeSystem();
    }

    /**
     * Récupération de l'instance unique (Pattern Singleton)
     * @return L'instance unique du ThemeManager
     */
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Initialisation complète du système de thèmes
     * Appelée une seule fois lors de la création de l'instance
     */
    private void initializeThemeSystem() {
        createThemesDirectoryIfNotExists();
        loadThemeConfig();
        createDefaultThemes();
        loadCurrentTheme();
    }

    /**
     * Crée le dossier de thèmes s'il n'existe pas
     */
    private void createThemesDirectoryIfNotExists() {
        try {
            Path themesPath = Paths.get(THEMES_DIRECTORY);
            if (!Files.exists(themesPath)) {
                Files.createDirectories(themesPath);
                System.out.println("Dossier themes créé : " + themesPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du dossier themes : " + e.getMessage());
        }
    }

    /**
     * Création de tous les thèmes par défaut du jeu
     * Définit les sprites et couleurs pour chaque thème
     */
    private void createDefaultThemes() {
        // ===== THÈME CLASSIQUE =====
        ThemeData classicTheme = new ThemeData("classic", "Classique", "Thème Bomberman original");

        // Sprites des joueurs (4 variations)
        classicTheme.setPlayerSprites(Arrays.asList(
                "/images/bomberman_p1.png", "/images/bomberman_p2.png",
                "/images/bomberman_p3.png", "/images/bomberman_p4.png"
        ));

        // Sprites des ennemis
        classicTheme.setEnemySprites(Map.of(
                "bomber", "/images/bomber_perso.png",
                "yellow", "/images/yellow_perso.png"
        ));

        // Sprites des objets
        classicTheme.setBombSprite("/images/bombe_pixel.png");
        classicTheme.setDestructibleWallSprite("/images/block_rock.png");

        // Sprites des bonus
        classicTheme.setPowerUpSprites(Map.of(
                "EXTRA_BOMB", "/images/EXTRAT_BOMB.png",
                "RANGE_UP", "/images/RANGE_UP.png",
                "LIFE", "/images/LIFE.png",
                "SPEED", "/images/SPEED.png"
        ));

        // ===== THÈME LEGEND =====
        ThemeData legendTheme = new ThemeData("legend", "Legend", "Thème glacial pour le mode Legend");

        // Sprites ninja pour le mode Legend
        legendTheme.setPlayerSprites(Arrays.asList(
                "/images/nija_white_bomberman.png", "/images/nija_black_bomberman.png",
                "/images/nija_white_bomberman.png", "/images/nija_black_bomberman.png"
        ));

        // Même ennemis que classique
        legendTheme.setEnemySprites(Map.of(
                "bomber", "/images/bomber_perso.png",
                "yellow", "/images/yellow_perso.png"
        ));

        // Objets avec thème glacial
        legendTheme.setBombSprite("/images/bombe_pixel.png");
        legendTheme.setDestructibleWallSprite("/images/ice_cube.png");  // Cubes de glace

        // Bonus avec variantes "neige"
        legendTheme.setPowerUpSprites(Map.of(
                "EXTRA_BOMB", "/images/EXTRAT_BOMB_SNOW.png",
                "RANGE_UP", "/images/RANGE_UP_SNOW.png",
                "LIFE", "/images/LIFE_SNOW.png",
                "SPEED", "/images/SPEED_SNOW.png"
        ));

        // ===== THÈME RETRO =====
        ThemeData retroTheme = new ThemeData("retro", "Rétro", "Thème pixel art old-school");
        retroTheme.copyFrom(classicTheme);  // Copie du thème classique

        // ===== THÈME FUTURISTE =====
        ThemeData futuristicTheme = new ThemeData("futuristic", "Futuriste", "Thème sci-fi moderne");
        futuristicTheme.copyFrom(classicTheme);  // Copie du thème classique

        // Enregistrement de tous les thèmes
        availableThemes.put("classic", classicTheme);
        availableThemes.put("legend", legendTheme);
        availableThemes.put("retro", retroTheme);
        availableThemes.put("futuristic", futuristicTheme);
    }

    /**
     * Chargement de la configuration des thèmes depuis le fichier
     */
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

    /**
     * Sauvegarde de la configuration des thèmes dans un fichier
     */
    private void saveThemeConfig() {
        themeConfig.setProperty("current.theme", currentTheme);
        try (OutputStream output = new FileOutputStream(THEME_CONFIG_FILE)) {
            themeConfig.store(output, "Bomberman Theme Configuration");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la config des thèmes : " + e.getMessage());
        }
    }

    /**
     * Chargement et validation du thème actuel
     */
    private void loadCurrentTheme() {
        if (!availableThemes.containsKey(currentTheme)) {
            currentTheme = DEFAULT_THEME;
            saveThemeConfig();
        }
        clearImageCache();
    }

    /**
     * Change le thème actuel et sauvegarde la configuration
     * @param themeId Identifiant du nouveau thème
     */
    public void setCurrentTheme(String themeId) {
        if (availableThemes.containsKey(themeId)) {
            this.currentTheme = themeId;
            saveThemeConfig();
            clearImageCache();  // Vide le cache pour recharger les nouvelles images
            System.out.println("Thème changé vers : " + themeId);
        } else {
            System.err.println("Thème non trouvé : " + themeId);
        }
    }

    /**
     * Vide le cache d'images (utile lors du changement de thème)
     */
    private void clearImageCache() {
        imageCache.clear();
    }

    /**
     * Chargement d'une image thématique avec mise en cache
     * @param imagePath Chemin vers l'image
     * @return L'image chargée ou null si erreur
     */
    public Image getThemedImage(String imagePath) {
        String cacheKey = currentTheme + ":" + imagePath;

        // Vérification du cache
        if (imageCache.containsKey(cacheKey)) {
            return imageCache.get(cacheKey);
        }

        // Chargement et mise en cache
        Image image = loadImageSafely(imagePath);
        if (image != null) {
            imageCache.put(cacheKey, image);
        }

        return image;
    }

    /**
     * Chargement sécurisé d'une image avec gestion d'erreur
     * @param imagePath Chemin vers l'image
     * @return L'image chargée ou null si erreur
     */
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

    // ===== MÉTHODES D'ACCÈS AUX SPRITES THÉMATIQUES =====

    /**
     * Récupère le sprite d'un joueur selon son index
     * @param playerIndex Index du joueur (0-3)
     * @return L'image du sprite ou null
     */
    public Image getPlayerSprite(int playerIndex) {
        ThemeData theme = getCurrentThemeData();
        if (theme != null && playerIndex < theme.getPlayerSprites().size()) {
            return getThemedImage(theme.getPlayerSprites().get(playerIndex));
        }
        return null;
    }

    /**
     * Récupère le sprite d'un ennemi selon son type
     * @param enemyType Type d'ennemi ("bomber", "yellow", etc.)
     * @return L'image du sprite ou null
     */
    public Image getEnemySprite(String enemyType) {
        ThemeData theme = getCurrentThemeData();
        if (theme != null && theme.getEnemySprites().containsKey(enemyType)) {
            return getThemedImage(theme.getEnemySprites().get(enemyType));
        }
        return null;
    }

    /**
     * Récupère le sprite de bombe du thème actuel
     * @return L'image de la bombe ou null
     */
    public Image getBombSprite() {
        ThemeData theme = getCurrentThemeData();
        if (theme != null) {
            return getThemedImage(theme.getBombSprite());
        }
        return null;
    }

    /**
     * Récupère le sprite de mur destructible du thème actuel
     * @return L'image du mur destructible ou null
     */
    public Image getDestructibleWallSprite() {
        ThemeData theme = getCurrentThemeData();
        if (theme != null) {
            return getThemedImage(theme.getDestructibleWallSprite());
        }
        return null;
    }

    /**
     * Récupère le sprite d'un power-up selon son type
     * @param powerUpType Type de power-up ("EXTRA_BOMB", "RANGE_UP", etc.)
     * @return L'image du power-up ou null
     */
    public Image getPowerUpSprite(String powerUpType) {
        ThemeData theme = getCurrentThemeData();
        if (theme != null && theme.getPowerUpSprites().containsKey(powerUpType)) {
            return getThemedImage(theme.getPowerUpSprites().get(powerUpType));
        }
        return null;
    }

    /**
     * Récupère les couleurs du thème actuel
     * @return L'objet ThemeColors avec toutes les couleurs
     */
    public ThemeColors getThemeColors() {
        ThemeData theme = getCurrentThemeData();
        return theme != null ? theme.getColors() : new ThemeColors();
    }

    // ===== ACCESSEURS PUBLICS =====

    /**
     * @return L'identifiant du thème actuel
     */
    public String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * @return Les données du thème actuel
     */
    public ThemeData getCurrentThemeData() {
        return availableThemes.get(currentTheme);
    }

    /**
     * @return Une copie de tous les thèmes disponibles
     */
    public Map<String, ThemeData> getAvailableThemes() {
        return new HashMap<>(availableThemes);
    }

    /**
     * @return La liste des noms de tous les thèmes
     */
    public List<String> getThemeNames() {
        return new ArrayList<>(availableThemes.keySet());
    }

    // ===== GESTION DES THÈMES PERSONNALISÉS =====

    /**
     * Ajoute un thème personnalisé au gestionnaire
     * @param theme Le thème à ajouter
     */
    public void addCustomTheme(ThemeData theme) {
        availableThemes.put(theme.getId(), theme);
        try {
            saveThemeData(theme);
            System.out.println("Thème personnalisé ajouté : " + theme.getName());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du thème personnalisé : " + e.getMessage());
        }
    }

    /**
     * Sauvegarde les données d'un thème dans un fichier
     * @param theme Le thème à sauvegarder
     * @throws IOException Si erreur d'écriture
     */
    private void saveThemeData(ThemeData theme) throws IOException {
        Path themeFile = Paths.get(THEMES_DIRECTORY, theme.getId() + ".theme");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(themeFile.toFile()))) {
            oos.writeObject(theme);
        }
    }

    /**
     * Supprime un thème personnalisé
     * @param themeId Identifiant du thème à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteTheme(String themeId) {
        // Protection des thèmes par défaut
        if ("classic".equals(themeId) || "legend".equals(themeId)) {
            return false;
        }

        if (availableThemes.containsKey(themeId)) {
            availableThemes.remove(themeId);
            try {
                Path themeFile = Paths.get(THEMES_DIRECTORY, themeId + ".theme");
                Files.deleteIfExists(themeFile);

                // Si c'était le thème actuel, retour au thème par défaut
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
