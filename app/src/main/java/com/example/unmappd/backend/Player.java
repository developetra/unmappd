package com.example.unmappd.backend;

import java.util.ArrayList;

public class Player {

    // Constructor
    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    private String name;
    private int score;
    private ArrayList<Object> guesses = new ArrayList<>();  // TODO save guesses in an appropriate way


    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public ArrayList getGuesses() {
        return guesses;
    }
}
