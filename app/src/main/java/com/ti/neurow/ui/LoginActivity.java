package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity {

    // Declare buttons and EditTexts
    EditText usernameEditText,passwordEditText;
    Button loginButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Lock orientation to portrait

        setContentView(R.layout.activity_login);

        // Define views to elements in XML
        usernameEditText = (EditText) findViewById(R.id.edtTxtPromptUserID);
        passwordEditText = (EditText)findViewById(R.id.edtTxtPromptPassword);
        loginButton = (Button)findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create instance of database in this activity
                DatabaseHelper db = new DatabaseHelper(LoginActivity.this);

                // Get username and password from fields
                String Username = usernameEditText.getText().toString(); // extract username from EditText
                String Password = usernameEditText.getText().toString(); // extract password from EditText

                if (TextUtils.isEmpty(Username)) { // if username EditText is empty
                    Toast.makeText(LoginActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidUsername(Username)) { // if username EditText is not valid
                    Toast.makeText(LoginActivity.this, "Username can only contain letters and numbers", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Password)) { // if password EditText is empty
                    Toast.makeText(LoginActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidPassword(Password)) { // if password EditText is not valid
                    Toast.makeText(LoginActivity.this, "Password can only contain the following special characters: !@#$%^&*()-_+=", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean userExists = db.user_exists(Username); // boolean used to check existence of user in DB

                // Check if user exists in database
                if (!userExists || !db.getPassword(Username).equals(Password)) { // if user doesn't exist or passwords don't match
                    Toast.makeText(LoginActivity.this, "Invalid User ID or password", Toast.LENGTH_SHORT).show();
                }

                // TODO: below comparison doesn't seem to be case-sensitive, meaning it will accept "foobar123" and "Foobar123"
                else if (userExists && db.getPassword(Username).equals(Password)) { // if user exists and passwords match

                    GlobalVariables.loggedInUsername = Username; // update universal loggedinUsername value

                    Toast.makeText(LoginActivity.this, "[TEST] User " + Username + " has been logged in!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "[TEST] loggedInUsername: " + GlobalVariables.loggedInUsername, Toast.LENGTH_SHORT).show();

                    // Logged in, now launch PromptRotateActivity
                    Intent i = new Intent(LoginActivity.this, PromptRotateActivity.class);
                    startActivity(i); // launches PromptRotateActivity
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return; // exit method
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Check if the user is coming from the LoginActivity
        if (isTaskRoot()) { // if user just logged in/registered

            Intent intent = new Intent(this, MainUIActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else {
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
        // Launch Log-in activity
        Intent i = new Intent(this, PromptRotateActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // close activity
    }
}