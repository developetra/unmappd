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
import android.widget.Button;
import android.widget.TextView;

import com.example.unmappd.R;

public class RankingActivity extends AppCompatActivity implements GameService.GameServiceListener {

    protected GameService gameService;
    protected boolean gameServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // Bind to Game Service
        Intent bindIntent = new Intent(RankingActivity.this, GameService.class);
        bindService(bindIntent, gameServiceCon, Context.BIND_AUTO_CREATE);

    }

    public void startSetup (View view){

        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }

    public void startMap(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        //Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /**
     * This method initializes the UI elements with the player names and scores.
     */
    private void initUI() {

        int numberOfPlayers = gameService.getGame().getNumberOfPlayers();

        // Init UI with player name and player score


        TextView nameP1View = (TextView)findViewById(R.id.namePlayer1);
        TextView scoreP1View = (TextView)findViewById(R.id.scorePlayer1);

        TextView nameP2View = (TextView)findViewById(R.id.namePlayer2);
        TextView scoreP2View = (TextView)findViewById(R.id.scorePlayer2);

        TextView nameP3View = (TextView)findViewById(R.id.namePlayer3);
        TextView scoreP3View = (TextView)findViewById(R.id.scorePlayer3);

        TextView nameP4View = (TextView)findViewById(R.id.namePlayer4);
        TextView scoreP4View = (TextView)findViewById(R.id.scorePlayer4);


        switch (numberOfPlayers){
            case 1:
                nameP2View.setVisibility(View.GONE);
                scoreP2View.setVisibility(View.GONE);
                nameP3View.setVisibility(View.GONE);
                scoreP3View.setVisibility(View.GONE);
                nameP4View.setVisibility(View.GONE);
                scoreP4View.setVisibility(View.GONE);
                // player 1
                nameP1View.setText(gameService.getGame().getPlayers().get(0).getName());
                scoreP1View.setText(String.valueOf(gameService.getGame().getPlayers().get(0).getScore()));
                break;
            case 2:
                nameP2View.setVisibility(View.VISIBLE);
                scoreP2View.setVisibility(View.VISIBLE);
                nameP3View.setVisibility(View.GONE);
                scoreP3View.setVisibility(View.GONE);
                nameP4View.setVisibility(View.GONE);
                scoreP4View.setVisibility(View.GONE);
                // player 1
                nameP1View.setText(gameService.getGame().getPlayers().get(0).getName());
                scoreP1View.setText(String.valueOf(gameService.getGame().getPlayers().get(0).getScore()));
                // player 2
                nameP2View.setText(gameService.getGame().getPlayers().get(1).getName());
                scoreP2View.setText(String.valueOf(gameService.getGame().getPlayers().get(1).getScore()));
                break;
            case 3:
                nameP2View.setVisibility(View.VISIBLE);
                scoreP2View.setVisibility(View.VISIBLE);
                nameP3View.setVisibility(View.VISIBLE);
                scoreP3View.setVisibility(View.VISIBLE);
                nameP4View.setVisibility(View.GONE);
                scoreP4View.setVisibility(View.GONE);
                // player 1
                nameP1View.setText(gameService.getGame().getPlayers().get(0).getName());
                scoreP1View.setText(String.valueOf(gameService.getGame().getPlayers().get(0).getScore()));
                // player 2
                nameP2View.setText(gameService.getGame().getPlayers().get(1).getName());
                scoreP2View.setText(String.valueOf(gameService.getGame().getPlayers().get(1).getScore()));
                // player 3
                nameP3View.setText(gameService.getGame().getPlayers().get(2).getName());
                scoreP3View.setText(String.valueOf(gameService.getGame().getPlayers().get(2).getScore()));
                break;
            case 4:
                nameP2View.setVisibility(View.VISIBLE);
                scoreP2View.setVisibility(View.VISIBLE);
                nameP3View.setVisibility(View.VISIBLE);
                scoreP3View.setVisibility(View.VISIBLE);
                nameP4View.setVisibility(View.VISIBLE);
                scoreP4View.setVisibility(View.VISIBLE);
                // player 1
                nameP1View.setText(gameService.getGame().getPlayers().get(0).getName());
                scoreP1View.setText(String.valueOf(gameService.getGame().getPlayers().get(0).getScore()));
                // player 2
                nameP2View.setText(gameService.getGame().getPlayers().get(1).getName());
                scoreP2View.setText(String.valueOf(gameService.getGame().getPlayers().get(1).getScore()));
                // player 3
                nameP3View.setText(gameService.getGame().getPlayers().get(2).getName());
                scoreP3View.setText(String.valueOf(gameService.getGame().getPlayers().get(2).getScore()));
                // player 4
                nameP4View.setText(gameService.getGame().getPlayers().get(3).getName());
                scoreP4View.setText(String.valueOf(gameService.getGame().getPlayers().get(3).getScore()));
                break;
        }

        Log.d("test", "Ranking UI initialized");
    }



    // ===== Game Service Connection =====

    private ServiceConnection gameServiceCon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            gameService = binder.getService();
            gameServiceBound = true;
            gameService.registerListener(RankingActivity.this);
            Log.d("test", "created Setup");
            Log.d("test", "Service bound to Estimation");

            // initialize UI dynamically
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
    }

    public void playerReachedTarget(boolean endOfGame){

        if(endOfGame == true){
            Button buttonMap = findViewById(R.id.startMapButton);
            buttonMap.setVisibility((View.GONE));

            Button buttonSetup = findViewById(R.id.startSetupButton);
            buttonSetup.setVisibility((View.VISIBLE));

            Log.d("test", "Ranking changed button for final ranking");
        }
        else {
            Button buttonMap = findViewById(R.id.startMapButton);
            buttonMap.setVisibility((View.VISIBLE));

            Button buttonSetup = findViewById(R.id.startSetupButton);
            buttonSetup.setVisibility((View.GONE));
        }
    }
}
