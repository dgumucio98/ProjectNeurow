package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.ti.neurow.R;

import pl.droidsonroids.gif.GifImageView;

public class Pace30Activity extends AppCompatActivity {

    // Define some elements
    Chronometer chron; // declare chronometer (count-up timer)
    TextView txtWorkoutName; // declare TextViews for animation, etc.
    Button btnStartChron; // declare timer button
    boolean isChronRunning = false; // define boolean state of the timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Tweak visible elements
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide Action bar and Status bar
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Lock orientation to landscape
        setContentView(R.layout.activity_pace30);


        // [EXPERIMENTAL] Animate workout name
        txtWorkoutName = findViewById(R.id.txtWorkoutName); // define workout name text
        int[] letterPositions = new int[]{-70, -60, -50, -30, -20, -10, 0, 10, 20, 30, 50, 60};
        AnimatorSet animatorSet = new AnimatorSet();

        for (int i = 0; i < txtWorkoutName.getText().length(); i++) {
            // Create an ObjectAnimator for each letter
            ObjectAnimator animator = ObjectAnimator.ofFloat(
                    txtWorkoutName, // the target view
                    "translationY", // the property to animate
                    0f, // starting value
                    letterPositions[i], // ending value
                    0f // back to starting value
            );
            // Set the duration of each animation
            animator.setDuration(500);
            // Set the delay for each animation to create a wave effect
            animator.setStartDelay(i * 100);
            // Add the animator to the AnimatorSet
            animatorSet.play(animator);
        }

        txtWorkoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatorSet.start();
            }
        });

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
                btnStartChron.setVisibility(View.GONE); // hide the button
            }
        });
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