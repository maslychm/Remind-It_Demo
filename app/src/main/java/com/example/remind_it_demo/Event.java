package com.example.remind_it_demo;

import android.app.Application;

import java.util.Date;

public class Event extends Application {
    private String username;
    private String userID;
    private boolean isPublic;
    private String name;
    private String description;
    private double location;
    private boolean repeats;
    private Date dueDate;
    private String completionMethod;

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getLocation() {
        return location;
    }

    public boolean isRepeats() {
        return repeats;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getCompletionMethod() {
        return completionMethod;
    }
}
