package com.example.eventy.events.model;

import java.time.LocalDateTime;
import java.util.List;

public class Event {
    private Long id;
    private String name;
    private String description;
    private int maxNumberParticipants;
    private boolean isPublic;
    private EventType eventType;
    private Location location;
    private LocalDateTime date;
    private List<Activity> agenda;
    private Long organizerId;

    public Event() {

    }

    public Event(Long id, String name, String description, int maxNumberParticipants, boolean isPublic, EventType eventType, Location location, LocalDateTime date, List<Activity> agenda, Long organizerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxNumberParticipants = maxNumberParticipants;
        this.isPublic = isPublic;
        this.eventType = eventType;
        this.location = location;
        this.date = date;
        this.agenda = agenda;
        this.organizerId = organizerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getMaxNumberParticipants() {
        return maxNumberParticipants;
    }

    public void setMaxNumberParticipants(int maxNumberParticipants) {
        this.maxNumberParticipants = maxNumberParticipants;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Activity> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<Activity> agenda) {
        this.agenda = agenda;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }
}
