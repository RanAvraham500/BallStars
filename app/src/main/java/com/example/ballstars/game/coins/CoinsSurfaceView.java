package com.example.ballstars.game.coins;

import static com.example.ballstars.game.Ball.RADIUS;
import static com.example.ballstars.game.coins.Obstacle.OBSTACLE_RADIUS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


import com.example.ballstars.game.BallSurfaceView;
import com.example.ballstars.game.GameClock;
import com.example.ballstars.game.GameEffects;

import java.util.concurrent.CopyOnWriteArrayList;

@SuppressLint("ViewConstructor")
public class CoinsSurfaceView extends BallSurfaceView implements GameClock.ClockListener, Obstacle.OnCollisionActions {
    private final Activity activity;
    private Paint clockPaint, scorePaint;
    private final CopyOnWriteArrayList<Obstacle> obstacles = new CopyOnWriteArrayList<>();
    private int score = 0;
    public CoinsSurfaceView(Activity activity, int skinResource, GameEffects gameEffects) {
        super(activity, skinResource, gameEffects);

        this.activity = activity;

        gameClock = GameClock.createTimer(this, 60);

        setUpPaints();

        Obstacle.frameHeight = SCREEN_BOTTOM - (3.5f * OBSTACLE_RADIUS);
        Obstacle.frameWidth = SCREEN_RIGHT - (2f * OBSTACLE_RADIUS);
    }

    private void setUpPaints() {
        clockPaint = new Paint();
        clockPaint.setTextSize(100);
        clockPaint.setTextAlign(Paint.Align.CENTER);
        clockPaint.setColor(Color.RED);
        //clockPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.main_variant, getContext().getTheme()));

        scorePaint = new Paint();
        scorePaint.setTextSize(60);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setColor(Color.RED);
    }

    @Override
    protected void drawGame(Canvas canvas) {
        obstacles.forEach(obstacle -> drawObstacle(obstacle, canvas));
    }
    private boolean checkCollision(Obstacle obstacle) {
        return distance(obstacle.getX(), obstacle.getY()) < RADIUS + OBSTACLE_RADIUS;
    }
    private float distance(float obstacleX, float obstacleY) {
        return (float) Math.sqrt(Math.pow(ballX - obstacleX, 2) + Math.pow(ballY - obstacleY, 2));
    }
    private void drawObstacle(Obstacle obstacle, Canvas canvas) {
        if (obstacle.reduceTTL(1)) {
            if (checkCollision(obstacle)) {
                //there is collision
                gameEffects.playCollisionSound(obstacle.getType());
                obstacle.onCollision();
                obstacles.remove(obstacle);
            } else {
                canvas.drawBitmap(
                        obstacle.getBitmap(),
                        obstacle.getX(), obstacle.getY(),
                        null
                );
            }
        } else {
            //TTL ran out
            obstacles.remove(obstacle);
        }
    }

    @Override
    protected void drawText(Canvas canvas) {
        int clockX = (canvas.getWidth() / 2);
        int clockY = 150;

        canvas.drawText(gameTime, clockX, clockY, clockPaint);
        canvas.drawText("Score: " + score, clockX, clockY + 75, scorePaint);
    }

    @Override
    public void restart() {
        super.restart();
        score = 0;
        obstacles.removeAll(obstacles);
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void onSecondTickClock(String time) {
        gameTime = time;
        obstacles.add(new Obstacle(activity, this));
    }

    @Override
    public void onFinishClock() {
        listener.onGameFinish();
        gameOn = false;
    }

    @Override
    public void onCollisionCoin() {
        //add one to score
        score++;
    }

    @Override
    public void onCollisionBomb() {
        //reduce five seconds from timer
        gameClock.updateOnCollision(-5);
    }

    @Override
    public void onCollisionTime() {
        //add ten seconds to timer
        gameClock.updateOnCollision(10);
    }
}
