package com.example.remind_it_demo;

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

    RequestQueue queue;

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
                    Toast.makeText(getApplicationContext(), "Please fill in login and password",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Try logging in and open activity from withing it
                // Because it's an async task, main thread will finish first
                // so just wait for full POST response
                sendLoginRequest(login, password, view);
            }
        });

        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity(v);
            }
        });

        // Ignore SSL Certificate Check
        handleSSLHandshake();
    }

    public void sendLoginRequest(String login, String password, final View view) {

        // Create JSON Object for Login data
        JSONObject loginData;

        try {
            loginData = new JSONObject().put("username", login).put("password", password).put("mobile", true);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        //showProgress(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                "https://themeanteam.site/users/auth",
                loginData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("auth response from server", response.toString());
                try {
                    if (response.getBoolean("success")) {
                        saveUserData(response);
                        fetchEvents();
                        goToRemindersPageActivity(view);
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
                // TODO: Handle error
            }
        });

        queue.add(jsonObjectRequest);
    }

    public void saveUserData(JSONObject response) {
        try {
            // Log response details for debugging
            Log.i("responseTOKEN ", response.getString("token"));
            Log.i("responseID", response.getJSONObject("user").getString("id"));
            Log.i("responseName", response.getJSONObject("user").getString("username"));

            // Save into userData
            App.userData.setUserID(response.getJSONObject("user").getString("id"));
            App.userData.setUsername(response.getJSONObject("user").getString("username"));
            App.userData.setToken(response.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchEvents() {
        RequestQueue queueEvents = Volley.newRequestQueue(getApplicationContext());
        JSONObject getEventData;
        try {
            getEventData = new JSONObject().put("userID",App.userData.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Log.i("fetchEvents() ", getEventData.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://themeanteam.site/events/read",
                getEventData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("eventlist response from server", response.toString());
                try {
                    if (response.getBoolean("success")) {
                        // TODO pass correct values and correctly parse
                        // into JSONArray
                        //JSONArray arr = response.getJSONObject("user").getJSONArray("reminders");
                        //if (arr != null) {
                         //   System.out.println(arr.length());
                            //   App.userData.setUserEvents(arr);
                        //}

                    } else {
                        // If no success
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                // TODO: Handle error
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization",App.userData.getToken());
                return headers;
            }
        };
        queueEvents.add(jsonObjectRequest);

        //showProgress(false);


        //JSONArray userEventsJSON = //call to backends userEvents
        //JSONArray nearbyEventsJSON = //call to backends nearbyEvents
        //App.userData.setUserEvents(userEventsJSON);
        //App.userData.setNearbyEvents(nearbyEventsJSON);
    }

    public void goToRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterPageActivity.class);
        startActivity(intent);
    }

    public void goToRemindersPageActivity(View view) {
        Intent intent = new Intent(this, RemindersPageActivity.class);
        startActivity(intent);
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        }
        catch (Exception ignored) { }
    }
}
