package com.example.ballstars.dialogs.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.ballstars.CustomView;
import com.example.ballstars.R;
import com.example.ballstars.firebase.CommunicateWithFirebase;
import com.example.ballstars.firebase.User;
import com.example.ballstars.routing.RoutingActivity;
import com.example.ballstars.service.BallStarsService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class SettingsDialog extends DialogFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "settings_dialog";
    private User user;
    private CustomView<ImageButton> btnBackgroundMusic, btnSoundEffects, btnVibration;
    private SeekBar sbBackgroundMusic, sbSoundEffects;
    private boolean backgroundMusicEnabled, gameSoundsEnabled, vibrationEnabled;
    private int backgroundMusicVolume, gameSoundsVolume;
    private ViewGroup dialogLayout;
    private TextView tvBackgroundMusicVolume, tvSoundEffectsVolume;

    public SettingsDialog() {}

    public SettingsDialog(User user) {
        this.user = user;
        backgroundMusicEnabled = user.getUserSettings().isBackgroundMusicEnabled();
        gameSoundsEnabled = user.getUserSettings().isGameSoundEffectsEnabled();
        vibrationEnabled = user.getUserSettings().isVibrationEnabled();

        backgroundMusicVolume = (int) (user.getUserSettings().getBackgroundMusicVolume() * 100);
        gameSoundsVolume = (int) (user.getUserSettings().getGameSoundEffectsVolume() * 100);
    }

    public static SettingsDialog display(FragmentManager fragmentManager, User user) {
        SettingsDialog dialog = new SettingsDialog(user);
        dialog.show(fragmentManager, TAG);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.ThemeOverlay_Material);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_settings, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        toolbar.inflateMenu(R.menu.settings_save);
        toolbar.setOnMenuItemClickListener(item -> {
            saveData();
            return true;
        });
        toolbar.setNavigationOnClickListener(v -> closeAlert());

        btnBackgroundMusic = new CustomView<>(requireActivity(), view.findViewById(R.id.btnBackgroundMusic), this);
        btnSoundEffects = new CustomView<>(requireActivity(), view.findViewById(R.id.btnSoundEffects), this);
        btnVibration = new CustomView<>(requireActivity(), view.findViewById(R.id.btnVibration), this);


        sbBackgroundMusic = view.findViewById(R.id.sbBackgroundMusic);
        sbBackgroundMusic.setProgress(backgroundMusicVolume);
        sbBackgroundMusic.setOnSeekBarChangeListener(this);

        sbSoundEffects = view.findViewById(R.id.sbSoundEffects);
        sbSoundEffects.setProgress(gameSoundsVolume);
        sbSoundEffects.setOnSeekBarChangeListener(this);

        tvBackgroundMusicVolume = view.findViewById(R.id.tvBackgroundMusicVolume);
        tvBackgroundMusicVolume.setText(backgroundMusicVolume + "");

        tvSoundEffectsVolume = view.findViewById(R.id.tvSoundEffectsVolume);
        tvSoundEffectsVolume.setText(gameSoundsVolume + "");

        dialogLayout = view.findViewById(R.id.dialogLayout);

        setUpSettingsButtons();

        return view;
    }

    private void saveData() {
        user.getUserSettings().setBackgroundMusicEnabled(backgroundMusicEnabled);
        user.getUserSettings().setBackgroundMusicVolume(backgroundMusicVolume / 100f);
        user.getUserSettings().setGameSoundEffectsEnabled(gameSoundsEnabled);
        user.getUserSettings().setGameSoundEffectsVolume(gameSoundsVolume / 100f);
        user.getUserSettings().setVibrationEnabled(vibrationEnabled);

        Intent intent = new Intent(requireActivity(), BallStarsService.class);
        if (backgroundMusicEnabled) {
            intent.addCategory("background_music");
            intent.putExtra("volume", backgroundMusicVolume / 100f);
        } else {
            intent.addCategory("no_background_music");
        }
        requireActivity().startService(intent);

        CommunicateWithFirebase firebase = new CommunicateWithFirebase(requireActivity(), getParentFragmentManager());
        firebase.updateUser(user, this::dismiss);
    }

    public void setUpSettingsButtons() {
        if (!backgroundMusicEnabled) {
            sbBackgroundMusic.setEnabled(false);
            addDisabledIconLayer(btnBackgroundMusic.getView());
        } else {
            btnBackgroundMusic.getView().setImageResource(R.drawable.music_icon);
        }

        if (!gameSoundsEnabled) {
            sbSoundEffects.setEnabled(false);
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
                sbBackgroundMusic.setEnabled(false);
                addDisabledIconLayer(btnBackgroundMusic.getView());
                preformSnackBar("Music Disabled");
            } else {
                sbBackgroundMusic.setEnabled(true);
                btnBackgroundMusic.getView().setImageResource(R.drawable.music_icon);
                preformSnackBar("Music Enabled");
            }
            backgroundMusicEnabled = !backgroundMusicEnabled;
        }
        else if (btnSoundEffects.isView(v)) {
            if (gameSoundsEnabled) {
                sbSoundEffects.setEnabled(false);
                addDisabledIconLayer(btnSoundEffects.getView());
                preformSnackBar("Sound Effects Disabled");

            } else {
                sbSoundEffects.setEnabled(true);
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
    }

    private void addDisabledIconLayer(ImageButton btn) {
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

    private void closeAlert() {
        new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.Theme_Material3_Light_Dialog_Alert)
                .setTitle("Close Settings")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SettingsDialog.this.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void preformSnackBar(String message) {
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
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER | Gravity.BOTTOM;
        lp.width = 600;
        view.setLayoutParams(lp);
        snackbar.show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar.equals(sbBackgroundMusic)) {
            backgroundMusicVolume = i;
            tvBackgroundMusicVolume.setText(backgroundMusicVolume + "");
        } else {
            gameSoundsVolume = i;
            tvSoundEffectsVolume.setText(gameSoundsVolume + "");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
