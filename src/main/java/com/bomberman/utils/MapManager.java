/**
 * Gestionnaire de cartes personnalisées
 * Sérialisation, validation et métadonnées
 */
package com.bomberman.utils;

import com.bomberman.model.MapData;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private static final String MAPS_DIRECTORY = "maps";
    private static final String MAP_EXTENSION = ".bmmap"; // Bomberman Map

    /**
     * Constructeur - Crée le dossier de cartes si nécessaire
     */
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
            System.err.println("Erreur création dossier maps : " + e.getMessage());
        }
    }

    /**
     * Sauvegarde une carte dans un fichier
     */
    public void saveMap(MapData mapData) throws IOException {
        if (!mapData.isValid()) {
            throw new IllegalArgumentException("Carte non valide : zones de spawn bloquées ou pas assez de cases jouables");
        }

        String fileName = sanitizeFileName(mapData.getName()) + MAP_EXTENSION;
        Path filePath = Paths.get(MAPS_DIRECTORY, fileName);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(mapData);
            System.out.println("Carte sauvegardée : " + filePath.toAbsolutePath());
        }
    }

    /**
     * Charge une carte depuis un fichier
     */
    public MapData loadMap(String mapName) throws IOException, ClassNotFoundException {
        String fileName = sanitizeFileName(mapName) + MAP_EXTENSION;
        Path filePath = Paths.get(MAPS_DIRECTORY, fileName);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Carte non trouvée : " + mapName);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            MapData mapData = (MapData) ois.readObject();
            System.out.println("Carte chargée : " + filePath.toAbsolutePath());
            return mapData;
        }
    }

    /**
     * Supprime une carte
     */
    public void deleteMap(String mapName) throws IOException {
        String fileName = sanitizeFileName(mapName) + MAP_EXTENSION;
        Path filePath = Paths.get(MAPS_DIRECTORY, fileName);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("Carte supprimée : " + filePath.toAbsolutePath());
        } else {
            throw new FileNotFoundException("Carte non trouvée : " + mapName);
        }
    }

    /**
     * Retourne la liste des cartes disponibles
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
            System.err.println("Erreur lecture dossier maps : " + e.getMessage());
        }

        return mapNames;
    }

    /**
     * Vérifie si une carte existe
     */
    public boolean mapExists(String mapName) {
        String fileName = sanitizeFileName(mapName) + MAP_EXTENSION;
        Path filePath = Paths.get(MAPS_DIRECTORY, fileName);
        return Files.exists(filePath);
    }

    /**
     * Obtient des informations sur une carte
     */
    public String getMapInfo(String mapName) {
        try {
            MapData mapData = loadMap(mapName);
            return String.format("Carte: %s\nCréée: %tc\n%s\nDescription: %s",
                    mapData.getName(),
                    mapData.getCreationTime(),
                    mapData.getStats(),
                    mapData.getDescription()
            );
        } catch (Exception e) {
            return "Erreur lecture informations : " + e.getMessage();
        }
    }

    /**
     * Nettoie le nom de fichier pour éviter les caractères problématiques
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Exporte une carte vers un format texte lisible
     */
    public void exportMapToText(String mapName, String outputPath) throws IOException, ClassNotFoundException {
        MapData mapData = loadMap(mapName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("# Carte: " + mapData.getName());
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