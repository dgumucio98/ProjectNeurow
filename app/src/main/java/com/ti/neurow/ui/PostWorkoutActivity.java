package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.jjoe64.graphview.GraphView; // for graphs
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
        DataPoint[] dp = new DataPoint[length];
        for (int i = 0; i < length-1; i += 2) {
            dp[j] = new DataPoint(GlobalVariables.finalListTimePower.get(i), GlobalVariables.finalListTimePower.get(i + 1));
            System.out.println(dp[i]);
            if (i % 2 == 0){
                j++;
            }
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);

        Power_vs_Time.animate();
        Power_vs_Time.getViewport().setScrollable(true);
        Power_vs_Time.getViewport().setScalable(true);
        Power_vs_Time.getViewport().setScalableY(true);
        Power_vs_Time.getViewport().setScrollableY(true);
        series.setColor(getResources().getColor(R.color.purple_200));
        Power_vs_Time.addSeries(series);
    }
}