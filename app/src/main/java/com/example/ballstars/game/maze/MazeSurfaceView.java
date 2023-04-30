package com.example.ballstars.game.maze;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import com.example.ballstars.R;
import com.example.ballstars.game.BallSurfaceView;
import com.example.ballstars.game.GameClock;
import com.example.ballstars.game.GameEffects;
import com.example.ballstars.game.coins.Obstacle;

import java.util.concurrent.CopyOnWriteArrayList;

@SuppressLint("ViewConstructor")
public class MazeSurfaceView extends BallSurfaceView implements GameClock.ClockListener {
    public static float TICK = 4; //portion updated
    public static final int SPACING = 150;
    public static float LENGTH = 1000;
    private final int MAX_Y = 750, MIN_Y = 250;
    private final Paint mazePaint, allowedRectPaint;
    private final CopyOnWriteArrayList<TrapezeHelper> trapezeHelpers = new CopyOnWriteArrayList<>();

    public MazeSurfaceView(Activity activity, int skinResource, GameEffects gameEffects) {
        super(activity, skinResource, gameEffects);
        trapezeHelpers.add(new TrapezeHelper(
                new FPoint(SCREEN_RIGHT - TICK, randomY()),
                new FPoint(SCREEN_RIGHT, randomY())
        ));


        gameClock = GameClock.createStopwatch(this);

        mazePaint = new Paint();
        mazePaint.setColor(ResourcesCompat.getColor(getResources(), R.color.main_color, getContext().getTheme()));


        allowedRectPaint = new Paint();
        allowedRectPaint.setColor(Color.TRANSPARENT);
    }

    private float randomY() {
        return (float) (MIN_Y + Math.random() * ((MAX_Y - MIN_Y) + 1));
    }

    @Override
    protected void drawGame(Canvas canvas) {
        boolean isIn = false;
        for (TrapezeHelper trapezeHelper : trapezeHelpers) {
            FPoint currPoint2;
            if (trapezeHelper.isDone()) {
                currPoint2 = trapezeHelper.getPoint2();
                if (trapezeHelpers.indexOf(trapezeHelper) == trapezeHelpers.size() - 1) {
                    addNew(currPoint2);
                }
            } else {
                currPoint2 = trapezeHelper.getTempPoint();
            }
            //bottom
            Path bottomTrapeze = new Path();
            bottomTrapeze.moveTo(trapezeHelper.getPoint1().x, trapezeHelper.getPoint1().y + SPACING);
            bottomTrapeze.lineTo(trapezeHelper.getPoint1().x, SCREEN_BOTTOM);
            bottomTrapeze.lineTo(currPoint2.x, SCREEN_BOTTOM);
            bottomTrapeze.lineTo(currPoint2.x, currPoint2.y + SPACING);
            bottomTrapeze.close();
            canvas.drawPath(bottomTrapeze, mazePaint);

            //top
            Path topTrapeze = new Path();
            topTrapeze.moveTo(trapezeHelper.getPoint1().x, trapezeHelper.getPoint1().y - SPACING);
            topTrapeze.lineTo(trapezeHelper.getPoint1().x, SCREEN_TOP);
            topTrapeze.lineTo(currPoint2.x, SCREEN_TOP);
            topTrapeze.lineTo(currPoint2.x, currPoint2.y - SPACING);
            topTrapeze.close();
            canvas.drawPath(topTrapeze, mazePaint);


            canvas.drawPath(trapezeHelper.getAllowedPath(), allowedRectPaint);
            if (gameOn) {
                trapezeHelper.updateRectF();
                if (trapezeHelper.isDone()) {
                    trapezeHelper.getPoint1().x -= TICK;
                    trapezeHelper.getPoint2().x -= TICK;
                } else {
                    trapezeHelper.advanceTemp();
                    trapezeHelper.getPoint1().x -= TICK;
                }
                if (trapezeHelper.getPoint2().x == 0) {
                    trapezeHelpers.remove(trapezeHelper);
                }

                if (checkIfInBounds(trapezeHelper)) {
                    isIn = true;
                }
            }
        }
        if (gameOn) {
            if (isIn || ballX <= trapezeHelpers.get(0).getPoint1().x + 10) {
                Log.d("outOfBoundsChecker", "in!");
            } else {
                Log.e("outOfBoundsChecker", "out!");
                gameOn = false;
            }
            if (!gameOn) {
                gameEffects.playCollisionSound(Obstacle.ObstacleType.BOMB);
                TICK = 4;
                listener.onGameFinish();
            }
        }
    }

    private void addNew(FPoint point1) {
        if (TICK <= 20) {
            TICK += 0.1f;
        }

        trapezeHelpers.add(new TrapezeHelper(
                new FPoint(point1.x - TICK - 0.5f, point1.y),
                new FPoint(SCREEN_RIGHT, randomY())
        ));
    }

    private boolean checkIfInBounds(TrapezeHelper trapezeHelper) {
        RectF allowedRectF = trapezeHelper.getAllowedRectF();
        Region r = new Region();
        r.setPath(
                trapezeHelper.getAllowedPath(),
                new Region(
                        (int)allowedRectF.left,
                        (int)allowedRectF.top,
                        (int)allowedRectF.right,
                        (int)allowedRectF.bottom
                )
        );

        return r.contains((int) ballX, (int) ballY);
    }

    @Override
    protected void drawText(Canvas canvas) {
        float clockX = canvas.getWidth() / 2f;
        float clockY = 150;
        canvas.drawText(gameTime, clockX, clockY, clockTextPaint);
        canvas.drawText("Speed: " + ((int)(TICK * 10))/10f, clockX, clockY + 75, belowClockTextPaint);

    }

    @Override
    public void restart() {
        super.restart();
        TICK = 4;
        trapezeHelpers.removeAll(trapezeHelpers);

        trapezeHelpers.add(new TrapezeHelper(
                new FPoint(SCREEN_RIGHT - TICK, randomY()),
                new FPoint(SCREEN_RIGHT, randomY())
        ));
    }

    @Override
    public int getScore() {
        return gameClock.translateToScore();
    }

    @Override
    public void onSecondTickClock(String time) {
        gameTime = time;
    }

    @Override
    public void onFinishClock() {

    }
}
