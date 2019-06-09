package com.example.unmappd.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.unmappd.R;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializes UI elements
        initUI();

        Log.d("test", "created Main");
    }

    private void initUI() {

        Button startGameButton = findViewById(R.id.startButton);

        startGameButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                startGameService();
                startActivity(intent);
            }
        });
    }

    private void startGameService() {
        Log.d("test", "Starting game service");
        Intent serviceIntent = new Intent(getApplicationContext(), GameService.class);
        startService(serviceIntent);
    }

}
