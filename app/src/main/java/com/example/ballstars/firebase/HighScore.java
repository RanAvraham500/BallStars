package com.example.ballstars.firebase;

import java.io.Serializable;

public class HighScore implements Serializable {
    private int coins;
    private int maze;

    public HighScore() {
        coins = 0;
        maze = 0;
    }

    public HighScore(int coinCollecting, int mazeOfDeath) {
        this.coins = coinCollecting;
        this.maze = mazeOfDeath;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getMaze() {
        return maze;
    }

    public void setMaze(int maze) {
        this.maze = maze;
    }

}