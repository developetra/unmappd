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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * GameService - Stores all the variables of the game that the different activities need.
 *
 * @author Petra Langenbacher, Franziska Barckmann, Ann-Kathrin Schmid
 */
public class GameService extends Service {

    // ===== Service

    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000;
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1;

    private final IBinder binder = new LocalBinder();
    private final List<GameServiceListener> listeners = new ArrayList<GameServiceListener>();

    // ===== Location

    protected LocationManager locService;

    private ArrayList<Landmark> landmarkList;

    private Location playerPosition = null;

    // ===== Game

    private Landmark targetLandmark = null;

    private Game game;
    private ArrayList<Landmark> selectedLandmarks;
    private final EstimationCalculator calculator = new EstimationCalculator();

    enum GeoDirection {
        NORTH,
        NORTHEAST,
        EAST,
        SOUTHEAST,
        SOUTH,
        SOUTHWEST,
        WEST,
        NORTHWEST,
        UNKNOWN
    }

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

    public Landmark getTargetLandmark() {

        return targetLandmark;
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
        Landmark.readJson(this);

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

        void playerReachedTarget(boolean endOfGame);
    }


    // ===== Event Handling - Location Manager

    /**
     * Initialises LocationManager.
     *
     * @author Franziska Barckmann
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

                    float radius = 20;
                    if (distance < radius) {
                        Log.d("test", "Player reached target");
                        int gameRounds = game.getRounds();
                        if (gameRounds > 1) {
                            gameRounds = gameRounds - 1;
                            game.setRounds(gameRounds);
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
     * @author Franziska Barckmann
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

        int directionScore= 0;

        if (game.getMode() == true) {
            directionScore = processDirectionGuessesAdvanced(playerIndex);
        } else {
            directionScore = processDirectionGuessesSimple(playerIndex);
        }


        game.getPlayers().get(playerIndex).setScore(score + directionScore);


    }

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

    /**
     * Processes the direction guesses of a given player (Mode: simple).
     *
     * @param playerIndex
     * @return
     * @author Petra Langenbacher
     */
    public int processDirectionGuessesSimple(int playerIndex) {

        int directionScore = 0;

        // get guesses of player
        ArrayList<String> directionGuesses = game.getPlayers().get(playerIndex).getDirections();

        // Landmark 1 - get direction and compare it to the players guess
        Location l1 = new Location("");
        l1.setLatitude(selectedLandmarks.get(0).getLatitude());
        l1.setLongitude(selectedLandmarks.get(0).getLongitude());
        Log.d("test", "direction is " + getSimpleDirectionOfLandmark(l1));
        Log.d("test", "direction guess is " + directionGuesses.get(0));
        if (directionGuesses.get(0).equals(getSimpleDirectionOfLandmark(l1).toString())) {
            directionScore = directionScore + 5;
        }

        // Landmark 2 - get direction and compare it to the players guess
        Location l2 = new Location("");
        l2.setLatitude(selectedLandmarks.get(1).getLatitude());
        l2.setLongitude(selectedLandmarks.get(1).getLongitude());
        Log.d("test", "direction is " + getSimpleDirectionOfLandmark(l2));
        Log.d("test", "direction guess is " + directionGuesses.get(1));
        if (directionGuesses.get(1).equals(getSimpleDirectionOfLandmark(l2).toString())) {
            directionScore = directionScore + 5;
        }

        // Landmark 3 - get direction and compare it to the players guess
        Location l3 = new Location("");
        l3.setLatitude(selectedLandmarks.get(2).getLatitude());
        l3.setLongitude(selectedLandmarks.get(2).getLongitude());
        Log.d("test", "direction is " + getSimpleDirectionOfLandmark(l3));
        Log.d("test", "direction guess is " + directionGuesses.get(2));
        if (directionGuesses.get(2).equals(getSimpleDirectionOfLandmark(l3).toString())) {
            directionScore = directionScore + 5;
        }

        // Landmark 4 - get direction and compare it to the players guess
        Location l4 = new Location("");
        l4.setLatitude(selectedLandmarks.get(3).getLatitude());
        l4.setLongitude(selectedLandmarks.get(3).getLongitude());
        Log.d("test", "direction is " + getSimpleDirectionOfLandmark(l4));
        Log.d("test", "direction guess is " + directionGuesses.get(3));
        if (directionGuesses.get(3).equals(getSimpleDirectionOfLandmark(l4).toString())) {
            directionScore = directionScore + 5;
        }
        Log.d("test", "direction score is " + directionScore);
        return directionScore;
    }


