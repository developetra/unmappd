package com.example.unmappd.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.unmappd.R;

/**
 * MainActivity - Requests permission to access fine location and shows a button to start the game.
 *
 * @authors: Franziska Barckmann, Petra Langenbacher, Ann-Kathrin Schmid
 */
public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;

    /**
     * Calls methods to request permissions and initialise the UI elements when activity is created.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        // Initializes UI elements
        initUI();

        Log.d("test", "created Main");
    }

    /**
     * Requests permission to access fine location.
     */
    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    /**
     * Initialises UI elements.
     */
    private void initUI() {

        Button startGameButton = findViewById(R.id.startButton);

        startGameButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                startGameService();
                startActivity(intent);
            }
        });
    }

    public void onInfoButtonClicked(View view){

        AlertDialog.Builder infoDialog = new AlertDialog.Builder(MainActivity.this);
        infoDialog.setTitle("How Unmappd works:");
        infoDialog.setMessage("Set up the game by choosing number of rounds, number of players and player names. " +
                "Every player guesses the distance and direction from the current position to the four shown landmarks. " +
                "The better the guesses, the higher your received score. Your scores are shown in the ranking. " +
                "On the map you can see your position and the four landmarks. Choose a landmark you want to visit next " +
                "by clicking on its button. " +
                "As soon as you arrived that location, you'll get a small fact about it and you can continue with the " +
                "next round. Have fun! - Your Unmappd Team");
        infoDialog.setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        infoDialog.show();
    }

    /**
     * Starts GameService.
     */
    private void startGameService() {
        Log.d("test", "Starting game service");
        Intent serviceIntent = new Intent(getApplicationContext(), GameService.class);
        startService(serviceIntent);
    }

}
