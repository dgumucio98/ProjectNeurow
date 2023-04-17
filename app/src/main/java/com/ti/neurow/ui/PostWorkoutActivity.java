package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.jjoe64.graphview.GraphView; // for graphs
import com.jjoe64.graphview.GridLabelRenderer;
import com.ti.neurow.GlobalVariables; // for access to finalListTimePower
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.ti.neurow.db.DatabaseHelper;

import com.ti.neurow.R;

import java.util.ArrayList;

import timber.log.Timber;


public class PostWorkoutActivity extends AppCompatActivity {

    GraphView Power_vs_Time; // declare left graph
    GraphView Power_vs_Pull; // declare right graph

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Tweak visible elements
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide Action bar and Status bar
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Lock orientation to landscape
        setContentView(R.layout.activity_post_workout);

        Power_vs_Time = findViewById(R.id.Power_vs_Time);
        Power_vs_Pull = findViewById(R.id.Power_vs_Pull);
        TextView txtFtp = findViewById(R.id.txtFtp);

        txtFtp.setText("FTP: " + GlobalVariables.ftp + "W"); // set FTP text box to updated FTP value

        /////// Power vs Time Graphing ///////
        // [TEST] Populate list before graphing
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
        double minX1 = 0;
        double maxY1 = series1.getHighestValueY() + 2;
        double minY1 = 0;

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

        /////// Power of Pull Graphing ///////
        // Create database instance
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
    }

    @Override
    public void onBackPressed() { // handle back button press (take us to workout dashboard)
        Intent intent = new Intent(PostWorkoutActivity.this, WorkoutMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        startActivity(intent);
        finish(); // Can't go back
    }
}