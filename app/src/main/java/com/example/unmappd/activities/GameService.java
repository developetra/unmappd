package com.example.unmappd.activities;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.unmappd.backend.EstimationCalculator;
import com.example.unmappd.data.Game;
import com.example.unmappd.data.Landmark;
import com.example.unmappd.data.Player;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Game Service - Stores all the variables of the game that the different activities need.
 *
 * @author Petra Langenbacher, Franziska Barckmann
 */
public class GameService extends Service {

    // ===== Service

    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000;
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1;

    private final IBinder binder = new LocalBinder();
    private final List<GameServiceListener> listeners = new ArrayList<GameServiceListener>();

    // ===== Game

    private int gameRounds; // TODO refactor as enum

    protected LocationManager locService;
    protected LocationListener locListener;

    private List<Landmark> landmarkList;

    private Location playerPosition = null;

    // TODO set targetlandmark in Map Activity
    private Landmark targetLandmark = null;

    private Game game;
    private ArrayList<Landmark> selectedLandmarks;

    private final EstimationCalculator calculator = new EstimationCalculator();


    /**
     * Getter and Setter Methods.
     */

    public Location getPlayerPosition() {

        return playerPosition;
    }

    public void setGame(Game game) {

        this.game = game;
    }

    public Game getGame() {

        return this.game;
    }

    public ArrayList<Landmark> getSelectedLandmarks() {

        return selectedLandmarks;
    }

    public void setTargetLandmark(Landmark targetLandmark) {

        this.targetLandmark = targetLandmark;
    }

    /**
     * Initialises landmarkList with landmarks from json file and calls method to initialise the LocationManager.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // load landmarks from json
        this.landmarkList = Landmark.readJson(this);

        initLocationManager();
        Log.d("test", "GameService started");

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    /**
     * Local Binder.
     */

    public class LocalBinder extends Binder {
        GameService getService() {
            return GameService.this;
        }
    }


    /**
     * Game Service Listener Methods.
     *
     * @param listener
     */
    public void registerListener(GameServiceListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(GameServiceListener listener) {
        listeners.remove(listener);
    }

    public interface GameServiceListener {

        void updatePlayerPosition(Location location);

        void playerReachedTarget(boolean endOfGame); // TODO Add function in MapsActivity (hier geht es im Game weiter)
    }


    // ===== Event Handling - Location Manager

    /**
     * Initialises LocationManager.
     */
    private void initLocationManager() {
        Log.d("test", "LocationManager initialized");
        locService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d("test", "Location changed");
                playerPosition = location;
                // Inform all listeners about changed player position
                for (GameServiceListener listener : listeners) {
                    listener.updatePlayerPosition(location);
                }

                // Notify listeners when player is near  target landmark
                if (targetLandmark != null) {
                    Location targetLocation = new Location("");
                    targetLocation.setLatitude(targetLandmark.getLatitude());
                    targetLocation.setLongitude(targetLandmark.getLongitude());

                    float distance = playerPosition.distanceTo(targetLocation);
                    Log.d("test", "Distance to target: " + distance);

                    //TODO Festlegen einer maximalen Nähe zur target landmark
//                    if (distance < PROXY_RADIUS) {
                    initNextRound();
//                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("test", "Permission not given!");
            return;
        }

        locService.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, locListener);

    }

    /**
     * This method processes the guess of a player after estimations are entered.
     * The estimation position is calculated and the score updated.
     *
     * @param playerIndex
     */
    public void processGuess(int playerIndex) {

        double[] result = calculator.calculateEstimation(playerPosition, selectedLandmarks, game.getPlayers().get(playerIndex).getGuesses());

        Log.d("test", "playerposition Long: " + playerPosition.getLongitude());
        Log.d("test", "playerposition Lat: " + playerPosition.getLatitude());
        Log.d("test", "estimated position Long: " + result[0]);
        Log.d("test", "estimated position Lat: " + result[1]);

        Location estimation = new Location("");

        estimation.setLongitude(result[0]);
        estimation.setLatitude(result[1]);

        updatePlayerScore(playerIndex, estimation);
    }

