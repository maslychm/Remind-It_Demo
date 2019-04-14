package com.example.remind_it_demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPageActivity extends AppCompatActivity {

    private Button loginButton;
    private Button goToRegisterButton;
    private EditText loginEdit;
    private EditText passwordEdit;
    private View progressView;
    private View loginFormView;

    private String login;
    private String password;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loginButton = findViewById(R.id.LoginButton);
        goToRegisterButton = findViewById(R.id.GoToRegister);
        loginEdit = findViewById(R.id.loginEnter);
        passwordEdit = findViewById(R.id.passwordEnter);

        final RequestQueue queue = Volley.newRequestQueue(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                login = loginEdit.getText().toString();
                password = passwordEdit.getText().toString();

                if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in login and password",Toast.LENGTH_SHORT).show();
                }
                // Create JSON Object for Login data
                JSONObject loginData;
                try {
                    loginData = new JSONObject().put("username", login).put("password", password).put("mobile", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                // Call to DB login request
                // if goes through and userData correctly initialized, proceed to Reminders Page Activity

                //showProgress(true);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( "http://192.168.1.23:3000/users/auth",
                        loginData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray arr = response.getJSONObject("user").getJSONArray("reminders");
                                if (arr != null) {
                                    System.out.println(arr.length());
                                    App.userData.setUserEvents(arr);
                                    goToRemindersPageActivity(v);
                                }
                                //showProgress(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                        /// TODO: Handle error
                    }
                });

                queue.add(jsonObjectRequest);
            }
        });

        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity(v);
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.progressBar);
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
