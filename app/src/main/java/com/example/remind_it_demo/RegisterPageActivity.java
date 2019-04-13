package com.example.remind_it_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterPageActivity extends AppCompatActivity {

    private EditText loginRegEdit;
    private EditText passwordRegEdit;
    private Button registerButton;
    private Button goToLoginButton;

    private String login;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        loginRegEdit = findViewById(R.id.LoginEditText);
        passwordRegEdit = findViewById(R.id.PasswordEditText);
        registerButton = findViewById(R.id.RegisterButton);
        goToLoginButton = findViewById(R.id.GoToLoginButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginRegEdit.getText().toString(); // TODO Bug in this line complains about null reference
                password = passwordRegEdit.getText().toString();

                if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in login and password",Toast.LENGTH_SHORT).show();
                }

                // Call to DB register request
                // If OK, toast
                // Toast.makeText(getApplicationContext(), "Thank you for registering " + login,Toast.LENGTH_SHORT).show();

                //goToLoginActivity(view);
            }
        });

        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity(v);
            }
        });
    }

    public void goToLoginActivity(View view) {
        finish();
    }
}
