package com.example.ballstars.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameClock implements Runnable {
    private ScheduledExecutorService exec;
    private final ClockListener listener;
    private boolean running = true;
    private final int START_TIME;
    private int seconds = 0;
    private int minutes = 0;

    private GameClock(ClockListener listener) {
        this.listener = listener;
        START_TIME = 0;
    }
    private GameClock(ClockListener listener, int totalSeconds) {
        this.listener = listener;

        minutes = totalSeconds / 60;
        seconds = totalSeconds - (60 * minutes);

        START_TIME = totalSeconds;
    }

    public static GameClock createTimer(ClockListener listener, int totalSeconds) {
        return new GameClock(listener ,totalSeconds);
    }

    public static GameClock createStopwatch(ClockListener listener) {
        return new GameClock(listener);
    }

    @Override
    public void run() {
        if (running) {
            listener.onSecondTickClock(getCurrentTime());
            if (START_TIME == 0) { //stopwatch
                seconds++;
                if (seconds >= 60) {
                    seconds -= 60;
                    minutes++;
                }
            } else {
                seconds--;
                if (seconds < 0) {
                    seconds += 60;
                    minutes--;
                    if (minutes < 0) {
                        finish();
                    }
                }
            }
        }
    }

    public void updateOnCollision(int updateSeconds) {
        if (running) {
            seconds += updateSeconds;

            while (seconds >= 60) {
                seconds -= 60;
                minutes++;
            }

            while (seconds < 0) {
                seconds += 60;
                minutes--;
            }

            if (minutes < 0) {
                finish();
            }
            listener.onSecondTickClock(getCurrentTime());
        }
    }

    public void start() {
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    public void pause() {
        running = false;
    }

    public void resume() {
        running = true;
        exec.execute(this);
    }

    public void reset() {
        minutes = START_TIME / 60;
        seconds = START_TIME - (60 * minutes);
        if (exec.isShutdown()) {
            start();
        }
    }

    public void finish() {
        minutes = 0;
        seconds = 0;
        exec.shutdown();
        listener.onFinishClock();
    }

    private String getCurrentTime() {
        String strMinutes = minutes + "", strSeconds = seconds + "";
        if (minutes < 10) strMinutes = "0" + strMinutes;
        if (seconds < 10) strSeconds = "0" + strSeconds;

        return strMinutes + ":" + strSeconds;
    }

    public int translateToScore() {
        return minutes * 60 + seconds;
    }

    public interface ClockListener {
        void onSecondTickClock(String time);
        void onFinishClock();
    }
}
