package com.micronimbus.doctorshaheb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Start a new thread to delay and then decide where to go
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isRegistered = prefs.getBoolean("isRegistered", false);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            Intent intent;
            if (!isRegistered) {
                intent = new Intent(Splashscreen.this, Login.class);
            } else if (!isLoggedIn) {
                intent = new Intent(Splashscreen.this, Registration.class);
            } else {
                intent = new Intent(Splashscreen.this, MainActivity.class);
            }

            startActivity(intent);
            finish(); // Close Splashscreen

        }).start();
    }
}
