package com.example.unmappd.data;

import java.util.ArrayList;

public class Player {

    // Constructor
    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    private String name;
    private int score;
    private ArrayList<Integer> guesses = new ArrayList<>();


    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<Integer> getGuesses() {
        return guesses;
    }

    public void addGuess(Integer distance){
        guesses.add(distance);
    }

    public void removeGuesses(){
        guesses = null;
    }
}