    /**
     * Calculates the distance of the player position to the estimated
     * location of the player and changes the score accordingly.
     *
     * @param playerIndex
     * @param estimation
     * @author Petra Langenbacher, Ann-Kathrin Schmid
     */
    private void updatePlayerScore(int playerIndex, Location estimation) {

        float distanceGuessPlayer = playerPosition.distanceTo(estimation);
        Log.d("test", "playerposition Long: " + playerPosition.getLongitude());
        Log.d("test", "estimated distance: " + distanceGuessPlayer);

        int score = game.getPlayers().get(playerIndex).getScore();

        // score calculation
        if (distanceGuessPlayer < 100) {
            score = score + 50;
        } else if (distanceGuessPlayer > 200 && distanceGuessPlayer < 300) {
            score = score + 40;
        } else if (distanceGuessPlayer > 300 && distanceGuessPlayer < 400) {
            score = score + 30;
        } else if (distanceGuessPlayer > 400 && distanceGuessPlayer < 500) {
            score = score + 20;
        } else if (distanceGuessPlayer > 500 && distanceGuessPlayer < 600) {
            score = score + 10;
        } else if (distanceGuessPlayer > 600) {
            score = score + 5;
        }
        game.getPlayers().get(playerIndex).setScore(score);


    }

//    public void onPlayerReachedLandmark() {
//         for(GameServiceListener listener : listeners) {
//            listener.updatePlayerScore();
//        }
//        voidinitNextRound();
//    }

    public void requestGPSupdate() {

        // locService.requestLocationUpdates(locationProvider, 0, 0, this);
    }


    /**
     * Initializes the next round by removing current selected landmarks,
     * removing the players guesses and picking new landmarks.
     *
     * @author Franziska Barckmann
     */
    public void initNextRound() {

        //check if there is a next round
        if (gameRounds >= 1) {
            gameRounds = gameRounds - 1;

            //empty current selected landmarks
            selectedLandmarks.clear();

            // remove visited landmark from landmarkLIst
            landmarkList.remove(targetLandmark);

            //remove current guesses of players
            game.clearAllGuesses();

            //clear target landmark
            targetLandmark = null;

            // pick closest landmarks
            selectedLandmarks = pickLandmarks();
            Log.d("test", String.valueOf(selectedLandmarks));

            // notify listeners
            for (GameServiceListener listener : listeners) {
                listener.playerReachedTarget(false);
            }
        } else {

            for (GameServiceListener listener : listeners) {
                listener.playerReachedTarget(true);
            }
        }

    }

    /**
     * Sorts all landmarks by the distance to the player position and
     * returns the four landmarks that are closest to the player.
     *
     * @return ArrayList with the four closest landmarks
     * @author Petra Langenbacher
     */
    public ArrayList<Landmark> pickLandmarks() {

        // arraylist with the closest landmarks
        ArrayList<Landmark> closestLandmarks = new ArrayList<Landmark>();

        // hashmap with landmarks and their distance to the position of the player
        HashMap<Float, Landmark> landmarkMap = new HashMap<Float, Landmark>();

        // fill hashmap with landmarks
        for (Landmark current : landmarkList) {
            Location currentLocation = new Location("");
            currentLocation.setLatitude(current.getLatitude());
            currentLocation.setLongitude(current.getLongitude());
            float distance = playerPosition.distanceTo(currentLocation);
            landmarkMap.put(distance, current);
        }

        // sort a list with the keys of the hashmap
        List sortedDistances = new ArrayList(landmarkMap.keySet());
        Collections.sort(sortedDistances);

        // add the closest landmarks to arraylist
        closestLandmarks.add(landmarkMap.get(sortedDistances.get(0)));
        closestLandmarks.add(landmarkMap.get(sortedDistances.get(1)));
        closestLandmarks.add(landmarkMap.get(sortedDistances.get(2)));
        closestLandmarks.add(landmarkMap.get(sortedDistances.get(3)));

        return closestLandmarks;
    }

    /**
     * Initialises first game by calling the method to pick the closest landmarks.
     */
    public void initFirstGame() {
        selectedLandmarks = pickLandmarks();
    }
}
