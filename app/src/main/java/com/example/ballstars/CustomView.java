package com.example.ballstars;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

public class CustomView<T extends View> implements View.OnTouchListener {
    private final T mView;
    private Animation animClick;
    private boolean visible = true;

    public CustomView(T view) {
        mView = view;
    }

    public CustomView(Activity context, T button, View.OnClickListener onClickListener) {
        mView = button;
        mView.setOnClickListener(onClickListener);
        if (mView instanceof AppCompatButton || mView instanceof ImageButton) {
            mView.setOnTouchListener(this);
            animClick = AnimationUtils.loadAnimation(context, R.anim.button_bounce);
        }

    }
    public void disappear() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mView, "alpha", 1f, 0);
        fadeOut.setDuration(150);
        fadeOut.start();
        visible = false;
    }

    public void show() {
        if (!visible) {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mView, "alpha", 0f, 1);
            fadeOut.setDuration(150);
            fadeOut.start();
            visible = true;
        }
    }

    public T getView() {
        return mView;
    }

    public boolean isView(View view) {
        return mView.equals(view);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                mView.startAnimation(animClick);
                mView.performClick();
                return true;
        }
        return true;
    }

}
