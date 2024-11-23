package com.example.eventy.events.model;

public class Activity {
    private String name;
    private String description;
    private String location;
    private String startTime;
    private String endTime;

    public Activity(String name, String description, String location, String startTime, String endTime) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
