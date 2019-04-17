package com.maslychm.remind_it_demo;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import static com.maslychm.remind_it_demo.App.NOTIFICATION_CHANNEL;

public class NewReminderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    // UI elements
    private TextView displayCoord;
    private EditText editTextName;
    private EditText editTextDescription;
    private Button buttonSetDueDate;
    private Button buttonAddReminder;
    private Button buttonCancel;
    private Button buttonCurrentLocation;
    private Switch repeatSwitch;
    private Switch nearbySwitch;
    private Button openPickLocationButton;
    private Button setDueTimeButton;

    DialogFragment timePicker;

    // Helping vars
    private String dateString;
    private NotificationManagerCompat notificationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private RequestQueue queue;

    // Event constructor variables
    private Calendar calendar;
    private Calendar innerCalendar;
    boolean repeatCheck = false;
    boolean mustBeNear = false;
    double latitude = 0.0f;
    double longitude = 0.0f;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        notificationManager = NotificationManagerCompat.from(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        TimeZone tz = TimeZone.getTimeZone("EDT");
        calendar =  Calendar.getInstance();
        calendar.setTimeZone(tz);
        queue = Volley.newRequestQueue(this);

        displayCoord = (TextView) findViewById(R.id.latlong);
        editTextName = (EditText) findViewById(R.id.edit_text_name);
        editTextDescription = (EditText) findViewById(R.id.edit_text_description);
        buttonCancel = (Button) findViewById(R.id.cancelButton);
        buttonAddReminder = (Button) findViewById(R.id.addReminder);
        buttonSetDueDate = (Button) findViewById(R.id.setDueDateButton);
        buttonCurrentLocation = (Button) findViewById(R.id.useLocationButton);
        repeatSwitch = (Switch) findViewById(R.id.repeatSwitch);
        nearbySwitch = (Switch) findViewById(R.id.nearbySwitch);
        openPickLocationButton = (Button) findViewById(R.id.openLocPicker);
        setDueTimeButton = (Button) findViewById(R.id.timePicker);

        // Finish the New Reminder Activity when clicked Button cancel
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        // Add reminder locally and request HTTPS when clicked add
        buttonAddReminder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addNewReminder(view);
            }
        });

        // Display date picker
        buttonSetDueDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        setDueTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");

                innerCalendar = Calendar.getInstance();
                innerCalendar = ((TimePickerFragment) timePicker).getCalendar();
            }
        });

        buttonCurrentLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // If location permissions are granted and location returned, save it
                if (ContextCompat.checkSelfPermission(NewReminderActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(NewReminderActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        longitude = location.getLongitude();
                                        latitude = location.getLatitude();
                                        String locationString = "" +latitude + ", " + longitude;
                                        displayCoord.setText(locationString);
                                    }
                                }
                            });
                }
            }
        });

        repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               repeatCheck = isChecked;
            }
        });

        nearbySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mustBeNear = isChecked;
            }
        });

        openPickLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goToPickLocationActivity(view);
            }
        });
    }

    public void goToPickLocationActivity(View view) {
        Intent intent = new Intent(this, PickLocationActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        buttonSetDueDate.setText(dateString);
    }

    public void addNewReminder(View view) {
        // Create event from userData and boxes, add event
        String userID = App.userData.getUserID();
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();

        // A non practical way to do this, but if set at this time, means the clock pick is already closed...
        if (!(timePicker == null)) {
            innerCalendar = ((TimePickerFragment) timePicker).getCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, innerCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, innerCalendar.get(Calendar.MINUTE));
        }

        event = new Event(userID,name,description);
        //event.set_id();
        event.setDueDate(calendar.toInstant());
        event.setPublic(false);
        event.setRepeats(repeatCheck);
        event.setRepeatUnit(""); //TODO set repeat unit
        event.setRepeatConst(0); //TODO set repeatConst
        event.setLongitude(longitude);
        event.setLatitude(latitude);
        event.setMustBeNear(mustBeNear);
        event.setComplete(false);

        boolean success = sendAddReminderRequest(event);
    }

    public boolean sendAddReminderRequest(Event event) {

        // Create JSON Object for Event data
        JSONObject eventData;

        try {
            eventData = new JSONObject()
                    .put("userID", event.getUserID())
                    .put("isPublic", event.isPublic())
                    .put("name", event.getName())
                    .put("description",event.getDescription())
                    .put("lat",event.getLatitude())
                    .put("lng",event.getLongitude())
                    .put("repeats",event.isRepeats())
                    .put("repeatUnit",event.getRepeatUnit())
                    .put("repeatConst",event.getRepeatConst())
                    .put("dueDate",event.getDueDate())
                    .put("mustBeNear",event.isMustBeNear())
                    .put("isComplete",event.isComplete());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        Log.i("Sending new reminder","reminder");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                getString(R.string.newReminder_url),
                eventData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("auth response from server", response.toString());
                try {
                    if (response.getBoolean("success")) {
                        Toast.makeText(getApplicationContext(), "Event successfully added", Toast.LENGTH_SHORT).show();
                        setEventID(response.getJSONObject("event").getString("_id"));
                    } else {
                        Toast.makeText(getApplicationContext(), "Event not created on DB", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("New reminder error",error.toString());
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

    public void setEventID(String _id) {
        event.set_id(_id);
        App.userData.addEvent(event);
        finish();
    }

    // Send title and message as a notification through channel_1_ID
    // passed from App.java
    public void sendOnChannel1(View view) {
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();

        // Create a new activity intent for clicking on notification
        // contentIntent takes activityIntent as an argument
        Intent activityIntent = new Intent(this, NewReminderActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", name);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.notif)
                .setContentTitle(name)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();

        notificationManager.notify(1,notification);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                LatLng pos = (LatLng) data.getParcelableExtra("LatLng");
                latitude = pos.latitude;
                longitude = pos.longitude;

                displayCoord.setText("" + pos.latitude + "\n" + pos.longitude);
            }
        }
    }
}