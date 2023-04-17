package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;

import com.ti.neurow.GlobalVariables;
import com.ti.neurow.db.DatabaseHelper; // access database
import com.ti.neurow.db.User; // for user handling
import com.ti.neurow.R;

import java.util.regex.Pattern; // regular expression support for registration validation

import timber.log.Timber;

public class RegisterActivity extends AppCompatActivity {

    // Declare buttons and EditTexts
    EditText usernameEditText,passwordEditText;
    Button registerButton, backButton;
    // Need to over ride the  listener upon creation
    Button bypassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Lock orientation to portrait

        setContentView(R.layout.activity_register);

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


        // Define views to elements in XML
        usernameEditText = (EditText) findViewById(R.id.edtTxtPromptUserID);
        passwordEditText = (EditText)findViewById(R.id.edtTxtPromptPassword);
        registerButton = (Button)findViewById(R.id.btnRegister);
        bypassButton = (Button)findViewById(R.id.btnBypass);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create instance of database in this activity
                DatabaseHelper db = new DatabaseHelper(RegisterActivity.this);

                // Get username and password from fields
                String Username = usernameEditText.getText().toString(); // extract username from EditText
                String Password = passwordEditText.getText().toString(); // extract password from EditText

                if (TextUtils.isEmpty(Username)) { // if username EditText is empty
                    Toast.makeText(RegisterActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidUsername(Username)) { // if username EditText is not valid
                    Toast.makeText(RegisterActivity.this, "Username can only contain letters and numbers", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Password)) { // if password EditText is empty
                    Toast.makeText(RegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidPassword(Password)) { // if password EditText is not valid
                    Toast.makeText(RegisterActivity.this, "Password can only contain the following special characters: !@#$%^&*()-_+=", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean userExists = db.user_exists(Username); // boolean used to check existence of user in DB

                // Check if user exists in database
                if (userExists) { // prompt that username is taken with toast
                    Toast.makeText(RegisterActivity.this, "Sorry, that username is taken!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else { // add user to database
                    User newUser = new User(Username, Password, 0, 0, 0, 0, 0, 0, 0,0);
                    boolean success = db.add_account(newUser);
                    if (success == true) { // if successful
                        Toast.makeText(RegisterActivity.this, "[TEST] User " + Username + " has been registered!", Toast.LENGTH_SHORT).show();
                        GlobalVariables.loggedInUsername = Username; // update global value
                        Toast.makeText(RegisterActivity.this, "[TEST] loggedInUsername: " + GlobalVariables.loggedInUsername, Toast.LENGTH_SHORT).show();

                        // Logged in, now launch PromptRotateActivity
                        Intent i = new Intent(RegisterActivity.this, PromptRotateActivity.class);
                        //Needed to pass BLE device
                        if(device != null) {
                            i.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
                        }
                        startActivity(i); // launches PromptRotateActivity
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }
                    else { // if not successful
                        Toast.makeText(RegisterActivity.this, "[TEST] User " + Username + " has NOT been registered", Toast.LENGTH_SHORT).show();
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

                Intent i = new Intent(RegisterActivity.this, PromptRotateActivity.class);
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

    // DEV BYPASS: Launch PromptRotateActivity when "Bypass Login" button is pressed (bypasses actual user authentication)
    //Moved the function into the onclick listener in the onCreate
    public void launchPromptRotate(View v) {
        // Launch Log-in activity

        GlobalVariables.loggedInUsername = "MrBypass"; // set as Mr. Bypass

        // Launch PromptRotateActivity
        Intent i = new Intent(this, PromptRotateActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // close activity
    }

}