package com.example.ballstars.firebase;

import com.example.ballstars.dialogs.settings.Settings;
import com.example.ballstars.home.store_fr.StoreFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class User implements Serializable {
    private String email;
    private String username;
    private int money = 0;
    private HighScore highScore = new HighScore();
    private ArrayList<Integer> availableSkins = (ArrayList<Integer>) Arrays.stream(new int[StoreFragment.indexToSkinId.length]).boxed().collect(Collectors.toList());
    private int chosenSkin = 0;
    private String chosenLevel = "Coins";
    private Settings userSettings = new Settings();
    public User() {
        email = "";
        username = "";
        availableSkins.set(0, 1);
        chosenSkin = StoreFragment.indexToSkinId[0];
        chosenLevel = "Coins";
    }

    public User(String email, String username) {
        this.email = email;
        this.username = username;
        availableSkins.set(0, 1);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public HighScore getHighScore() {
        return highScore;
    }

    public void setHighScore(HighScore highScore) {
        this.highScore = highScore;
    }

    public ArrayList<Integer> getAvailableSkins() {
        return availableSkins;
    }

    public void setAvailableSkins(ArrayList<Integer> availableSkins) {
        this.availableSkins = availableSkins;
    }

    public int getChosenSkin() {
        return chosenSkin;
    }

    public void setChosenSkin(int chosenSkin) {
        this.chosenSkin = chosenSkin;
    }

    public String getChosenLevel() {
        return chosenLevel;
    }

    public void setChosenLevel(String chosenLevel) {
        this.chosenLevel = chosenLevel;
    }

    public Settings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(Settings userSettings) {
        this.userSettings = userSettings;
    }

}