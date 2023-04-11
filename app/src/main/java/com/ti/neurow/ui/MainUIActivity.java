package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ti.neurow.R;
import com.ti.neurow.ble.MainActivity;
import com.ti.neurow.ble.UserBTConfig;
import com.ti.neurow.db.MainDBActivity;

public class MainUIActivity extends AppCompatActivity {

    // Declare views
    ImageView rower, rowerIcon; // image
    TextView neurowText, welcomeText; // text views
    Button existingUser, newUser, BLEData, DBdata, Config, AddToDB; // buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Lock orientation to portrait

        setContentView(R.layout.activity_ui_main);

        /* Additions to pass the BLE device */
        Intent intent = getIntent();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        boolean isDeviceReceived = false;
        if (device != null) {
            //throw new RuntimeException("Missing BluetoothDevice from MainActivity!");
            isDeviceReceived = true;
        }
        if(isDeviceReceived == true) {
            Toast.makeText(this, "The BLE device was successfully passed.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "The BLE device was not passed.", Toast.LENGTH_LONG).show();
        }
        /* End addition */

        // Animate rower icon and "Neurow" text
        rower = (ImageView)findViewById(R.id.rower_icon);
        neurowText = (TextView)findViewById(R.id.neurow_text);
        welcomeText = (TextView)findViewById(R.id.txtWelcome);
        existingUser = findViewById(R.id.btnExistingUser);
        newUser = findViewById(R.id.btnNewUser);
        rowerIcon = findViewById(R.id.rower_icon);
        BLEData = findViewById(R.id.btnBLEData);
        DBdata = findViewById(R.id.btnDBViewer);
        Config = findViewById(R.id.btnBluetoothConnections);
        AddToDB = findViewById(R.id.btnAddToDB);


        Animation animation1 = AnimationUtils.loadAnimation(MainUIActivity.this, R.anim.slide_in_left);
        Animation animation2 = AnimationUtils.loadAnimation(MainUIActivity.this, R.anim.slide_in_right);
        rower.startAnimation(animation1);
        neurowText.startAnimation(animation2);

        // Handler: Move rower icon and make TextView disappear after 2.5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rower.animate().translationYBy(-350).setDuration(1000).start();
                neurowText.animate().alpha(0.0f).setDuration(700).start();
            }
        }, 2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeText.setVisibility(View.VISIBLE);
                welcomeText.setAlpha(0f);
                welcomeText.animate().alpha(1f).setDuration(500).start();

                existingUser.setVisibility(View.VISIBLE);
                existingUser.setAlpha(0f);
                existingUser.animate().alpha(1f).setDuration(500).start();

                newUser.setVisibility(View.VISIBLE);
                newUser.setAlpha(0f);
                newUser.animate().alpha(1f).setDuration(500).start();

                BLEData.setVisibility(View.VISIBLE);
                BLEData.setAlpha(0f);
                BLEData.animate().alpha(1f).setDuration(500).start();

                DBdata.setVisibility(View.VISIBLE);
                DBdata.setAlpha(0f);
                DBdata.animate().alpha(1f).setDuration(500).start();

                Config.setVisibility(View.VISIBLE);
                Config.setAlpha(0f);
                Config.animate().alpha(1f).setDuration(500).start();
            }
        }, 3500);


        // Existing user button listener
        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainUIActivity.this, LoginActivity.class);
                //Needed to pass BLE device
                if(device != null) {
                    i.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
                }
                startActivity(i); // Launch Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // New user button listener
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent to launch next activity (SignupActivity)
                Intent i = new Intent(MainUIActivity.this, RegisterActivity.class);
                startActivity(i); // Launch Registration
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Connections Configurator button listener
        Config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent to launch next activity (RawDataActivity)
                //Intent i = new Intent(MainUIActivity.this, UserBTConfig.class);
                Intent i = new Intent(MainUIActivity.this, MainActivity.class);
                startActivity(i); // Launch BLE Data View
            }
        });

        // TEMP: BLE Data Viewer (Dev) button listener
        BLEData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent to launch next activity (RawDataActivity)
                Intent i = new Intent(MainUIActivity.this, MainActivity.class);
                startActivity(i); // Launch BLE Data View
            }
        });

        // TEMP: DB Data Viewer (Dev) button listener
        DBdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent to launch next activity (RawDataActivity)
                Intent i = new Intent(MainUIActivity.this, MainDBActivity.class);
                startActivity(i); // Launch BLE Data View
            }
        });

        // Easter egg message: If rower icon is held
        rowerIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Display message
                Toast.makeText(MainUIActivity.this, "An app by Diego, Alyson, Meredith, and Nick", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Config button cue
        Config.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Display message
                Toast.makeText(MainUIActivity.this, "Set up Bluetooth device connection", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}