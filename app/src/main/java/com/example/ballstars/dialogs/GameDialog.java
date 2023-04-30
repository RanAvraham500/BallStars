package com.example.ballstars.dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.ballstars.CustomView;
import com.example.ballstars.R;
import com.example.ballstars.firebase.CommunicateWithFirebase;
import com.example.ballstars.firebase.User;
import com.example.ballstars.game.BallSurfaceView;
import com.example.ballstars.game.coins.CoinsSurfaceView;
import com.example.ballstars.game.maze.MazeSurfaceView;
import com.example.ballstars.home.HomeNavigationActivity;
import com.example.ballstars.service.BallStarsService;
import com.google.android.material.snackbar.Snackbar;

public class GameDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "game_dialog";
    private CommunicateWithFirebase firebase;
    private LinearLayout dialogLayout;
    private TextView tvGameState, tvGameScore;
    private CustomView<ImageButton> btnBackgroundMusic, btnSoundEffects, btnVibration;
    private CustomView<AppCompatButton> btnResume, btnRestart, btnBack;
    private final GameDialogType type;
    private final BallSurfaceView customSurfaceView;
    private final User user;
    private boolean backgroundMusicEnabled, gameSoundsEnabled, vibrationEnabled;

    public GameDialog() {
        type = GameDialogType.PAUSE;
        customSurfaceView = null;
        user = null;
    }
    public GameDialog(GameDialogType type, BallSurfaceView customSurfaceView, User user) {
        this.type = type;
        this.customSurfaceView = customSurfaceView;
        customSurfaceView.pause();

        this.user = user;
        backgroundMusicEnabled = user.getUserSettings().isBackgroundMusicEnabled();
        gameSoundsEnabled = user.getUserSettings().isGameSoundEffectsEnabled();
        vibrationEnabled = user.getUserSettings().isVibrationEnabled();
    }

    public static GameDialog display(FragmentManager fragmentManager,
                                     GameDialogType type,
                                     BallSurfaceView customSurfaceView,
                                     User user) {
        GameDialog dialog = new GameDialog(type, customSurfaceView, user);
        dialog.show(fragmentManager, TAG);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, com.google.android.material.R.style.Base_Theme_AppCompat_Dialog);
        firebase = new CommunicateWithFirebase(requireActivity(), getParentFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_game, container, false);

        dialogLayout = view.findViewById(R.id.dialogLayout);

        tvGameState = view.findViewById(R.id.tvGameState);
        tvGameScore = view.findViewById(R.id.tvGameScore);
        tvGameScore.setText("Score: " + customSurfaceView.getScore());

        btnBackgroundMusic = new CustomView<>(requireActivity(), view.findViewById(R.id.btnBackgroundMusic), this);
        btnSoundEffects = new CustomView<>(requireActivity(), view.findViewById(R.id.btnSoundEffects), this);
        btnVibration = new CustomView<>(requireActivity(), view.findViewById(R.id.btnVibration), this);
        setUpSettingsButtons();

        btnBack = new CustomView<>(requireActivity(), view.findViewById(R.id.btnBack), this);
        btnRestart = new CustomView<>(requireActivity(), view.findViewById(R.id.btnRestart), this);
        btnResume = new CustomView<>(requireActivity(), view.findViewById(R.id.btnResume), this);

        switch (type) {
            case PAUSE -> {
                //game paused
                tvGameState.setText("Game Paused");
            }
            case END -> {
                //game ended
                tvGameState.setText("Game Ended");
                btnResume.getView().setVisibility(View.GONE);
            }
        }
        return view;
    }

    public void setUpSettingsButtons() {
        if (!backgroundMusicEnabled) {
            addDisabledIconLayer(btnBackgroundMusic.getView());
        } else {
            btnBackgroundMusic.getView().setImageResource(R.drawable.music_icon);
        }

        if (!gameSoundsEnabled) {
            addDisabledIconLayer(btnSoundEffects.getView());
        } else {
            btnSoundEffects.getView().setImageResource(R.drawable.game_icon);
        }

        if (!vibrationEnabled) {
            addDisabledIconLayer(btnVibration.getView());
        } else {
            btnVibration.getView().setImageResource(R.drawable.vibration_icon);
        }
    }

    @Override
    public void onClick(View v) {
        if (btnBackgroundMusic.isView(v)) {
            if (backgroundMusicEnabled) {
                addDisabledIconLayer(btnBackgroundMusic.getView());
                preformSnackBar("Music Disabled");
            } else {
                btnBackgroundMusic.getView().setImageResource(R.drawable.music_icon);
                preformSnackBar("Music Enabled");
            }
            backgroundMusicEnabled = !backgroundMusicEnabled;
        }
        else if (btnSoundEffects.isView(v)) {
            if (gameSoundsEnabled) {
                addDisabledIconLayer(btnSoundEffects.getView());
                preformSnackBar("Sound Effects Disabled");

            } else {
                btnSoundEffects.getView().setImageResource(R.drawable.game_icon);
                preformSnackBar("Sound Effects Enabled");
            }
            gameSoundsEnabled = !gameSoundsEnabled;

        }
        else if (btnVibration.isView(v)) {
            if (vibrationEnabled) {
                addDisabledIconLayer(btnVibration.getView());
                preformSnackBar("Vibration Disabled");
            } else {
                btnVibration.getView().setImageResource(R.drawable.vibration_icon);
                preformSnackBar("Vibration Enabled");
            }
            vibrationEnabled = !vibrationEnabled;
        }

        else if (btnResume.isView(v)){
            customSurfaceView.resume();
            dismiss();
        }
        else if (btnRestart.isView(v)) {
            LoadingDialog.OnLoadingFinishedListener listener = () -> {
                    customSurfaceView.restart();
                    GameDialog.this.dismiss();
            };

            if (customSurfaceView.getClass().equals(MazeSurfaceView.class)) {
                firebase.updateMazeHighScore(customSurfaceView.getScore(), listener, user);
            } else {
                firebase.updateCoinsHighScore(customSurfaceView.getScore(), listener, user);
            }
        }
        else { //btnBack
            //loadingDialog.success(() -> goToActivity(HomeNavigationActivity.class, user));
            LoadingDialog.OnLoadingFinishedListener listener = () -> {
                firebase.goToActivity(HomeNavigationActivity.class, user);
            };
            user.getUserSettings().setBackgroundMusicEnabled(backgroundMusicEnabled);
            user.getUserSettings().setGameSoundEffectsEnabled(gameSoundsEnabled);
            user.getUserSettings().setVibrationEnabled(vibrationEnabled);

            if (customSurfaceView.getClass().equals(CoinsSurfaceView.class)) {
                firebase.updateCoinsHighScore(customSurfaceView.getScore(), listener, user);
            } else {
                MazeSurfaceView.TICK = 4;
                firebase.updateMazeHighScore(customSurfaceView.getScore(), listener, user);
            }
        }
    }
    public void addDisabledIconLayer(ImageButton btn) {
        Drawable[] layers = new Drawable[2];
        layers[0] = btn.getDrawable();
        layers[1] = ResourcesCompat.getDrawable(
                requireActivity().getResources(),
                R.drawable.disabled_icon,
                requireActivity().getTheme()
        );
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        btn.setImageDrawable(layerDrawable);
    }

    public void preformSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(dialogLayout, message, 750);
        snackbar.setBackgroundTint(ResourcesCompat.getColor(
                requireActivity().getResources(),
                R.color.secondary_color,
                requireActivity().getTheme()));

        snackbar.setTextColor(ResourcesCompat.getColor(
                requireActivity().getResources(),
                R.color.main_color,
                requireActivity().getTheme()));

        View view = snackbar.getView();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER | Gravity.BOTTOM;
        lp.width = 600;
        view.setLayoutParams(lp);
        snackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = requireDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        requireDialog().getWindow().setAttributes(params);
        requireDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Intent intent = new Intent(requireActivity(), BallStarsService.class);
        if (backgroundMusicEnabled) {
            intent.addCategory("background_music");
            intent.putExtra("volume", user.getUserSettings().getBackgroundMusicVolume());
        } else {
            intent.addCategory("no_background_music");
        }
        requireActivity().startService(intent);

        user.getUserSettings().setBackgroundMusicEnabled(backgroundMusicEnabled);
        user.getUserSettings().setGameSoundEffectsEnabled(gameSoundsEnabled);
        user.getUserSettings().setVibrationEnabled(vibrationEnabled);
        customSurfaceView.updateGameEffects(user.getUserSettings());

        super.onDismiss(dialog);
    }

    public enum GameDialogType {
        PAUSE, END
    }
}

