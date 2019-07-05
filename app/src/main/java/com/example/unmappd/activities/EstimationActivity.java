package com.example.unmappd.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.unmappd.R;
import com.example.unmappd.backend.Landmark;
import com.example.unmappd.backend.LandmarkDatabase;
import com.example.unmappd.backend.Player;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * This class serves at activity where the players can enter their distance guesses.
 *
 * @author Franziska Barckmann
 */
public class EstimationActivity extends AppCompatActivity implements GameService.GameServiceListener{

    protected GameService gameService;
    protected boolean gameServiceBound;

    // starting with 1
    private int numberOfPlayers;
    // playerIndex starting with 1
    private int playerIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimation);

        // Update playerIndex and numberOfPlayers from intent
        Bundle b = this.getIntent().getExtras();
        numberOfPlayers = b.getInt("numberOfPlayers");
        playerIndex = b.getInt("playerIndex");

        // Bind to Game Service
        Intent bindIntent = new Intent(EstimationActivity.this, GameService.class);
        bindService(bindIntent, gameServiceCon, Context.BIND_AUTO_CREATE);

        Log.d("test", "numberOfPlayers is" + String.valueOf(numberOfPlayers));
        Log.d("test", "playerIndex is" + String.valueOf(playerIndex));

        readJson ();
        //TODO Auswahl und Anzeigen von 4 Landmarken in der Nähe
    }

    private void initUI() {
        // Init UI with player name
        TextView nameView = (TextView)findViewById(R.id.playerName);
        ArrayList<Player> players = gameService.getGame().getPlayers();
        nameView.setText(players.get(playerIndex-1).getName());

        Log.d("test", "Estimation UI initialized");
    }

    public void startMap (View view){

        // Save user input to player object

        EditText inputdistance1 = findViewById(R.id.distanceLandmark1);
        EditText inputdistance2 = findViewById(R.id.distanceLandmark2);
        EditText inputdistance3 = findViewById(R.id.distanceLandmark3);
        EditText inputdistance4 = findViewById(R.id.distanceLandmark4);

        int distance1 = Integer.parseInt(inputdistance1.getText().toString());
        int distance2 = Integer.parseInt(inputdistance2.getText().toString());
        int distance3 = Integer.parseInt(inputdistance3.getText().toString());
        int distance4 = Integer.parseInt(inputdistance4.getText().toString());

        gameService.getGame().getPlayers().get(playerIndex-1).addGuess(distance1);
        gameService.getGame().getPlayers().get(playerIndex-1).addGuess(distance2);
        gameService.getGame().getPlayers().get(playerIndex-1).addGuess(distance3);
        gameService.getGame().getPlayers().get(playerIndex-1).addGuess(distance4);

        // if next player -> load activity again
        if(playerIndex < numberOfPlayers) {
            Intent refresh = new Intent(this, EstimationActivity.class);
            Bundle b = new Bundle();
            b.putInt("numberOfPlayers", numberOfPlayers);
            b.putInt("playerIndex", playerIndex+1);
            refresh.putExtras(b);
            startActivity(refresh);
        }
        else {
            // if no next player -> start map activity
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

    // ===== Game Service Connection =====

    private ServiceConnection gameServiceCon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            gameService = binder.getService();
            gameServiceBound = true;
            gameService.registerListener(EstimationActivity.this);
            Log.d("test", "created Setup");
            Log.d("test", "Service bound to Estimation");
            initUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            gameServiceBound = false;
        }
    };

    // ===== Listener Methods =====

    public void updatePlayerPosition(Location location){
        // do nothing
        Log.d("test", "Setup is updating player position");
    }

    // ===== Get Landmarks from json =====

    public void readJson () {
        String json = null;
        try {
            InputStream is = getAssets().open("database.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Log.d("test", json);
        LandmarkDatabase database = new Gson().fromJson(json, LandmarkDatabase.class);
        ArrayList<Landmark> landmarks = database.getLandmarks();
        Log.d("test", "Json: " + landmarks);
    }
}
