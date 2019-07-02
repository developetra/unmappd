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
    private ArrayList<Object> guesses = new ArrayList<>();


    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public ArrayList getGuesses() {
        return guesses;
    }

    public void addGuess(int distance){
        guesses.add(distance);
    }

    public void removeGuesses(){
        guesses = null;
    }
}
