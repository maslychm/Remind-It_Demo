package com.example.remind_it_demo;

import android.app.Application;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public class UserData extends Application {
    private String username;
    private String email;
    private LocalDate birthday;
    private LocalDate creationDate;
    private String token;
    private String userID;

    private List<Event> userEvents;
    private List<Event> nearbyEvents;

    public UserData() {
        this.userEvents = new ArrayList<Event>();
        this.nearbyEvents = new ArrayList<Event>();
    }

    public void addEvent(Event event) {
        this.userEvents.add(event);
    }

    // For now just remove the object if it's the same
    public void removeEvent(Event event) {
        for (Event e : this.userEvents)
            if (event.getName().equals(e.getName()))
                this.userEvents.remove(e);
                return;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public List<Event> getUserEvents() {
        return userEvents;
    }

    public List<Event> getNearbyEvents() {
        return nearbyEvents;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserEvents(List<Event> userEvents) {
        this.userEvents = userEvents;
    }

    public void setNearbyEvents(List<Event> nearbyEvents) {
        this.nearbyEvents = nearbyEvents;
    }
}
