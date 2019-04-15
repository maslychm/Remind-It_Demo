package com.maslychm.remind_it_demo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;


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
                newReminderActivity(view);
            }
        });

        ListView listview = findViewById(R.id.RemindersList);
        ArrayList<Event> events = App.userData.getUserEvents();
        EventListAdapter adapter = new EventListAdapter(getApplicationContext(), R.layout.list_item_layout, events);
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
