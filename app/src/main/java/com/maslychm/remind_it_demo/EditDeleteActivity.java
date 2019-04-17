package com.maslychm.remind_it_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.maps.model.LatLng;
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

    Event passedEvent;
    private Event event;

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

        passedEvent = (Event) getIntent().getSerializableExtra("Event");

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
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete event locally
                Log.i("ERROR HERE IF NULL", ((Event) getIntent().getSerializableExtra("Event")).toString());
                passedEvent = (Event) getIntent().getSerializableExtra("Event");
                App.userData.removeEvent(passedEvent);
                sendDeleteRequest(passedEvent);
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

    public Event editEvent() {
        String userID = App.userData.getUserID();
        String name = editName.getText().toString();
        String description = descriptionEdit.getText().toString();

        Event newEvent = new Event(userID,name,description);
        newEvent.setDueDate(passedEvent.getDueDate());

        // A non practical way to do this, but if set at this time, means the clock pick is already closed...
        if (!(timePicker == null)) {
            innerCalendar = ((TimePickerFragment) timePicker).getCalendar();
            if (!(innerCalendar == null)) {
                calendar.set(Calendar.HOUR_OF_DAY, innerCalendar.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, innerCalendar.get(Calendar.MINUTE));
                newEvent.setDueDate(calendar.toInstant());
            }
        }

        // build a new event

        newEvent.set_id(passedEvent.get_id());
        //newEvent.setDueDate(calendar.toInstant());
        newEvent.setPublic(false);
        newEvent.setRepeats(repeatCheck);
        newEvent.setRepeatUnit(""); //TODO set repeat unit
        newEvent.setRepeatConst(0); //TODO set repeatConst
        newEvent.setLongitude(longitude);
        newEvent.setLatitude(latitude);
        newEvent.setMustBeNear(mustBeNear);
        newEvent.setComplete(false);

        sendEditRequest(newEvent);

        // remove the old local event
        App.userData.removeEvent(passedEvent);

        //add new event to local storage
        App.userData.addEvent(newEvent);

        return newEvent;
    }

    public boolean sendEditRequest(final Event newEvent) {
        JSONObject eventData;

        try {
            eventData = new JSONObject()
                    .put("_id", newEvent.get_id())
                    .put("userID", newEvent.getUserID())
                    .put("isPublic", newEvent.isPublic())
                    .put("name", newEvent.getName())
                    .put("description",newEvent.getDescription())
                    .put("lat",newEvent.getLatitude())
                    .put("lng",newEvent.getLongitude())
                    .put("repeats",newEvent.isRepeats())
                    .put("repeatUnit",newEvent.getRepeatUnit())
                    .put("repeatConst",newEvent.getRepeatConst())
                    .put("dueDate",newEvent.getDueDate())
                    .put("mustBeNear",newEvent.isMustBeNear())
                    .put("isComplete",newEvent.isComplete());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        Log.i("EditDelete sending UPDATE", eventData.toString());
        Log.i("Edit request to","");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                getString(R.string.editReminder_url) + newEvent.get_id(),
                eventData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("UPDATE Response",response.toString());
                try {
                    if (response.getBoolean("success")) {
                        Toast.makeText(getApplicationContext(),"Successfully updated event", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error occured during update", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Update error",error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization",App.userData.getToken());
                //headers.put("Content-Type","application/json");
                headers.put("id",newEvent.get_id());
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
        return true;
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

        Log.i("EditDelete sending DELETE", eventData.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                getString(R.string.deleteReminder_url) + event.get_id(),
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
                headers.put("Content-Type","application/json");
                //headers.put("_id",event.get_id());
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
        return true;
    }

    public void fillEventData() {
        editName.setText(passedEvent.getName());
        descriptionEdit.setText(passedEvent.getDescription());
        ZonedDateTime zdt = ZonedDateTime.ofInstant(passedEvent.getDueDate(), ZoneId.systemDefault());
        calendar = GregorianCalendar.from(zdt);
        dateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        dueDateButton.setText(dateString);
        repeatSwitch.setChecked(passedEvent.isRepeats());
        nearbySwitch.setChecked(passedEvent.isMustBeNear());
        String latLngText = "" + passedEvent.getLatitude() + passedEvent.getLongitude();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                LatLng pos = (LatLng) data.getParcelableExtra("LatLng");
                latitude = pos.latitude;
                longitude = pos.longitude;

                latLngView.setText("" + pos.latitude + "\n" + pos.longitude);
            }
        }
    }
}
