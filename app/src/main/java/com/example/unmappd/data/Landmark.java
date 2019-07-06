package com.example.unmappd.data;

/**
 * Class Landmark - Represents a landmark that is defined by its name, picture and location.
 *
 * @author Petra Langenbacher, Franziska Barckmann
 */
public class Landmark {
    private String name;
    private String longitude;
    private String latitude;
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

    /**
     * Constructor.
     * @param name
     * @param longitude
     * @param latitude
     * @param path
     */
    public Landmark(String name, String longitude, String latitude, String path) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.path = path;
    }

    /**
     * Returns the name of a landmark.
     * @return name of the landmark
     */
    public String getName(){
        return name;
    }

    /**
     * Returns the longitude of the location of a landmark.
     * @return longitude as String
     */
    public String getLongitude(){
        return longitude;
    }

    /**
     * Returns the latitude of the location of a landmark.
     * @return latitude as String
     */
    public String getLatitude(){
        return latitude;
    }

    /**
     * Returns the path to the picture of a landmark.
     * @return path as a String
     */
    public String getPath(){
        return path;
    }

}
