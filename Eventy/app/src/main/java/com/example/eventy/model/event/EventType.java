package com.example.eventy.model.event;

import java.util.Objects;

public class EventType {
    private String name;
    private String description;
    private Boolean isActive;

    public EventType() {}

    public EventType(String name, String description, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventType eventType = (EventType) o;
        return Objects.equals(name, eventType.name) && Objects.equals(description, eventType.description) && Objects.equals(isActive, eventType.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, isActive);
    }

    @Override
    public String toString() {
        return "EventType{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
