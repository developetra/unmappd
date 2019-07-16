package com.example.unmappd.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.unmappd.R;
import com.example.unmappd.data.Game;
import com.example.unmappd.data.Player;

import java.util.ArrayList;

/**
 * Setup Activity - This class serves as activity where a new game can be created with a chosen number of rounds and players.
 *
 * @author Petra Langenbscher
 */
public class SetupActivity extends AppCompatActivity implements GameService.GameServiceListener {

    protected GameService gameService;
    protected boolean gameServiceBound;

    boolean hasLocation = false;


    /**
     * Initialises Ui elements and adapts name input fields to chosen number of players.
     *
     * @param savedInstanceState
     */
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
        Spinner playersSpinner = (Spinner) findViewById(R.id.players_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> playersAdapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        playersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        playersSpinner.setAdapter(playersAdapter);
        playersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                View player2 = findViewById(R.id.InputPlayer2);
                View player3 = findViewById(R.id.InputPlayer3);
                View player4 = findViewById(R.id.InputPlayer4);
                switch (pos) {
                    case 0:
                        player2.setVisibility(View.GONE);
                        player3.setVisibility(View.GONE);
                        player4.setVisibility(View.GONE);
                        break;
                    case 1:
                        player2.setVisibility(View.VISIBLE);
                        player3.setVisibility(View.GONE);
                        player4.setVisibility(View.GONE);
                        break;
                    case 2:
                        player2.setVisibility(View.VISIBLE);
                        player3.setVisibility(View.VISIBLE);
                        player4.setVisibility(View.GONE);
                        break;
                    case 3:
                        player2.setVisibility(View.VISIBLE);
                        player3.setVisibility(View.VISIBLE);
                        player4.setVisibility(View.VISIBLE);
                        break;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /**
     * Calls method to create a new game and starts a new activity to start estimation process.
     *
     * @param view
     */
    public void startEstimation(View view) {
        int numberOfPlayers = initGame();


//        while(gameService.getPlayerPosition() == null){
//            gameService.
//            Spinner
//
//        }

        // Open new Activity
        Intent intent = new Intent(this, EstimationActivity.class);
        Bundle b = new Bundle();
        b.putInt("numberOfPlayers", numberOfPlayers + 1);
        b.putInt("playerIndex", 1);
        intent.putExtras(b);
        startActivity(intent);
    }


    /**
     * Creates a new game with chosen number of rounds, number of players and names of players.
     *
     * @return number of players
     */
    private int initGame() {
        // Get number of rounds and players
        Spinner roundsspinner = findViewById(R.id.rounds_spinner);
        int numberOfRounds = roundsspinner.getSelectedItemPosition() + 1;
        Spinner playersspinner = findViewById(R.id.players_spinner);
        int numberOfPlayers = playersspinner.getSelectedItemPosition();

        // Get player names
        EditText inputName1 = findViewById(R.id.editText1);
        String name1 = inputName1.getText().toString();
        Player player1 = new Player(name1, 0);
        EditText inputName2 = findViewById(R.id.editText2);
        String name2 = inputName2.getText().toString();
        Player player2 = new Player(name2, 0);
        EditText inputName3 = findViewById(R.id.editText3);
        String name3 = inputName3.getText().toString();
        Player player3 = new Player(name3, 0);
        EditText inputName4 = findViewById(R.id.editText4);
        String name4 = inputName4.getText().toString();
        Player player4 = new Player(name4, 0);

        // List of players
        ArrayList<Player> players = new ArrayList<>();

        // Add players to List
        switch (numberOfPlayers) {
            case 0:
                players.add(player1);
                break;
            case 1:
                players.add(player1);
                players.add(player2);
                break;
            case 2:
                players.add(player1);
                players.add(player2);
                players.add(player3);
                break;
            case 3:
                players.add(player1);
                players.add(player2);
                players.add(player3);
                players.add(player4);
                break;
        }

        // Create new Game and set rounds and players
        Game currentGame = new Game(numberOfRounds, players);

        gameService.setGame(currentGame);

        gameService.initFirstGame();

        Log.d("test", "Number of Rounds is " + String.valueOf(currentGame.getRounds()));
        Log.d("test", "Number of Players is " + String.valueOf(numberOfPlayers));
        return numberOfPlayers;
    }


    /**
     * Game Service Connection.
     */
    private ServiceConnection gameServiceCon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            gameService = binder.getService();
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


    /**
     * Updates player position - NOT used in this activity.
     *
     * @param location
     */
    @SuppressLint("ResourceAsColor")
    public void updatePlayerPosition(Location location) {
        Button setupButton = (Button)findViewById(R.id.startButton);
        setupButton.setEnabled(true);
        setupButton.setText("Setup Game");

    }

    public void playerReachedTarget(boolean endOfGame) {
        // do nothing
    }

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Waiting for GPS");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

}
