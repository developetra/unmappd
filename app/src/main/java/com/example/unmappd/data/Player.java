package com.example.unmappd.data;

import java.util.ArrayList;

/**
 * Class Player - Represents a player of the game that is defined by his name, his score and his current distance guesses.
 *
 * @author Franziska Barckmann, Petra Langenbacher
 */
public class Player implements Comparable<Player> {

    private String name;
    private int score;
    private ArrayList<Integer> guesses = new ArrayList<>();
    private ArrayList<String> directions = new ArrayList<>();


    /**
     * Constructor.
     *
     * @param name  - name as a String
     * @param score - score as an int
     */
    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }


    /**
     * Getter - Returns the name of the player.
     *
     * @return name as String
     */
    public String getName() {
        return name;
    }


    /**
     * Getter - Returns the score of the player.
     *
     * @return score as int
     */
    public int getScore() {
        return score;
    }


    /**
     * Setter - Sets the score of the player.
     *
     * @param score as int
     */
    public void setScore(int score) {
        this.score = score;
    }


    /**
     * Getter - Returns the guesses of the player.
     *
     * @return guesses as ArrayList of Integers
     */
    public ArrayList<Integer> getGuesses() {
        return guesses;
    }


    /**
     * This method adds a guess to the players list of distance guesses.
     *
     * @param distance as Integer
     */
    public void addGuess(Integer distance) {
        guesses.add(distance);
    }


    /**
     * This method removes all guesses from the players list of distance guesses.
     */
    public void removeGuesses() {
        guesses.clear();
    }


    /**
     * Getter - Returns the guessed directions of the player.
     *
     * @return directions as ArrayList of Strings
     */
    public ArrayList<String> getDirections() {
        return directions;
    }


    /**
     * This method adds a direction to the players list of direction guesses.
     *
     * @param direction as String
     */
    public void addDirection(String direction) {
        directions.add(direction);
    }


    /**
     * This method removes all guesses from the players list of direction guesses.
     */
    public void removeDirections() {
        directions.clear();
    }


    /**
     * This method is used to compare the score of two players.
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Player o) {
        if (this.getScore() > o.getScore()) {
            return -1;
        } else if (this.getScore() < o.getScore()) {
            return 1;
        } else {
            return 0;
        }
    }
}
