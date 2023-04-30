package com.example.ballstars.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.ballstars.CustomView;
import com.example.ballstars.R;

public class LoadingDialog extends DialogFragment {
    private static final String TAG = "loading_dialog";
    private CustomView<TextView> tvLoadingMessage;
    private CustomView<ProgressBar> pbLoading;
    private CustomView<ImageView> ivSuccess, ivFailure;

    public LoadingDialog() {}

    public static LoadingDialog display(FragmentManager fragmentManager) {
        LoadingDialog dialog = new LoadingDialog();
        dialog.show(fragmentManager, TAG);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, com.google.android.material.R.style.ThemeOverlay_Material3_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_loading, container, false);
        tvLoadingMessage = new CustomView<>(view.findViewById(R.id.tvLoadingMessage));
        pbLoading = new CustomView<>(view.findViewById(R.id.pbLoading));
        ivSuccess = new CustomView<>(view.findViewById(R.id.ivSuccess));
        ivSuccess.disappear();
        ivFailure = new CustomView<>(view.findViewById(R.id.ivFailure));
        ivFailure.disappear();
        return view;
    }

    public void success(OnLoadingFinishedListener onLoadingFinishedListener) {
        tvLoadingMessage.disappear();
        pbLoading.disappear();
        ivSuccess.getView().setVisibility(View.VISIBLE);
        ivSuccess.show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoadingFinishedListener.onLoadingFinished();
                LoadingDialog.this.dismiss();
            }
        }, 500);
    }

    public void failure() {
        tvLoadingMessage.disappear();
        pbLoading.disappear();
        ivFailure.getView().setVisibility(View.VISIBLE);
        ivFailure.show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadingDialog.this.dismiss();
            }
        }, 500);
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = requireDialog().getWindow().getAttributes();
        params.width = 600;
        params.height = 600;
        requireDialog().getWindow().setAttributes(params);
        requireDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requireDialog().setCancelable(false);
    }

    public interface OnLoadingFinishedListener {
        void onLoadingFinished();
    }
}