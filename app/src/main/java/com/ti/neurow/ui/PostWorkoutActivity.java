package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.jjoe64.graphview.GraphView; // for graphs
import com.jjoe64.graphview.GridLabelRenderer;
import com.ti.neurow.GlobalVariables; // for access to finalListTimePower
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import com.ti.neurow.R;


public class PostWorkoutActivity extends AppCompatActivity {

    GraphView Power_vs_Time; // declare left graph
    GraphView Power_vs_Pull; // declare right graph

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Toast.makeText(PostWorkoutActivity.this,"[TEST] Reached beginning of PostWorkoutActivity".toString(),Toast.LENGTH_SHORT).show();

        // Tweak visible elements
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide Action bar and Status bar
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Lock orientation to landscape
        setContentView(R.layout.activity_post_workout);

        Power_vs_Time = findViewById(R.id.Power_vs_Time);
        Power_vs_Pull = findViewById(R.id.Power_vs_Pull);

        // [TEST] Populate list before graphing
        int length = GlobalVariables.finalListTimePower.size(); // length of list
        int j = 0; // double-time iterator
        DataPoint[] dp = new DataPoint[length/2];
        for (int i = 0; i < length - 1; i += 2) {
            dp[j] = new DataPoint(GlobalVariables.finalListTimePower.get(i), GlobalVariables.finalListTimePower.get(i + 1));
            if (i % 2 == 0){
                j++;
            }
        }

        // Set series to graph
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);

        Power_vs_Time.addSeries(series); // add our data
        Power_vs_Time.setTitle("Power vs. Time"); // set title of graph
        Power_vs_Time.setTitleColor(getResources().getColor(R.color.purple_200)); // set color of title
        Power_vs_Time.setTitleTextSize(35); // set title text size

        // Get the maximum and minimum x and y values from the series
        double maxX = series.getHighestValueX();
        double minX = 0;
        double maxY = series.getHighestValueY() + 2;
        double minY = 0;

        // Set the bounds of the viewport to the maximum and minimum values
        Power_vs_Time.getViewport().setMinX(minX);
        Power_vs_Time.getViewport().setMaxX(maxX);
        Power_vs_Time.getViewport().setMinY(minY);
        Power_vs_Time.getViewport().setMaxY(maxY);

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

        series.setColor(getResources().getColor(R.color.teal_200)); // set color of line
    }
}