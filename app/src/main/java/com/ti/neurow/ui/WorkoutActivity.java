package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
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

    // Define UI elements
    TextView txtWorkoutAttribute, txtWorkoutName, txtDistanceMetric, txtCaloriesMetric, txtUserID; // text views
    Chronometer chron; // chronometer (count-up timer)
    Button btnBegin; // green begin button
    boolean isChronRunning = false; // define boolean state of the timer

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
        txtDistanceMetric = (TextView) findViewById(R.id.txtDistanceMetric); // distance text box
        txtCaloriesMetric = (TextView) findViewById(R.id.txtCaloriesMetric); // calories text box

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

        // Delete database tables to make sure it is new data being populated
        DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this); // prepare database
        db.delete_dataframe33_table();
        db.delete_dataframe35_table();
        db.delete_table3D();

        // ***** This is where the workout call procedure begins *****
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
                        Toast.makeText(WorkoutActivity.this, newMessage, Toast.LENGTH_SHORT).show();
                        //Timber.d(newMessage);
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
                        // suggestion given will just be one string message
                        Timber.d(newMessage);
                    }
                });

//                // 3. Start on-screen Chronometer
//
//                // Chronometer Functionality
//                if (!isChronRunning) { // if NOT running
//                    chron.setBase(SystemClock.elapsedRealtime()); // start counting from current time
//                    chron.start(); // start the chronometer
//                    btnBegin.setText("Stop");
//                    isChronRunning = true; // set status to true
//                }
//                else {
//                    chron.stop();
//                    isChronRunning = false; // set status to false
//                    btnBegin.setText("Start");
//                }

                // 4. Call workout methods

                // Conditions to call specific workout
                if (methodName.equals("ftpCalc")) { // CALL FTPCALC
                    ftpCalcTask ftpCalcTask = new ftpCalcTask();
                    ftpCalcTask.execute();
                }
                else if (methodName.equals("interval1")) { // CALL INTERVAL1
                    // Create workout's background task and execute
                }
                else if (methodName.equals("interval2")) { // CALL INTERVAL2
                    // Create workout's background task and execute
                }
                else if (methodName.equals("interval3")) { // CALL INTERVAL3
                    // Create workout's background task and execute
                }
                else if (methodName.equals("pace20")) { // CALL PACE20
                    // Create workout's background task and execute
                }
                else if (methodName.equals("pace30")) { // CALL PACE30
                    // Create workout's background task and execute
                }
                else if (methodName.equals("pace40")) { // CALL PACE40
                    // Create workout's background task and execute
                }
            }
        });
    }

    // ftpCalc Background functionality class: defines background task
    private class ftpCalcTask extends AsyncTask<Void, Integer, Integer> {

        @Override // 1st function for background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // Function algorithm beginning
            int sum = 0;
            int length = 0;
            ArrayList<Double> powtimearray = new ArrayList<Double>(); // create new arraylist

            double pastTime = 0.0;
            int infiniteCount = 0;
            int i = 0;
            
            // main loop
            while (db.getTime_33() < 180.0) { // [TEST] 3 minutes
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double) db.getPower());

                // Update UI elements
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;

                // Send data to main UI thread
                publishProgress(distance, calories); // Update the UI with the current counter value

                pastTime = db.getTime_33();
                i++;
            }

            double avgPow = (double) sum / length; // calculate average power
            
            int ftp = (int) (0.95 * avgPow); // calculate ftp (95% of average power)
            GlobalVariables.ftp = ftp; // set ftp as global so UI can display it

            // Load power zones
            int pz_1 = 0; //Very Easy: <55% of FTP
            int pz_2 = (int) (0.56 * ftp); // Moderate: 56%-75% of FTP
            int pz_3 = (int) (0.76 * ftp); // Sustainable: 76%-90% of FTP
            int pz_4 = (int) (0.91 * ftp); // Challenging: 91%-105% of FTP
            int pz_5 = (int) (1.06 * ftp); // Hard: 106%-120% of FTP
            int pz_6 = (int) (1.21 * ftp); // Very Hard: 121%-150% of FTP
            int pz_7 = (int) (1.51 * ftp); // Max Effort: >151% of FTP

            // populate database User table
            db.updateuserFTP(GlobalVariables.loggedInUsername, ftp, pz_1, pz_2, pz_3, pz_4, pz_5, pz_6, pz_7);

            // Set global arraylist of time and power
            GlobalVariables.finalListTimePower = powtimearray;
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int dist = values[0]; // extract distance value passed
            int cal = values[1]; // extract calories value passed

            // Update the UI with the current counter value
            txtDistanceMetric.setText("Distance: " + dist);
            // Update other UI elements as needed
            txtCaloriesMetric.setText("Calories: " + cal);
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // This is called when the background task is finished

            // Exit WorkoutActivity, launch PostWorkoutActivity
            Intent i = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
            startActivity(i); // Launch BLE Data View
            finish(); // can't go back
        }
    }

    // interval1 background functionality class
    private class interval1Task extends AsyncTask<Void, Integer, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
        }
    }

    // interval2 background functionality class
    private class interval2Task extends AsyncTask<Void, Integer, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
        }
    }

    // interval3 background functionality class
    private class interval3Task extends AsyncTask<Void, Integer, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
        }
    }

    // pace20 background functionality class
    private class pace20Task extends AsyncTask<Void, Integer, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
        }
    }

    // pace30 background functionality class
    private class pace30Task extends AsyncTask<Void, Integer, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
        }
    }

    // pace40 background functionality class
    private class pace40Task extends AsyncTask<Void, Integer, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
        }
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