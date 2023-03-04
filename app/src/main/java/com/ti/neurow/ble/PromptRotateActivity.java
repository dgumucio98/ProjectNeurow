package com.ti.neurow.ble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ti.neurow.R;
import com.ti.neurow.ui.WorkoutMainActivity;

public class PromptRotateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_prompt_rotate);

        // Toast if user clicks the gif
        pl.droidsonroids.gif.GifImageView gifRotate = findViewById(R.id.gifRotate);
        gifRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PromptRotateActivity.this, "Rotate your device to get started", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            startActivity(new Intent(this, WorkoutMainActivity.class));
            finish(); // Can't go back
        }
    }
}