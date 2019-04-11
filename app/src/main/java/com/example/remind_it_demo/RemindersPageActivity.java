package com.example.remind_it_demo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class RemindersPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_page);

        // Create floating button and add click listener
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Adding a new reminder", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                newReminderActivity(view);
            }
        });


        String test[] = App.userData.test;
        Event event1 = new Event("mighty","1","mighty","hello");
        Event event2 = new Event("mighty1","2","mighty1","hello1");
        App.userData.addEvent(event1);
        App.userData.addEvent(event2);

        List events = App.userData.getUserEvents();

        // Create array adapter and give adapter to listView
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, events);
        ListView listview = (ListView) findViewById(R.id.RemindersList);
        listview.setAdapter(adapter);
    }

    // TODO make list clickable with references to each reminder

    // TODO add pics with a timer or a location pic to each reminder
    // TODO Or figure out how to use RecyclerView

    /*
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        // Then you start a new Activity via Intent
        Intent intent = new Intent();
        intent.setClass(this, ListItemDetail.class);
        intent.putExtra("position", position);
        // Or / And
        intent.putExtra("id", id);
        startActivity(intent);
    }
    */
    public void newReminderActivity(View view) {
        Intent intent = new Intent(this,NewReminderActivity.class);
        startActivity(intent);
    }

}
