package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ti.neurow.R;
import com.ti.neurow.ble.PromptRotateActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Lock orientation to portrait
    }

    // Launch MainActivity when back button is pressed
    public void launchMain (View v) {
        // Launch Log-in activity
        Intent i = new Intent(this, MainUIActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // Launch PromptRotateActivity when login button is pressed (temporary functionality)
    public void launchPromptRotate (View v) {
        // Launch Log-in activity
        //System.out.println("The rotation has begun.\n");
        // This works, but doesn't seem to be calling the proper intent
        Intent i = new Intent(this, PromptRotateActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // Can't go back
    }
}