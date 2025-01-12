package com.example.eventy.events.model;

import java.time.LocalDateTime;
import java.util.List;

public class OrganizeEvent {
    private String name;

    private String description;

    private int maxNumberParticipants;

    private boolean isPublic;

    private Long eventTypeId;

    private CreateLocation location;

    private LocalDateTime date;

    private List<CreateActivity> agenda;

    private List<String> emails;

    private Long organizerId;

    public OrganizeEvent() {

    }

    public OrganizeEvent(String name, String description, int maxNumberParticipants, boolean isPublic, Long eventType, CreateLocation location, LocalDateTime date, List<CreateActivity> agenda, List<String> emails, Long organizerId) {
        this.name = name;
        this.description = description;
        this.maxNumberParticipants = maxNumberParticipants;
        this.isPublic = isPublic;
        this.eventTypeId = eventType;
        this.location = location;
        this.date = date;
        this.agenda = agenda;
        this.emails = emails;
        this.organizerId = organizerId;
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

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean aPublic) {
        isPublic = aPublic;
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
    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }
}

