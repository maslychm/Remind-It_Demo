package com.example.remind_it_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
    }

    public void goToRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterPageActivity.class);
        startActivity(intent);
    }
}
