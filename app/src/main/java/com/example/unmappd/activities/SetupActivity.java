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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.unmappd.R;

public class SetupActivity extends AppCompatActivity implements GameService.GameServiceListener{

    protected GameService gameService;
    protected boolean gameServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Bind to Game Service
        Intent bindIntent = new Intent(SetupActivity.this, GameService.class);
        bindService(bindIntent, gameServiceCon, Context.BIND_AUTO_CREATE);

        // Rounds Spinner
        Spinner roundsSpinner = (Spinner) findViewById(R.id.rounds_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> roundsAdapter = ArrayAdapter.createFromResource(this,
                R.array.rounds_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        roundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        roundsSpinner.setAdapter(roundsAdapter);

        // Players Spinner
        Spinner PlayersSpinner = (Spinner) findViewById(R.id.players_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> playersAdapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        playersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        PlayersSpinner.setAdapter(playersAdapter);
    }

    public void startEstimation (View view){
        Intent intent = new Intent(this, EstimationActivity.class);
        startActivity(intent);
    }

    // ===== Game Service Connection =====

    private ServiceConnection gameServiceCon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            GameService gameService = binder.getService();
            gameServiceBound = true;
            gameService.registerListener(SetupActivity.this);
            Log.d("test", "created Setup");
            Log.d("test", "Service bound to Setup");
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
