package com.example.unmappd.data;

import com.example.unmappd.R;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class Landmark - Represents a landmark that is defined by its name, picture and location (LatLong).
 *
 * @author Petra Langenbacher, Franziska Barckmann
 */
public class Landmark {
    private String name;
    private double longitude;
    private double latitude;
    private String path;
    private String info;

    // ===== Static Class Methods =====

    /**
     * Static method that creates and returns a list of landmark objects from a json file.
     *
     * @param context
     * @return list of landmark objects
     */
    public static ArrayList<Landmark> readJson(Context context) {
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
        Landmark[] landmarkArray = new Gson().fromJson(json, Landmark[].class);

        Log.d("test", "LM List: " + landmarkArray[0].getLongitude());

        ArrayList<Landmark> landmarkList = new ArrayList<Landmark>(Arrays.asList(landmarkArray));
        //List<Landmark> landmarkList = Arrays.asList(landmarkArray);

        return landmarkList;
    }

    /**
     * Constructor.
     *
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
     * Getter - Returns the name of a landmark.
     *
     * @return name of the landmark as String
     */
    public String getName() {
        return name;
    }

    /**
     * Getter - Returns the longitude of the location of a landmark.
     *
     * @return longitude as double
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Getter - Returns the latitude of the location of a landmark.
     *
     * @return latitude as double
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Getter - Returns the path to the picture of a landmark.
     *
     * @return path as a String
     */
    public String getPath() {
        return path;
    }

    /**
     * Getter - Returns the info of a landmark.
     *
     * @return info as a String
     */
    public String getInfo() {
        return info;
    }

}
