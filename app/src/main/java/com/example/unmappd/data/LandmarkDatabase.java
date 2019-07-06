package com.example.unmappd.data;

import com.example.unmappd.data.Landmark;

import java.util.ArrayList;

public class LandmarkDatabase {

    private ArrayList<Landmark> landmarks = new ArrayList<>();

    // Default Constructor
    public LandmarkDatabase() {
        super();
    }

    // Constructor
    public LandmarkDatabase(ArrayList<Landmark> landmarks) {
        this.landmarks = landmarks;
    }

    public ArrayList<Landmark> getLandmarks() {
        return landmarks;
    }

    public void setLandmarks(ArrayList<Landmark> landmarks) {
        this.landmarks = landmarks;
        }

}

