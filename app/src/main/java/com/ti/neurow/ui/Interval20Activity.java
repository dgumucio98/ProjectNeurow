package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;

import com.ti.neurow.R;

import pl.droidsonroids.gif.GifImageView;

public class Interval20Activity extends AppCompatActivity {

    // Define some variables
    Chronometer chron; // declare chronometer (count-up timer)
    Button btnStartChron; // declare timer button
    boolean isChronRunning = false; // define boolean state of the timer
    GifImageView gifRipple; // define ripple gif

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Tweak visible elements
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide Action bar and Status bar
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Lock orientation to landscape
        setContentView(R.layout.activity_interval20);

        // Chronometer Functionality
        chron = (Chronometer) findViewById(R.id.simpleChronometer);
        btnStartChron = (Button) findViewById(R.id.btnBegin);

        btnStartChron.setOnClickListener(new View.OnClickListener() { // start/stop button listener
            @Override
            public void onClick(View v) {
                if (!isChronRunning) { // if NOT running
                    chron.setBase(SystemClock.elapsedRealtime()); // start counting from current time
                    chron.start(); // start the chronometer
                    btnStartChron.setText("Stop");
                    isChronRunning = true; // set status to true
                }
                else {
                    chron.stop();
                    isChronRunning = false; // set status to false
                    btnStartChron.setText("Start");
                }
            }
        });


        gifRipple = (GifImageView) findViewById(R.id.gifRippleRed);
        btnStartChron = (Button) findViewById(R.id.btnBegin);
        btnStartChron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifRipple.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override // Handle back button press during workout
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Current Workout");
        builder.setMessage("Are you sure you want to exit your current workout? Any unsaved progress will be lost.");

        builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.show();
    }
}