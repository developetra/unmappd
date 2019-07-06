package com.example.unmappd.data;

import com.example.unmappd.R;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class Landmark - Represents a landmark that is defined by its name, picture and location.
 *
 * @author Petra Langenbacher, Franziska Barckmann
 */
public class Landmark {
    private String name;
    private double longitude;
    private double latitude;
    private String path;

    // ===== Static Class Methods =====

    /**
     * Static method that creates and returns a list of landmark objects from a json file.
     * @param context
     * @return
     *  list of landmark objects
     */
    public static Landmark[] readJson (Context context) {
        String json = null;
        // convert json to String
        try {
            InputStream is = context.getAssets().open("database.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Log.d("test", json);

        // convert json String to List of Landmarks
        Landmark[] landmarkList = new Gson().fromJson(json, Landmark[].class);

        Log.d("test", "LM List: " + landmarkList[0].getLongitude());

        return landmarkList;
    }

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
    /**
     * Constructor.
     * @param name
     * @param longitude
     * @param latitude
     * @param path
     */
    public Landmark(String name, double longitude, double latitude, String path) {
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
    public double getLongitude(){
        return longitude;
    }

    /**
     * Returns the latitude of the location of a landmark.
     * @return latitude as String
     */
    public double getLatitude(){
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
