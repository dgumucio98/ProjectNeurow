package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ti.neurow.GlobalVariables;
import com.ti.neurow.R;
import com.ti.neurow.db.DatabaseHelper;
import com.ti.neurow.wkt.workouts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

// Strictly-Landscape activity
public class DashboardActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, View.OnLongClickListener {

    // Create instance of database in this activity
    DatabaseHelper db = new DatabaseHelper(DashboardActivity.this);

    // Stuff to display a tip and change it
    private Handler tipChanger = new Handler(); // tip text box handler
    private int previousTipIndex = -1; // prevents same tip twice in a row

    // Define prediction choices array
    private static final String[] PREDICTION_CHOICES = {"Pace (20-minute)", "Pace (30-minute)", "Pace (40-minute)",
            "Interval (20-minute)", "Interval (30-minute)", "Interval (40-minute)" }; // stores prediction choice

    private String[] tips = { // on-screen tips and facts
            "⭐ Predict your performance with the 'Power Predictor' button",
            "⭐ First time? Find your FTP with the 'FTP Calculator' workout ⭐",
            "⭐ Did you know? Rowing is one of the original sports in the modern Olympic Games ⭐",
            "⭐ Did you know? Rowing machines utilize ~84% of the body's muscles ⭐",
            "⭐ Expect to be sore :) ⭐",
            "⭐ A good Power of Pull graph looks parabolic ⭐",
            "⭐ Did you know? Rowing machines are also known as 'ergometers' ⭐",
            "⭐ Watch that form! Sit tall with your shoulders back, and engage your core ⭐",
            "⭐ Focus on your breathing. Inhale on the way back and exhale on the way forward ⭐",
            "⭐ Fuel up before your workout with a healthy snack like a banana or a handful of nuts ⭐",
            "⭐ If your Power of Pull graph is flat, try rowing with more force",
            "⭐ If your Power of Pull graph's y-intercept is high you're exploding too much at the catch ⭐",
            "⭐ If your Power of Pull graph dips in the middle, work on a smooth transition between legs, back, arms ⭐",
            "⭐ If your power of pull graph is short you need more leg drive ⭐",
            "⭐ Matt, you're our favorite coxswain, even if you don't know port from starboard! ⭐"
    };

    // Define UI elements
    TextView MDY, txtUserID, txtUserFtp, txtTip;

    private Handler handler = new Handler(); // handler to update status time live

