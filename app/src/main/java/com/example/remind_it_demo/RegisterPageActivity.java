package com.example.remind_it_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RegisterPageActivity extends AppCompatActivity {

    private EditText loginRegEdit;
    private EditText passwordRegEdit;
    private EditText emailRegEdit;
    private Button registerButton;
    private Button goToLoginButton;

    private String login;
    private String password;
    private String email;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        loginRegEdit = findViewById(R.id.LoginEditText);
        passwordRegEdit = findViewById(R.id.PasswordEditText);
        emailRegEdit = findViewById(R.id.emailEditText);
        registerButton = findViewById(R.id.RegisterButton);
        goToLoginButton = findViewById(R.id.GoToLoginButton);

        queue = Volley.newRequestQueue(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginRegEdit.getText().toString();
                password = passwordRegEdit.getText().toString();
                email = emailRegEdit.getText().toString();

                if (login == null || login.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in login, email and password",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call to DB register request
                sendRegisterRequest(login, email, password);
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

        handleSSLHandshake();
    }

    public void sendRegisterRequest(String login, String email, String password) {
        JSONObject registerData;

        try {
            registerData = new JSONObject().put("username", login).put("password", password).put("mobile", true).put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        //showProgress(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( "https://themeanteam.site/users/reg",
                registerData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("reg response from server", response.toString());
                try {
                    if (response.getBoolean("success")) {

                        /*
                        JSONArray arr = response.getJSONObject("user").getJSONArray("reminders");
                        if (arr != null) {
                            System.out.println(arr.length());
                            App.userData.setUserEvents(arr);
                        }
                        //showProgress(false);
                        */

                        // Log response details for debugging
                        Log.i("responseID",response.getJSONObject("user").getString("id"));
                        Log.i("responseName",response.getJSONObject("user").getString("username"));

                        // Save into userData
                        App.userData.setUserID(response.getJSONObject("user").getString("id"));
                        App.userData.setUsername(response.getJSONObject("user").getString("username"));

                        // Load in userEvents and publicEvent

                        //JSONArray userEventsJSON = //call to backends userEvents
                        //JSONArray nearbyEventsJSON = //call to backends nearbyEvents
                        //App.userData.setUserEvents(userEventsJSON);
                        //App.userData.setNearbyEvents(nearbyEventsJSON);
                        Toast.makeText(RegisterPageActivity.this, "Successfully registered user" + response.getJSONObject("user").getString("username"), Toast.LENGTH_SHORT).show();

                    } else {
                        if ("User already exists.".equals(response.getString("msg"))) {
                            Toast.makeText(RegisterPageActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(RegisterPageActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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

    public void goToLoginActivity(View view) {
        finish();
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
