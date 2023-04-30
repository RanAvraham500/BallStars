package com.example.ballstars.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.ballstars.R;
import com.example.ballstars.dialogs.GameDialog;
import com.example.ballstars.firebase.User;
import com.example.ballstars.game.coins.CoinsSurfaceView;
import com.example.ballstars.game.maze.MazeSurfaceView;

public class GameActivity extends AppCompatActivity implements BallSurfaceView.OnGameFinishListener, View.OnClickListener {
    private User user;
    private BallSurfaceView customSurfaceView;
    private GameEffects gameEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        overridePendingTransition(0, 0); //no enter animation

        user = (User) getIntent().getSerializableExtra("user");

        gameEffects = new GameEffects(this, user.getUserSettings());

        ViewGroup surfaceViewParent = findViewById(R.id.surfaceViewParent);

        switch (user.getChosenLevel()) {
            case "Coins" -> {
                customSurfaceView = new CoinsSurfaceView(this, user.getChosenSkin(), gameEffects);
            }
            case "Maze" -> {
                customSurfaceView = new MazeSurfaceView(this, user.getChosenSkin(), gameEffects);
            }
            default -> {
                throw new IllegalArgumentException("Level name isn't compatible");
            }
        }
        surfaceViewParent.addView(customSurfaceView);
    }

    @Override
    public void onGameFinish() {
        GameDialog.display(getSupportFragmentManager(), GameDialog.GameDialogType.END, customSurfaceView, user);
    }

    @Override
    public void onClick(View view) {
        GameDialog.display(getSupportFragmentManager(), GameDialog.GameDialogType.PAUSE, customSurfaceView, user);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameEffects.destroy();;
    }
}