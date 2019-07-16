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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unmappd.R;
import com.example.unmappd.data.Landmark;
import com.example.unmappd.data.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * FinalRankingActivity - This class serves as activity that shows the final ranking of the players at the end of the game.
 *
 * @author Petra Langenbacher
 */

public class FinalRankingActivity extends AppCompatActivity implements GameService.GameServiceListener{

    protected GameService gameService;
    protected boolean gameServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_ranking);

        // Bind to Game Service
        Intent bindIntent = new Intent(FinalRankingActivity.this, GameService.class);
        bindService(bindIntent, gameServiceCon, Context.BIND_AUTO_CREATE);

    }

    public void startNewGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    /**
     * This method initializes the UI elements with the player names and scores.
     * @author Petra Langenbacher
     */
    private void initUI() {

        int numberOfPlayers = gameService.getGame().getNumberOfPlayers();

        // Init UI with player name and player score

        TextView nameP1View = (TextView) findViewById(R.id.namePlayer1);
        TextView scoreP1View = (TextView) findViewById(R.id.scorePlayer1);
        ImageView imageP1View = (ImageView) findViewById(R.id.ranking_first);

        TextView nameP2View = (TextView) findViewById(R.id.namePlayer2);
        TextView scoreP2View = (TextView) findViewById(R.id.scorePlayer2);
        ImageView imageP2View = (ImageView) findViewById(R.id.ranking_second);

        TextView nameP3View = (TextView) findViewById(R.id.namePlayer3);
        TextView scoreP3View = (TextView) findViewById(R.id.scorePlayer3);
        ImageView imageP3View = (ImageView) findViewById(R.id.ranking_third);

        TextView nameP4View = (TextView) findViewById(R.id.namePlayer4);
        TextView scoreP4View = (TextView) findViewById(R.id.scorePlayer4);
        ImageView imageP4View = (ImageView) findViewById(R.id.ranking_last);

        // sort players by their score
        ArrayList<Player> sortedPlayers = sortPlayersByScore();

        switch (numberOfPlayers) {
            case 1:
                nameP2View.setVisibility(View.GONE);
                scoreP2View.setVisibility(View.GONE);
                imageP2View.setVisibility(View.GONE);
                nameP3View.setVisibility(View.GONE);
                scoreP3View.setVisibility(View.GONE);
                imageP3View.setVisibility(View.GONE);
                nameP4View.setVisibility(View.GONE);
                scoreP4View.setVisibility(View.GONE);
                imageP4View.setVisibility(View.GONE);
                // player 1
                nameP1View.setText(sortedPlayers.get(0).getName());
                scoreP1View.setText(String.valueOf(sortedPlayers.get(0).getScore()));
                break;
            case 2:
                nameP3View.setVisibility(View.GONE);
                scoreP3View.setVisibility(View.GONE);
                imageP3View.setVisibility(View.GONE);
                nameP4View.setVisibility(View.GONE);
                scoreP4View.setVisibility(View.GONE);
                imageP4View.setVisibility(View.GONE);
                // player 1
                nameP1View.setText(sortedPlayers.get(0).getName());
                scoreP1View.setText(String.valueOf(sortedPlayers.get(0).getScore()));
                // player 2
                nameP2View.setText(sortedPlayers.get(1).getName());
                scoreP2View.setText(String.valueOf(sortedPlayers.get(1).getScore()));
                break;
            case 3:
                nameP4View.setVisibility(View.GONE);
                scoreP4View.setVisibility(View.GONE);
                imageP4View.setVisibility(View.GONE);
                // player 1
                nameP1View.setText(sortedPlayers.get(0).getName());
                scoreP1View.setText(String.valueOf(sortedPlayers.get(0).getScore()));
                // player 2
                nameP2View.setText(sortedPlayers.get(1).getName());
                scoreP2View.setText(String.valueOf(sortedPlayers.get(1).getScore()));
                // player 3
                nameP3View.setText(sortedPlayers.get(2).getName());
                scoreP3View.setText(String.valueOf(sortedPlayers.get(2).getScore()));
                break;
            case 4:
                // player 1
                nameP1View.setText(sortedPlayers.get(0).getName());
                scoreP1View.setText(String.valueOf(sortedPlayers.get(0).getScore()));
                // player 2
                nameP2View.setText(sortedPlayers.get(1).getName());
                scoreP2View.setText(String.valueOf(sortedPlayers.get(1).getScore()));
                // player 3
                nameP3View.setText(sortedPlayers.get(2).getName());
                scoreP3View.setText(String.valueOf(sortedPlayers.get(2).getScore()));
                // player 4
                nameP4View.setText(sortedPlayers.get(3).getName());
                scoreP4View.setText(String.valueOf(sortedPlayers.get(3).getScore()));
                break;
        }

        Log.d("test", "Ranking UI initialized");
    }

    /**
     * Sorts the players of a game by their score.
     * @return ArrayList of players sorted by their score
     * @author Petra Langenbacher
     */
    public ArrayList<Player> sortPlayersByScore() {

        // arraylist with sorted players
        ArrayList<Player> sortedPlayers = gameService.getGame().getPlayers();

        Collections.sort(sortedPlayers);

        return sortedPlayers;
    }


    // ===== Game Service Connection =====

    private ServiceConnection gameServiceCon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            gameService = binder.getService();
            gameServiceBound = true;
            gameService.registerListener(FinalRankingActivity.this);
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

    public void updatePlayerPosition(Location location) {
        // do nothing
    }

    @Override
    public void playerReachedTarget(boolean endOfGame) {
        // do nothing
    }


    /**
     * This method overrides the onBackPressed() method and
     * disables the back button so that the players can't navigate back and forward during a game.
     */
    @Override
    public void onBackPressed() {
        // do nothing
    }
}
