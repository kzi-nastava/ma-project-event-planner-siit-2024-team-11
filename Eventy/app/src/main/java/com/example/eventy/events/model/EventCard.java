package com.example.eventy.events.model;

import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.model.utils.Location;

import java.util.Date;
import java.util.Objects;

public class EventCard {
    private String name;
    private String description;
    private Integer maxParticipants;
    private PrivacyType privacyType;
    private Date date;
    private Location location;
    private EventType eventType;

    public EventCard() { }

    public EventCard(String name, String description, Integer maxParticipants, PrivacyType privacyType, Date date, Location location, EventType eventType) {
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.privacyType = privacyType;
        this.date = date;
        this.location = location;
        this.eventType = eventType;
    }

    public EventCard(String businessMeeting, EventType meetingType, String s, int i, PrivacyType privacyType, Date date) {
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

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public PrivacyType getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(PrivacyType privacyType) {
        this.privacyType = privacyType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventCard eventCard = (EventCard) o;
        return Objects.equals(name, eventCard.name) && Objects.equals(description, eventCard.description) && Objects.equals(maxParticipants, eventCard.maxParticipants) && privacyType == eventCard.privacyType && Objects.equals(date, eventCard.date) && Objects.equals(location, eventCard.location) && Objects.equals(eventType, eventCard.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, maxParticipants, privacyType, date, location, eventType);
    }

    @Override
    public String toString() {
        return "EventCard{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", privacyType=" + privacyType +
                ", date=" + date +
                ", location=" + location +
                ", eventType=" + eventType +
                '}';
    }
}
