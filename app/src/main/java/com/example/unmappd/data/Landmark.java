package com.example.unmappd.data;

public class Landmark {
    private String name;
    private int longitude;
    private int latitude;
    private String path;

    // ===== Static Class Methods =====

    /**
     * Use mercator projection to transform latitude and longitude values to cartesian coordinates.
     */
    // TODO
    // public static double[] toCartesian(double lat, double lng) {}

    /**
     * Use mercator projection to transform cartesian coordinates to latitude and longitude values.
     */
    // TODO
    // public static double[] toLatLng(double x, double y) {}

    // Constructor
    public Landmark(String name, int longitude, int latitude, String path) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.path = path;
    }

    public String getName(){
        return name;
    }
    public int getLongitude(){
        return longitude;
    }
    public int getLatitude(){
        return latitude;
    }
    public String getPath(){
        return path;
    }

}
