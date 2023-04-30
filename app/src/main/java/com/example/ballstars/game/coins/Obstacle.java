package com.example.ballstars.game.coins;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.ballstars.R;

public class Obstacle {
    private final ObstacleType type;
    private final OnCollisionActions collisionActions;
    private final Bitmap obstacleBitmap;
    public final static float OBSTACLE_RADIUS = 40;
    private float millisTTL; //millis to live (time.to.live)
    public static float frameWidth, frameHeight;
    private final float obstacleX, obstacleY;

    public Obstacle(Activity activity, OnCollisionActions collisionActions) {
        type = randomType();

        this.collisionActions = collisionActions;

        obstacleBitmap = switch (type) {
            case COIN -> Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(activity.getResources(), R.drawable.coin),
                    (int) OBSTACLE_RADIUS * 2,
                    (int) OBSTACLE_RADIUS * 2,
                    false
            );
            case BOMB -> Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(activity.getResources(), R.drawable.bomb),
                    (int) OBSTACLE_RADIUS * 2,
                    (int) OBSTACLE_RADIUS * 2,
                    false
            );
            case TIME -> Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(activity.getResources(), R.drawable.hour_glass),
                    (int) OBSTACLE_RADIUS * 2,
                    (int) OBSTACLE_RADIUS * 2,
                    false
            );
        };

        millisTTL = switch (type) {
            case COIN -> 1500;
            case BOMB -> 2000;
            case TIME -> 1000;
        };

        obstacleX = randomX();
        obstacleY = randomY();
    }
    private final double COIN_PER = 0.75, BOMB_PER = 0.15, TIME_PER = 0.15;
    private ObstacleType randomType() {
        double random = Math.random();
        if (random < COIN_PER) {
            //coin
            return ObstacleType.COIN;
        } else if (random < BOMB_PER + COIN_PER) {
            //bomb
            return ObstacleType.BOMB;
        } else {
            //time
            return ObstacleType.TIME;
        }
    }
    private float randomX() {
        return (float) (Math.random() * (frameWidth + 1));
    }
    private float randomY() {
        return (float) (Math.random() * (frameHeight + 1));
    }

    public boolean reduceTTL(float reduce) {
        millisTTL -= reduce;
        return millisTTL > 0;
    }

    public void onCollision() {
        switch (type) {
            case COIN -> collisionActions.onCollisionCoin();
            case BOMB -> collisionActions.onCollisionBomb();
            case TIME -> collisionActions.onCollisionTime();
        }
    }

    public Bitmap getBitmap() {
        return obstacleBitmap;
    }
    public float getX() {
        return obstacleX;
    }
    public float getY() {
        return obstacleY;
    }

    public ObstacleType getType() {
        return type;
    }

    public enum ObstacleType {
        COIN, BOMB, TIME
    }

    public interface OnCollisionActions {
        void onCollisionCoin();
        void onCollisionBomb();
        void onCollisionTime();
    }
}
