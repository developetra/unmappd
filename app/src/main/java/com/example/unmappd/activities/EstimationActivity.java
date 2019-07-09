package com.example.unmappd.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unmappd.R;
import com.example.unmappd.data.Landmark;
import com.example.unmappd.data.Player;

import java.util.ArrayList;


/**
 * EstimationActivity - This class serves as activity where the players can enter their distance guesses.
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

        Log.d("test", "numberOfPlayers is" + numberOfPlayers);
        Log.d("test", "playerIndex is" + playerIndex);

    }

    /**
     * This method initializes the activities user interface by loading current player's name,
     * the four landmarks' names and the four landmarks' images.
     */
    private void initUI() {
        // Init UI with player name
        TextView nameView = findViewById(R.id.playerName);
        ArrayList<Player> players = gameService.getGame().getPlayers();
        nameView.setText(players.get(playerIndex-1).getName());

        // Get current selected landmarks
        ArrayList<Landmark> landmarkList = gameService.getSelectedLandmarks();
        Resources res = getResources();

        // Load landmark information for every landmark
        for (int i = 0; i < landmarkList.size(); i++) {

            int index = i+1;

            // set landmark name
            String nameIdString = "nameLandmark" + index ;
            int nameIdInt = res.getIdentifier(nameIdString, "id", getPackageName());

            TextView landmarkNameView1 = (TextView) findViewById(nameIdInt);
            landmarkNameView1.setText(landmarkList.get(i).getName());

            // set landmark image
            String imageIdString = "imageLandmark" + index;
            int imageIdInt = res.getIdentifier(imageIdString, "id", getPackageName());

            ImageView landmarkImageView1 = (ImageView) findViewById(imageIdInt);
            String mDrawableName = landmarkList.get(i).getPath();
            int resID = res.getIdentifier(mDrawableName, "drawable", getPackageName());
            Drawable drawable = res.getDrawable(resID);
            landmarkImageView1.setImageDrawable(drawable);
        }

        Log.d("test", "Estimation UI initialized");
    }

    /**
     * This method is called after the click on the "next" button.
     * It saves the user input to the current player and calls the gameservice to process the guess information.
     * If there is another player, the activity starts itself again, if there is no next player
     * the ranking activity is started.
     * @param view
     */
    public void startRanking (View view){

        // Get user input
        EditText inputdistance1 = findViewById(R.id.distanceLandmark1);
        EditText inputdistance2 = findViewById(R.id.distanceLandmark2);
        EditText inputdistance3 = findViewById(R.id.distanceLandmark3);
        EditText inputdistance4 = findViewById(R.id.distanceLandmark4);

        // Check that input is not empty, if empty notify user
        if( TextUtils.isEmpty(inputdistance1.getText())){
            inputdistance1.setError( "Please enter a distance." );}
        else if (TextUtils.isEmpty(inputdistance2.getText())){
            inputdistance2.setError( "Please enter a distance." );}
        else if (TextUtils.isEmpty(inputdistance3.getText())){
            inputdistance3.setError( "Please enter a distance." );}
        else if (TextUtils.isEmpty(inputdistance4.getText())){
            inputdistance4.setError( "Please enter a distance." );}
        else{
            // user input given -> save guesses
            int distance1 = Integer.parseInt(inputdistance1.getText().toString());
            int distance2 = Integer.parseInt(inputdistance2.getText().toString());
            int distance3 = Integer.parseInt(inputdistance3.getText().toString());
            int distance4 = Integer.parseInt(inputdistance4.getText().toString());

            gameService.getGame().getPlayers().get(playerIndex-1).addGuess(distance1);
            gameService.getGame().getPlayers().get(playerIndex-1).addGuess(distance2);
            gameService.getGame().getPlayers().get(playerIndex-1).addGuess(distance3);
            gameService.getGame().getPlayers().get(playerIndex-1).addGuess(distance4);

            // start processing of the guess of a player
            gameService.processGuess(playerIndex-1);

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
                // if no next player -> start ranking activity
                Intent intent = new Intent(this, RankingActivity.class);
                startActivity(intent);
            }
        }
    }

    // ===== Game Service Connection =====

    /**
     * Game Service Connection
     */
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

    /**
     * Listener method - NOT used in this activity.
     */
    public void updatePlayerPosition(Location location){
        // do nothing
    }

    /**
     * Listener method - NOT used in this activity.
     */
    public void playerReachedTarget(boolean endOfGame){
        // do nothing
    }

}
