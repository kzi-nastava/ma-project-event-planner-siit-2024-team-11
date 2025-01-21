package com.example.eventy.events.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventFilters {
    private String search;
    private String selectedLocation;
    private ArrayList<String> selectedEventTypes;
    private String maxParticipants;
    private LocalDateTime selectedStartDateTime;
    private LocalDateTime selectedEndDateTime;

    public EventFilters() {}

    public EventFilters(String search, String selectedLocation, ArrayList<String> selectedEventTypes, String maxParticipants, LocalDateTime selectedStartDateTime, LocalDateTime selectedEndDateTime) {
        this.search = search;
        this.selectedLocation = selectedLocation;
        this.selectedEventTypes = selectedEventTypes;
        this.maxParticipants = maxParticipants;
        this.selectedStartDateTime = selectedStartDateTime;
        this.selectedEndDateTime = selectedEndDateTime;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public ArrayList<String> getSelectedEventTypes() {
        return selectedEventTypes;
    }

    public void setSelectedEventTypes(ArrayList<String> selectedEventTypes) {
        this.selectedEventTypes = selectedEventTypes;
    }

    public String getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(String maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public LocalDateTime getSelectedStartDateTime() {
        return selectedStartDateTime;
    }

    public void setSelectedStartDateTime(LocalDateTime selectedStartDateTime) {
        this.selectedStartDateTime = selectedStartDateTime;
    }

    public LocalDateTime getSelectedEndDateTime() {
        return selectedEndDateTime;
    }

    public void setSelectedEndDateTime(LocalDateTime selectedEndDateTime) {
        this.selectedEndDateTime = selectedEndDateTime;
    }

    @Override
    public String toString() {
        return "EventFilter{" +
                "search='" + search + '\'' +
                ", selectedLocation='" + selectedLocation + '\'' +
                ", selectedEventTypes=" + selectedEventTypes.toString() +
                ", maxParticipants=" + maxParticipants +
                ", selectedStartDateTime=" + selectedStartDateTime +
                ", selectedEndDateTime=" + selectedEndDateTime +
                '}';
    }
}
