package com.maslychm.remind_it_demo;

import android.app.Application;

import java.time.Instant;

import androidx.annotation.NonNull;

public class Event extends Application {
    private String userID;
    private boolean isPublic;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private boolean repeats;
    private String repeatUnit;
    private int repeatConst;
    private Instant dueDate;
    private boolean mustBeNear;
    private boolean isComplete;

    public Event(String userID, String name, String description) {
        this.userID = userID;
        this.name = name;
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isRepeats() {
        return repeats;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRepeats(boolean repeated) {
        this.repeats = repeated;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public String getRepeatUnit() {
        return repeatUnit;
    }

    public void setRepeatUnit(String repeatUnit) {
        this.repeatUnit = repeatUnit;
    }

    public int getRepeatConst() {
        return repeatConst;
    }

    public void setRepeatConst(int repeatConst) {
        this.repeatConst = repeatConst;
    }

    public boolean isMustBeNear() {
        return mustBeNear;
    }

    public void setMustBeNear(boolean mustBeNear) {
        this.mustBeNear = mustBeNear;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean Complete) {
        isComplete = Complete;
    }
}
