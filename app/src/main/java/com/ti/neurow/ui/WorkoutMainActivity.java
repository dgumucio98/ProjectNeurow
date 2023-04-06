package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.Date;

// Strictly-Landscape activity
public class WorkoutMainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, View.OnLongClickListener {

    // Color values for setting WorkoutActivity TextViews
    String hexLightBlue = "#7A9CCC"; // light blue
    String hexLightOrange = "#FA9939"; // light orange
    String hexMediumBlue = "#6082B6"; // medium blue
    String hexMediumOrange = "#CD7F32"; // medium orange
    String hexDarkBlue = "#3C5A8C"; // dark blue
    String hexDarkOrange = "#CC7723"; // dark orange

    TextView MDY; // declare month-day-year text view
    TextView txtUserID; // declare username displayer

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

        // Create date status element
        MDY = findViewById(R.id.txtDate);
        Date currentTime = Calendar.getInstance().getTime();
        String formatDate = DateFormat.getDateInstance().format(currentTime);
        MDY.setText(formatDate);

        // Define buttons used for on-long-click
        Button btnWorkout1 = findViewById(R.id.btnWorkout1);
        Button btnWorkout2 = findViewById(R.id.btnWorkout2);
        Button btnWorkout3 = findViewById(R.id.btnWorkout3);

        // Set listeners for buttons
        btnWorkout1.setOnLongClickListener(this);
        btnWorkout2.setOnLongClickListener(this);
        btnWorkout3.setOnLongClickListener(this);
    }

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

        if (itemId == R.id.interval1) {
            int color = Color.parseColor(hexLightBlue); // parse custom color
            launchWorkoutActivity.putExtra("attributeColor", color); // pass color data
            launchWorkoutActivity.putExtra("attributeText", "20-MINUTE"); // pass text data
            launchWorkoutActivity.putExtra("attributeName", "INTERVAL WORKOUT"); // pass titles text data
        } else if (itemId == R.id.interval2) {
            int color = Color.parseColor(hexMediumBlue);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "30-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "INTERVAL WORKOUT"); // pass titles text data
        } else if (itemId == R.id.interval3) {
            int color = Color.parseColor(hexDarkBlue);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "40-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "INTERVAL WORKOUT"); // pass titles text data

        } else if (itemId == R.id.pace_interval1) {
            int color = Color.parseColor(hexLightOrange);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "20-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "PACE WORKOUT"); // pass titles text data
        } else if (itemId == R.id.pace_interval2) {
            int color = Color.parseColor(hexMediumOrange);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "30-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "PACE WORKOUT"); // pass titles text data
        } else if (itemId == R.id.pace_interval3) {
            int color = Color.parseColor(hexDarkOrange);
            launchWorkoutActivity.putExtra("attributeColor", color);
            launchWorkoutActivity.putExtra("attributeText", "40-MINUTE");
            launchWorkoutActivity.putExtra("attributeName", "PACE WORKOUT"); // pass titles text data
        }
        startActivity(launchWorkoutActivity); // start workout activity
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down); // animate
        return false;
    }

    // Log Out button: Launch LoginActivity
    public void launchMain (View v) {
        onBackPressed(); // call onBackPressed() method to display the confirmation dialog
    }

    // FTP Calculator button:  Launch WorkoutActivity
    public void launchFTPCalc (View v) {

        // Launch Workout1 activity
        Intent i = new Intent(this, WorkoutActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override // Handle back button press
    public void onBackPressed() {
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