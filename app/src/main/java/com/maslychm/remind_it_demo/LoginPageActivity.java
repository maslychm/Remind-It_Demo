package com.maslychm.remind_it_demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    private RequestQueue queue;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loginButton = findViewById(R.id.LoginButton);
        goToRegisterButton = findViewById(R.id.GoToRegister);
        loginEdit = findViewById(R.id.loginEnter);
        passwordEdit = findViewById(R.id.passwordEnter);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.progressBar);

        queue = Volley.newRequestQueue(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                login = loginEdit.getText().toString();
                password = passwordEdit.getText().toString();

                // Check both fields have input
                if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in login and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendLoginRequest(login, password, view);
            }
        });

        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity(v);
            }
        });
    }

    public void sendLoginRequest(String login, String password, final View view) {
        JSONObject loginData;

        try {
            loginData = new JSONObject().put("username", login).put("password", password).put("mobile", true);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.i("LoginPage Activity","Sending login request with: " + login + " : " + password);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                getString(R.string.login_url),
                loginData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("LoginActivity: Auth response from server", response.toString());
                try {
                    if (response.getBoolean("success")) {
                        if (!saveUserData(response)) {
                            Toast.makeText(LoginPageActivity.this, "Could not load user info", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!(fetchReminders(view))) {
                            Toast.makeText(LoginPageActivity.this, "Could not load events", Toast.LENGTH_SHORT);
                        }
                    } else {
                        Toast.makeText(LoginPageActivity.this, "Login or password incorrect", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                Log.e("LoginActivity", "Error response from the endpoint");
            }
        });

        queue.add(jsonObjectRequest);
    }

    public boolean saveUserData(JSONObject response) {
        try {
            Log.i("responseTOKEN ", response.getString("token"));
            Log.i("responseID", response.getJSONObject("user").getString("id"));
            Log.i("responseName", response.getJSONObject("user").getString("username"));

            App.userData.setUserID(response.getJSONObject("user").getString("id"));
            App.userData.setUsername(response.getJSONObject("user").getString("username"));
            App.userData.setToken(response.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Could not save user data", "Incorrect response???");
            return false;
        }
        return true;
    }

    public boolean fetchReminders(final View view) {
        //RequestQueue queueEvents = Volley.newRequestQueue(getApplicationContext());
        JSONArray getEventData;

        try {
            getEventData = new JSONArray();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                getString(R.string.fetchReminders_url) + App.userData.getUserID(),
                getEventData, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.i("Fetch Reminders RESPONSE: ",response.toString());
                if (response != null) {
                    App.userData.setUserEvents(response);
                    fetchAllEvents(view);
                    //goToRemindersPageActivity(view);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login page volley fetch reminders error",error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization",App.userData.getToken());
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

        return true;
    }

    public boolean fetchAllEvents(final View view) {
        //RequestQueue queueEvents = Volley.newRequestQueue(getApplicationContext());
        JSONArray getEventData;

        try {
            getEventData = new JSONArray();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                getString(R.string.fetchAllEvents) + "/",
                getEventData, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.i("Fetch Events RESPONSE: ",response.toString());
                if (response != null) {
                    App.userData.setNearbyEvents(response);
                    goToRemindersPageActivity(view);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login page volley fetch events error",error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization",App.userData.getToken());
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

        return true;
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
