package com.example.fitness_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    //splash screen for the program.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int SPLASH_TIMER = 3000;
        new Handler().postDelayed(() -> {

            Intent intent=new Intent(SplashScreenActivity.this, PhoneVerActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIMER);
    }
}