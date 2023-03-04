package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ti.neurow.R;

public class SplashActivity extends AppCompatActivity {

    // Declare icon and text icon
    ImageView rower;
    TextView neurowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action bar and Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Lock orientation to landscape

        // Animate rower icon and "Neurow" text
        rower = (ImageView)findViewById(R.id.rower_icon);
        Animation animation1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in);
        rower.startAnimation(animation1);
        neurowText = (TextView)findViewById(R.id.neurow_text);
        Animation animation2 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in);
        neurowText.startAnimation(animation2);

        // Handler: Take us to MainActivity after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainUIActivity.class);
                startActivity(intent);
                finish();
            }
        },2500);
    }
}