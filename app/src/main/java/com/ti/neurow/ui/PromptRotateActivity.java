package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ti.neurow.GlobalVariables;
import com.ti.neurow.R;

public class PromptRotateActivity extends AppCompatActivity {

    // Declare views
    TextView WelcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_prompt_rotate);



        // Define elements
        WelcomeMessage = (TextView) findViewById(R.id.txtWelcome);
        WelcomeMessage.setText("Welcome, " + GlobalVariables.loggedInUsername);


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

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Changed the intent for a longer statement to insert device check
            Intent i = new Intent(this, WorkoutMainActivity.class);
            //Needed to pass BLE device
            if(device != null) {
                i.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
            }
            startActivity(i);
            finish(); // Can't go back
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

}