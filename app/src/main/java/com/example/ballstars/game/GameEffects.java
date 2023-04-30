package com.example.ballstars.game;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.ballstars.R;
import com.example.ballstars.dialogs.settings.Settings;
import com.example.ballstars.game.coins.Obstacle;

public class GameEffects {
    private final Context context;
    private SoundPool onCollisionSoundPool;
    private final int coinSoundId, bombSoundId, timeSoundId, startSoundId;
    private boolean gameSoundsEffectsEnabled, vibrationEnabled;
    private float gameSoundsEffectsVolume;

    public GameEffects(Context context, Settings userSettings) {
        this.context = context;
        onCollisionSoundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .build();

        coinSoundId = onCollisionSoundPool.load(context, R.raw.coin_sound, 1);
        bombSoundId = onCollisionSoundPool.load(context, R.raw.bomb_sound, 1);
        timeSoundId = onCollisionSoundPool.load(context, R.raw.time_sound, 1);
        startSoundId = onCollisionSoundPool.load(context, R.raw.start_sound, 1);


        updateUserSettings(userSettings);
    }
    public void updateUserSettings(Settings userSettings) {
        gameSoundsEffectsEnabled = userSettings.isGameSoundEffectsEnabled();
        vibrationEnabled = userSettings.isVibrationEnabled();
        gameSoundsEffectsVolume = userSettings.getGameSoundEffectsVolume();
    }
    public void playStartSound() {
        if (onCollisionSoundPool != null && gameSoundsEffectsEnabled) {
            vibrate();
            onCollisionSoundPool.play(startSoundId, gameSoundsEffectsVolume, gameSoundsEffectsVolume, 0, 0, 1);
        }
    }

    public void playCollisionSound(Obstacle.ObstacleType type) {
        if (onCollisionSoundPool != null && gameSoundsEffectsEnabled) {
            vibrate();
            switch (type) {
                case COIN -> {
                    playSound(coinSoundId);
                }
                case BOMB -> {
                    playSound(bombSoundId);
                }
                case TIME -> {
                    playSound(timeSoundId);
                }
            }
        }
    }
    private void playSound(int soundId) {
        onCollisionSoundPool.play(soundId, gameSoundsEffectsVolume, gameSoundsEffectsVolume, 0, 0, 1);
    }

    private void vibrate() {
        if (vibrationEnabled) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                VibrationEffect effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            }
        }
    }

    public void destroy() {
        if (onCollisionSoundPool != null) {
            onCollisionSoundPool.release();
            onCollisionSoundPool = null;
        }
    }
}
