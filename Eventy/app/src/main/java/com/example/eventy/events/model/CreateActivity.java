package com.example.eventy.events.model;

import java.time.LocalDateTime;

public class CreateActivity {
    private String name;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public CreateActivity(String name, String description, String location, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CreateActivity(Activity activity) {
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.location = activity.getLocation();
        this.startTime = activity.getStartTime();
        this.endTime = activity.getEndTime();
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
