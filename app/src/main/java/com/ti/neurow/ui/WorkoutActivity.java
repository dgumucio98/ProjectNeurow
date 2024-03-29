package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ti.neurow.ble.pm5Utility;
import com.ti.neurow.db.data33;
import com.ti.neurow.db.data35;

import com.ti.neurow.GlobalVariables; // for access to finalListTimePower
import com.ti.neurow.VariableChanges; // for message listener
import com.ti.neurow.db.DatabaseHelper;
import com.ti.neurow.wkt.workouts; // for workout testing
import com.ti.neurow.R;

import java.util.ArrayList;

import timber.log.Timber;

public class WorkoutActivity extends AppCompatActivity {

    pm5Utility testingDevice = new pm5Utility(GlobalVariables.globalBleDevice);
    //testingDevice.setPollSpeed("FASTEST");

    // Define UI elements
    ImageView iconHeartbeat1, iconHeartbeat2;
    Button btnStart, btnStop; // buttons
    TextView txtStartPrompt, txtWorkoutAttribute, txtWorkoutName, txtFeedback, txtTimeMetric, txtDistanceMetric, txtCaloriesMetric, txtAvgPwrMetric, txtDriveLengthMetric,
            txtDriveTimeMetric, txtAvgDriveForceMetric, txtStrokeCountMetric, txtIntervalPZMetric, txtIntervalFixMetric, txtPaceFeedbackMetric, txtInstructionMetric; // metrics

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toast.makeText(getApplicationContext(), "[TEST] WorkoutActivity created!", Toast.LENGTH_SHORT).show();

        // Hide Action bar and Status bar, lock orientation to landscape
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide Action bar and Status bar
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Lock orientation to landscape
        setContentView(R.layout.activity_workout);

        // Define elements
        txtWorkoutAttribute = findViewById(R.id.txtWorkoutAttribute);
        txtWorkoutName = findViewById(R.id.txtWorkoutName);
        txtStartPrompt = findViewById(R.id.txtStartPrompt);
        txtFeedback = findViewById(R.id.txtFeedback);
        btnStart = findViewById(R.id.btnStart);
        iconHeartbeat1 = findViewById(R.id.iconHeartbeat1);
        iconHeartbeat2 = findViewById(R.id.iconHeartbeat2);

        btnStop = findViewById(R.id.btnStop); // button that stops workouts
        RelativeLayout MetricsRelativeLayout = findViewById(R.id.MetricsRelativeLayout); // metrics layout
        RelativeLayout StartRelativeLayout = findViewById(R.id.StartRelativeLayout); // starting layout

        // Assign metrics to UI elements
        txtTimeMetric = findViewById(R.id.txtTimeMetric); // distance text box
        txtDistanceMetric = findViewById(R.id.txtDistanceMetric); // distance text box
        txtCaloriesMetric = findViewById(R.id.txtCaloriesMetric); // calories text box
        txtDriveLengthMetric = findViewById(R.id.txtDriveLengthMetric); // drive length metric
        txtDriveTimeMetric = findViewById(R.id.txtDriveTimeMetric); // drive time metric
        txtAvgPwrMetric = findViewById(R.id.txtAvgPwrMetric); // average power text box
        txtAvgDriveForceMetric = findViewById(R.id.txtAvgDriveForce); // split time text box
        txtStrokeCountMetric = findViewById(R.id.txtStrokeCountMetric); // split time text box
        txtIntervalPZMetric = findViewById(R.id.txtIntervalPZMetric); // interval PZ feedback text box
        txtIntervalFixMetric = findViewById(R.id.txtIntervalFixMetric); // interval fix feedback text box
        txtPaceFeedbackMetric = findViewById(R.id.txtPaceFeedbackMetric); // pace feedback text box

        // Receive workout choice data from DashboardActivity
        int colorToSet = getIntent().getIntExtra("attributeColor", Color.WHITE); // default is white (means problem)
        String textToSet = getIntent().getStringExtra("attributeText");
        String titleToSet = getIntent().getStringExtra("attributeName");
        String methodName = getIntent().getStringExtra("methodName");

        // Change Workout UI elements
        txtWorkoutAttribute.setText(textToSet);
        txtWorkoutAttribute.setTextColor(colorToSet);
        txtWorkoutName.setText(titleToSet);

