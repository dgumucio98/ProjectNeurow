package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;

import com.ti.neurow.GlobalVariables;
import com.ti.neurow.db.DatabaseHelper; // access database
import com.ti.neurow.R;

import java.util.regex.Pattern; // regular expression support for registration validation

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    // Declare buttons and EditTexts
    EditText usernameEditText,passwordEditText;
    Button loginButton, backButton, bypassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Lock orientation to portrait

        setContentView(R.layout.activity_login);


        /* Additions to pass the BLE device */
        Intent intent = getIntent();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        boolean isDeviceReceived = false;
        if (device != null) {
            //throw new RuntimeException("Missing BluetoothDevice from MainActivity!");
            isDeviceReceived = true;
        }
        // For logging and debugging, uncomment for app visual queue
        if(isDeviceReceived == true) {
            Timber.i("The BLE device was successfully passed.");
            //Toast.makeText(this, "The BLE device was successfully passed.", Toast.LENGTH_LONG).show();
        } else {
            Timber.i("The BLE device was not passed.");
            //Toast.makeText(this, "The BLE device was not passed.", Toast.LENGTH_LONG).show();
        }
        /* End addition */

        // Define views to elements in XML

        // Define UI elements
        usernameEditText = (EditText) findViewById(R.id.edtTxtPromptUserID);
        passwordEditText = (EditText)findViewById(R.id.edtTxtPromptPassword);
        loginButton = (Button)findViewById(R.id.btnLogin);
        //Bypass button to use onclick listener below
        bypassButton = (Button)findViewById(R.id.btnBypass);

        // Log In button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create instance of database in this activity
                DatabaseHelper db = new DatabaseHelper(LoginActivity.this);

                // Get username and password from fields
                String Username = usernameEditText.getText().toString(); // extract username from EditText
                String Password = passwordEditText.getText().toString(); // extract password from EditText

                if (TextUtils.isEmpty(Username) || !isValidUsername(Username)) { // if username EditText is empty
                    Toast.makeText(LoginActivity.this, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                    return; // exit listener
                }
                if (TextUtils.isEmpty(Password) || !isValidPassword(Password)) { // if password EditText is empty
                    Toast.makeText(LoginActivity.this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean userExists = db.user_exists(Username); // boolean used to check existence of user in DB

                // If all above conditions are met, check if username & password pair matches database record
                if (!TextUtils.isEmpty(Username) && isValidUsername(Username) && !TextUtils.isEmpty(Password) && isValidPassword(Password)) {

                    // Username or Password DNE Case
                    if (!userExists || !db.getPassword(Username).equals(Password)) { // if user doesn't exist or passwords don't match
                        Toast.makeText(LoginActivity.this, "Invalid User ID or password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Username exists and Password matches Case
                    else if (userExists && db.getPassword(Username).equals(Password)) { // if user exists and passwords match

                        GlobalVariables.loggedInUsername = Username; // update universal loggedinUsername value

                        // Logged in, now launch PromptRotateActivity
                        Intent i = new Intent(LoginActivity.this, PromptRotateActivity.class);

                        //Needed to pass BLE device
                        if(device != null) {
                            i.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
                        }

                        startActivity(i); // launches PromptRotateActivity
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return;
                    }
                }
            }
        });

        bypassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch Log-in activity
                GlobalVariables.loggedInUsername = "MrBypass"; // set as Mr. Bypass
                // Launch PromptRotateActivity

                Intent i = new Intent(LoginActivity.this, PromptRotateActivity.class);
                //Needed to pass BLE device
                if(device != null) {
                    i.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
                }
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish(); // close activity

            }
        });
    }

    @Override
    public void onBackPressed() { // Override back button press
        // Check if the user is coming from the LoginActivity
        if (isTaskRoot()) { // if user just logged in/registered
            Intent intent = new Intent(this, MainUIActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else {
            // If the user is not coming from LoginActivity, continue with the default behavior
            super.onBackPressed();

        }
    }

    // Define valid username guidelines
    boolean isValidUsername(String username) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$"); // no special characters allowed
        return pattern.matcher(username).matches();
    }

    // Define valid password guidelines
    boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()-_+=]*$"); // some special characters allowed
        return pattern.matcher(password).matches();
    }

    void logUserIn(String username, String password) {
        String Username = usernameEditText.getText().toString();
        String Password = usernameEditText.getText().toString();
    }

    // DEV BYPASS: Launch PromptRotateActivity when login button is pressed (bypasses actual user authentication)
    public void launchPromptRotate(View v) {

        GlobalVariables.loggedInUsername = "MrBypass"; // set as Mr. Bypass

        // Launch PromptRotateActivity
        Intent i = new Intent(this, PromptRotateActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // close activity
    }
}