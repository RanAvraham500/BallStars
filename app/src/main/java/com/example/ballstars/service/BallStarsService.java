package com.example.ballstars.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.ballstars.R;
import com.example.ballstars.game.coins.Obstacle;

public class BallStarsService extends Service {
    private MediaPlayer backgroundMusicPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.angry_birds_bg_music);
        backgroundMusicPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasCategory("background_music")) {
            if (backgroundMusicPlayer != null) {
                float volume = intent.getFloatExtra("volume", 1f);
                Log.i("volume1", "volume: " + volume);
                backgroundMusicPlayer.setVolume(volume, volume);
                backgroundMusicPlayer.start();
            }
        } else {
            if (backgroundMusicPlayer != null) {
                if (backgroundMusicPlayer.isPlaying()) {
                    backgroundMusicPlayer.pause();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
