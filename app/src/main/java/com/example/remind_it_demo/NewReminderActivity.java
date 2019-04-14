package com.example.remind_it_demo;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import static com.example.remind_it_demo.App.NOTIFICATION_CHANNEL;

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
    private Switch addLocationSwitch;

    // Helping vars
    private String dateString;
    private NotificationManagerCompat notificationManager;
    private FusedLocationProviderClient fusedLocationClient;

    // Event constructor variables
    private Calendar calendar;
    boolean repeatCheck = false;
    boolean addLocationCheck = false;
    double latitude = 0.0f;
    double longitude = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        notificationManager = NotificationManagerCompat.from(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        calendar = calendar = Calendar.getInstance();

        displayCoord = (TextView) findViewById(R.id.latlong);
        editTextName = (EditText) findViewById(R.id.edit_text_name);
        editTextDescription = (EditText) findViewById(R.id.edit_text_description);
        buttonCancel = (Button) findViewById(R.id.cancelButton);
        buttonAddReminder = (Button) findViewById(R.id.addReminder);
        buttonSetDueDate = (Button) findViewById(R.id.setDueDateButton);
        buttonCurrentLocation = (Button) findViewById(R.id.useLocationButton);
        repeatSwitch = (Switch) findViewById(R.id.repeatSwitch);
        addLocationSwitch = (Switch) findViewById(R.id.addLocationSwitch);

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

                                        displayCoord.setText("" + latitude + ", " + longitude);
                                    }
                                }
                            });
                }
            }
        });

        repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    repeatCheck = true;
                } else {
                    repeatCheck = false;
                }
            }
        });

        addLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addLocationCheck = true;
                } else {
                    addLocationCheck = false;
                }
            }
        });
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
        String userName = App.userData.getUsername();
        String userID = App.userData.getUserID();
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();

        Event event = new Event(userID,name,description);
        event.setDueDate(calendar.toInstant());
        event.setPublic(false);
        event.setRepeats(repeatCheck);
        event.setLongitude(longitude);
        event.setLatitude(latitude);
        if (addLocationCheck)
            event.setCompletionMethod("location");
        else event.setCompletionMethod("dateTime");
        App.userData.addEvent(event);
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
}