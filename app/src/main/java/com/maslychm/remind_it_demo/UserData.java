package com.maslychm.remind_it_demo;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserData extends Application {
    private String username;
    private String email;
    private LocalDate birthday;
    private LocalDateTime creationDate;
    private String token;
    private String userID;

    private ArrayList<Event> userEvents;
    private ArrayList<Event> nearbyEvents;

    public UserData() {
        this.userEvents = new ArrayList<Event>();
        this.nearbyEvents = new ArrayList<Event>();
    }

    public void addEvent(Event event) {
        this.userEvents.add(event);
    }

    // For now just remove the object if it's the same
    public void removeEvent(Event event) {
        Log.i("In UserData ", "remove called:" + event.toString());
        int length = this.userEvents.size();

        for (int i = 0; i < length; i++) {
            //Log.i("in UserDta: ", "comparing " + event.toString() + this.userEvents.get(i));
            if (event.get_id().equals(this.userEvents.get(i).get_id())){
                Log.i("In UserData ", "removing object E:" + event.get_id());
                this.userEvents.remove(event);
                return;
            }
        }
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public ArrayList<Event> getUserEvents() {
        return userEvents;
    }

    public ArrayList<Event> getNearbyEvents() {
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

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserEvents(ArrayList<Event> userEvents) {
        this.userEvents = userEvents;
    }

    public void setUserEvents(JSONArray userEvents) {
        int total = userEvents.length();
        int i = 0;
        JSONObject jsonReminder;
        Event tempEvent;
        ArrayList<Event> reminders = new ArrayList<Event>(total);

        for (i = 0; i < total; i++) {
            try {
                jsonReminder = userEvents.getJSONObject(i);
                tempEvent = new Event(App.userData.userID, jsonReminder.getString("name"),
                        jsonReminder.getString("description"));
                tempEvent.set_id(jsonReminder.getString("_id"));
                tempEvent.setDueDate(Instant.parse(jsonReminder.getString("dueDate")));
                tempEvent.setPublic(jsonReminder.getBoolean("isPublic"));
                tempEvent.setLatitude(jsonReminder.getDouble("lat"));
                tempEvent.setLongitude(jsonReminder.getDouble("lng"));
                tempEvent.setRepeats(jsonReminder.getBoolean("repeats"));
                tempEvent.setRepeatUnit(jsonReminder.getString("repeatUnit"));
                tempEvent.setRepeatConst(jsonReminder.getInt("repeatConst"));
                tempEvent.setMustBeNear(jsonReminder.getBoolean("mustBeNear"));
                tempEvent.setComplete(jsonReminder.getBoolean("isComplete"));

                reminders.add(tempEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        this.setUserEvents(reminders);
    }

    public void setNearbyEvents(ArrayList<Event> nearbyEvents) {
        this.nearbyEvents = nearbyEvents;
    }
}
