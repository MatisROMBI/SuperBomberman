/**
 * Gestionnaire centralisé pour tous les sons et musiques du jeu
 * Supporte les effets sonores courts et les musiques en boucle
 */
package com.bomberman.model;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Music {
    // Contrôles généraux
    private boolean sonActif = true;        // Activer/désactiver les effets sonores
    private boolean musiqueActif = true;    // Activer/désactiver les musiques

    // Effets sonores courts (AudioClip pour performance)
    private AudioClip explosionClip;
    private AudioClip gameOver;
    private AudioClip musiqueDepart;
    private AudioClip musiqueDeVictoire;

    // Musiques de fond longues (MediaPlayer pour streaming)
    private MediaPlayer musiquePlayer;          // Musique mode classique
    private MediaPlayer legendMusicPlayer;      // Musique mode LEGEND

    /**
     * Constructeur - Charge tous les fichiers audio
     */
    public Music() {
        try {
            // Chargement des effets sonores courts
            explosionClip = new AudioClip(getClass().getResource("/sons/explosion.mp3").toExternalForm());
            musiqueDepart = new AudioClip(getClass().getResource("/sons/MusiqueDeDepart.mp3").toExternalForm());
            gameOver = new AudioClip(getClass().getResource("/sons/est-ce-que-tu-veux-abandonner-made-with-Voicemod.mp3").toExternalForm());
            musiqueDeVictoire = new AudioClip(getClass().getResource("/sons/MusiqueDeVictoire.mp3").toExternalForm());

            // Chargement des musiques de fond
            Media InGameMusic = new Media(getClass().getResource("/sons/MusiqueMonde3.mp3").toExternalForm());
            musiquePlayer = new MediaPlayer(InGameMusic);
            musiquePlayer.setCycleCount(MediaPlayer.INDEFINITE); // Lecture en boucle

            Media legendMusic = new Media(getClass().getResource("/sons/Super Bomberman - World 6 (SNES OST).mp3").toExternalForm());
            legendMusicPlayer = new MediaPlayer(legendMusic);
            legendMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Lecture en boucle
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des fichiers audio : " + e.getMessage());
        }
    }

    // ---- Effets sonores ----
    public void jouerExplosion() {
        if (sonActif && explosionClip != null) explosionClip.play();
    }

    public void demarrerGameOverMusique() {
        if (musiqueActif && gameOver != null) gameOver.play();
    }

    public void demarrerMusiqueDepart() {
        if (musiqueActif && musiqueDepart != null) musiqueDepart.play();
    }

    public void demarrerMusiqueDeVictoire() {
        if (musiqueActif && musiqueDeVictoire != null) musiqueDeVictoire.play();
    }

    // ---- Musiques de fond ----
    public void demarrerMusique() {
        if (musiqueActif && musiquePlayer != null) musiquePlayer.play();
    }

    public void arreterMusique() {
        if (musiquePlayer != null) musiquePlayer.pause();
    }

    // ---- Musique mode LEGEND ----
    public void demarrerLegendMusic() {
        if (musiqueActif && legendMusicPlayer != null) legendMusicPlayer.play();
    }

    public void arreterLegendMusic() {
        if (legendMusicPlayer != null) legendMusicPlayer.stop();
    }

    // ---- Arrêt des sons ----
    public void arreterGameOverMusique() { if (gameOver != null) gameOver.stop(); }
    public void arreterMusiqueDepart() { if (musiqueDepart != null) musiqueDepart.stop(); }
    public void arreterMusiqueDeVictoire() { if (musiqueDeVictoire != null) musiqueDeVictoire.stop(); }

    // ---- Contrôles généraux ----
    public void setSonActif(boolean actif) { sonActif = actif; }
    public void setMusiqueActif(boolean actif) {
        musiqueActif = actif;
        if (!musiqueActif) {
            arreterMusique();
            arreterLegendMusic();
        }
    }
}
