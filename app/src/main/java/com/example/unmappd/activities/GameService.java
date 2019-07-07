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

import java.util.ArrayList;
import java.util.List;

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

    private Landmark[] landmarkList;

    private Location playerPosition = null;

    // TODO set targetlandmark in Map Activity
    private Landmark targetLandmark = null;

    private Game game;
    private ArrayList<Landmark> selectedLandmarks;

    private final EstimationCalculator calculator = new EstimationCalculator();

    // ===== Getter and Setter Methods

    public Location getPlayerPosition(){

        return playerPosition;
    }

    public void setGame(Game game){

        this.game = game;
    }

    public Game getGame(){

        return this.game;
    }

    public ArrayList<Landmark> getSelectedLandmarks(){

        return selectedLandmarks;
    }

    public void setTargetLandmark(Landmark targetLandmark){

        this.targetLandmark = targetLandmark;
    }

    // ===== Service Methods

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


    // ===== Local Binder

    public class LocalBinder extends Binder {
        GameService getService() {
            return GameService.this;
        }
    }

    // ===== Game Service Listener Methods

    public void registerListener(GameServiceListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(GameServiceListener listener) {
        listeners.remove(listener);
    }

    public interface GameServiceListener {

        void updatePlayerPosition(Location location);
        // void playerReachedTarget(); // TODO Add function in MapsActivity (hier geht es im Game weiter)
    }


    // ===== Event Handling - Location Manager

    private void initLocationManager() {
        Log.d("test", "LocationManager initialized");
        LocationManager locService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d("test", "Location changed");
                playerPosition = location;
                // Inform all listeners about changed player position
                for (GameServiceListener listener : listeners){
                    listener.updatePlayerPosition(location);
                }

                // Notify listeners when player is near  target landmark
                if(targetLandmark != null) {
                    Location targetLocation = new Location("");
                    targetLocation.setLatitude(targetLandmark.getLatitude());
                    targetLocation.setLongitude(targetLandmark.getLongitude());

                    float distance = playerPosition.distanceTo(targetLocation);
                    Log.d("test", "Distance to target: "+distance);

                    //TODO Festlegen einer maximalen Nähe zur target landmark
//                    if (distance < PROXY_RADIUS) {
//                        onPlayerReachedTarget(false);
//                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
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

    // ===== private Methods

    /**
     * This method processes the guess of a player after estimations are entered.
     * The estimation position is calculated and the score updated.
     * @param playerIndex
     */
    public void processGuess(int playerIndex){

        // TODO finalize calculation
        // calculator.calculateEstimation(playerPosition, selectedLandmarks, game.getPlayers().get(playerIndex).getGuesses());
        // TODO updatePlayerScore();
    }

//    public void onPlayerReachedLandmark() {
//         for(GameServiceListener listener : listeners) {
//            listener.updatePlayerScore();
//        }
//        voidinitNextRound();
//    }

    /**
     * This method initializes the next round by
     * removing current selected landmarks,
     * removing the players guesses and
     * picking new landmarks.
     */
    public void initNextRound(){

        //TODO check if there is a next round

        //empty current selected landmarks
        selectedLandmarks.clear();

        //remove current guesses of players
        game.clearAllGuesses();

        //clear target landmark
        targetLandmark = null;

        //TODO pick landmarks or next round

        //TODO notify listeners

    }
}
