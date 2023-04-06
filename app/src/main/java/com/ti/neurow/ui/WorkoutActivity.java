package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import com.ti.neurow.db.data33;
import com.ti.neurow.db.data35;

import com.ti.neurow.GlobalVariables; // for access to finalListTimePower
import com.ti.neurow.VariableChanges; // for message listener
import com.ti.neurow.db.DatabaseHelper;
import com.ti.neurow.wkt.workouts; // for workout testing
import com.ti.neurow.R;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class WorkoutActivity extends AppCompatActivity {

    // Define some elements
    TextView txtWorkoutAttribute;
    Chronometer chron; // declare chronometer (count-up timer)
    Button btnAlyson, btnStartChron;
    boolean isChronRunning = false; // define boolean state of the timer
    GifImageView gifRipple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Tweak visible elements
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide Action bar and Status bar
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Lock orientation to landscape
        setContentView(R.layout.activity_workout);

        // Define elements
        txtWorkoutAttribute = (TextView) findViewById(R.id.txtWorkoutAttribute);


//        // Chronometer Functionality
//        chron = (Chronometer) findViewById(R.id.simpleChronometer);
//        btnStartChron = (Button) findViewById(R.id.btnBegin);
//
//        btnStartChron.setOnClickListener(new View.OnClickListener() { // start/stop button listener
//            @Override
//            public void onClick(View v) {
//                if (!isChronRunning) { // if NOT running
//                    chron.setBase(SystemClock.elapsedRealtime()); // start counting from current time
//                    chron.start(); // start the chronometer
//                    btnStartChron.setText("Stop");
//                    isChronRunning = true; // set status to true
//                }
//                else {
//                    chron.stop();
//                    isChronRunning = false; // set status to false
//                    btnStartChron.setText("Start");
//                }
//            }
//        });

        // Get data from WorkoutMainActivity
        int colorToSet = getIntent().getIntExtra("attributeColor", Color.WHITE); // default is white (means problem)
        String textToSet = getIntent().getStringExtra("attributeText");

        // Set txtWorkoutAttribute text and color
        txtWorkoutAttribute.setText(textToSet);
        txtWorkoutAttribute.setTextColor(colorToSet);

        // [TEST] Alyson button listener
        btnAlyson = (Button) findViewById(R.id.btnAlyson);
        btnAlyson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //[TEST] testing how to populate database with realtime BLE data changes
                // Prepare database
                DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);
                //Database population begins on button click
                //THIS IS FOR DATAFRAME33
                VariableChanges myGlobalTime33 = new VariableChanges(); // declare instance of VariableChanges
                GlobalVariables.globalTimeInstance33 = myGlobalTime33; //set the GlobalVaribale variable globalTimeInstance to instance
                // [TEST] Test global time variable change happening
                //the listener populates data33 table with the global variables of each variable
                //whether the load is successful gets toasted
                GlobalVariables.globalTimeInstance33.setTimeListener(new VariableChanges.TimeListener() {
                    @Override
                    public void onTimeChanged(double newTime) {
                        data33 realdata33 = new data33(
                                GlobalVariables.elapsedTime33,
                                GlobalVariables.intervalCount33,
                                GlobalVariables.averagePower33,
                                GlobalVariables.totalCalories33,
                                GlobalVariables.splitIntAvgPace33,
                                GlobalVariables.splitIntAvgPwr33,
                                GlobalVariables.splitIntAvgCal33,
                                GlobalVariables.lastSplitTime33,
                                GlobalVariables.lastSplitDist33
                        );
                        boolean success = db.add_dataframe33(realdata33);
                        if (success == true) {
                            Toast.makeText(
                                    WorkoutActivity.this,
                                    "Successfully entered table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        } else {
                            Toast.makeText(
                                    WorkoutActivity.this,
                                    "Did not enter table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        }
                    }
                });

                //THIS IS FOR DATAFRAME35
                VariableChanges myGlobalTime35 = new VariableChanges(); // declare instance of VariableChanges
                GlobalVariables.globalTimeInstance35 = myGlobalTime35; //set the GlobalVaribale variable globalTimeInstance to instance
                // [TEST] Test global time variable change happening
                //the listener populates data35 table with the global variables of each variable
                //whether the load is successful gets toasted
                GlobalVariables.globalTimeInstance35.setTimeListener(new VariableChanges.TimeListener() {
                    @Override
                    public void onTimeChanged(double newTime) {
                        data35 realdata35 = new data35(
                                GlobalVariables.elapsedTime35,
                                GlobalVariables.distance35,
                                GlobalVariables.driveLength35,
                                GlobalVariables.driveTime35,
                                GlobalVariables.strokeRecTime35,
                                GlobalVariables.strokeDistance35,
                                GlobalVariables.peakDriveForce35,
                                GlobalVariables.averageDriveForce35,
                                GlobalVariables.workPerStroke35,
                                GlobalVariables.strokeCount35
                        );
                        boolean success = db.add_dataframe35(realdata35);
                        if (success == true) {
                            Toast.makeText(
                                    WorkoutActivity.this,
                                    "Successfully entered table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        } else {
                            Toast.makeText(
                                    WorkoutActivity.this,
                                    "Did not enter table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        }
                    }
                });


                // Prepare workout, and dynamic variable objects
                workouts workouts = new workouts();
                VariableChanges myMessage = new VariableChanges();
                VariableChanges myDouble = new VariableChanges();

                // [TEST] Test variable change happening within ftpCalc method with string message
                myMessage.setMessageListener(new VariableChanges.MessageListener() {
                    @Override
                    public void onMessageChanged(String newMessage) {
                        Toast.makeText(WorkoutActivity.this, "[TEST] " + newMessage, Toast.LENGTH_SHORT).show();
                    }
                });

                // [TEST] Test variable change happening within ftpCalc method with double time
                myDouble.setTimeListener(new VariableChanges.TimeListener() {
                    @Override
                    public void onTimeChanged(double newTime) {
                        Toast.makeText(WorkoutActivity.this, "[TEST] " + newTime, Toast.LENGTH_SHORT).show();
                    }
                });

                // [TEST] Run ftpCalc workout method
                ArrayList pow = workouts.ftpCalc(myDouble, myMessage, db);

                // [TEST] Set global list to workout result list
                GlobalVariables.finalListTimePower = pow;

                // [TEST] view list
                Toast.makeText(WorkoutActivity.this, "[TEST] Resulting ArrayList: " + pow.toString(), Toast.LENGTH_SHORT).show();

                // [TEST] Start PostWorkoutActivity
                Intent i = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                startActivity(i); // Launch BLE Data View
                finish(); // can't go back
            }
        });
    }

    @Override // Handle back button press during workout
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this);
        builder.setTitle("Exit current workout?");
        builder.setMessage("Your workout is currently running. Any unsaved progress will be lost.");

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