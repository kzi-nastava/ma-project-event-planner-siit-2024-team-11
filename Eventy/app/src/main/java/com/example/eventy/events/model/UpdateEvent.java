package com.example.eventy.events.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateEvent {
    private Long id;

    private String name;

    private String description;

    private int maxNumberParticipants;

    private Long eventTypeId;

    private CreateLocation location;

    private LocalDateTime date;

    private List<CreateActivity> agenda;

    ///////////////////////////////

    public UpdateEvent() {

    }

    public UpdateEvent(Long id, String name, String description, int maxNumberParticipants, Long eventType, CreateLocation location, LocalDateTime date, List<CreateActivity> agenda) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxNumberParticipants = maxNumberParticipants;
        this.eventTypeId = eventType;
        this.location = location;
        this.date = date;
        this.agenda = agenda;
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

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public CreateLocation getLocation() {
        return location;
    }

    public void setLocation(CreateLocation location) {
        this.location = location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<CreateActivity> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<CreateActivity> agenda) {
        this.agenda = agenda;
    }
}
