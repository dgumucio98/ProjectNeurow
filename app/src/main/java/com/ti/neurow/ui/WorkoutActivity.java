package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
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
import timber.log.Timber;

public class WorkoutActivity extends AppCompatActivity {

    // Define some elements
    TextView txtWorkoutAttribute, txtWorkoutName, txtDistanceMetric, txtCaloriesMetric;
    Chronometer chron; // declare chronometer (count-up timer)
    Button btnAlyson, btnBegin;
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
        txtWorkoutAttribute = (TextView) findViewById(R.id.txtWorkoutAttribute); // workout "subtitle"
        txtWorkoutName = (TextView) findViewById(R.id.txtWorkoutName); // workout name (interval/pace)
        chron = (Chronometer) findViewById(R.id.simpleChronometer); // chronometer
        btnBegin = (Button) findViewById(R.id.btnBegin); // button that starts workouts

        // Receive workout choice data from WorkoutMainActivity
        int colorToSet = getIntent().getIntExtra("attributeColor", Color.WHITE); // default is white (means problem)
        String textToSet = getIntent().getStringExtra("attributeText");
        String titleToSet = getIntent().getStringExtra("attributeName");

        Toast.makeText(WorkoutActivity.this, "[TEST] Getting method name!", Toast.LENGTH_SHORT).show();
        String methodName = getIntent().getStringExtra("methodName");
        Toast.makeText(WorkoutActivity.this, "[TEST] Got method name! methodName: " + methodName, Toast.LENGTH_SHORT).show();


        // Change Workout UI elements
        txtWorkoutAttribute.setText(textToSet);
        txtWorkoutAttribute.setTextColor(colorToSet);
        txtWorkoutName.setText(titleToSet);


        // Update on-screen Metrics using handler
//        Handler handler = new Handler();
//        Runnable runnableCode = new Runnable() {
//            @Override
//            public void run() {
//                // Update the text of the metric TextViews
//                txtDistanceMetric.setText(GlobalVariables.distance35.toString());
//                txtCaloriesMetric.setText(GlobalVariables.totalCalories33.toString());
//
//                // Polling rate (good range ~400-800ms)
//                handler.postDelayed(this, 500);
//            }
//        };


        // ***** This is where the workout gets called and begins *****
        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // when btnBegin is clicked

                // 1. Declare/Initialize instances, set listeners

                Toast.makeText(WorkoutActivity.this, "[TEST] (Inside green button) method name: " + methodName, Toast.LENGTH_SHORT).show();
                Toast.makeText(WorkoutActivity.this, "[TEST] Green button clicked!", Toast.LENGTH_SHORT).show();

                DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this); // prepare database
                workouts workouts = new workouts(); // construct workouts instance
                Toast.makeText(WorkoutActivity.this, "[TEST] Database created!", Toast.LENGTH_SHORT).show();


                // Dataframe 33
                VariableChanges myGlobalTime33 = new VariableChanges(); // declare instance of VariableChanges
                GlobalVariables.globalTimeInstance33 = myGlobalTime33; // set the GlobalVariable variable globalTimeInstance33 to instance

                // Dataframe 35
                VariableChanges myGlobalTime35 = new VariableChanges(); // declare instance of VariableChanges
                GlobalVariables.globalTimeInstance35 = myGlobalTime35; //set the GlobalVariable variable globalTimeInstance35 to instance

                // Dataframe 3D
                VariableChanges myGlobalTime3D = new VariableChanges(); // declare instance of VariableChanges
                GlobalVariables.globalTimeInstance3D = myGlobalTime3D; //set the GlobalVariable variable globalTimeInstance3D to

                // Dataframe 33 Change Listener
                GlobalVariables.globalTimeInstance33.setTimeListener(new VariableChanges.TimeListener() { // populates data33 table with the global variables of each variable
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
                                    "[TEST] Successfully entered table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        } else {
                            Toast.makeText(
                                    WorkoutActivity.this,
                                    "[TEST] Did not enter table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        }
                    }
                });

                // Dataframe 35 Change Listener
                GlobalVariables.globalTimeInstance35.setTimeListener(new VariableChanges.TimeListener() { // populates data35 table with the global variables of each variable
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
                                    "[TEST] Successfully entered table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        } else {
                            Toast.makeText(
                                    WorkoutActivity.this,
                                    "[TEST] Did not enter table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        }
                    }
                });

                // Dataframe 3D Change Listener
                GlobalVariables.globalTimeInstance3D.setMessageListener(new VariableChanges.MessageListener() { // populates data3D table with the global variables of each variable
                    @Override
                    public void onMessageChanged(String newMessage) {
                        boolean success = db.add_3Dmessage(GlobalVariables.pol3D, GlobalVariables.message3D);
                        if (success == true) {
                            Toast.makeText(
                                    WorkoutActivity.this,
                                    "[TEST] Successfully entered table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        } else {
                            Toast.makeText(
                                    WorkoutActivity.this,
                                    "[TEST] Did not enter table",
                                    Toast.LENGTH_SHORT
                            ).show(); //Testing
                        }
                    }
                });

                // 2. Setup for workout calls

                // Listeners
                VariableChanges pzSetChanges = new VariableChanges(); // listener for which pz to be in
                VariableChanges pzFixChanges = new VariableChanges(); // listener for pz user feedback
                VariableChanges suggestionChanges = new VariableChanges(); // listener for suggestion

                // Listen for command of which pz to be in
                pzSetChanges.setMessageListener(new VariableChanges.MessageListener() {
                    @Override
                    public void onMessageChanged(String newMessage) {
                        Timber.d(newMessage);
                    }
                });

                // Listen for user feedback of power zone leaves
                pzFixChanges.setMessageListener(new VariableChanges.MessageListener() {
                    @Override
                    public void onMessageChanged(String newMessage) {
                        Timber.d(newMessage);
                    }
                });

                // Listen for post-workout suggestions
                suggestionChanges.setMessageListener(new VariableChanges.MessageListener() {
                    @Override
                    public void onMessageChanged(String newMessage) {
                        // TODO: Display visual UI element for suggestion given
                        Timber.d(newMessage);
                    }
                });

                // 3. Start on-screen Chronometer

                // Chronometer Functionality
                if (!isChronRunning) { // if NOT running
                    chron.setBase(SystemClock.elapsedRealtime()); // start counting from current time
                    chron.start(); // start the chronometer
                    btnBegin.setText("Stop");
                    isChronRunning = true; // set status to true
                }
                else {
                    chron.stop();
                    isChronRunning = false; // set status to false
                    btnBegin.setText("Start");
                }

                // 4. Call workout methods

                Toast.makeText(WorkoutActivity.this, "[TEST] Made it before workout if statement", Toast.LENGTH_SHORT).show();


                Toast.makeText(WorkoutActivity.this, "[TEST] Called ftpCalc", Toast.LENGTH_SHORT).show();
                ArrayList pow = workouts.ftpCalc(db); // call, set global list to workout result list
                Toast.makeText(WorkoutActivity.this, "[TEST] Workout finished", Toast.LENGTH_SHORT).show();
                GlobalVariables.finalListTimePower = pow; // set global graphing variable
                Toast.makeText(WorkoutActivity.this, "[TEST] Pow has been set after workout", Toast.LENGTH_SHORT).show();


                // Conditions to call specific workout (UNCOMMENT)
//                if (methodName == "ftpCalc") { // CALL FTPCALC
//                    Toast.makeText(WorkoutActivity.this, "[TEST] Called ftpCalc", Toast.LENGTH_SHORT).show();
//                    ArrayList pow = workouts.ftpCalc(db); // call, set global list to workout result list
//                    Toast.makeText(WorkoutActivity.this, "Workout finished", Toast.LENGTH_SHORT).show();
//                    GlobalVariables.finalListTimePower = pow; // set global graphing variable
//                    // TODO: Read ftp global variable
//                    // TODO: Make text box appear with feedback
//                }
//                else if (methodName == "interval1") { // CALL INTERVAL1
//                    ArrayList pow = workouts.interval1(pzSetChanges, pzFixChanges, db);
//                    GlobalVariables.finalListTimePower = pow; // set global graphing variable
//                    workouts.intervalSuggestion(suggestionChanges, "1", GlobalVariables.failCount);
//                }
//                else if (methodName == "interval2") { // CALL INTERVAL2
//                    ArrayList pow = workouts.interval2(pzSetChanges, pzFixChanges, db);
//                    GlobalVariables.finalListTimePower = pow; // set global graphing variable
//                    workouts.intervalSuggestion(suggestionChanges, "2", GlobalVariables.failCount);
//                }
//                else if (methodName == "interval3") { // CALL INTERVAL3
//                    ArrayList pow = workouts.interval3(pzSetChanges, pzFixChanges, db);
//                    GlobalVariables.finalListTimePower = pow; // set global graphing variable
//                    workouts.intervalSuggestion(suggestionChanges, "3", GlobalVariables.failCount);
//                }
//                else if (methodName == "pace20") { // CALL PACE20
//                    ArrayList pow = workouts.pace(pzSetChanges, pzFixChanges, 20,db);
//                    GlobalVariables.finalListTimePower = pow; // set global graphing variable
//                    workouts.paceSuggestion(suggestionChanges, "20", GlobalVariables.failCount);
//
//                }
//                else if (methodName == "pace30") { // CALL PACE30
//                    ArrayList pow = workouts.pace(pzSetChanges, pzFixChanges, 30,db);
//                    GlobalVariables.finalListTimePower = pow; // set global graphing variable
//                    workouts.paceSuggestion(suggestionChanges, "30", GlobalVariables.failCount);
//                }
//                else if (methodName == "pace40") { // CALL PACE40
//                    ArrayList pow = workouts.pace(pzSetChanges, pzFixChanges, 40,db);
//                    GlobalVariables.finalListTimePower = pow; // set global graphing variable
//                    workouts.paceSuggestion(suggestionChanges, "30", GlobalVariables.failCount);
//                }

                // 5. Prepare to leave screen

                // Exit WorkoutActivity, launch PostWorkoutActivity
                Intent i = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                startActivity(i); // Launch BLE Data View
                finish(); // can't go back
            }
        });
    }

    @Override
    public void onBackPressed() { // handle back button press during workout
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