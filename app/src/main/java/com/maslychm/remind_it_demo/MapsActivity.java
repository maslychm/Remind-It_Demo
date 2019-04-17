package com.maslychm.remind_it_demo;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Criteria criteria;
    private Location location;
    private CameraPosition cameraPosition;
    private ArrayList<Event> userEvents;
    private ArrayList<Event> publicEvents;

    private TextView titleView;
    private TextView descriptionView;
    private TextView dueDateView;

    private HashMap<Marker, Event> markerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMyLocationEnabled(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        userEvents = App.userData.getUserEvents();
        publicEvents = App.userData.getNearbyEvents();

        titleView = (TextView) findViewById(R.id.titleView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);
        dueDateView = (TextView) findViewById(R.id.dueDateView);

        markerMap = new HashMap<>();

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please enable location for this app", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Move camera to user location
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,false));
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Event loadEvent = markerMap.get(marker);

                marker.showInfoWindow();

                titleView.setText(loadEvent.getName());

                descriptionView.setText(loadEvent.getDescription());

                LocalDateTime dueDateTime = LocalDateTime.ofInstant(loadEvent.getDueDate(), ZoneOffset.UTC);
                LocalDateTime curDateTime = LocalDateTime.now(ZoneOffset.systemDefault());

                String dueDate = "" + dueDateTime.getMonth().toString()
                        + " " + ((Integer)dueDateTime.getDayOfMonth()).toString()
                        + " at " + ((Integer)dueDateTime.getHour()).toString()
                        + ":" + ((Integer)dueDateTime.getMinute()).toString();

                if (((Integer)dueDateTime.getMinute()).toString().startsWith("0")) {
                    dueDate += "0";
                }
                dueDateView.setText(dueDate);

                return true;
            }
        });

        placeMarkers();
        placePublicMarkers();
    }

    public void placeMarkers() {
        mMap.clear();
        for (Event event: userEvents) {
            Double lat = event.getLatitude();
            Double lng = event.getLongitude();

            LatLng latlng = new LatLng(lat,lng);
            MarkerOptions markerOptions = new MarkerOptions().position(latlng).title(event.getName());
            Marker marker = mMap.addMarker(markerOptions);
            marker.setSnippet(event.getDescription());

            markerMap.put(marker, event);
        }
    }

    public void placePublicMarkers() {
         for (Event event: publicEvents) {
             Double lat = event.getLatitude();
             Double lng = event.getLongitude();

             LatLng latlng = new LatLng(lat,lng);
             MarkerOptions markerOptions = new MarkerOptions().position(latlng).title(event.getName());
             Marker marker = mMap.addMarker(markerOptions);
             marker.setSnippet(event.getDescription());
             marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

             markerMap.put(marker, event);
         }
    }
}
