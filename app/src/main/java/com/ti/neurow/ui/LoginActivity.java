package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;

import com.ti.neurow.db.DatabaseHelper; // access database
import com.ti.neurow.db.User; // for user handling
import com.ti.neurow.R;
import com.ti.neurow.ble.PromptRotateActivity;

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

        DatabaseHelper db = new DatabaseHelper(this); // create instance of database in this activity

        // Define views to elements in XML
        usernameEditText = (EditText) findViewById(R.id.edtTxtPromptUserID);
        passwordEditText = (EditText)findViewById(R.id.edtTxtPromptPassword);
        loginButton = (Button)findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get username and password from fields
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(LoginActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidUsername(username)) {
                    Toast.makeText(LoginActivity.this, "Username can only contain letters and numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPassword(password)) {
                    Toast.makeText(LoginActivity.this, "Password can only contain the following special characters: !@#$%^&*()-_+=", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if we get here, we should hypothetically be ready to login and move on
                // TEMPORARY FUNCTIONALITY: No registration happens, we just get taken to the next activity
                Toast.makeText(LoginActivity.this, "TODO (Meredith & Diego): Call this username from database", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, PromptRotateActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish(); // Can't go back
            }
        });
    }

    //*********************************
    // Input guidelines & user creation
    //*********************************

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

    //***********
    // Launchers
    //***********

    // TEMP: Launch PromptRotateActivity when login button is pressed (bypasses actual user authentication)
    public void launchPromptRotate(View v) {
        // Launch Log-in activity
        Intent i = new Intent(this, PromptRotateActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // close activity
    }
}