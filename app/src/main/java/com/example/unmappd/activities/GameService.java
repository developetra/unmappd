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
import com.example.unmappd.data.Player;

import java.util.ArrayList;
import java.util.List;

public class GameService extends Service {

    // Service

    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000;
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1;

    private final IBinder binder = new LocalBinder();
    private final List<GameServiceListener> listeners = new ArrayList<GameServiceListener>();

    // Game

    private int gameRounds; // TODO refactor as enum

    protected LocationManager locService;
    protected LocationListener locListener;

    private Location playerPosition = null;
    private Game game;

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

    // ===== Service Methods

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("test", "GameService started");
        initLocationManager();
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

    public void processGuesses(){

        for(Player p : game.getPlayers()){

            calculator.calculateEstimation(playerPosition, landmarkList);
        }
    }

//    public void onPlayerReachedLandmark() {
//         TODO adjust score?
//         for(GameServiceListener listener : listeners) {
//            listener.updatePlayerScore();
//        }
//        voidinitNextRound();
//    }

//    public voidinitNextRound(){
//        TODO empty current landmarks
//        TODO empty current guesses
//        TODO pick landmarks or next round
//        TODO notify listeners
//    }
}
