package com.example.remind_it_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Take user to the login page
    public void goToLoginActivity(View view) {
        Intent intent = new Intent(this, LoginPageActivity.class);
        startActivity(intent);
    }

    // For testing only: bypass login and go to fake reminders list
    public void bypassLogin(View view) {
        Intent intent = new Intent(this, RemindersPageActivity.class);
        startActivity(intent);
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
