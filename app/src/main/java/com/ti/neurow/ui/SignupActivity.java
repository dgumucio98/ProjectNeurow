package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ti.neurow.R;
import com.ti.neurow.ble.PromptRotateActivity;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Lock orientation to portrait

        setContentView(R.layout.activity_signup);
    }

    // Launch MainActivity when back button is pressed
    public void launchMain(View v) {
        // Launch Log-in activity
        Intent i = new Intent(SignupActivity.this, MainUIActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // Launch PromptRotateActivity when register button is pressed (temporarily bypasses actual user validation)
    public void launchPromptRotate(View v) {
        // Launch Log-in activity
        Intent i = new Intent(this, PromptRotateActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // Can't go back
    }
}