    @Override // Handles when workout buttons are long-clicked
    public boolean onLongClick(View v) {
        int id = v.getId();
        if (id == R.id.btnWorkout1) {
            Toast.makeText(this, "Find your FTP from a quick 20-minute row.", Toast.LENGTH_LONG).show();
        } else if (id == R.id.btnWorkout2) {
            Toast.makeText(DashboardActivity.this, "Pace yourself with live feedback on your row's power output.", Toast.LENGTH_LONG).show();
        } else if (id == R.id.btnWorkout3) {
            Toast.makeText(DashboardActivity.this, "High-intensity workout based on your personal Power Zones.", Toast.LENGTH_LONG).show();
        } else if (id == R.id.btnPredictor) {
            Toast.makeText(DashboardActivity.this, "Predict your next five rows for a specific workout.", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (GlobalVariables.timeToResetPM5 || GlobalVariables.timeout || GlobalVariables.stopTask ) { // tell user to reset PM5
            showPM5ResetDialog();
            GlobalVariables.timeToResetPM5 = false; // reset the flag
        }

        txtUserFtp = findViewById(R.id.txtUserFtp);

        // Handle coming back to activity from a timed-out workout
        if (GlobalVariables.timeout) {
            showWarningDialog(); // inform user their workout was timed out
        }

        // Populate global variables from database data
        try {
            if (Integer.compare(db.getFTP(GlobalVariables.loggedInUsername),-1) == 0) { // if user doesn't have FTP in database
                //TODO: update values
                GlobalVariables.ftp = 45;
                GlobalVariables.pz_1 = 0;
                GlobalVariables.pz_2 = 25;
                GlobalVariables.pz_3 = 34;
                GlobalVariables.pz_4 = 40;
                GlobalVariables.pz_5 = 47;
                GlobalVariables.pz_6 = 54;
                GlobalVariables.pz_7 = 67;
            }
            else { // user's FTP is in database
                GlobalVariables.ftp = db.getFTP(GlobalVariables.loggedInUsername);
                GlobalVariables.pz_1 = db.getPZ_1(GlobalVariables.loggedInUsername);
                GlobalVariables.pz_2 = db.getPZ_2(GlobalVariables.loggedInUsername);
                GlobalVariables.pz_3 = db.getPZ_3(GlobalVariables.loggedInUsername);
                GlobalVariables.pz_4 = db.getPZ_4(GlobalVariables.loggedInUsername);
                GlobalVariables.pz_5 = db.getPZ_5(GlobalVariables.loggedInUsername);
                GlobalVariables.pz_6 = db.getPZ_6(GlobalVariables.loggedInUsername);
                GlobalVariables.pz_7 = db.getPZ_7(GlobalVariables.loggedInUsername);
            }
            txtUserFtp.setText(Html.fromHtml("<b>My FTP:</b> " + GlobalVariables.ftp + " W"));
        } catch (NullPointerException e) {
            // handle the null value error here
            GlobalVariables.loggedInUsername = "CRASH";
//            txtUserFtp.setText(Html.fromHtml("<b>My FTP: CRASH</b>"));
        }
        txtUserFtp.setTextSize(25);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toast.makeText(getApplicationContext(), "[TEST] DashboardActivity created!", Toast.LENGTH_SHORT).show();

        // Hide Action bar and Status bar, lock orientation to landscape
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_dashboard);

        // Change tip text box every 7 seconds
        tipChanger.postDelayed(updateTipRunnable, 7000);

        Handler handler = new Handler(); // define handler

        // Define  UI elements
        TextView txtUserID = findViewById(R.id.txtUserID);
        TextView txtUserFtp = findViewById(R.id.txtUserFtp);
        txtUserID.setText(GlobalVariables.loggedInUsername);
        TextView txtPrediction = findViewById(R.id.txtPrediction);
        Button btnPredictor = findViewById(R.id.btnPredictor);

        // Define buttons used for on-long-click
        Button btnWorkout1 = findViewById(R.id.btnWorkout1);
        Button btnWorkout2 = findViewById(R.id.btnWorkout2);
        Button btnWorkout3 = findViewById(R.id.btnWorkout3);
        Button btnDemo = findViewById(R.id.btnDemo);
        MDY = findViewById(R.id.txtDate);
        txtTip = findViewById(R.id.txtTip);

        // Set listeners for buttons
        btnWorkout1.setOnLongClickListener(this);
        btnWorkout2.setOnLongClickListener(this);
        btnWorkout3.setOnLongClickListener(this);
        btnPredictor.setOnLongClickListener(this);
        btnDemo.setOnLongClickListener(this);

        // Display user's FTP
        txtUserFtp.setTextColor(getResources().getColor(R.color.mint_green));

        // Handle button clicks
        View.OnClickListener workoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToWorkoutActivity = new Intent(DashboardActivity.this, WorkoutActivity.class); // define intent for launching activity

                switch (v.getId()) {
                    case R.id.btnWorkout1: // ftpCalc button is clicked
                        int color1 = ContextCompat.getColor(getApplicationContext(), R.color.medium_gray); // parse custom color
                        goToWorkoutActivity.putExtra("attributeColor", color1); // pass color data
                        goToWorkoutActivity.putExtra("attributeText", "20-MINUTE"); // pass text data
                        goToWorkoutActivity.putExtra("attributeName", "FTP CALCULATOR"); // pass titles text data
                        goToWorkoutActivity.putExtra("methodName", "ftpCalc"); // pass target workout name data
                        break;
                    case R.id.btnDemo: // demo button is clicked
                        int color2 = ContextCompat.getColor(getApplicationContext(), R.color.brick_red); // parse custom color
                        goToWorkoutActivity.putExtra("attributeColor", color2); // pass color data
                        goToWorkoutActivity.putExtra("attributeText", "404 Demo"); // pass text data
                        goToWorkoutActivity.putExtra("attributeName", "DEMO WORKOUT"); // pass titles text data
                        goToWorkoutActivity.putExtra("methodName", "demo"); // pass target workout name data
                        break;
                    case R.id.btnPredictor: // predictor button is clicked
                        showPredictionOptionsDialog(); // launch dialog box
                        return; // return to avoid starting activity or animating
                }

                startActivity(goToWorkoutActivity); // start workout activity
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down); // animate
            }
        };

        // Set button listeners
        btnWorkout1.setOnClickListener(workoutClickListener);
        btnDemo.setOnClickListener(workoutClickListener);
        btnPredictor.setOnClickListener(workoutClickListener);

        // Start updateTime handler
        handler.post(updateTime);
    }

    // Define a runnable to update time status every second
    private Runnable updateTime = new Runnable() {
        public void run() {
            // Get the current time
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdFormat = new SimpleDateFormat("MMMM dd, yyyy\nh:mm a", Locale.getDefault());
            String strDate = mdFormat.format(calendar.getTime());

            // Update the TextView with the current time
            MDY.setText(strDate);

            // Schedule the next update in 1 second
            handler.postDelayed(this, 1000);
        }
    };

    // Show interval workout drop-down menu
    public void showPopup1(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.interval_workouts_menu);
        popup.show();
    }

    // Show pace workout drop-down menu
    public void showPopup2(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.pace_workouts_menu);
        popup.show();
    }

    // Handle when menu items are clicked
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        int itemId = menuItem.getItemId();
        Intent goToWorkoutActivity = new Intent(this, WorkoutActivity.class); // define intent for launching activity

        // Prepare data to be sent via putExtra
        if (itemId == R.id.interval20) {
            int color = ContextCompat.getColor(this, R.color.light_blue); // parse custom color
            goToWorkoutActivity.putExtra("attributeColor", color); // pass color data
            goToWorkoutActivity.putExtra("attributeText", "20-MINUTE"); // pass text data
            goToWorkoutActivity.putExtra("attributeName", "INTERVAL WORKOUT"); // pass titles text data
            goToWorkoutActivity.putExtra("methodName", "interval1"); // pass target workout name data

        } else if (itemId == R.id.interval30) {
            int color = ContextCompat.getColor(this, R.color.medium_blue);
            goToWorkoutActivity.putExtra("attributeColor", color);
            goToWorkoutActivity.putExtra("attributeText", "30-MINUTE");
            goToWorkoutActivity.putExtra("attributeName", "INTERVAL WORKOUT"); // pass titles text data
            goToWorkoutActivity.putExtra("methodName", "interval2"); // pass target workout name data
        } else if (itemId == R.id.interval40) {
            int color = ContextCompat.getColor(this, R.color.dark_blue);
            goToWorkoutActivity.putExtra("attributeColor", color);
            goToWorkoutActivity.putExtra("attributeText", "40-MINUTE");
            goToWorkoutActivity.putExtra("attributeName", "INTERVAL WORKOUT"); // pass titles text data
            goToWorkoutActivity.putExtra("methodName", "interval3"); // pass target workout name data

        } else if (itemId == R.id.pace20) {
            int color = ContextCompat.getColor(this, R.color.light_orange);
            goToWorkoutActivity.putExtra("attributeColor", color);
            goToWorkoutActivity.putExtra("attributeText", "20-MINUTE");
            goToWorkoutActivity.putExtra("attributeName", "PACE WORKOUT"); // pass titles text data
            goToWorkoutActivity.putExtra("methodName", "pace20"); // pass target workout name data

        } else if (itemId == R.id.pace30) {
            int color = ContextCompat.getColor(this, R.color.medium_orange);
            goToWorkoutActivity.putExtra("attributeColor", color);
            goToWorkoutActivity.putExtra("attributeText", "30-MINUTE");
            goToWorkoutActivity.putExtra("attributeName", "PACE WORKOUT"); // pass titles text data
            goToWorkoutActivity.putExtra("methodName", "pace30"); // pass target workout name data

        } else if (itemId == R.id.pace40) {
            int color = ContextCompat.getColor(this, R.color.dark_orange);
            goToWorkoutActivity.putExtra("attributeColor", color);
            goToWorkoutActivity.putExtra("attributeText", "40-MINUTE");
            goToWorkoutActivity.putExtra("attributeName", "PACE WORKOUT"); // pass titles text data
            goToWorkoutActivity.putExtra("methodName", "pace40"); // pass target workout name data
        }

        // Start workout activity
        startActivity(goToWorkoutActivity); // launch workout activity
        finish(); // destroy activity
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down); // animate
        return false;
    }

    // Prepares and shows dialog box for the Workout Prediction function
    private void showPredictionOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Predict which workout?")
                .setItems(PREDICTION_CHOICES, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        TextView txtPrediction = findViewById(R.id.txtPrediction); // create local instance of txtPrediction
                        DatabaseHelper db = new DatabaseHelper(DashboardActivity.this); // prepare database
                        workouts workouts = new workouts(); // construct workouts instance
                        String workoutType = "";
                        ArrayList<Double> allPow;
                        String prediction = "";
                        switch (which) {
                            case 0:
                                // "Pace-20 Prediction" selected
                                workoutType = "pace20";
                                allPow = db.getAllPower(GlobalVariables.loggedInUsername, workoutType);
                                prediction = workouts.powerPredictor(allPow);
                                txtPrediction.setText(prediction);
                                txtPrediction.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                // "Pace-30 Prediction" selected
                                workoutType = "pace30";
                                allPow = db.getAllPower(GlobalVariables.loggedInUsername, workoutType);
                                prediction = workouts.powerPredictor(allPow);
                                txtPrediction.setText(prediction);
                                txtPrediction.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                // "Pace-40 Prediction" selected
                                workoutType = "pace40";
                                allPow = db.getAllPower(GlobalVariables.loggedInUsername, workoutType);
                                prediction = workouts.powerPredictor(allPow);
                                txtPrediction.setText(prediction);
                                txtPrediction.setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                // "Interval-20 Prediction" selected
                                workoutType = "interval1";
                                allPow = db.getAllPower(GlobalVariables.loggedInUsername, workoutType);
                                prediction = workouts.powerPredictor(allPow);
                                txtPrediction.setText(prediction);
                                txtPrediction.setVisibility(View.VISIBLE);
                                break;
                            case 4:
                                // "Interval-30 Prediction" selected
                                workoutType = "interval2";
                                allPow = db.getAllPower(GlobalVariables.loggedInUsername, workoutType);
                                prediction = workouts.powerPredictor(allPow);
                                txtPrediction.setText(prediction);
                                txtPrediction.setVisibility(View.VISIBLE);
                                break;
                            case 5:
                                // "Interval-40 Prediction" selected
                                workoutType = "interval3";
                                allPow = db.getAllPower(GlobalVariables.loggedInUsername, workoutType);
                                prediction = workouts.powerPredictor(allPow);
                                txtPrediction.setText(prediction);
                                txtPrediction.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Prepares and shows dialog box prompting to reset the PM5
    private void showPM5ResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset the PM5 to Continue");
        builder.setMessage(Html.fromHtml("In order to start a new workout, the <b>PM5 Device</b> must be reset. Press the <b>MENU</b> button on the device to reset it."));

        builder.setPositiveButton("I've reset the PM5", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // close the dialog
            }
        });

        AlertDialog dialog = builder.show();
    }

    @Override
    public void onBackPressed() { // handle back button press
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out?");
        builder.setMessage("Exiting the dashboard will log you out.");

        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                GlobalVariables.loggedInUsername = "NULL"; // clear logged-in user (log out)

                DashboardActivity.super.onBackPressed(); // go back to existing MainUIActivity
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // close the dialog
            }
        });
        AlertDialog dialog = builder.show();
    }

    // Tip changer
    private Runnable updateTipRunnable = new Runnable() {
        @Override
        public void run() {
            int index;
            do {
                index = (int) (Math.random() * tips.length);
            } while (index == previousTipIndex); // loop until a different tip is selected

            previousTipIndex = index; // set the current tip as the previous tip

            txtTip.setText(tips[index]);

            txtTip.animate().alpha(1.0f).setDuration(500).withStartAction(new Runnable() {
                @Override
                public void run() {
                    txtTip.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtTip.animate().alpha(0.0f).setDuration(500).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    tipChanger.post(updateTipRunnable);
                                }
                            });
                        }
                    }, 8000);
                }
            });
        }
    };

    // Prepares and shows dialog box warning of workout timeout
    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uh-oh! Workout Timed Out");
        builder.setMessage("Your last workout was ended due to inactivity");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}