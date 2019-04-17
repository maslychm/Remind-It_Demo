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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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
    private boolean editingOccurred;
    private FusedLocationProviderClient fusedLocationClient;
    private RequestQueue queue;

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

        editingOccurred = false;

        event = (Event) getIntent().getSerializableExtra("Event");

        //Log.i("INSIDE EDIT DELETE", event.toString());

        timePicker = new TimePickerFragment();
        innerCalendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("EDT");
        calendar =  Calendar.getInstance();
        calendar.setTimeZone(tz);
        queue = Volley.newRequestQueue(this);

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
                if (editingOccurred) {
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
                } else {
                    finish();
                }
            }
        });

        dueDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePicker.show(getSupportFragmentManager(),"time picker");

                innerCalendar = Calendar.getInstance();
                innerCalendar = ((TimePickerFragment) timePicker).getCalendar();

                editingOccurred = true;
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
                editingOccurred = true;
            }
        });

        repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                repeatCheck = isChecked;
                editingOccurred = true;
            }
        });

        nearbySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mustBeNear = isChecked;
                editingOccurred = true;
            }
        });

        openPickLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //openMapLocationPickerActivity(view);
                editingOccurred = true;
            }
        });

        editName.setOnCapturedPointerListener(new View.OnCapturedPointerListener() {
            @Override
            public boolean onCapturedPointer(View view, MotionEvent event) {
                editingOccurred = true;
                return false;
            }
        });

        descriptionEdit.setOnCapturedPointerListener(new View.OnCapturedPointerListener() {
            @Override
            public boolean onCapturedPointer(View view, MotionEvent event) {
                editingOccurred = true;
                return false;
            }
        });

        applyEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editEvent();

                //sendEditRequest();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete event locally
                App.userData.removeEvent(event);
                sendDeleteRequest(event);
            }
        });
    }

    public Event editEvent() {
        String userID = App.userData.getUserID();
        String name = editName.getText().toString();
        String description = descriptionEdit.getText().toString();

        // A non practical way to do this, but if set at this time, means the clock pick is already closed...
        if (!(timePicker == null)) {
            innerCalendar = ((TimePickerFragment) timePicker).getCalendar();
            if (!(innerCalendar == null)) {
                calendar.set(Calendar.HOUR_OF_DAY, innerCalendar.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, innerCalendar.get(Calendar.MINUTE));
            }
        }

        // build a new event
        Event newEvent = new Event(userID,name,description);
        newEvent.set_id(event.get_id());
        newEvent.setDueDate(calendar.toInstant());
        newEvent.setPublic(false); //TODO set publicity
        newEvent.setRepeats(repeatCheck);
        newEvent.setRepeatUnit(""); //TODO set repeat unit
        newEvent.setRepeatConst(0); //TODO set repeatConst
        newEvent.setLongitude(longitude);
        newEvent.setLatitude(latitude);
        newEvent.setMustBeNear(mustBeNear);
        newEvent.setComplete(false);

        //sendEditRequest(event);

        // remove the old local event
        App.userData.removeEvent(event);

        //add new event to local storage
        App.userData.addEvent(newEvent);

        return newEvent;
    }

    public boolean sendDeleteRequest(final Event event) {
        JSONObject eventData;

        try {
            eventData = new JSONObject()
                    .put("_id", event.get_id());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        Log.i("EditDelete sending DELETE", event.get_id());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                getString(R.string.deleteReminder_url),
                eventData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("DELETE Response",response.toString());
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(),"Successfully deleted event", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error occured during deletion", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Deletion error",error.toString());
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
        if (editingOccurred) {
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
        } else {
            EditDeleteActivity.super.onBackPressed();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        dueDateButton.setText(dateString);
        editingOccurred = true;
    }
}
