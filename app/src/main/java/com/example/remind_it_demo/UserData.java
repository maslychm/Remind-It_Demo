package com.example.remind_it_demo;

import android.app.Application;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public class UserData extends Application {
    private String username;
    private String email;
    private LocalDate birthday;
    private String token;

    private List<Event> userEvents;
    private List<Event> nearbyEvents;

    public UserData(String username, String email, LocalDate birthday, String token) {
        this.username = username;
        this.email = email;
        this.birthday = birthday;
        this.token = token;
        this.userEvents = new ArrayList<Event>();
        this.nearbyEvents = new ArrayList<Event>();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getToken() {
        return token;
    }
}
