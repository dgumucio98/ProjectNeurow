package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ti.neurow.GlobalVariables;
import com.ti.neurow.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Strictly-Landscape activity
public class WorkoutMainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, View.OnLongClickListener {

    TextView MDY; // declare month-day-year text view
    TextView txtUserID; // declare username displayer

    private Handler handler = new Handler(); // handler to update status time live


    @Override // Handles when workout buttons are long-clicked
    public boolean onLongClick(View v) {
        int id = v.getId();
        if (id == R.id.btnWorkout1) {
            Toast.makeText(this, "Find your FTP from a quick 20-minute row.", Toast.LENGTH_LONG).show();
        } else if (id == R.id.btnWorkout2) {
            Toast.makeText(WorkoutMainActivity.this, "Pace yourself with live feedback on your row's power output.", Toast.LENGTH_LONG).show();
        } else if (id == R.id.btnWorkout3) {
            Toast.makeText(WorkoutMainActivity.this, "High-intensity workout based on your personal Power Zones.", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lock orientation to landscape (for this activity)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        setContentView(R.layout.activity_workout_main);

        TextView txtUserID = findViewById(R.id.txtUserID);
        txtUserID.setText(GlobalVariables.loggedInUsername);
        MDY  = findViewById(R.id.txtDate);

        handler.post(updateTime); // start  handler to update time every second

        // Define buttons used for on-long-click
        Button btnWorkout1 = findViewById(R.id.btnWorkout1);
        Button btnWorkout2 = findViewById(R.id.btnWorkout2);
        Button btnWorkout3 = findViewById(R.id.btnWorkout3);
        // TODO: define new Power Predictions button with popup menu and add functionality to each choice

        // Set listeners for buttons
        btnWorkout1.setOnLongClickListener(this);
        btnWorkout2.setOnLongClickListener(this);
        btnWorkout3.setOnLongClickListener(this);

        // Handle when ftpCalc button is clicked
        btnWorkout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchWorkoutActivity = new Intent(WorkoutMainActivity.this, WorkoutActivity.class); // define intent for launching activity

                int color = ContextCompat.getColor(getApplicationContext(), R.color.medium_gray); // parse custom color
                launchWorkoutActivity.putExtra("attributeColor", color); // pass color data
                launchWorkoutActivity.putExtra("attributeText", "20-MINUTE"); // pass text data
                launchWorkoutActivity.putExtra("attributeName", "FTP CALCULATOR"); // pass titles text data
                launchWorkoutActivity.putExtra("methodName", "ftpCalc"); // pass target workout name data

                startActivity(launchWorkoutActivity); // start workout activity
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down); // animate
            }
        });
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
        Intent launchWorkoutActivity = new Intent(this, WorkoutActivity.class); // define intent for launching activity

        // Prepare data to be sent via putExtra
        if (itemId == R.id.interval20) {
            int color = ContextCompat.getColor(this, R.color.light_blue); // parse custom color
            launchWorkoutActivity.putExtra("attributeColor", color); // pass color data
            launchWorkoutActivity.putExtra("attributeText", "20-MINUTE"); // pass text data
            launchWorkoutActivity.putExtra("attributeName", "INTERVAL WORKOUT"); // pass titles text data
            launchWorkoutActivity.putExtra("methodName", "interval1"); // pass target workout name data

        } else if (itemId == R.id.interval30) {
            int color = ContextCompat.getColor(this, R.color.medium_blue);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "30-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "INTERVAL WORKOUT"); // pass titles text data
            launchWorkoutActivity.putExtra("methodName", "interval2"); // pass target workout name data
        } else if (itemId == R.id.interval40) {
            int color = ContextCompat.getColor(this, R.color.dark_blue);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "40-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "INTERVAL WORKOUT"); // pass titles text data
            launchWorkoutActivity.putExtra("methodName", "interval3"); // pass target workout name data

        } else if (itemId == R.id.pace20) {
            int color = ContextCompat.getColor(this, R.color.light_orange);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "20-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "PACE WORKOUT"); // pass titles text data
            launchWorkoutActivity.putExtra("methodName", "pace20"); // pass target workout name data

        } else if (itemId == R.id.pace30) {
            int color = ContextCompat.getColor(this, R.color.medium_orange);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "30-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "PACE WORKOUT"); // pass titles text data
            launchWorkoutActivity.putExtra("methodName", "pace30"); // pass target workout name data

        } else if (itemId == R.id.pace40) {
            int color = ContextCompat.getColor(this, R.color.dark_orange);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "40-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "PACE WORKOUT"); // pass titles text data
            launchWorkoutActivity.putExtra("methodName", "pace40"); // pass target workout name data
        }

        // Start next workout activity
        startActivity(launchWorkoutActivity); // start workout activity
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down); // animate
        return false;
    }

    // Log Out button: Launch LoginActivity
    public void launchMain (View v) {
        onBackPressed(); // call onBackPressed() method to display the confirmation dialog
    }

    @Override
    public void onBackPressed() { // handle back button press
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out?");
        builder.setMessage("Exiting the dashboard will log you out.");

        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                GlobalVariables.loggedInUsername = "NULL"; // clear logged in user

                Intent intent = new Intent(WorkoutMainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent);
                finish(); // Can't go back
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
}