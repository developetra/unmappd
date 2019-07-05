package com.example.unmappd.backend;

public class Landmark {
    private String name;
    private String longitude;
    private String latitude;
    private String path;

    // Default Constructor
    public Landmark() {
        super();
    }

    // Constructor
    public Landmark(String name, String longitude, String latitude, String path) {
        this.name = name;
        this. longitude = longitude;
        this.latitude = latitude;
        this.path = path;
    }

    public String getName(){
        return name;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getPath(){
        return path;
    }
}
