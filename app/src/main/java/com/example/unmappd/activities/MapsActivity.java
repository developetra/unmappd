package com.example.unmappd.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unmappd.R;
import com.example.unmappd.data.Landmark;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GameService.GameServiceListener {

    private GoogleMap mMap;
    protected GameService gameService;
    protected boolean gameServiceBound;
    private Location currentLocation;

    private boolean serviceConnected= false;
    private boolean mapReady=false;
    // markers
    private Marker pMarker;
    private MarkerOptions playerMarkerOptions = new MarkerOptions();
    private Marker lm1Marker;
    private Marker lm2Marker;
    private Marker lm3Marker;
    private Marker lm4Marker;

    private ArrayList<Landmark> landmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Bind to service
        Intent bindIntent = new Intent(MapsActivity.this, GameService.class);
        bindService(bindIntent, gameServiceCon, Context.BIND_AUTO_CREATE);
        Log.d("test", "created Maps");
        Log.d("test", "Service bound to Maps");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**This method manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // get and set markers of landmarks
        mapReady=true;
        displayMarkers();

        TextView info = findViewById(R.id.infoText);
        info.setText("Please choose the location you want to visit next");
    }

    private void displayMarkers() {
        if(serviceConnected && mapReady){

            // get current position of player and set blue marker
            currentLocation = gameService.getPlayerPosition();
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            playerMarkerOptions.position(currentLatLng).title("You");
            playerMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            pMarker = mMap.addMarker(playerMarkerOptions);

            Log.d("test", Double.toString(currentLocation.getLatitude()));


            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f ) );
            mMap.getUiSettings().setMapToolbarEnabled(false);

            LatLng lm1 = new LatLng(landmarks.get(0).getLatitude(), landmarks.get(0).getLongitude());
            LatLng lm2 = new LatLng(landmarks.get(1).getLatitude(), landmarks.get(1).getLongitude());
            LatLng lm3 = new LatLng(landmarks.get(2).getLatitude(), landmarks.get(2).getLongitude());
            LatLng lm4 = new LatLng(landmarks.get(3).getLatitude(), landmarks.get(3).getLongitude());

            Log.d("test", Double.toString(landmarks.get(0).getLatitude()));

            lm1Marker = mMap.addMarker(new MarkerOptions().position(lm1).title(landmarks.get(0).getName()));
            lm2Marker = mMap.addMarker(new MarkerOptions().position(lm2).title(landmarks.get(1).getName()));
            lm3Marker = mMap.addMarker(new MarkerOptions().position(lm3).title(landmarks.get(2).getName()));
            lm4Marker = mMap.addMarker(new MarkerOptions().position(lm4).title(landmarks.get(3).getName()));

            // set text of landmark buttons
            changeTextOfButton(lm1Marker, currentLocation, R.id.radioButton1);
            changeTextOfButton(lm2Marker, currentLocation, R.id.radioButton2);
            changeTextOfButton(lm3Marker, currentLocation, R.id.radioButton3);
            changeTextOfButton(lm4Marker, currentLocation, R.id.radioButton4);
        }
    }

    // ===== Game Service Connection =====

    private ServiceConnection gameServiceCon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            gameService = binder.getService();
            gameServiceBound = true;
            gameService.registerListener(MapsActivity.this);

            landmarks = gameService.getSelectedLandmarks();

            serviceConnected=true;
            displayMarkers();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            gameServiceBound = false;
        }
    };


    private void changeTextOfButton(Marker lmMarker, Location currentPos, int radioButton) {
        Location targetLocation = new Location("");
        targetLocation.setLongitude(lmMarker.getPosition().longitude);
        targetLocation.setLatitude(lmMarker.getPosition().latitude);

        float distance = currentPos.distanceTo(targetLocation);
        int distanceRounded = Math.round(distance);
        Button btn = (Button) findViewById(radioButton);
        btn.setText(lmMarker.getTitle() + " - " + distanceRounded + "m");

    }

    // ===== Listener Methods

    public void updatePlayerPosition(Location location){
        currentLocation = location;
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        pMarker.setPosition(currentLatLng);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        Log.d("test", "Maps is updating player position");

    }

    @Override
    public void playerReachedTarget(boolean endOfGame) {

        int duration = Toast.LENGTH_LONG;

        Toast choose = Toast.makeText(MapsActivity.this, "Reached target", duration);
        choose.setGravity(Gravity.TOP, 0, 200);

        // end of game reached
        if(endOfGame){
            AlertDialog.Builder chooseTarget = new AlertDialog.Builder(MapsActivity.this);
            //chooseTarget.setTitle("Choose your target location");
            chooseTarget.setMessage("Reached target position. The game is over");

            chooseTarget.setNeutralButton("Show final result",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //got to ranking
                            Intent intent = new Intent(MapsActivity.this, RankingActivity.class);
                            startActivity(intent);
                        }
                    });
            chooseTarget.create().show();

            //TODO RankingActivity ohne "Continue" Button

        }

        // end of game not reached
        if(!endOfGame){
            AlertDialog.Builder chooseTarget = new AlertDialog.Builder(MapsActivity.this);
            //chooseTarget.setTitle("Choose your target location");
            chooseTarget.setMessage("You reached your target position");

            chooseTarget.setNeutralButton("Continue with next round",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //got to next round
                            Intent intent = new Intent(MapsActivity.this, EstimationActivity.class);
                            Bundle b = new Bundle();
                            int numberOfPlayers = gameService.getGame().getNumberOfPlayers();
                            b.putInt("numberOfPlayers", numberOfPlayers);
                            b.putInt("playerIndex", 1);
                            intent.putExtras(b);
                            startActivity(intent);

                        }
                    });
            chooseTarget.create().show();
        }

    }

    public void onRadioButtonClicked(View view) {
        if (((RadioButton) view).isChecked()) {
            RadioGroup raGroup = (RadioGroup) findViewById(R.id.radioGroup);
            raGroup.setVisibility(View.GONE);
            TextView info = findViewById(R.id.infoText);
            info.setText("please walk to your chosen location");

        }

        switch (view.getId()) {
            case R.id.radioButton1:
                lm2Marker.remove();
                lm3Marker.remove();
                lm4Marker.remove();

                gameService.setTargetLandmark(landmarks.get(0));
                break;
            case R.id.radioButton2:
                lm1Marker.remove();
                lm3Marker.remove();
                lm4Marker.remove();
                gameService.setTargetLandmark(landmarks.get(1));
                break;
            case R.id.radioButton3:
                lm2Marker.remove();
                lm1Marker.remove();
                lm4Marker.remove();
                gameService.setTargetLandmark(landmarks.get(2));
                break;
            case R.id.radioButton4:
                lm2Marker.remove();
                lm3Marker.remove();
                lm1Marker.remove();
                gameService.setTargetLandmark(landmarks.get(3));
                break;

        }
    }


}
