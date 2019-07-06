package com.example.unmappd.data;

import com.example.unmappd.data.Landmark;

import java.util.ArrayList;

/**
 * Class LandmarkDatabase - Represents a database that holds landmarks.
 *
 * @author Petra Langenbacher
 */
public class LandmarkDatabase {

    private ArrayList<Landmark> landmarks = new ArrayList<>();

    /**
     * Default Constructor.
     */
    public LandmarkDatabase() {
        super();
    }

    /**
     * Constructor.
     * @param landmarks - ArrayList of landmarks
     */
    public LandmarkDatabase(ArrayList<Landmark> landmarks) {
        this.landmarks = landmarks;
    }

    /**
     * Sets the landmarks of the current database.
     * @param landmarks - ArrayList of landmarks
     */
    public void setLandmarks(ArrayList<Landmark> landmarks) {
        this.landmarks = landmarks;
    }

    /**
     * Returns the landmarks of the current database.
     * @return ArrayList of landmarks
     */
    public ArrayList<Landmark> getLandmarks() {
        return landmarks;
    }



}

