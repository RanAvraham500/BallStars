package com.example.ballstars.game;

import static com.example.ballstars.game.Ball.RADIUS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.example.ballstars.R;
import com.example.ballstars.dialogs.settings.Settings;

@SuppressLint("ViewConstructor")
public abstract class BallSurfaceView extends SurfaceView implements Ball.CordsListener, Runnable, SurfaceHolder.Callback {
    private final SurfaceHolder holder;
    private Canvas canvas;
    protected OnGameFinishListener listener;
    private boolean running = true;
    protected boolean gameOn = true;
    protected final Ball ball;
    protected final float SCREEN_TOP = 0, SCREEN_BOTTOM, SCREEN_LEFT = 0, SCREEN_RIGHT;
    protected float ballX, ballY;
    private final Bitmap ballBitmap;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final GameClock preGameCountDown;
    private String preGameTime = "";
    protected GameClock gameClock;
    protected String gameTime = "";
    private boolean isCountDownDone = false;
    protected final Paint clockTextPaint, belowClockTextPaint;
    protected GameEffects gameEffects;

    public BallSurfaceView(Activity activity, int skinResource, GameEffects gameEffects) {
        super(activity);

        this.gameEffects = gameEffects;

        listener = (OnGameFinishListener) activity;

        holder = getHolder();
        holder.addCallback(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        SCREEN_BOTTOM = displayMetrics.heightPixels;
        SCREEN_RIGHT = displayMetrics.widthPixels;

        ball = new Ball(activity, skinResource, this, SCREEN_RIGHT - (2f * RADIUS), SCREEN_BOTTOM - (3f * RADIUS));
        ballX = ball.getX();
        ballY = ball.getY();
        ballBitmap = ball.getBallBitmap();

        preGameCountDown = GameClock.createTimer(new GameClock.ClockListener() {
            @Override
            public void onSecondTickClock(String time) {
                preGameTime = time;
            }

            @Override
            public void onFinishClock() {
                gameEffects.playStartSound();
                isCountDownDone = true;
                gameClock.start();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOnClickListener((OnClickListener) activity);
                    }
                });
            }
        }, 3);
        preGameCountDown.start();

        clockTextPaint = new Paint();
        clockTextPaint.setTextSize(100);
        clockTextPaint.setTextAlign(Paint.Align.CENTER);
        clockTextPaint.setColor(Color.RED);

        belowClockTextPaint = new Paint();
        belowClockTextPaint.setTextSize(60);
        belowClockTextPaint.setTextAlign(Paint.Align.CENTER);
        belowClockTextPaint.setColor(Color.RED);
    }

    @Override
    public void run() {
        if (running) {
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                canvas.save();
                canvas.drawColor(ResourcesCompat.getColor(getResources(), R.color.secondary_color, getContext().getTheme()));
                if (!isCountDownDone) {
                    drawPreGameClock();
                } else {
                    ball.start();
                    drawGame(canvas);
                    drawText(canvas);
                }
                drawBall();
                synchronized (this) {
                    if (holder.getSurface().isValid()) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }

            }
        }
        handler.postDelayed(this, 1);
    }

    @Override
    public void onCordChange(float x, float y) {
        if (gameOn) {
            ballX = x;
            ballY = y;
        }
    }

    protected abstract void drawGame(Canvas canvas);

    protected abstract void drawText(Canvas canvas);

    private void drawPreGameClock() {
        float clockX = canvas.getWidth() / 2f;
        float clockY = 150;
        canvas.drawText(preGameTime, clockX, clockY, clockTextPaint);
        canvas.drawText("Get Ready!", clockX, clockY + 75, belowClockTextPaint);
    }
    private void drawBall() {
        canvas.drawBitmap(
                ballBitmap,
                ballX, ballY,
                null);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        running = true;
        run();
        ball.register();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        //holder.removeCallback(this);
        running = false;
        handler.removeCallbacks(this);
        ball.unregister();
    }

    public void pause() {
        gameOn = false;
        gameClock.pause();
        ball.pause();
    }
    public void resume() {
        gameOn = true;
        gameClock.resume();
        ball.resume();
    }
    public void restart() {
        gameEffects.playStartSound();
        ball.reset();
        gameClock.reset();
        ballX = SCREEN_RIGHT / 2f;
        ballY = SCREEN_BOTTOM / 2f;
        resume();
    }
    public abstract int getScore();

    public void updateGameEffects(Settings userSettings) {
        gameEffects.updateUserSettings(userSettings);
    }


    public interface OnGameFinishListener {
        void onGameFinish();
    }
}