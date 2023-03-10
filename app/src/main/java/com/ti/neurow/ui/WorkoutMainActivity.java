package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ti.neurow.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

// Strictly-Landscape activity
public class WorkoutMainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, View.OnLongClickListener {

    TextView MDY; // declare month-day-year text view

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

    // Show interval workout popup menu
    public void showPopup1(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.interval_workouts_menu);
        popup.show();
    }

    // Show pace workout popup menu
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
        if (itemId == R.id.interval1) {
            Intent intent1 = new Intent(this, Interval20Activity.class);
            startActivity(intent1);
            return true;
        } else if (itemId == R.id.interval2) {
            return true;
        } else if (itemId == R.id.interval3) {
            return true;
        } else if (itemId == R.id.pace_interval1) {
            return true;
        } else if (itemId == R.id.pace_interval2) {
            Intent intent5 = new Intent(this, Pace30Activity.class);
            startActivity(intent5);
            return true;
        } else return itemId == R.id.pace_interval3;
    }

    // Launch LoginActivity when Log Out button is pressed
    public void launchMain (View v) {
        // Launch Log-in activity
        Intent i = new Intent(this, MainUIActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // Can't go back
    }

    // Launch Interval20Activity when FTP Calculator button is pressed
    public void launchWorkout1 (View v) {
        // Launch Workout1 activity
        Intent i = new Intent(this, Interval20Activity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override // Handle back button press
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("To exit the dashboard, you must log out first.");


        builder.setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
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