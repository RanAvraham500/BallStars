package com.example.ballstars.dialogs.settings;

import java.io.Serializable;

public class Settings implements Serializable {
    private boolean backgroundMusicEnabled;
    private float backgroundMusicVolume;
    private boolean gameSoundEffectsEnabled;
    private float gameSoundEffectsVolume;
    private boolean vibrationEnabled;

    public Settings() {
        backgroundMusicEnabled = true;
        backgroundMusicVolume = 1;

        gameSoundEffectsEnabled = true;
        gameSoundEffectsVolume = 1;

        vibrationEnabled = true;
    }

    public boolean isBackgroundMusicEnabled() {
        return backgroundMusicEnabled;
    }

    public void setBackgroundMusicEnabled(boolean backgroundMusicEnabled) {
        this.backgroundMusicEnabled = backgroundMusicEnabled;
    }

    public float getBackgroundMusicVolume() {
        return backgroundMusicVolume;
    }

    public void setBackgroundMusicVolume(float backgroundMusicVolume) {
        this.backgroundMusicVolume = backgroundMusicVolume;
    }

    public boolean isGameSoundEffectsEnabled() {
        return gameSoundEffectsEnabled;
    }

    public void setGameSoundEffectsEnabled(boolean gameSoundEffectsEnabled) {
        this.gameSoundEffectsEnabled = gameSoundEffectsEnabled;
    }

    public float getGameSoundEffectsVolume() {
        return gameSoundEffectsVolume;
    }

    public void setGameSoundEffectsVolume(float gameSoundEffectsVolume) {
        this.gameSoundEffectsVolume = gameSoundEffectsVolume;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
    }
}
