package com.maslychm.remind_it_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditDeleteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Event event;

    private EditText editName;
    private EditText descriptionEdit;
    private Button dueDateButton;
    private Switch repeatSwitch;
    private Switch nearbySwitch;
    private Button useCurrentLocationButton;
    private Button openPickLocationButton;
    private TextView latLngView;
    private Button cancelButton;
    private Button applyEditButton;
    private Button timePickerButton;
    private Button deleteButton;

    DialogFragment timePicker;
    private boolean editingOccured = true;
    private FusedLocationProviderClient fusedLocationClient;

    private Calendar calendar;
    private Calendar innerCalendar;

    String dateString;

    boolean repeatCheck = false;
    boolean mustBeNear = false;
    double latitude = 0.0f;
    double longitude = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete);

        event = (Event) getIntent().getSerializableExtra("Event");

        //Log.i("INSIDE EDIT DELETE", event.toString());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editName = findViewById(R.id.edit_text_name);
        descriptionEdit = findViewById(R.id.edit_text_description);
        dueDateButton = findViewById(R.id.setDueDateButton);
        repeatSwitch = findViewById(R.id.repeatSwitch);
        nearbySwitch = findViewById(R.id.nearbySwitch);
        useCurrentLocationButton = findViewById(R.id.useCurrentLocationButton);
        openPickLocationButton = findViewById(R.id.openLocPicker);
        latLngView = findViewById(R.id.latlong);
        cancelButton = findViewById(R.id.cancelButton);
        applyEditButton = findViewById(R.id.applyEdit);
        timePickerButton = findViewById(R.id.timePicker);
        deleteButton = findViewById(R.id.delete);

        fillEventData();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (editingOccured) {
                    new AlertDialog.Builder(EditDeleteActivity.this)
                            .setMessage("Cancel editing and exit?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    EditDeleteActivity.super.onBackPressed();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");

                innerCalendar = Calendar.getInstance();
                innerCalendar = ((TimePickerFragment) timePicker).getCalendar();
            }
        });

        useCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // If location permissions are granted and location returned, save it
                if (ContextCompat.checkSelfPermission(EditDeleteActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(EditDeleteActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        longitude = location.getLongitude();
                                        latitude = location.getLatitude();
                                        String locationString = "" +latitude + ", " + longitude;
                                        latLngView.setText(locationString);
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
                //openMapLocationPickerActivity(view);
            }
        });
    }

    public void fillEventData() {
        editName.setText(event.getName());
        descriptionEdit.setText(event.getDescription());
        ZonedDateTime zdt = ZonedDateTime.ofInstant(event.getDueDate(), ZoneId.systemDefault());
        calendar = GregorianCalendar.from(zdt);
        dateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        dueDateButton.setText(dateString);
        repeatSwitch.setChecked(event.isRepeats());
        nearbySwitch.setChecked(event.isMustBeNear());
        String latLngText = "" + event.getLatitude() + event.getLongitude();
        latLngView.setText(latLngText);
    }

    @Override
    public void onBackPressed() {
        if (editingOccured) {
            new AlertDialog.Builder(this)
                    .setMessage("Cancel editing and exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EditDeleteActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        dueDateButton.setText(dateString);
    }
}
