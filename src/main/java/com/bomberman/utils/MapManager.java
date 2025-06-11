package com.bomberman.utils;

import com.bomberman.model.MapData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire pour la sauvegarde et le chargement des maps personnalisées
 */
public class MapManager {
    private static final String MAPS_DIRECTORY = "maps";
    private static final String MAP_EXTENSION = ".bmmap"; // Bomberman Map

    public MapManager() {
        createMapsDirectoryIfNotExists();
    }

    /**
     * Crée le dossier maps s'il n'existe pas
     */
    private void createMapsDirectoryIfNotExists() {
        try {
            Path mapsPath = Paths.get(MAPS_DIRECTORY);
            if (!Files.exists(mapsPath)) {
                Files.createDirectories(mapsPath);
                System.out.println("Dossier maps créé : " + mapsPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du dossier maps : " + e.getMessage());
        }
    }

    /**
     * Sauvegarde une map dans un fichier
     */
    public void saveMap(MapData mapData) throws IOException {
        if (!mapData.isValid()) {
            throw new IllegalArgumentException("La map n'est pas valide : zones de spawn bloquées ou pas assez de cases jouables");
        }

        String fileName = sanitizeFileName(mapData.getName()) + MAP_EXTENSION;
        Path filePath = Paths.get(MAPS_DIRECTORY, fileName);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(mapData);
            System.out.println("Map sauvegardée : " + filePath.toAbsolutePath());
        }
    }

    /**
     * Charge une map depuis un fichier
     */
    public MapData loadMap(String mapName) throws IOException, ClassNotFoundException {
        String fileName = sanitizeFileName(mapName) + MAP_EXTENSION;
        Path filePath = Paths.get(MAPS_DIRECTORY, fileName);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Map non trouvée : " + mapName);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            MapData mapData = (MapData) ois.readObject();
            System.out.println("Map chargée : " + filePath.toAbsolutePath());
            return mapData;
        }
    }

    /**
     * Supprime une map
     */
    public void deleteMap(String mapName) throws IOException {
        String fileName = sanitizeFileName(mapName) + MAP_EXTENSION;
        Path filePath = Paths.get(MAPS_DIRECTORY, fileName);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("Map supprimée : " + filePath.toAbsolutePath());
        } else {
            throw new FileNotFoundException("Map non trouvée : " + mapName);
        }
    }

    /**
     * Retourne la liste des noms de maps disponibles
     */
    public List<String> getAvailableMaps() {
        List<String> mapNames = new ArrayList<>();

        try {
            Path mapsPath = Paths.get(MAPS_DIRECTORY);
            if (Files.exists(mapsPath) && Files.isDirectory(mapsPath)) {
                Files.walk(mapsPath, 1)
                        .filter(path -> path.toString().endsWith(MAP_EXTENSION))
                        .forEach(path -> {
                            String fileName = path.getFileName().toString();
                            String mapName = fileName.substring(0, fileName.lastIndexOf(MAP_EXTENSION));
                            mapNames.add(mapName);
                        });
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du dossier maps : " + e.getMessage());
        }

        return mapNames;
    }

    /**
     * Vérifie si une map existe
     */
    public boolean mapExists(String mapName) {
        String fileName = sanitizeFileName(mapName) + MAP_EXTENSION;
        Path filePath = Paths.get(MAPS_DIRECTORY, fileName);
        return Files.exists(filePath);
    }

    /**
     * Obtient des informations sur une map sans la charger complètement
     */
    public String getMapInfo(String mapName) {
        try {
            MapData mapData = loadMap(mapName);
            return String.format("Map: %s\nCréée: %tc\n%s\nDescription: %s",
                    mapData.getName(),
                    mapData.getCreationTime(),
                    mapData.getStats(),
                    mapData.getDescription()
            );
        } catch (Exception e) {
            return "Erreur lors de la lecture des informations : " + e.getMessage();
        }
    }

    /**
     * Nettoie le nom de fichier pour éviter les caractères problématiques
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Exporte une map vers un format texte lisible (pour debug/partage)
     */
    public void exportMapToText(String mapName, String outputPath) throws IOException, ClassNotFoundException {
        MapData mapData = loadMap(mapName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("# Map: " + mapData.getName());
            writer.println("# " + mapData.getStats());
            writer.println("# Créée: " + new java.util.Date(mapData.getCreationTime()));
            writer.println();

            for (int y = 0; y < Constants.BOARD_HEIGHT; y++) {
                for (int x = 0; x < Constants.BOARD_WIDTH; x++) {
                    switch (mapData.getGrid()[x][y]) {
                        case WALL: writer.print("#"); break;
                        case DESTRUCTIBLE_WALL: writer.print("X"); break;
                        case EMPTY: writer.print("."); break;
                        default: writer.print("?"); break;
                    }
                }
                writer.println();
            }
        }
    }
}