package com.bomberman.model;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Music {

    private boolean sonActif = true;        // Activer/désactiver tous les sons (FX)
    private boolean musiqueActif = true;    // Activer/désactiver la musique (BGM)

    private AudioClip explosionClip;
    private AudioClip gameOver;
    private AudioClip musiqueDepart;
    private AudioClip musiqueDeVictoire;

    private MediaPlayer musiquePlayer;          // Musique normale
    private MediaPlayer legendMusicPlayer;      // Musique mode LEGEND

    public Music() {
        // Sons courts (FX)
        explosionClip = new AudioClip(getClass().getResource("/sons/explosion.mp3").toExternalForm());
        musiqueDepart = new AudioClip(getClass().getResource("/sons/MusiqueDeDepart.mp3").toExternalForm());
        gameOver = new AudioClip(getClass().getResource("/sons/est-ce-que-tu-veux-abandonner-made-with-Voicemod.mp3").toExternalForm());
        musiqueDeVictoire = new AudioClip(getClass().getResource("/sons/MusiqueDeVictoire.mp3").toExternalForm());

        // Musique de fond "classique"
        Media InGameMusic = new Media(getClass().getResource("/sons/MusiqueMonde3.mp3").toExternalForm());
        musiquePlayer = new MediaPlayer(InGameMusic);
        musiquePlayer.setCycleCount(MediaPlayer.INDEFINITE); // Boucle

        // Musique mode LEGEND (attention au nom du fichier !)
        Media legendMusic = new Media(getClass().getResource("/sons/Super Bomberman - World 6 (SNES OST).mp3").toExternalForm());
        legendMusicPlayer = new MediaPlayer(legendMusic);
        legendMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Boucle
    }

    // ---- Sons FX ----
    public void jouerExplosion() {
        if (sonActif) explosionClip.play();
    }

    public void demarrerGameOverMusique() {
        if (musiqueActif) gameOver.play();
    }

    public void demarrerMusiqueDepart() {
        if (musiqueActif) musiqueDepart.play();
    }

    public void demarrerMusiqueDeVictoire() {
        if (musiqueActif) musiqueDeVictoire.play();
    }

    // ---- Musiques de fond (BGM) ----
    public void demarrerMusique() {
        if (musiqueActif) musiquePlayer.play();
    }

    public void arreterMusique() {
        musiquePlayer.pause();
    }

    // ---- Musique LEGEND ----
    public void demarrerLegendMusic() {
        if (musiqueActif) legendMusicPlayer.play();
    }

    public void arreterLegendMusic() {
        legendMusicPlayer.stop();
    }

    // ---- Arrêt des sons ----
    public void arreterGameOverMusique() { gameOver.stop(); }
    public void arreterMusiqueDepart()   { musiqueDepart.stop(); }
    public void arreterMusiqueDeVictoire() { musiqueDeVictoire.stop(); }

    // ---- Activation/désactivation ----
    public void setSonActif(boolean actif) { sonActif = actif; }
    public void setMusiqueActif(boolean actif) {
        musiqueActif = actif;
        if (!musiqueActif) {
            arreterMusique();
            arreterLegendMusic();
        }
    }
}
