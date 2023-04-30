package com.ti.neurow.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ti.neurow.GlobalVariables;
import com.ti.neurow.R;
import com.ti.neurow.ble.MainActivity;
import com.ti.neurow.ble.UserBTConfig;
import com.ti.neurow.ble.pm5Utility;
import com.ti.neurow.db.MainDBActivity;

import timber.log.Timber;

public class MainUIActivity extends AppCompatActivity {

    // Declare views
    ImageView rower, rowerIcon, TIIcon, imageArrow; // image
    TextView neurowText, welcomeText, txtSponsor, txtConnectPrompt, txtConnectionStatus,txtConnectionFeedback; // text views
    Button existingUser, newUser, BLEData, DBdata, Config, AddToDB; // buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toast.makeText(getApplicationContext(), "[TEST] MainUIActivity created!", Toast.LENGTH_SHORT).show();


        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Lock orientation to portrait

        setContentView(R.layout.activity_ui_main);

        GlobalVariables.BTconnected = false;

        // Define UI Elements
        rower = (ImageView)findViewById(R.id.rower_icon);
        neurowText = (TextView)findViewById(R.id.neurow_text);
        welcomeText = (TextView)findViewById(R.id.txtWelcome);
        existingUser = findViewById(R.id.btnExistingUser);
        newUser = findViewById(R.id.btnNewUser);
        rowerIcon = findViewById(R.id.rower_icon);
        Config = findViewById(R.id.btnBluetoothConnections);
        txtSponsor = findViewById(R.id.txtSponsor);
        TIIcon = findViewById(R.id.icon_TI);
        txtConnectPrompt = findViewById(R.id.txtConnectPrompt);
        imageArrow = findViewById(R.id.imageArrow);
        txtConnectionStatus = findViewById(R.id.txtConnectionStatus);
        txtConnectionFeedback = findViewById(R.id.txtConnectionFeedback);

        // Neurow text and icon Animation
        Animation animation1 = AnimationUtils.loadAnimation(MainUIActivity.this, R.anim.slide_in_left);
        Animation animation2 = AnimationUtils.loadAnimation(MainUIActivity.this, R.anim.slide_in_right);
        rower.startAnimation(animation1);
        neurowText.startAnimation(animation2);

        // For pulsating animation
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 1.2f, 1f);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scale = (float) valueAnimator.getAnimatedValue();
                txtConnectPrompt.setScaleX(scale);
                txtConnectPrompt.setScaleY(scale);
                imageArrow.setScaleX(scale);
                imageArrow.setScaleY(scale);

            }
        });

        animator.start();

        // Animation Handlers
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                rower.animate().translationYBy(-350).setDuration(800).start();
                neurowText.animate().alpha(0.0f).setDuration(500).start();
            }
        }, 1800);
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

                Config.setVisibility(View.VISIBLE);
                Config.setAlpha(0f);
                Config.animate().alpha(1f).setDuration(500).start();

                imageArrow.setVisibility(View.VISIBLE);
                imageArrow.setAlpha(0f);
                imageArrow.animate().alpha(1f).setDuration(500).start();

                txtConnectPrompt.setVisibility(View.VISIBLE);
                txtConnectPrompt.setAlpha(0f);
                txtConnectPrompt.animate().alpha(1f).setDuration(500).start();
            }
        }, 2400);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TIIcon.setVisibility(View.VISIBLE);
                TIIcon.setAlpha(0f);
                TIIcon.animate().alpha(1f).setDuration(500).start();

                txtSponsor.setVisibility(View.VISIBLE);
                txtSponsor.setAlpha(0f);
                txtSponsor.animate().alpha(1f).setDuration(500).start();
                showDisclaimerDialog();

            }
            }, 3000);


        // Existing user button listener
        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLoginActivity = new Intent(MainUIActivity.this, LoginActivity.class);
                startActivity(goToLoginActivity); // Launch Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                // Note: MainUIActivity is NEVER to be destroyed. Will always be reference homebase
            }
        });

        // New user button listener
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent to launch next activity (SignupActivity)
                Intent goToRegisterActivity = new Intent(MainUIActivity.this, RegisterActivity.class);
                //Needed to pass BLE device
                startActivity(goToRegisterActivity); // Launch Registration screen
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

        // Connections Configurator button listener
        Config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Only launch connections screen if device hasn't been connected
                if (GlobalVariables.BTconnected) { // device already connected
                    txtConnectionFeedback.setVisibility(View.VISIBLE);
                }
                else { // device not connected
                    Intent goToUserBTConfig = new Intent(MainUIActivity.this, MainActivity.class);
                    startActivity(goToUserBTConfig); // launch connections activity
                }

            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

        if (GlobalVariables.BTconnected) { // if device is connected
            txtConnectionStatus.setVisibility(View.VISIBLE); // display text

            // Hide connection suggestors
            imageArrow.setVisibility(View.GONE);
            txtConnectPrompt.setVisibility(View.GONE);
        }

    }

    // Prepares and shows dialog box prompting to reset the PM5
    private void showDisclaimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Safety Disclaimer");
        builder.setMessage(Html.fromHtml("Before use, please consult with your doctor " +
                "if you have any health problems or concerns that could be affected by intense exercise. " +
                "This includes, but is not limited to, heart disease, high blood pressure, diabetes, " +
                "pregnancy, and any other medical conditions that may make exercise more risky. " +
                "<b>If you experience any pain or discomfort while using Neurow, stop immediately " +
                "and seek medical attention.</b>"));

        builder.setPositiveButton("Acknowledge", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // close the dialog
            }
        });

        AlertDialog dialog = builder.show();
    }
}