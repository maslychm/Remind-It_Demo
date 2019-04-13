package com.example.remind_it_demo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.time.LocalDate;

public class App extends Application {
    public static final String NOTIFICATION_CHANNEL = "channel_1";
    public static UserData userData;

    @Override
    public void onCreate() {
        super.onCreate();

        userData = initUserData();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("For testing channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    // Init the userData object to then fill in during login
    private UserData initUserData() {
        userData = new UserData();

        userData.setUsername("mighty_lord");
        userData.setEmail("maslychm@gmail.com");
        userData.setBirthday(LocalDate.of(1999,8,16));
        userData.setUserID("123456");

        return userData;
    }
}
