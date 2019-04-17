package com.maslychm.remind_it_demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class RemindersPageActivity extends AppCompatActivity {

    private FloatingActionButton newRem;
    private FloatingActionButton mapButton;
    private ListView listview;
    private EventListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_page);

        // Create floating button and add click listener
        newRem = findViewById(R.id.newReminder);
        mapButton = findViewById(R.id.goToMapFab);
        listview = findViewById(R.id.RemindersList);

        newRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newReminderActivity(view);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap(view);
            }
        });

        adapter = new EventListAdapter(getApplicationContext(), R.layout.list_item_layout, App.userData.getUserEvents());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RemindersPageActivity.this, "" + App.userData.getUserEvents().get(position), Toast.LENGTH_LONG).show();
                openEditDeleteActivity(view, App.userData.getUserEvents().get(position));
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RemindersPageActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.swapItems(App.userData.getUserEvents());
    }

    public void newReminderActivity(View view) {
        Intent intent = new Intent(this,NewReminderActivity.class);
        startActivity(intent);
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void openEditDeleteActivity(View view, Event event) {
        Intent intent = new Intent(this, EditDeleteActivity.class);
        intent.putExtra("Event", event);
        startActivity(intent);
    }
}
