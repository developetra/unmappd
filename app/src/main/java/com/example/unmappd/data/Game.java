package com.example.unmappd.data;

import com.example.unmappd.data.Player;

import java.util.ArrayList;

/**
 * Class Game - Represents a game that is defined by its number of rounds and its players.
 *
 * @author Petra Langenbacher
 */
public class Game {
    private int gameRounds;
    private ArrayList<Player> players = new ArrayList<>();
    private boolean advancedMode;


    /**
     * Default Constructor.
     */
    public Game() {
        super();
    }


    /**
     * Constructor.
     *
     * @param gameRounds - a number between 1 and 4
     * @param players    - a number between 1 and 4
     */
    public Game(int gameRounds, ArrayList<Player> players) {
        this.gameRounds = gameRounds;
        this.players = players;
    }


    /**
     * Sets number of rounds for the current game.
     *
     * @param gameRounds - number between 1 and 4
     */
    public void setRounds(int gameRounds) {
        this.gameRounds = gameRounds;
    }


    /**
     * Returns number of rounds of the current game.
     *
     * @return number of rounds
     */
    public int getRounds() {
        return gameRounds;
    }


    /**
     * Sets players for the current game.
     *
     * @param players - ArrayList of players
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }


    /**
     * Returns the players of the current game.
     *
     * @return ArrayList of players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }


    /**
     * Returns the number of players.
     *
     * @return
     */
    public int getNumberOfPlayers() {
        return players.size();
    }


    /**
     * Clears the guesses of all players.
     */
    public void clearAllGuesses() {
        for (Player p : players) {
            p.removeGuesses();
        }
    }


    /**
     * Sets mode for the current game.
     *
     * @param advancedMode
     */
    public void setMode(boolean advancedMode) {
        this.advancedMode = advancedMode;
    }


    /**
     * Gets mode of the current game.
     *
     * @return
     */
    public boolean getMode() {
        return advancedMode;
    }
}