        // Delete database tables to make sure it is new data being populated
        DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this); // prepare database
        db.delete_dataframe33_table();
        db.delete_dataframe35_table();
        db.delete_table3D();

        // Listener for button to start workout
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // when btnStart is clicked

                // Task 1: Hide start button, prompt, and organize metric layout
                StartRelativeLayout.setVisibility(View.GONE); // hide starting layout
                MetricsRelativeLayout.setVisibility(View.VISIBLE); // show metrics layout

                // Task 2: Prepare and start workout tasks

                // 1. Declare/Initialize instances, set listeners
                DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this); // prepare database
                workouts workouts = new workouts(); // construct workouts instance

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
                    }
                });

                // Dataframe 3D Change Listener
                GlobalVariables.globalTimeInstance3D.setMessageListener(new VariableChanges.MessageListener() { // populates data3D table with the global variables of each variable
                    @Override
                    public void onMessageChanged(String newMessage) {
                        boolean success = db.add_3Dmessage(GlobalVariables.pol3D, GlobalVariables.message3D);
                    }
                });

                //Starting workout state for pm5
                testingDevice.startWorkOut();
                testingDevice.start33();
                testingDevice.start3D();
                testingDevice.start35();

                // 2. Reset workout interrupt flags
                GlobalVariables.timeout = false;
                GlobalVariables.stopTask = false;

                // 3. Call workout methods through conditions
                if (methodName.equals("ftpCalc")) { // CALL FTPCALC

                    // Remove feedback elements, since we don't show any for FTP Calculator
                    txtFeedback.setVisibility(View.GONE);
                    iconHeartbeat1.setVisibility(View.GONE);
                    iconHeartbeat2.setVisibility(View.GONE);

                    ftpCalcTask ftpCalcTask = new ftpCalcTask();
                    ftpCalcTask.execute();

                } else if (methodName.equals("interval1")) { // CALL INTERVAL1
                    interval1Task interval1Task = new interval1Task();
                    interval1Task.execute();

                } else if (methodName.equals("interval2")) { // CALL INTERVAL2
                    interval2Task interval2Task = new interval2Task();
                    interval2Task.execute();

                } else if (methodName.equals("interval3")) { // CALL INTERVAL3
                    interval3Task interval3Task = new interval3Task();
                    interval3Task.execute();

                } else if (methodName.equals("pace20")) { // CALL PACE20
                    pace20Task pace20Task = new pace20Task();
                    pace20Task.execute();

                } else if (methodName.equals("pace30")) { // CALL PACE30
                    pace30Task pace30Task = new pace30Task();
                    pace30Task.execute();

                } else if (methodName.equals("pace40")) { // CALL PACE40
                    pace40Task pace40Task = new pace40Task();
                    pace40Task.execute();

                } else if (methodName.equals("demo")) { // CALL DEMO
                    demoTask demoTask = new demoTask();
                    demoTask.execute();
                }
            } // end of onClick

        });

        // Listener for button to stop workout
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this);
                builder.setTitle("Stop workout?");
                builder.setMessage("Any ongoing workout progress will be lost.");

                // Stop option is clicked
                builder.setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WorkoutActivity.super.onBackPressed(); // go back (should be DashboardActivity)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        GlobalVariables.stopTask = true; // workout was cut short, set flag
                    }
                });

                // Continue option is clicked
                builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss(); // nothing to do here, close dialog
                    }
                });
                AlertDialog dialog = builder.show();
            }
        });
    }

    // ftpCalc background functionality class
    private class ftpCalcTask extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: workout functionality
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // Function algorithm beginning
            int sum = 0;
            int length = 0;
            ArrayList<Double> powtimearray = new ArrayList<>(); // create new arraylist
            double pastTime = db.getTime_33();
            int Iterations = 0;
            int numIterations = 0;

            // Main workout loop
            while (db.getTime_33() < 1200.0 && !GlobalVariables.stopTask)  {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;

                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    // Only populate when t0 != t1
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }

                // Update metric values
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;

                // Send data to main UI thread after each loop iteration
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, avgPower, avgDriveForce, strokeCount); // Update the UI with the current counter value
            }
            Timber.d("[TEST] 30 sec num of iterations: " + Iterations);

            // Final tasks
            if (!GlobalVariables.stopTask) { // only if workout (loop) was completed
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
                //write power zones into global variables?
                GlobalVariables.pz_1 = pz_1;
                GlobalVariables.pz_2 = pz_2;
                GlobalVariables.pz_3 = pz_3;
                GlobalVariables.pz_4 = pz_4;
                GlobalVariables.pz_5 = pz_5;
                GlobalVariables.pz_6 = pz_6;
                GlobalVariables.pz_7 = pz_7;

                // [TEST] Watch array contents for redundancies
                Timber.d("[TEST] powtimearray: %s", powtimearray.toString());


                // Populate database User table
                db.updateuserFTP(GlobalVariables.loggedInUsername, ftp,
                        pz_1, pz_2, pz_3, pz_4, pz_5, pz_6, pz_7);

                // Set global arraylist of time and power
                GlobalVariables.finalListTimePower = powtimearray;
            }
            Timber.d("DONE WITH doInBackground, ABOUT TO RETURN 0");
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            // Extract (using values array), Update UI elements here
            int time = (int) values[0];
            int minutes = time / 60;
            int seconds = time % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds); // display as MM:SS
            int dist = (int) values[1];
            int cal = (int) values[2];
            int driveLength = (int) values[3];
            int driveTime = (int) values[4];
            int avgPwr = (int) values[5];
            int avgDriveForce = (int) values[6];
            int strokeCount = (int) values[7];

            // Update the UI with the current counter value
            txtTimeMetric.setText(formattedTime);
            txtDistanceMetric.setText(dist + " m");
            txtCaloriesMetric.setText(cal + " cal");
            txtDriveLengthMetric.setText(driveLength + " m");
            txtDriveTimeMetric.setText(driveTime + " s");
            txtAvgPwrMetric.setText(avgPwr + " W");
            txtAvgDriveForceMetric.setText(avgDriveForce + " lbf");
            txtStrokeCountMetric.setText(Integer.toString(strokeCount));
            txtIntervalFixMetric.setText("Row for 20 minutes at a challenging, but sustainable pace!");
            txtIntervalPZMetric.setText(""); // not needed for ftpCalc
            txtPaceFeedbackMetric.setText(""); // not needed for FTP Calc
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            // Stop BLE data polling
            testingDevice.end33();
            Timber.d("[TEST] Polling of 33 ended");
            testingDevice.end35();
            Timber.d("[TEST] Polling of 35 ended");
            testingDevice.end3D();
            Timber.d("[TEST] Polling of 3D ended");
            testingDevice.endWorkOut();

            // Define intent and pass workout name to PostWorkoutActivity
            if (!GlobalVariables.stopTask) { // only launch PostWorkoutActivity if workout wasn't cut short
                Intent goToPostWorkoutActivity = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                goToPostWorkoutActivity.putExtra("workoutName", "ftpCalc"); // pass workout name data - necessary for specific suggestions

                startActivity(goToPostWorkoutActivity); // launch PostWorkoutActivity
                finish(); // can't go back
            }
            else { // if workout was cut short
                GlobalVariables.stopTask = false; // reset flag
                finish(); // destroy workout activity, go back to dashboard
            }
            Timber.d("DONE WITH onPostExecute");

        }
    }

    // Interval1 background functionality class
    private class interval1Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // interval1 (20 min) method code
            int count = 0; // count subsequent errors
            int failCount = 0; // actual fail count
            int sum = 0; // summing up power
            int length = 0; // number of power entries to calc average
            ArrayList<Double> powtimearray = new ArrayList<>(); // arraylist to hold time and power

            double pastTime = db.getTime_33();
            int Iterations = 0;
            int numIterations = 0;

            String pzMessage = ""; // declaring power zone message
            String fixMessage = ""; // declaring power zone error message

            // 5 min at zone 2
            while (db.getTime_33() <= 300 && !GlobalVariables.stopTask) { //TODO: CHANGE BACK TO 300
                // this is adding all of the powers to then get average
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                Iterations ++;
                double currentTime = db.getTime_33();
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!";
                        failCount++;
                        count = 0;
                    }
                } else { // only consecutive power zone exits increment count
                    fixMessage = "You are in zone! Keep it up!";
                    count = 0;
                }
                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update metric values
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();
                int strokeCount = GlobalVariables.strokeCount35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 340 && db.getTime_33() > 300 && !GlobalVariables.stopTask) { //change back to 340 and 300
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                Iterations ++;
                double currentTime = db.getTime_33();
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();


                // Send data to main UI thread

                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 360 && db.getTime_33() > 340 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();


                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 400 && db.getTime_33() > 360 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 420 && db.getTime_33() > 400 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 460 && db.getTime_33() > 420 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 480 && db.getTime_33() > 460 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 520 && db.getTime_33() > 480 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 540 && db.getTime_33() > 520 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 580 && db.getTime_33() > 540 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 600 && db.getTime_33() > 580 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 640 && db.getTime_33() > 600 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 660 && db.getTime_33() > 640 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 700 && db.getTime_33() > 660 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 720 && db.getTime_33() > 700 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 760 && db.getTime_33() > 720 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 780 && db.getTime_33() > 760 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 820 && db.getTime_33() > 780 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 840 && db.getTime_33() > 820 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 880 && db.getTime_33() > 840 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 20 sec at zone 2
            while (db.getTime_33() <= 900 && db.getTime_33() > 880 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 5 min at zone 1
            while (db.getTime_33() <= 1200 && db.getTime_33() > 900 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 1";
                if (db.getPower() >= GlobalVariables.pz_2) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 1!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            if (!GlobalVariables.stopTask) { // only if workout (loop) was completed
                double avgPow = (double) sum / (double) length; //uncomment
                GlobalVariables.failCount = failCount;
                db.add_history(GlobalVariables.loggedInUsername, "interval1", failCount, avgPow);
                GlobalVariables.finalListTimePower = powtimearray;
            }
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            // Extract (using values array), Update UI elements here
            int time = (int) values[0];
            int minutes = time / 60;
            int seconds = time % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds); // display as MM:SS
            int dist = (int) values[1];
            int cal = (int) values[2];
            int driveLength = (int) values[3];
            int driveTime = (int) values[4];
            int avgPwr = (int) values[5];
            int avgDriveForce = (int) values[6];
            int strokeCount = (int) values[7];
            String pzMessage = (String) values[8];
            String fixMessage = (String) values[9];

            // Update the UI with the current counter value
            txtTimeMetric.setText(formattedTime);
            txtDistanceMetric.setText(dist + " m");
            txtCaloriesMetric.setText(cal + " cal");
            txtDriveLengthMetric.setText(driveLength + " m");
            txtDriveTimeMetric.setText(driveTime + " s");
            txtAvgPwrMetric.setText(avgPwr + " W");
            txtAvgDriveForceMetric.setText(avgDriveForce + "  lbf");
            txtStrokeCountMetric.setText(Integer.toString(strokeCount));
            txtIntervalPZMetric.setText(pzMessage);
            txtIntervalFixMetric.setText(fixMessage);
            txtPaceFeedbackMetric.setText("");

        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Stop BLE data polling
            testingDevice.end33();
            Timber.d("[TEST] Polling of 33 ended");
            testingDevice.end35();
            Timber.d("[TEST] Polling of 35 ended");
            testingDevice.end3D();
            Timber.d("[TEST] Polling of 3D ended");
            testingDevice.endWorkOut();

            // Define intent and pass workout name to PostWorkoutActivity
            if (!GlobalVariables.stopTask) { // only launch PostWorkoutActivity if workout wasn't cut short
                Intent goToPostWorkoutActivity = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                goToPostWorkoutActivity.putExtra("workoutName", "interval1"); // pass workout name data - necessary for specific suggestions

                startActivity(goToPostWorkoutActivity); // launch PostWorkoutActivity
                finish(); // can't go back
            }
            else { // if workout was cut short
                GlobalVariables.stopTask = false; // reset flag
                finish(); // destroy workout activity, go back to dashboard
            }
        }
    }

    // Interval2 background functionality class
    private class interval2Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // Interval2 (30 min) method code
            int count = 0; //count subsequent errors
            int failCount = 0; //actual fail count
            int sum = 0; //summing up power
            int length = 0; //number of power entries to calc average
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            double pastTime = db.getTime_33();
            int Iterations = 0;
            int numIterations = 0;

            String pzMessage = ""; //declaring power zone message
            String fixMessage = ""; //declaring power zone error message

            // 6 min at zone 3
            while (db.getTime_33() <= 360 && !GlobalVariables.stopTask) { //TODO: change back to 360
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 3";
                if (db.getPower() < GlobalVariables.pz_3 || db.getPower() >= GlobalVariables.pz_4) {
                    count++;
                    if (count > 2) {
                        fixMessage = "You aren't in power zone 3!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update metric values
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();
                int strokeCount = GlobalVariables.strokeCount35.intValue();

                // Send data to main UI thread

                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            // UNCOMMENT FOR FULL WORKOUT
            // 5 min at zone 1
            while (db.getTime_33() <= 660 && db.getTime_33() > 360 && !GlobalVariables.stopTask) { //change back to 660 and 360
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 1";
                if (db.getPower() >= GlobalVariables.pz_2) {
                    count++;
                    if (count > 2) {
                        fixMessage = "You aren't in power zone 1!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 5 min at zone 4
            while (db.getTime_33() <= 960 && db.getTime_33() > 660 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 4";
                //System.out.println("Row in power zone 4");
                if (db.getPower() < GlobalVariables.pz_4 || db.getPower() >= GlobalVariables.pz_5) {
                    count++;
                    if (count > 2) {
                        fixMessage = "You aren't in power zone 4!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 5 min at zone 1
            while (db.getTime_33() <= 1260 && db.getTime_33() > 960 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 1";
                if (db.getPower() >= GlobalVariables.pz_2) {
                    count++;
                    if (count > 2) {
                        fixMessage = "You aren't in power zone 1!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 4 min at zone 5
            while (db.getTime_33() <= 1500 && db.getTime_33() > 1260 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 2) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 5 min at zone 1
            while (db.getTime_33() <= 1800 && db.getTime_33() > 1500 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 1";
                if (db.getPower() >= GlobalVariables.pz_2) {
                    count++;
                    if (count > 2) {
                        fixMessage = "You aren't in power zone 1!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            if (!GlobalVariables.stopTask) { // only if workout (loop) was completed
                double avgPow = (double) sum / (double) length; //uncomment
                GlobalVariables.failCount = failCount;
                db.add_history(GlobalVariables.loggedInUsername, "interval2", failCount, avgPow);
                GlobalVariables.finalListTimePower = powtimearray;
            }
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            // Extract (using values array), Update UI elements here
            int time = (int) values[0];
            int minutes = time / 60;
            int seconds = time % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds); // display as MM:SS
            int dist = (int) values[1];
            int cal = (int) values[2];
            int driveLength = (int) values[3];
            int driveTime = (int) values[4];
            int avgPwr = (int) values[5];
            int avgDriveForce = (int) values[6];
            int strokeCount = (int) values[7];
            String pzMessage = (String) values[8];
            String fixMessage = (String) values[9];

            // Update the UI with the current counter value
            txtTimeMetric.setText(formattedTime);
            txtDistanceMetric.setText(dist + " m");
            txtCaloriesMetric.setText(cal + " cal");
            txtDriveLengthMetric.setText(driveLength + " m");
            txtDriveTimeMetric.setText(driveTime + " s");
            txtAvgPwrMetric.setText(avgPwr + " W");
            txtAvgDriveForceMetric.setText(avgDriveForce + "  lbf");
            txtStrokeCountMetric.setText(Integer.toString(strokeCount));
            txtIntervalPZMetric.setText(pzMessage);
            txtIntervalFixMetric.setText(fixMessage);
            txtPaceFeedbackMetric.setText("");

        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            // Stop BLE data polling
            testingDevice.end33();
            Timber.d("[TEST] Polling of 33 ended");
            testingDevice.end35();
            Timber.d("[TEST] Polling of 35 ended");
            testingDevice.end3D();
            Timber.d("[TEST] Polling of 3D ended");
            testingDevice.endWorkOut();

            // Define intent and pass workout name to PostWorkoutActivity
            if (!GlobalVariables.stopTask) { // only launch PostWorkoutActivity if workout wasn't cut short
                Intent goToPostWorkoutActivity = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                goToPostWorkoutActivity.putExtra("workoutName", "interval2"); // pass workout name data - necessary for specific suggestions

                startActivity(goToPostWorkoutActivity); // launch PostWorkoutActivity
                finish(); // can't go back
            }
            else { // if workout was cut short
                GlobalVariables.stopTask = false; // reset flag
                finish(); // destroy workout activity, go back to dashboard
            }
        }
    }

    // Interval3 background functionality class
    private class interval3Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            //interval 3 (40 min) method code
            int count = 0; //count subsequent errors
            int failCount = 0; //actual fail count
            int sum = 0; //summing up power
            int length = 0; //number of power entries to calc average
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            double pastTime = db.getTime_33();
            int Iterations = 0;
            int numIterations = 0;

            String pzMessage = ""; //declaring power zone message
            String fixMessage = ""; //declaring power zone error message


            // 2 min at zone 2
            while (db.getTime_33() <= 120 && !GlobalVariables.stopTask) { //TODO: change back to 120
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                //System.out.println("Row in power zone 2");
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update metric values
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();
                int strokeCount = GlobalVariables.strokeCount35.intValue();

                // Send data to main UI thread

                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 1 min at zone 5
            while (db.getTime_33() <= 180 && db.getTime_33() > 120 && !GlobalVariables.stopTask) { //change back to 180 120
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                //System.out.println("Row at a fast pace!!");
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 2 min at zone 2
            while (db.getTime_33() <= 300 && db.getTime_33() > 180 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                //System.out.println("Row in power zone 2");
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 1 min at zone 5
            while (db.getTime_33() <= 360 && db.getTime_33() > 300 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                //System.out.println("Row at a fast pace!!");
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 2 min at zone 2
            while (db.getTime_33() <= 480 && db.getTime_33() > 360 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                //System.out.println("Row in power zone 2");
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 1 min at zone 5
            while (db.getTime_33() <= 540 && db.getTime_33() > 480 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 5";
                //System.out.println("Row at a fast pace!!");
                if (db.getPower() < GlobalVariables.pz_5 || db.getPower() >= GlobalVariables.pz_6) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 2 min at zone 2
            while (db.getTime_33() <= 660 && db.getTime_33() > 540 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 2";
                //System.out.println("Row in power zone 2");
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 10 min at zone 4
            while (db.getTime_33() <= 1260 && db.getTime_33() > 660 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 4";
                //System.out.println("Row in power zone 4");
                if (db.getPower() < GlobalVariables.pz_4 || db.getPower() >= GlobalVariables.pz_5) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 4!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 5 min at zone 1
            while (db.getTime_33() <= 1560 && db.getTime_33() > 1260 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 1";
                //System.out.println("Row in power zone 1");
                if (db.getPower() >= GlobalVariables.pz_2) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 1!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 10 min at zone 4
            while (db.getTime_33() <= 2160 && db.getTime_33() > 1560 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 4";
                //System.out.println("Row in power zone 4");
                if (db.getPower() < GlobalVariables.pz_4 || db.getPower() >= GlobalVariables.pz_5) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 4!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            // 5 min at zone 1
            while (db.getTime_33() <= 2460 && db.getTime_33() > 2160 && !GlobalVariables.stopTask) {
                Iterations ++;
                double currentTime = db.getTime_33();
                sum += db.getPower();
                length += 1;
                pzMessage = "Row in power zone 1";
                //System.out.println("Row in power zone 1");
                if (db.getPower() >= GlobalVariables.pz_2) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 1!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 20000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 15,200 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }

                } else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            if (!GlobalVariables.stopTask) { // only if workout (loop) was completed
                double avgPow = (double) sum / (double) length; //uncomment
                GlobalVariables.failCount = failCount;
                db.add_history(GlobalVariables.loggedInUsername, "interval3", failCount, avgPow);
                GlobalVariables.finalListTimePower = powtimearray;
            }
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            // Extract (using values array), Update UI elements here
            int time = (int) values[0];
            int minutes = time / 60;
            int seconds = time % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds); // display as MM:SS
            int dist = (int) values[1];
            int cal = (int) values[2];
            int driveLength = (int) values[3];
            int driveTime = (int) values[4];
            int avgPwr = (int) values[5];
            int avgDriveForce = (int) values[6];
            int strokeCount = (int) values[7];
            String pzMessage = (String) values[8];
            String fixMessage = (String) values[9];

            // Update the UI with the current counter value
            txtTimeMetric.setText(formattedTime);
            txtDistanceMetric.setText(dist + " m");
            txtCaloriesMetric.setText(cal + " cal");
            txtDriveLengthMetric.setText(driveLength + " m");
            txtDriveTimeMetric.setText(driveTime + " s");
            txtAvgPwrMetric.setText(avgPwr + " W");
            txtAvgDriveForceMetric.setText(avgDriveForce + "  lbf");
            txtStrokeCountMetric.setText(Integer.toString(strokeCount));
            txtIntervalPZMetric.setText(pzMessage);
            txtIntervalFixMetric.setText(fixMessage);
            txtPaceFeedbackMetric.setText("");

        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Stop BLE data polling
            testingDevice.end33();
            Timber.d("[TEST] Polling of 33 ended");
            testingDevice.end35();
            Timber.d("[TEST] Polling of 35 ended");
            testingDevice.end3D();
            Timber.d("[TEST] Polling of 3D ended");
            testingDevice.endWorkOut();

            // Define intent and pass workout name to PostWorkoutActivity
            if (!GlobalVariables.stopTask) { // only launch PostWorkoutActivity if workout wasn't cut short
                Intent goToPostWorkoutActivity = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                goToPostWorkoutActivity.putExtra("workoutName", "interval3"); // pass workout name data - necessary for specific suggestions

                startActivity(goToPostWorkoutActivity); // launch PostWorkoutActivity
                finish(); // can't go back
            }
            else { // if workout was cut short
                GlobalVariables.stopTask = false; // reset flag
                finish(); // destroy workout activity, go back to dashboard
            }
        }
    }

    // Pace20 background functionality class
    private class pace20Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            //pace code 20 min
            int failCount = 0;
            int count = 0;
            int sum = 0;
            int length = 0;
            ArrayList<Double> powtimearray = new ArrayList<>(); // arraylist to hold time and power

            String pzMessage = "";
            String fixMessage = "";

            int Iterations = 0; //how many loop iterations
            int numIterations = 0; //time=time iterations

            double pastTime = db.getTime_33();

            pzMessage = "Begin Rowing!";
            while (db.getTime_33() <= 1200 && !GlobalVariables.stopTask) { // less than 20-min
                Iterations++;
                sum += db.getPower();
                length += 1;
                double currentTime = db.getTime_33();
                // if difference between current stroke and previous stroke is greater than 4 watts
                if (Math.abs(db.getPower() - db.getPastPower()) > 2) { //TODO: what num is good here
                    count++;
                    if (count > 2) {
                        fixMessage = "Your power output is inconsistent!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "Nice pace, Keep it up!";
                    count = 0;
                }
                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 10000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 10,000 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }
                }
                else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();
                
                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            if (!GlobalVariables.stopTask) { // only if workout (loop) was completed
                double avgPow = (double) sum / (double) length; //uncomment
                GlobalVariables.failCount = failCount;
                db.add_history(GlobalVariables.loggedInUsername, "pace20", failCount, avgPow);
                GlobalVariables.finalListTimePower = powtimearray;
            }
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            // Extract (using values array), Update UI elements here
            int time = (int) values[0];
            int minutes = time / 60;
            int seconds = time % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds); // display as MM:SS
            int dist = (int) values[1];
            int cal = (int) values[2];
            int driveLength = (int) values[3];
            int driveTime = (int) values[4];
            int avgPwr = (int) values[5];
            int avgDriveForce = (int) values[6];
            int strokeCount = (int) values[7];
            String pzMessage = (String) values[8];
            String fixMessage = (String) values[9];

            // Update the UI with the current counter value
            txtTimeMetric.setText(formattedTime);
            txtDistanceMetric.setText(dist + " m");
            txtCaloriesMetric.setText(cal + " cal");
            txtDriveLengthMetric.setText(driveLength + " m");
            txtDriveTimeMetric.setText(driveTime + " s");
            txtAvgPwrMetric.setText(avgPwr + " W");
            txtAvgDriveForceMetric.setText(avgDriveForce + " lbf");
            txtStrokeCountMetric.setText(Integer.toString(strokeCount));
            txtIntervalFixMetric.setText(fixMessage);
            txtIntervalPZMetric.setText(pzMessage);
            txtPaceFeedbackMetric.setText(""); // not needed for this workout

        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Stop BLE data polling
            testingDevice.end33();
            Timber.d("[TEST] Polling of 33 ended");
            testingDevice.end35();
            Timber.d("[TEST] Polling of 35 ended");
            testingDevice.end3D();
            Timber.d("[TEST] Polling of 3D ended");
            testingDevice.endWorkOut();

            // Define intent and pass workout name to PostWorkoutActivity
            if (!GlobalVariables.stopTask) { // only launch PostWorkoutActivity if workout wasn't cut short
                Intent goToPostWorkoutActivity = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                goToPostWorkoutActivity.putExtra("workoutName", "pace20"); // pass workout name data - necessary for specific suggestions

                startActivity(goToPostWorkoutActivity); // launch PostWorkoutActivity
                finish(); // can't go back
            }
            else { // if workout was cut short
                GlobalVariables.stopTask = false; // reset flag
                finish(); // destroy workout activity, go back to dashboard
            }
        }
    }

    // Pace30 background functionality class
    private class pace30Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            //pace code 30 min
            int failCount = 0;
            int count = 0;
            int sum = 0;
            int length = 0;
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            String pzMessage = "";
            String fixMessage = "";

            int Iterations = 0; //how many loop iterations
            int numIterations = 0; //time=time iterations

            double pastTime = db.getTime_33();

            pzMessage = "Begin Rowing!";
            while (db.getTime_33() <= 1800 && !GlobalVariables.stopTask) { // less than 30-min
                Iterations++;
                sum += db.getPower();
                length += 1;
                double currentTime = db.getTime_33();
                //if difference between current stroke and previous stroke is greater than 4 watts
                if (Math.abs(db.getPower() - db.getPastPower()) > 2) { //TODO: what num is good here
                    count++;
                    if (count > 2) {
                        fixMessage = "Your power output is inconsistent!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "Nice pace, keep it up!";
                    count = 0;
                }
                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 10000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 10,000 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }
                }
                else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update metric values
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();
                int strokeCount = GlobalVariables.strokeCount35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            if (!GlobalVariables.stopTask) { // only if workout (loop) was completed
                double avgPow = (double) sum / (double) length; //uncomment
                GlobalVariables.failCount = failCount;
                db.add_history(GlobalVariables.loggedInUsername, "pace30", failCount, avgPow);
                GlobalVariables.finalListTimePower = powtimearray;
            }
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            // Extract (using values array), Update UI elements here
            int time = (int) values[0];
            int minutes = time / 60;
            int seconds = time % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds); // display as MM:SS
            int dist = (int) values[1];
            int cal = (int) values[2];
            int driveLength = (int) values[3];
            int driveTime = (int) values[4];
            int avgPwr = (int) values[5];
            int avgDriveForce = (int) values[6];
            int strokeCount = (int) values[7];
            String pzMessage = (String) values[8];
            String fixMessage = (String) values[9];

            // Update the UI with the current counter value
            txtTimeMetric.setText(formattedTime);
            txtDistanceMetric.setText(dist + " m");
            txtCaloriesMetric.setText(cal + " cal");
            txtDriveLengthMetric.setText(driveLength + " m");
            txtDriveTimeMetric.setText(driveTime + " s");
            txtAvgPwrMetric.setText(avgPwr + " W");
            txtAvgDriveForceMetric.setText(avgDriveForce + " lbf");
            txtStrokeCountMetric.setText(Integer.toString(strokeCount));
            txtIntervalFixMetric.setText(fixMessage);
            txtIntervalPZMetric.setText(pzMessage);
            txtPaceFeedbackMetric.setText(""); // not needed for this workout


        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Stop BLE data polling
            testingDevice.end33();
            Timber.d("[TEST] Polling of 33 ended");
            testingDevice.end35();
            Timber.d("[TEST] Polling of 35 ended");
            testingDevice.end3D();
            Timber.d("[TEST] Polling of 3D ended");
            testingDevice.endWorkOut();

            // Define intent and pass workout name to PostWorkoutActivity
            if (!GlobalVariables.stopTask) { // only launch PostWorkoutActivity if workout wasn't cut short
                Intent goToPostWorkoutActivity = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                goToPostWorkoutActivity.putExtra("workoutName", "pace30"); // pass workout name data - necessary for specific suggestions

                startActivity(goToPostWorkoutActivity); // launch PostWorkoutActivity
                finish(); // can't go back
            }
            else { // if workout was cut short
                GlobalVariables.stopTask = false; // reset flag
                finish(); // destroy workout activity, go back to dashboard
            }
        }
    }

    // Pace40 background functionality class
    private class pace40Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            //pace code 40 min
            int failCount = 0;
            int count = 0;
            int sum = 0;
            int length = 0;
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            String pzMessage = "";
            String fixMessage = "";

            int Iterations = 0; //how many loop iterations
            int numIterations = 0; //time=time iterations

            double pastTime = db.getTime_33();

            pzMessage = "Begin Rowing!";
            // TODO: change back to 2400
            while (db.getTime_33() <= 2400 && !GlobalVariables.stopTask) { // less than 40-min
                Iterations++;
                sum += db.getPower();
                length += 1;
                double currentTime = db.getTime_33();
                //if difference between current stroke and previous stroke is greater than 4 watts
                if (Math.abs(db.getPower() - db.getPastPower()) > 2) { //TODO: what num is good here
                    count++;
                    if (count > 2) {
                        fixMessage = "Your power output is inconsistent!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "Nice pace, keep it up!";
                    count = 0;
                }
                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 10000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 10,000 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }
                }
                else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }
                // Update metric values
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();
                int strokeCount = GlobalVariables.strokeCount35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, avgPower, driveLength, driveTime, avgDriveForce, strokeCount, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            if (!GlobalVariables.stopTask) { // only if workout (loop) was completed
                double avgPow = (double) sum / (double) length; //uncomment
                GlobalVariables.failCount = failCount;
                db.add_history(GlobalVariables.loggedInUsername, "pace40", failCount, avgPow);
                GlobalVariables.finalListTimePower = powtimearray;
            }
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            // Extract (using values array), Update UI elements here
            int time = (int) values[0];
            int minutes = time / 60;
            int seconds = time % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds); // display as MM:SS
            int dist = (int) values[1];
            int cal = (int) values[2];
            int driveLength = (int) values[3];
            int driveTime = (int) values[4];
            int avgPwr = (int) values[5];
            int avgDriveForce = (int) values[6];
            int strokeCount = (int) values[7];
            String pzMessage = (String) values[8];
            String fixMessage = (String) values[9];

            // Update the UI with the current counter value
            txtTimeMetric.setText(formattedTime);
            txtDistanceMetric.setText(dist + " m");
            txtCaloriesMetric.setText(cal + " cal");
            txtDriveLengthMetric.setText(driveLength + " m");
            txtDriveTimeMetric.setText(driveTime + " s");
            txtAvgPwrMetric.setText(avgPwr + " W");
            txtAvgDriveForceMetric.setText(avgDriveForce + " lbf");
            txtStrokeCountMetric.setText(Integer.toString(strokeCount));
            txtIntervalFixMetric.setText(fixMessage);
            txtIntervalPZMetric.setText(pzMessage);
            txtPaceFeedbackMetric.setText(""); // not needed for this workout
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            // Stop BLE data polling
            testingDevice.end33();
            Timber.d("[TEST] Polling of 33 ended");
            testingDevice.end35();
            Timber.d("[TEST] Polling of 35 ended");
            testingDevice.end3D();
            Timber.d("[TEST] Polling of 3D ended");
            testingDevice.endWorkOut();

            // Define intent and pass workout name to PostWorkoutActivity
            if (!GlobalVariables.stopTask) { // only launch PostWorkoutActivity if workout wasn't cut short
                Intent goToPostWorkoutActivity = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                goToPostWorkoutActivity.putExtra("workoutName", "pace40"); // pass workout name data - necessary for specific suggestions

                startActivity(goToPostWorkoutActivity); // launch PostWorkoutActivity
            }
            else { // if workout was cut short
                GlobalVariables.stopTask = false; // reset flag
            }
            finish(); // destroy workout activity, go back to dashboard
        }
    }

    // Demo workout Background functionality class
    private class demoTask extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // 45 SECOND DEMO WORKOUT CODE
            int pzCount = 0; //count subsequent errors
            int paceCount = 0; //count subsequent errors
            int failCount = 0; //actual fail count
            int sum = 0; //summing up power
            int length = 0; //number of power entries to calc average
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            String pzMessage = ""; //declaring power zone message
            String fixMessage = ""; //declaring power zone error message
            String paceMessage = ""; //declaring pace message

            int Iterations = 0; //how many loop iterations
            int numIterations = 0; //time=time iterations

            double pastTime = db.getTime_33();
            // 45 seconds at zone 2
            while (db.getTime_33() <= 45 && !GlobalVariables.stopTask) {
                Iterations++;
                sum += db.getPower();
                length += 1;
                double currentTime = db.getTime_33();
                pzMessage = "Row in power zone 4";
                if (db.getPower() < GlobalVariables.pz_4 || db.getPower() >= GlobalVariables.pz_5) {
                    pzCount++;
                    if (pzCount > 4) {
                        fixMessage = "You aren't in power zone 4! " + GlobalVariables.pz_4 + " < " + GlobalVariables.pz_5;
                        failCount++;
                        pzCount = 0;
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!";
                    pzCount = 0;
                }
                if (Math.abs(db.getPower() - db.getPastPower()) > 2) { //TODO: what num is good here
                    paceCount++;
                    if (paceCount > 2) {
                        paceMessage = "Your power output is inconsistent!";
                        failCount++;
                        paceCount = 0;
                    }
                } else {
                    paceMessage = "Nice pace, keep it up!";
                    paceCount = 0;
                }
                // Time-out procedure
                if (Double.compare(pastTime, currentTime) == 0) {
                    numIterations += 1;
                    if (numIterations > 10000) { // TODO: edit? set a threshold of 300 iterations
                        GlobalVariables.timeout = true; // set timeout flag
                        GlobalVariables.stopTask = true; // set stop task flag
                        Timber.d("[TEST] 10,000 iterations exceeded");
                        break; // terminate the loop if threshold is exceeded
                    }
                }
                else {
                    powtimearray.add(db.getTime_33());
                    powtimearray.add((double) db.getPower());
                    pastTime = currentTime;
                    numIterations = 0;
                }

                // Update metric values
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int avgPower = GlobalVariables.averagePower33;
                int avgDriveForce = GlobalVariables.averageDriveForce35.intValue();
                int strokeCount = GlobalVariables.strokeCount35.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, avgPower, avgDriveForce, strokeCount, pzMessage, fixMessage, paceMessage); // Update the UI with the current counter value
                //TODO: where to put thread sleep for message display. here? after setting text box?
/*                try {
                    Thread.sleep(500); // pause for 500 milliseconds
                } catch (InterruptedException e) {
                    // handle the exception if the thread is interrupted while sleeping
                    Timber.d("[TEST]exception in demo thread sleep block");
                }*/
            }
            Timber.d("[TEST] 45 sec num of iterations: " + Iterations);

            if (!GlobalVariables.stopTask) { // only if workout (loop) was completed
                double avgPow = (double) sum / (double) length; //uncomment
                GlobalVariables.failCount = failCount;
                db.add_history(GlobalVariables.loggedInUsername, "interval1", failCount, avgPow);
                GlobalVariables.finalListTimePower = powtimearray;
            }
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            // Extract (using values array), Update UI elements here
            int time = (int) values[0];
            int minutes = time / 60;
            int seconds = time % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds); // display as MM:SS
            int dist = (int) values[1];
            int cal = (int) values[2];
            int driveLength = (int) values[3];
            int driveTime = (int) values[4];
            int avgPwr = (int) values[5];
            int avgDriveForce = (int) values[6];
            int strokeCount = (int) values[7];
            String pzMessage = (String) values[8];
            String fixMessage = (String) values[9];
            String paceMessage = (String) values[10];

            // Update the UI with the current counter value
            txtTimeMetric.setText(formattedTime);
            txtDistanceMetric.setText(dist + " m");
            txtCaloriesMetric.setText(cal + " cal");
            txtDriveLengthMetric.setText(driveLength + " m");
            txtDriveTimeMetric.setText(driveTime + " s");
            txtAvgPwrMetric.setText(avgPwr + " W");
            txtAvgDriveForceMetric.setText(avgDriveForce + " lbf");
            txtStrokeCountMetric.setText(Integer.toString(strokeCount));
            txtIntervalPZMetric.setText(pzMessage);
            txtIntervalFixMetric.setText(fixMessage);
            txtPaceFeedbackMetric.setText(paceMessage);
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            // Stop BLE data polling
            testingDevice.end33();
            Timber.d("[TEST] Polling of 33 ended");
            testingDevice.end35();
            Timber.d("[TEST] Polling of 35 ended");
            testingDevice.end3D();
            Timber.d("[TEST] Polling of 3D ended");
            testingDevice.endWorkOut();

            // Define intent and pass workout name to PostWorkoutActivity
            if (!GlobalVariables.stopTask) { // only launch PostWorkoutActivity if workout wasn't cut short
                Intent goToPostWorkoutActivity = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
                goToPostWorkoutActivity.putExtra("workoutName", "demo"); // pass workout name data - necessary for specific suggestions

                startActivity(goToPostWorkoutActivity); // launch PostWorkoutActivity
                finish(); // can't go back
            } else { // if workout was cut short
                GlobalVariables.stopTask = false; // reset flag
                finish(); // destroy workout activity, go back to dashboard
            }
        }
    }

    @Override
    public void onBackPressed() { // mimic btnStop functionality
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this);
        builder.setTitle("Stop workout?");
        builder.setMessage("Any ongoing workout progress will be lost.");

        builder.setPositiveButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Stop BT data stream
//                        testingDevice.end33();
//                        testingDevice.end35();
//                        testingDevice.end3D();

                WorkoutActivity.super.onBackPressed(); // just go back to Dashboard
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                GlobalVariables.stopTask = true; // set task stop flag

                finish(); // Can't go back
            }
        });

        builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // close the dialog
            }
        });
        AlertDialog dialog = builder.show();
    }
}
