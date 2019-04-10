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

public class RemindersPageActivity extends AppCompatActivity {

    public static String[] namesArray = {"Mykola","Kenneth","Jose","Jorge","Chris","Kyle","Misty"};

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

        // Create array adapter and give adapter to listView
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, namesArray);
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
