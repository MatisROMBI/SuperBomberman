package com.bomberman.model;

import com.bomberman.Main;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Music {

    private boolean sonActif = true;   // activer ou désactiver tous les sons
    private boolean musiqueActif = true; // activer ou désactiver la musique

    private AudioClip explosionClip;
    private MediaPlayer musiquePlayer;
    private AudioClip gameOver;
    private AudioClip musiqueDepart;
    private AudioClip musiqueDeVictoire;

    public Music() {
        explosionClip = new AudioClip(getClass().getResource("/sons/explosion.mp3").toExternalForm());

        musiqueDepart = new AudioClip(getClass().getResource("/sons/MusiqueDeDepart.mp3").toExternalForm());

        gameOver = new AudioClip(getClass().getResource("/sons/est-ce-que-tu-veux-abandonner-made-with-Voicemod.mp3").toExternalForm());

        musiqueDeVictoire = new AudioClip(getClass().getResource("/sons/MusiqueDeVictoire.mp3").toExternalForm());

        Media InGameMusic = new Media(getClass().getResource("/sons/MusiqueMonde3.mp3").toExternalForm());

        musiquePlayer = new MediaPlayer(InGameMusic);
        musiquePlayer.setCycleCount(MediaPlayer.INDEFINITE); // boucle la musique
    }

    // Méthode pour jouer le son explosion si le son est activé
    public void jouerExplosion() {
        if (sonActif) {
            explosionClip.play();
        }
    }

    // Méthode pour démarrer la musique de fond si la musique est activée
    public void demarrerMusique() {
        if (musiqueActif) {
            musiquePlayer.play();
        }
    }

    public void demarrerMusiqueDepart() {
        if (musiqueActif) {
            musiqueDepart.play();
        }
    }

    public void demarrerGameOverMusique() {
        if (musiqueActif) {
            gameOver.play();
        }
    }

    public void demarrerMusiqueDeVictoire() {
        if (musiqueActif) {
            musiqueDeVictoire.play();
        }
    }

    // Méthode pour arrêter la musique
    public void arreterMusique() {
        musiquePlayer.pause();
    }

    public void arreterGameOverMusique() {
        gameOver.stop();
    }

    public void arreterMusiqueDepart() {
        musiqueDepart.stop();
    }

    public void arreterMusiqueDeVictoire() {
        musiqueDeVictoire.stop();
    }

    // Pour activer/désactiver les sons
    public void setSonActif(boolean actif) {
        sonActif = actif;
    }

    // Pour activer/désactiver la musique
    public void setMusiqueActif(boolean actif) {
        musiqueActif = actif;
        if (!musiqueActif) {
            arreterMusique();
        }
    }

}