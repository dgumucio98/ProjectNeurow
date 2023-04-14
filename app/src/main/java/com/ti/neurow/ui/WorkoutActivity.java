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
    TextView txtWorkoutAttribute, txtWorkoutName, txtDistanceMetric, txtCaloriesMetric,
            txtUserID, txtFeedbackMetric, txtInstructionMetric, txtAvgPowerMetric; // text views
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

        // Metrics
        txtDistanceMetric = (TextView) findViewById(R.id.txtDistanceMetric); // distance text box
        txtCaloriesMetric = (TextView) findViewById(R.id.txtCaloriesMetric); // calories text box
        txtFeedbackMetric = (TextView) findViewById(R.id.txtFeedbackMetric); // feedback text box
        txtInstructionMetric = (TextView) findViewById(R.id.txtInstructionMetric); // feedback text box
        txtAvgPowerMetric = (TextView) findViewById(R.id.txtAvgPowerMetric); // feedback text box



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
                    interval1Task interval1Task = new interval1Task();
                    interval1Task.execute();
                }
                else if (methodName.equals("interval2")) { // CALL INTERVAL2
                    interval2Task interval2Task = new interval2Task();
                    interval2Task.execute();
                }
                else if (methodName.equals("interval3")) { // CALL INTERVAL3
                    interval3Task interval3Task = new interval3Task();
                    interval3Task.execute();
                }
                else if (methodName.equals("pace20")) { // CALL PACE20
                    pace20Task pace20Task = new pace20Task();
                    pace20Task.execute();
                }
                else if (methodName.equals("pace30")) { // CALL PACE30
                    pace30Task pace30Task = new pace30Task();
                    pace30Task.execute();
                }
                else if (methodName.equals("pace40")) { // CALL PACE40
                    pace40Task pace40Task = new pace40Task();
                    pace40Task.execute();
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
                // TODO: add rest of variables
                // keep as int not object bc no strings?
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
            //TODO: add rest of variables here
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
    private class interval1Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            //interval1 (20 min) method code
            int k1 = 0;
            int k2 = 0;
            int k3 = 0;
            int k4 = 0;
            int k5 = 0;
            int k6 = 0;
            int k7 = 0;
            int k8 = 0;
            int k9 = 0;
            int k10 = 0;
            int k11 = 0;
            int k12 = 0;
            int k13 = 0;
            int k14 = 0;
            int k15 = 0;
            int k16 = 0;
            int k17 = 0;
            int k18 = 0;
            int k19 = 0;
            int k20 = 0;
            int k21 = 0;
            int k22 = 0;

            int count = 0; //count subsequent errors
            int failCount = 0; //actual fail count
            int sum = 0; //summing up power
            int length = 0; //number of power entries to calc average
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            String pzMessage = ""; //declaring power zone message
            String fixMessage = ""; //delcaring power zone error message

            // 5 min at zone 2
            while (db.getTime_33() <= 300) {
                //this is adding all of the powers to then get average
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                if (k1 == 0) {
                    pzMessage = "Row in power zone 2";
                    k1 = 100;
                }
                if (db.getPower() < GlobalVariables.pz_2 || db.getPower() >= GlobalVariables.pz_3) {
                    count++;
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!";
                        failCount++;
                        count = 0;
                    }
                } else {    // only consecutive power zone exits increment count
                    fixMessage = "You are in zone!! Keep it up!!";
                    count = 0;
                }
                //TODO: at end of while loop publish variables we want, pzMessage, fixMessage
            }
            // 40 sec at zone 5
            while (db.getTime_33() <= 340 && db.getTime_33() > 300) {
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                if (k2 == 0) {
                    pzMessage = "Row in power zone 5";
                    k2 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 360 && db.time_33 > 340) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k3 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k3 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 40 sec at zone 5
            while (db.time_33 <= 400 && db.time_33 > 360) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k4 == 0) {
                    pzMessage = "Row in power zone 5"
                    pzSetChanges.setMessage(pzMessage)
                    k4 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 420 && db.time_33 > 400) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k5 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k5 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 40 sec at zone 5
            while (db.time_33 <= 460 && db.time_33 > 420) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k6 == 0) {
                    pzMessage = "Row in power zone 5"
                    pzSetChanges.setMessage(pzMessage)
                    k6 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 480 && db.time_33 > 460) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k7 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k7 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 40 sec at zone 5
            while (db.time_33 <= 520 && db.time_33 > 480) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k8 == 0) {
                    pzMessage = "Row in power zone 5"
                    pzSetChanges.setMessage(pzMessage)
                    k8 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 540 && db.time_33 > 520) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k9 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k9 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 40 sec at zone 5
            while (db.time_33 <= 580 && db.time_33 > 540) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k10 == 0) {
                    pzMessage = "Row in power zone 5"
                    pzSetChanges.setMessage(pzMessage)
                    k10 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 600 && db.time_33 > 580) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k11 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k11 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 40 sec at zone 5
            while (db.time_33 <= 640 && db.time_33 > 600) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k12 == 0) {
                    pzMessage = "Row in power zone 5"
                    pzSetChanges.setMessage(pzMessage)
                    k12 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 660 && db.time_33 > 640) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k13 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k13 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 40 sec at zone 5
            while (db.time_33 <= 700 && db.time_33 > 660) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k14 == 0) {
                    pzMessage = "Row in power zone 5"
                    pzSetChanges.setMessage(pzMessage)
                    k14 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 720 && db.time_33 > 700) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k15 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k15 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 40 sec at zone 5
            while (db.time_33 <= 760 && db.time_33 > 720) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k16 == 0) {
                    pzMessage = "Row in power zone 5"
                    pzSetChanges.setMessage(pzMessage)
                    k16 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 780 && db.time_33 > 760) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k17 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k17 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 40 sec at zone 5
            while (db.time_33 <= 820 && db.time_33 > 780) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k18 == 0) {
                    pzMessage = "Row in power zone 5"
                    pzSetChanges.setMessage(pzMessage)
                    k18 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 840 && db.time_33 > 820) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k19 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k19 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 40 sec at zone 5
            while (db.time_33 <= 880 && db.time_33 > 840) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k20 == 0) {
                    pzMessage = "Row in power zone 5"
                    pzSetChanges.setMessage(pzMessage)
                    k20 = 100
                }
                if (db.power < GlobalVariables.pz_5 || db.power >= GlobalVariables.pz_6) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 5!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 20 sec at zone 2
            while (db.time_33 <= 900 && db.time_33 > 880) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k21 == 0) {
                    pzMessage = "Row in power zone 2"
                    pzSetChanges.setMessage(pzMessage)
                    k21 = 100
                }
                if (db.power < GlobalVariables.pz_2 || db.power >= GlobalVariables.pz_3) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 2!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            // 5 min at zone 1
            while (db.time_33 <= 1200 && db.time_33 > 900) {
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                if (k22 == 0) {
                    pzMessage = "Row in power zone 1"
                    pzSetChanges.setMessage(pzMessage)
                    k22 = 100
                }
                if (db.power >= GlobalVariables.pz_2) {
                    count++
                    if (count > 4) {
                        fixMessage = "You aren't in power zone 1!!!!"
                        pzFixChanges.setMessage(fixMessage)
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "You are in zone!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            val avgPow = sum.toDouble() / length //uncomment
            //interval_suggestor("1", failCount)
            failCount = 25 // for  integration testing
            GlobalVariables.failCount = failCount
            db.add_history(GlobalVariables.loggedInUsername, "interval1", failCount, avgPow)

            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here

            //int time = (int) values[0]; // extract distance value passed
            int dist = (int) values[1]; // extract distance value passed
            int cal = (int) values[2]; // extract calories value passed
            int avgPwr = (int) values[6]; // extract average power
            String instruction = (String) values[8]; // extract instruction
            String feedback = (String) values[9]; // extract feedback

            // Update the UI with the current counter value
            txtDistanceMetric.setText("Distance: " + dist);
            txtCaloriesMetric.setText("Calories: " + cal);
            txtInstructionMetric.setText(instruction);
            txtFeedbackMetric.setText(feedback);
            txtAvgPowerMetric.setText("Average Power: " + avgPwr);
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
            // Exit WorkoutActivity, launch PostWorkoutActivity
            Intent i = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
            startActivity(i); // Launch BLE Data View
            finish(); // can't go back
        }
    }

    // interval2 background functionality class
    private class interval2Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            //interval2 (30 min) method code
            int k1 = 0;
            int k2 = 0;
            int k3 = 0;
            int k4 = 0;
            int k5 = 0;
            int k6 = 0;

            int count = 0; //count subsequent errors
            int failCount = 0; //actual fail count
            int sum = 0; //summing up power
            int length = 0; //number of power entries to calc average
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            String pzMessage = ""; //declaring power zone message
            String fixMessage = ""; //declaring power zone error message

            // 6 min at zone 3
            while (db.getTime_33() <= 30) { //TODO: change back to 360
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                if (k1 == 0) {
                    pzMessage = "Row in power zone 3";
                    k1 = 100;
                }
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
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, strokeCount, avgPower, lastSplit, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            // UNCOMMENT FOR FULL WORKOUT
/*            // 5 min at zone 1
            while (db.getTime_33() <= 660 && db.getTime_33() > 360) {
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                if (k2 == 0) {
                    pzMessage = "Row in power zone 1";
                    k2 = 100;
                }
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
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, strokeCount, avgPower, lastSplit, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            // 5 min at zone 4
            while (db.getTime_33() <= 960 && db.getTime_33() > 660) {
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                if (k3 == 0) {
                    pzMessage = "Row in power zone 4";
                    k3 = 100;
                }
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
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, strokeCount, avgPower, lastSplit, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            // 5 min at zone 1
            while (db.getTime_33() <= 1260 && db.getTime_33() > 960) {
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                if (k4 == 0) {
                    pzMessage = "Row in power zone 1";
                    k4 = 100;
                }
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
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, strokeCount, avgPower, lastSplit, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            // 4 min at zone 5
            while (db.getTime_33() <= 1500 && db.getTime_33() > 1260) {
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                if (k5 == 0) {
                    pzMessage = "Row in power zone 5";
                    k5 = 100;
                }
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
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, strokeCount, avgPower, lastSplit, pzMessage, fixMessage); // Update the UI with the current counter value
            }
            // 5 min at zone 1
            while (db.getTime_33() <= 1800 && db.getTime_33() > 1500) {
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                if (k6 == 0) {
                    pzMessage = "Row in power zone 1";
                    k6 = 100;
                }
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
                // Update UI elements
                int elapsedTime = GlobalVariables.elapsedTime33.intValue();
                int distance = GlobalVariables.distance35.intValue();
                int calories = GlobalVariables.totalCalories33;
                int driveLength = GlobalVariables.driveLength35.intValue();
                int driveTime = GlobalVariables.driveTime35.intValue();
                int strokeCount = GlobalVariables.strokeCount35;
                int avgPower = GlobalVariables.averagePower33;
                int lastSplit = GlobalVariables.lastSplitTime33.intValue();

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, strokeCount, avgPower, lastSplit, pzMessage, fixMessage); // Update the UI with the current counter value
            }*/
            double avgPower = (double) sum / (double) length;
            GlobalVariables.failCount = failCount;
            db.add_history(GlobalVariables.loggedInUsername, "interval2", failCount, avgPower);
            GlobalVariables.finalListTimePower = powtimearray;
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here

            //int time = (int) values[0]; // extract distance value passed
            int dist = (int) values[1]; // extract distance value passed
            int cal = (int) values[2]; // extract calories value passed
            int avgPwr = (int) values[6]; // extract average power
            String instruction = (String) values[8]; // extract instruction
            String feedback = (String) values[9]; // extract feedback

            // Update the UI with the current counter value
            txtDistanceMetric.setText("Distance: " + dist);
            txtCaloriesMetric.setText("Calories: " + cal);
            txtInstructionMetric.setText(instruction);
            txtFeedbackMetric.setText(feedback);
            txtAvgPowerMetric.setText("Average Power: " + avgPwr);
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
            // Exit WorkoutActivity, launch PostWorkoutActivity
            Intent i = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
            startActivity(i); // Launch BLE Data View
            finish(); // can't go back
        }
    }

    // interval3 background functionality class
    private class interval3Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here

            //int time = (int) values[0]; // extract distance value passed
            int dist = (int) values[1]; // extract distance value passed
            int cal = (int) values[2]; // extract calories value passed
            int avgPwr = (int) values[6]; // extract average power
            String instruction = (String) values[8]; // extract instruction
            String feedback = (String) values[9]; // extract feedback

            // Update the UI with the current counter value
            txtDistanceMetric.setText("Distance: " + dist);
            txtCaloriesMetric.setText("Calories: " + cal);
            txtInstructionMetric.setText(instruction);
            txtFeedbackMetric.setText(feedback);
            txtAvgPowerMetric.setText("Average Power: " + avgPwr);
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
            // Exit WorkoutActivity, launch PostWorkoutActivity
            Intent i = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
            startActivity(i); // Launch BLE Data View
            finish(); // can't go back
        }
    }

    // pace20 background functionality class
    private class pace20Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            //pace code 20 min
            int failCount = 0;
            int count = 0;
            int sum = 0;
            int length = 0;
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            String pzMessage = "";
            String fixMessage = "";

            pzMessage = "Begin Rowing!";
            // TODO: change back to 1200
            while (db.getTime_33() <= 30) { // less than 20-min
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                //if difference between current stroke and previous stroke is greater than 4 watts
                if (Math.abs(db.getPower() - db.getPastPower()) > 2) { //TODO: what num is good here
                    count++;
                    if (count > 2) {
                        fixMessage = "Your power output is inconsistent! Try to improve pacing!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "Nice pace!! Keep it up!!";
                    count = 0;
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

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, strokeCount, avgPower, lastSplit, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            GlobalVariables.failCount = failCount;
            double avgPow = (double) sum / (double) length; //uncomment
            db.add_history(GlobalVariables.loggedInUsername, "pace20", failCount, avgPow);
            GlobalVariables.finalListTimePower = powtimearray;
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here

            //int time = (int) values[0]; // extract distance value passed
            int dist = (int) values[1]; // extract distance value passed
            int cal = (int) values[2]; // extract calories value passed
            int avgPwr = (int) values[6]; // extract average power
            String instruction = (String) values[8]; // extract instruction
            String feedback = (String) values[9]; // extract feedback

            // Update the UI with the current counter value
            txtDistanceMetric.setText("Distance: " + dist);
            txtCaloriesMetric.setText("Calories: " + cal);
            txtInstructionMetric.setText(instruction);
            txtFeedbackMetric.setText(feedback);
            txtAvgPowerMetric.setText("Average Power: " + avgPwr);
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
            // Exit WorkoutActivity, launch PostWorkoutActivity
            Intent i = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
            startActivity(i); // Launch BLE Data View
            finish(); // can't go back
        }
    }

    // pace30 background functionality class
    private class pace30Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            //pace code 30 min
            int failCount = 0;
            int count = 0;
            int sum = 0;
            int length = 0;
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            String pzMessage = "";
            String fixMessage = "";

            pzMessage = "Begin Rowing!";
            while (db.getTime_33() <= 1800) { // less than 30-min
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                //if difference between current stroke and previous stroke is greater than 4 watts
                if (Math.abs(db.getPower() - db.getPastPower()) > 2) { //TODO: what num is good here
                    count++;
                    if (count > 2) {
                        fixMessage = "Your power output is inconsistent! Try to improve pacing!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "Nice pace!! Keep it up!!";
                    count = 0;
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

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, strokeCount, avgPower, lastSplit, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            GlobalVariables.failCount = failCount;
            double avgPow = (double) sum / (double) length; //uncomment
            db.add_history(GlobalVariables.loggedInUsername, "pace30", failCount, avgPow);
            GlobalVariables.finalListTimePower = powtimearray;
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here

            //int time = (int) values[0]; // extract distance value passed
            int dist = (int) values[1]; // extract distance value passed
            int cal = (int) values[2]; // extract calories value passed
            int avgPwr = (int) values[6]; // extract average power
            String instruction = (String) values[8]; // extract instruction
            String feedback = (String) values[9]; // extract feedback

            // Update the UI with the current counter value
            txtDistanceMetric.setText("Distance: " + dist);
            txtCaloriesMetric.setText("Calories: " + cal);
            txtInstructionMetric.setText(instruction);
            txtFeedbackMetric.setText(feedback);
            txtAvgPowerMetric.setText("Average Power: " + avgPwr);
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
            // Exit WorkoutActivity, launch PostWorkoutActivity
            Intent i = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
            startActivity(i); // Launch BLE Data View
            finish(); // can't go back
        }
    }

    // pace40 background functionality class
    private class pace40Task extends AsyncTask<Void, Object, Integer> {

        @Override // 1st function for background task: defines background task
        protected Integer doInBackground(Void... voids) {

            // Create database instance
            DatabaseHelper db = new DatabaseHelper(WorkoutActivity.this);

            // [PASTE WORKOUT HERE]
            //pace code 20 min
            int failCount = 0;
            int count = 0;
            int sum = 0;
            int length = 0;
            ArrayList<Double> powtimearray = new ArrayList<>(); //arraylist to hold time and power

            String pzMessage = "";
            String fixMessage = "";

            pzMessage = "Begin Rowing!";
            while (db.getTime_33() <= 2400) { // less than 20-min
                sum += db.getPower();
                length += 1;
                powtimearray.add(db.getTime_33());
                powtimearray.add((double)db.getPower());
                //if difference between current stroke and previous stroke is greater than 4 watts
                if (Math.abs(db.getPower() - db.getPastPower()) > 2) { //TODO: what num is good here
                    count++;
                    if (count > 2) {
                        fixMessage = "Your power output is inconsistent! Try to improve pacing!";
                        failCount++;
                        count = 0;
                    }
                } else {
                    fixMessage = "Nice pace!! Keep it up!!";
                    count = 0;
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

                // Send data to main UI thread
                publishProgress(elapsedTime, distance, calories, driveLength, driveTime, strokeCount, avgPower, lastSplit, pzMessage, fixMessage); // Update the UI with the current counter value

            }
            GlobalVariables.failCount = failCount;
            double avgPow = (double) sum / (double) length; //uncomment
            db.add_history(GlobalVariables.loggedInUsername, "pace40", failCount, avgPow);
            GlobalVariables.finalListTimePower = powtimearray;
            return 0;
        }

        @Override // 2nd function for background task: updates UI
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            // Extract (using values array), Update UI elements here

            //int time = (int) values[0]; // extract distance value passed
            int dist = (int) values[1]; // extract distance value passed
            int cal = (int) values[2]; // extract calories value passed
            int avgPwr = (int) values[6]; // extract average power
            String instruction = (String) values[8]; // extract instruction
            String feedback = (String) values[9]; // extract feedback

            // Update the UI with the current counter value
            txtDistanceMetric.setText("Distance: " + dist);
            txtCaloriesMetric.setText("Calories: " + cal);
            txtInstructionMetric.setText(instruction);
            txtFeedbackMetric.setText(feedback);
            txtAvgPowerMetric.setText("Average Power: " + avgPwr);
        }

        @Override // 3rd function for background task: follows background task after completion
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Define intent and launch next activity
            // Exit WorkoutActivity, launch PostWorkoutActivity
            Intent i = new Intent(WorkoutActivity.this, PostWorkoutActivity.class);
            startActivity(i); // Launch BLE Data View
            finish(); // can't go back
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