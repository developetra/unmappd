package com.example.unmappd.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.unmappd.R;

public class MainActivity extends AppCompatActivity implements GameService.GameServiceListener{

    protected GameService gameService;
    protected boolean gameServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button = findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(intent);

                // Bind to service
                Intent bindIntent = new Intent(MainActivity.this, GameService.class);
                bindService(bindIntent, gameServiceCon, Context.BIND_AUTO_CREATE);
            }
        });

        Log.d("test", "created Main");
    }

    // ===== Game Service Connection =====

    private ServiceConnection gameServiceCon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            gameService = binder.getService();
            gameServiceBound = true;
            gameService.registerListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            gameServiceBound = false;
        }
    };

    // Listener Methods
    public void updatePlayerPosition(Location location){
        // do nothing
        Log.d("test", "Main listened");
    }

}
