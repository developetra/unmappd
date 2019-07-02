package com.example.unmappd.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.unmappd.R;
import com.example.unmappd.backend.Game;
import com.example.unmappd.backend.Player;

import java.util.ArrayList;
import java.util.List;

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

        // Update playerIndex and numberOfPlayers
        Bundle b = this.getIntent().getExtras();
        numberOfPlayers = b.getInt("numberOfPlayers");
        playerIndex = b.getInt("playerIndex");

        // Bind to Game Service
        Intent bindIntent = new Intent(EstimationActivity.this, GameService.class);
        bindService(bindIntent, gameServiceCon, Context.BIND_AUTO_CREATE);

        Log.d("test", "numberOfPlayers is" + String.valueOf(numberOfPlayers));
        Log.d("test", "playerIndex is" + String.valueOf(playerIndex));

        //TODO Auswahl und Anzeigen von 4 Landmarken in der Nähe
    }

    public void startMap (View view){

        // TODO save information to players

        EditText distance1 = findViewById(R.id.distanceLandmark1);
        EditText distance2 = findViewById(R.id.distanceLandmark2);
        EditText distance3 = findViewById(R.id.distanceLandmark3);
        EditText distance4 = findViewById(R.id.distanceLandmark4);

        // gameService.getGame().getPlayers().get(playerIndex-1).setGuess

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

            // Init UI with player name

            TextView nameView = (TextView)findViewById(R.id.playerName);
            ArrayList<Player> players = gameService.getGame().getPlayers();
            nameView.setText(players.get(playerIndex-1).getName());
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
}