    /**
     * Processes the direction guesses of a given player (Mode: advanced).
     *
     * @param playerIndex
     * @return
     * @author Petra Langenbacher
     */
    public int processDirectionGuessesAdvanced(int playerIndex) {

        int directionScore = 0;

        // get guesses of player
        ArrayList<String> directionGuesses = game.getPlayers().get(playerIndex).getDirections();

        // Landmark 1 - get direction and compare it to the players guess
        Location l1 = new Location("");
        l1.setLatitude(selectedLandmarks.get(0).getLatitude());
        l1.setLongitude(selectedLandmarks.get(0).getLongitude());
        Log.d("test", "direction is " + getAdvancedDirectionOfLandmark(l1));
        Log.d("test", "direction guess is " + directionGuesses.get(0));
        if (directionGuesses.get(0).equals(getAdvancedDirectionOfLandmark(l1).toString())) {
            directionScore = directionScore + 5;
        }

        // Landmark 2 - get direction and compare it to the players guess
        Location l2 = new Location("");
        l2.setLatitude(selectedLandmarks.get(1).getLatitude());
        l2.setLongitude(selectedLandmarks.get(1).getLongitude());
        Log.d("test", "direction is " + getAdvancedDirectionOfLandmark(l2));
        Log.d("test", "direction guess is " + directionGuesses.get(1));
        if (directionGuesses.get(1).equals(getAdvancedDirectionOfLandmark(l2).toString())) {
            directionScore = directionScore + 5;
        }

        // Landmark 3 - get direction and compare it to the players guess
        Location l3 = new Location("");
        l3.setLatitude(selectedLandmarks.get(2).getLatitude());
        l3.setLongitude(selectedLandmarks.get(2).getLongitude());
        Log.d("test", "direction is " + getAdvancedDirectionOfLandmark(l3));
        Log.d("test", "direction guess is " + directionGuesses.get(2));
        if (directionGuesses.get(2).equals(getAdvancedDirectionOfLandmark(l3).toString())) {
            directionScore = directionScore + 5;
        }

        // Landmark 4 - get direction and compare it to the players guess
        Location l4 = new Location("");
        l4.setLatitude(selectedLandmarks.get(3).getLatitude());
        l4.setLongitude(selectedLandmarks.get(3).getLongitude());
        Log.d("test", "direction is " + getAdvancedDirectionOfLandmark(l4));
        Log.d("test", "direction guess is " + directionGuesses.get(3));
        if (directionGuesses.get(3).equals(getAdvancedDirectionOfLandmark(l4).toString())) {
            directionScore = directionScore + 5;
        }
        Log.d("test", "direction score is " + directionScore);
        return directionScore;
    }

    /**
     * Calculates geographic direction of given landmark (Mode: simple).
     *
     * @param location
     * @return
     * @author Petra Langenbacher
     */
    public GeoDirection getSimpleDirectionOfLandmark(Location location) {

        GeoDirection direction = GeoDirection.UNKNOWN;
        float bearTo = playerPosition.bearingTo(location);

        if (bearTo < 0) {
            bearTo = bearTo + 360;
        }
        if (bearTo > 0 && bearTo < 45 | bearTo > 315 && bearTo < 360) {
            direction = GeoDirection.NORTH;
        } else if (bearTo > 45 && bearTo < 135) {
            direction = GeoDirection.EAST;
        } else if (bearTo > 135 && bearTo < 225) {
            direction = GeoDirection.SOUTH;
        } else if (bearTo > 225 && bearTo < 315) {
            direction = GeoDirection.WEST;
        }

        return direction;
    }

    /**
     * Calculates geographic direction of given landmark (Mode: advanced).
     *
     * @param location
     * @return
     * @author Petra Langenbacher
     */
    public GeoDirection getAdvancedDirectionOfLandmark(Location location) {

        GeoDirection direction = GeoDirection.UNKNOWN;
        float bearTo = playerPosition.bearingTo(location);

        if (bearTo < 0) {
            bearTo = bearTo + 360;
        }
        if (bearTo > 0 && bearTo < 22.5 | bearTo > 337.5 && bearTo < 360) {
            direction = GeoDirection.NORTH;
        } else if (bearTo > 22.5 && bearTo < 67.5) {
            direction = GeoDirection.NORTHEAST;
        } else if (bearTo > 67.5 && bearTo < 112.5) {
            direction = GeoDirection.EAST;
        } else if (bearTo > 112.5 && bearTo < 157.5) {
            direction = GeoDirection.SOUTHEAST;
        } else if (bearTo > 157.5 && bearTo < 202.5) {
            direction = GeoDirection.SOUTH;
        } else if (bearTo > 202.5 && bearTo < 247.5) {
            direction = GeoDirection.SOUTHWEST;
        } else if (bearTo > 247.5 && bearTo < 292.5) {
            direction = GeoDirection.WEST;
        } else if (bearTo > 292.5 && bearTo < 337.5) {
            direction = GeoDirection.NORTHWEST;
        }

        return direction;
    }
}

