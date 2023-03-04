package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ti.neurow.R;
import com.ti.neurow.ble.MainActivity;

// Main Activity class (MainActivity is a subclass of AppCompactActivity, hence "extends")
public class MainUIActivity extends AppCompatActivity {

    // Declare class objects we will give functionality to
    Button existingUser, newUser, BLEData; // buttons
    ImageView rowerIcon; // only used for long-press toast message

    @Override
    protected void onCreate(Bundle savedInstanceState) { // cold-start method to start activity
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        // Lock orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_ui_main); // specifies .xml layout to be used

        // Define elements (by ID in .xml)
        existingUser = findViewById(R.id.btnExistingUser);
        newUser = findViewById(R.id.btnNewUser);
        rowerIcon = findViewById(R.id.rower_icon);
        BLEData = findViewById(R.id.btnBLEData);

        // Existing user button listener
        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainUIActivity.this, LoginActivity.class);
                startActivity(i); // Launch Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // New user button listener
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent to launch next activity (SignupActivity)
                Intent i = new Intent(MainUIActivity.this, SignupActivity.class);
                startActivity(i); // Launch Signup
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // TEMP: BLE Data View (Dev) button listener
        BLEData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent to launch next activity (RawDataActivity)
                Intent i = new Intent(MainUIActivity.this, MainActivity.class);
                startActivity(i); // Launch BLE Data View
            }
        });

        // Easter egg: If rower icon is held
        rowerIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Display message
                Toast.makeText(MainUIActivity.this, "An app by Diego, Alyson, Meredith, and Nick", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}