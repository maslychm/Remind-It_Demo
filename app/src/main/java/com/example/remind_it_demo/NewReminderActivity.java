package com.example.remind_it_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.remind_it_demo.App.NOTIFICATION_CHANNEL;

public class NewReminderActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    private EditText editTextName;
    private EditText editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        notificationManager = NotificationManagerCompat.from(this);
        editTextName = findViewById(R.id.edit_text_name);
        editTextDescription = findViewById(R.id.edit_text_description);

        // Finish the New Reminder Activity when clicked Button cancel
        final Button cancel = (Button) findViewById(R.id.button6);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void addNewReminder(View view) {
        // Create event from userData and boxes, add event
        String userName = App.userData.getUserID();
        String userID = App.userData.getUserID();
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();

        Event event = new Event(userName,userID,name,description);
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
