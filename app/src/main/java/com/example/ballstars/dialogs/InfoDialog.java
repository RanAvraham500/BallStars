package com.example.ballstars.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.ballstars.R;

public class InfoDialog extends DialogFragment {
    private static final String TAG = "info_dialog";
    private final InfoDialogType type;
    public InfoDialog() {
        type = InfoDialogType.HOME;
    }

    public InfoDialog(InfoDialogType type) {
        this.type = type;
    }

    public static InfoDialog display(FragmentManager fragmentManager, InfoDialogType type) {
        InfoDialog dialog = new InfoDialog(type);
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
        View view = inflater.inflate(R.layout.fragment_dialog_info, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView tvInfoHeader1 = view.findViewById(R.id.tvInfoHeader1),
                tvInfoContent1 = view.findViewById(R.id.tvInfoContent1),
                tvInfoHeader2 = view.findViewById(R.id.tvInfoHeader2),
                tvInfoContent2 = view.findViewById(R.id.tvInfoContent2);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        switch (type) {
            case HOME -> {
                toolbar.setTitle("Home Information Page");

                tvInfoHeader1.setText("Navigation Page");
                tvInfoContent1.setText(getResources().getString(R.string.info_navigation));

                tvInfoHeader2.setText("Game Rules");
                tvInfoContent2.setText(getResources().getString(R.string.info_game));
            }
            case REGISTRATION -> {
                toolbar.setTitle("Registration Information Page");

                tvInfoHeader1.setText("Sign In");
                tvInfoContent1.setText(getResources().getString(R.string.info_sign_in));

                tvInfoHeader2.setText("Sign Up");
                tvInfoContent2.setText(getResources().getString(R.string.info_sign_up));
            }
        }
        return view;
    }

    public enum InfoDialogType {
        REGISTRATION,
        HOME
    }
}
