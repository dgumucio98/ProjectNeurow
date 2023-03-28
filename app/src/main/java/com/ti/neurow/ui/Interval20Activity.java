package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;


import com.ti.neurow.GlobalVariables; // for access to finalListTimePower
import com.ti.neurow.VariableChanges; // for message listener
import com.ti.neurow.db.DatabaseHelper;
import com.ti.neurow.wkt.workouts; // for workout testing
import com.ti.neurow.R;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class Interval20Activity extends AppCompatActivity {

    // Define some elements
    Chronometer chron; // declare chronometer (count-up timer)
    Button btnAlyson, btnStartChron; // declare timer button
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

        // [TEST] see current value of loggedInUsername
        Toast.makeText(this,"[TEST] @Interval20Activity, loggedInUsername = " + GlobalVariables.loggedInUsername, Toast.LENGTH_LONG).show();


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

        // [TEST] Alyson button listener
        btnAlyson = (Button) findViewById(R.id.btnAlyson);
        btnAlyson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Prepare database, workout, and dynamic variable objects
                DatabaseHelper db = new DatabaseHelper(Interval20Activity.this);
                workouts workouts = new workouts();
                VariableChanges myChanges = new VariableChanges();

                myChanges.setMessageListener(new VariableChanges.MessageListener() {
                    @Override
                    public void onMessageChanged(String newMessage) {
                        Toast.makeText(Interval20Activity.this,newMessage,Toast.LENGTH_SHORT).show();
                    }
                });

                // Run ftpCalc workout method
                ArrayList pow = workouts.ftpCalc(myChanges,db);

                // Set global list to workout result list
                GlobalVariables.finalListTimePower = pow;

                // [TEST] view list
                Toast.makeText(Interval20Activity.this,"Resulting list: " + pow.toString(),Toast.LENGTH_LONG).show();

                // Start PostWorkoutActivity
                Intent i = new Intent(Interval20Activity.this, PostWorkoutActivity.class);
                startActivity(i); // Launch BLE Data View
                finish(); // can't go back

                Toast.makeText(Interval20Activity.this,"[TEST] Reached end of button listener",Toast.LENGTH_SHORT).show();

            }
        });

        TextView txtMessage = findViewById(R.id.txtTest);
        String message = getIntent().getStringExtra("message");
        txtMessage.setText(message);

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