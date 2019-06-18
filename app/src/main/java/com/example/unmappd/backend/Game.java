package com.example.unmappd.backend;

import java.util.ArrayList;

public class Game {
    private int gameRounds;
    private ArrayList<Player> players = new ArrayList<>();

    // Default Constructor
    public Game() {
        super();
    }

    // Constructor
    public Game(int gameRounds, ArrayList<Player> players) {
        this.gameRounds = gameRounds;
        this.players = players;
    }

    public void setRounds(int gameRounds) {
        this.gameRounds = gameRounds;
    }

    public int getRounds() {
        return gameRounds;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
