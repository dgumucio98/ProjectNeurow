package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView; // for graphs
import com.ti.neurow.GlobalVariables; // for access to finalListTimePower
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.ti.neurow.db.DatabaseHelper;

import com.ti.neurow.R;
import com.ti.neurow.wkt.workouts;

import java.util.ArrayList;

import timber.log.Timber;


public class PostWorkoutActivity extends AppCompatActivity {


    // Define UI elements
    Button btnDone;
    TextView txtSuggestion;
    GraphView Power_vs_Time; // declare left graph
    GraphView Power_vs_Pull; // declare right graph

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(getApplicationContext(), "[TEST] PostWorkoutActivity created!", Toast.LENGTH_SHORT).show();


        // Hide Action bar and Status bar, lock orientation to landscape
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide Action bar and Status bar
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Lock orientation to landscape
        setContentView(R.layout.activity_post_workout);

        Power_vs_Time = findViewById(R.id.Power_vs_Time);
        Power_vs_Pull = findViewById(R.id.Power_vs_Pull);
        btnDone = findViewById(R.id.btnDone);
        TextView txtFtpVal = findViewById(R.id.txtFtpVal);

        TextView txtSuggestion = findViewById(R.id.txtSuggestion);

        workouts workouts = new workouts(); // create instance of workouts

        txtFtpVal.setText(GlobalVariables.ftp + "W"); // set FTP text box to updated FTP value

        // Power vs Time Graphing
        int length1 = GlobalVariables.finalListTimePower.size(); // length of list
        int j = 0; // double-time iterator
        DataPoint[] dp1 = new DataPoint[length1/2];
        for (int i = 0; i < length1 - 1; i += 2) {
            dp1[j] = new DataPoint(GlobalVariables.finalListTimePower.get(i), GlobalVariables.finalListTimePower.get(i + 1));
            if (i % 2 == 0){
                j++;
            }
        }
        // Set series to graph
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(dp1);

        Power_vs_Time.addSeries(series1); // add our data
        Power_vs_Time.setTitle("Power vs. Time"); // set title of graph
        Power_vs_Time.setTitleColor(getResources().getColor(R.color.purple_200)); // set color of title
        Power_vs_Time.setTitleTextSize(35); // set title text size

        // Get the maximum and minimum x and y values from the series
        double maxX1 = series1.getHighestValueX();
        double minX1 = 0.0;
        double maxY1 = series1.getHighestValueY() + 2.0;
        double minY1 = 0.0;

        // Set the bounds of the viewport to the maximum and minimum values
        Power_vs_Time.getViewport().setMinX(minX1);
        Power_vs_Time.getViewport().setMaxX(maxX1);
        Power_vs_Time.getViewport().setMinY(minY1);
        Power_vs_Time.getViewport().setMaxY(maxY1);

        // Enable scrolling and scaling
        Power_vs_Time.getViewport().setScalable(true);
        Power_vs_Time.getViewport().setScalableY(true);
        Power_vs_Time.getViewport().setScrollable(true);
        Power_vs_Time.getViewport().setScrollableY(true);

        Power_vs_Time.getGridLabelRenderer().setVerticalAxisTitle("Power (W)"); // set vertical label
        Power_vs_Time.getGridLabelRenderer().setHorizontalAxisTitle("Time (sec)"); // set horizontal label

        // Axis colors
        Power_vs_Time.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);
        Power_vs_Time.getGridLabelRenderer().setVerticalAxisTitleColor(Color.WHITE);
        Power_vs_Time.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        Power_vs_Time.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);

        Power_vs_Time.animate();
        
        series1.setColor(getResources().getColor(R.color.teal_200)); // set color of line

        // Power of Pull Graphing
        DatabaseHelper db = new DatabaseHelper(PostWorkoutActivity.this);
        ArrayList<Double> powPull = db.get3D_avg_y();
        Timber.d("ArrayList contents: %s", powPull);
        int length2 = powPull.size();
        DataPoint[] dp2 = new DataPoint[length2];
        for (int i = 0; i < length2; i++) {
            dp2[i] = new DataPoint(i, powPull.get(i));
        }
        // Set series to graph
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(dp2);

        Power_vs_Pull.addSeries(series2); // add our data
        Power_vs_Pull.setTitle("Power of Pull"); // set title of graph
        Power_vs_Pull.setTitleColor(getResources().getColor(R.color.purple_200)); // set color of title
        Power_vs_Pull.setTitleTextSize(35); // set title text size

        // Get the maximum and minimum x and y values from the series
        double maxX2 = series2.getHighestValueX();
        double minX2 = 0;
        double maxY2 = series2.getHighestValueY() + 2;
        double minY2 = 0;

        // Set the bounds of the viewport to the maximum and minimum values
        Power_vs_Pull.getViewport().setMinX(minX2);
        Power_vs_Pull.getViewport().setMaxX(maxX2);
        Power_vs_Pull.getViewport().setMinY(minY2);
        Power_vs_Pull.getViewport().setMaxY(maxY2);

        // Enable scrolling and scaling
        Power_vs_Pull.getViewport().setScalable(true);
        Power_vs_Pull.getViewport().setScalableY(true);
        Power_vs_Pull.getViewport().setScrollable(true);
        Power_vs_Pull.getViewport().setScrollableY(true);

        Power_vs_Pull.getGridLabelRenderer().setVerticalAxisTitle("Power (W)"); // set vertical label
        Power_vs_Pull.getGridLabelRenderer().setHorizontalAxisTitle("Sequential Time Increments"); // set horizontal label

        // Axis colors
        Power_vs_Pull.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);
        Power_vs_Pull.getGridLabelRenderer().setVerticalAxisTitleColor(Color.WHITE);
        Power_vs_Pull.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        Power_vs_Pull.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);

        Power_vs_Pull.animate();

        series2.setColor(getResources().getColor(R.color.teal_200)); // set color of line

        // Done button listener
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostWorkoutActivity.this, DashboardActivity.class); // send user back to dashboard
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Show suggestion depending on workout
        String workoutName = getIntent().getStringExtra("workoutName");
        String suggestion = workouts.Suggestion(workoutName);
        txtSuggestion.setText(suggestion);
    }

    @Override
    public void onBackPressed() { // handle back button press (take us to workout dashboard)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // Can't go back
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "[TEST] PostWorkoutActivity destroyed!", Toast.LENGTH_SHORT).show();
    }

}