package com.example.unmappd.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.unmappd.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GameService.GameServiceListener {

    private GoogleMap mMap;
    protected GameService gameService;
    protected boolean gameServiceBound;
    Location mLocation;
    private Location currentLocation;
    private Marker pMarker;
    MarkerOptions playerMarker = new MarkerOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Bind to service
        Intent bindIntent = new Intent(MapsActivity.this, GameService.class);
        bindService(bindIntent, gameServiceCon, Context.BIND_AUTO_CREATE);
        Log.d("test", "created Maps");
        Log.d("test", "Service bound to Maps");


    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    LatLng lm1 = new LatLng(49.898135, 10.9027636);
    LatLng lm2 = new LatLng(49.891796, 10.894640);
    LatLng lm3 = new LatLng(49.894568, 10.889364);
    LatLng lm4 = new LatLng(49.891111, 10.883208);

    private Marker lm1Marker;
    MarkerOptions lm1MarkerOptions = new MarkerOptions();


    private Marker lm2Marker;
    MarkerOptions lm2MarkerOptions = new MarkerOptions();

    private Marker lm3Marker;
    MarkerOptions lm3MarkerOptions = new MarkerOptions();

    private Marker lm4Marker;
    MarkerOptions lm4MarkerOptions = new MarkerOptions();


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
        lm1MarkerOptions.position(lm1);
        lm1Marker = mMap.addMarker(lm1MarkerOptions);

        lm1MarkerOptions.position(lm2);
        lm2Marker = mMap.addMarker(lm1MarkerOptions);

        lm1MarkerOptions.position(lm3);
        lm3Marker = mMap.addMarker(lm1MarkerOptions);

        lm1MarkerOptions.position(lm4);
        lm4Marker = mMap.addMarker(lm1MarkerOptions);
//
//        mMap.addMarker(new MarkerOptions().position(lm1).title("Marker LM1"));
//        mMap.addMarker(new MarkerOptions().position(lm2).title("Marker LM2"));
//        mMap.addMarker(new MarkerOptions().position(lm3).title("Marker LM3"));
//        mMap.addMarker(new MarkerOptions().position(lm4).title("Marker LM4"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lm1));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( 14.0f ) );

    }




    // ===== Game Service Connection =====

    private ServiceConnection gameServiceCon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            gameService = binder.getService();
            gameServiceBound = true;
            gameService.registerListener(MapsActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            gameServiceBound = false;
        }
    };

    // Listener Methods
   // Marker playerMarker;


    public void updatePlayerPosition(Location location){
        currentLocation = location;
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(pMarker == null){
            playerMarker.position(currentLatLng).title("Your Position");
            playerMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            pMarker = mMap.addMarker(playerMarker);
        }else{
            pMarker.setPosition(currentLatLng);
        }

        //mMap.addMarker(new MarkerOptions().position(newPos).title("Aktuelle Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        Log.d("test", "Maps is updating player position");

    }

    public void onRadioButtonClicked(View view) {
        if (((RadioButton) view).isChecked()) {
            RadioGroup raGroup = (RadioGroup) findViewById(R.id.radioGroup);
            raGroup.setVisibility(View.GONE);
            //mMap.clear();
        }

        switch (view.getId()) {
            case R.id.radioButton1:
                lm2Marker.remove();
                lm3Marker.remove();
                lm4Marker.remove();
                break;
            case R.id.radioButton2:
                lm1Marker.remove();
                lm3Marker.remove();
                lm4Marker.remove();
                break;
            case R.id.radioButton3:
                lm2Marker.remove();
                lm1Marker.remove();
                lm4Marker.remove();
                break;
            case R.id.radioButton4:
                lm2Marker.remove();
                lm3Marker.remove();
                lm1Marker.remove();
                break;

        }
    }
}
