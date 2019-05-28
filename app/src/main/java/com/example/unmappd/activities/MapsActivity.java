package com.example.unmappd.activities;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.unmappd.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GameService.GameServiceListener{

    private GoogleMap mMap;
    protected GameService gameService;
    protected boolean gameServiceBound;
    Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("test", "created Maps");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Bamberg and move the camera
        LatLng bamberg = new LatLng(49.898135, 10.9027636);
        mMap.addMarker(new MarkerOptions().position(bamberg).title("Marker in Bamberg"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bamberg));

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

    public void updatePlayerPosition(Location location){
        mMap.clear();
        LatLng newPos = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(newPos).title("Aktuelle Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));
        Log.d("test", "Maps listened");

    }
}
