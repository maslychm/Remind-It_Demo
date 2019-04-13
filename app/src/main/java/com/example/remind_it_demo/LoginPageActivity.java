package com.example.remind_it_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPageActivity extends AppCompatActivity {

    private Button loginButton;
    private Button goToRegisterButton;
    private EditText loginEdit;
    private EditText passwordEdit;

    private String login;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loginButton = findViewById(R.id.LoginButton);
        goToRegisterButton = findViewById(R.id.GoToRegister);
        loginEdit = findViewById(R.id.loginEnter);
        passwordEdit = findViewById(R.id.passwordEnter);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginEdit.getText().toString();
                password = passwordEdit.getText().toString();

                if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in login and password",Toast.LENGTH_SHORT).show();
                }

                // Call to DB login request
                // if goes through and userData correctly initialized, proceed to Reminders Page Activity

                //goToRemindersPageActivity(view);
            }
        });

        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity(v);
            }
        });
    }

    public void goToRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterPageActivity.class);
        startActivity(intent);
    }

    public void goToRemindersPageActivity(View view) {
        Intent intent = new Intent(this, RemindersPageActivity.class);
        startActivity(intent);
    }
}
